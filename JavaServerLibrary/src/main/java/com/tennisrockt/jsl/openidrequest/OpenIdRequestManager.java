package com.tennisrockt.jsl.openidrequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.dmfs.httpessentials.HttpMethod;
import org.dmfs.httpessentials.client.HttpRequest;
import org.dmfs.httpessentials.client.HttpRequestEntity;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.client.HttpResponse;
import org.dmfs.httpessentials.client.HttpResponseHandler;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.headers.Headers;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.httpessentials.types.MediaType;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.grants.ClientCredentialsGrant;
import org.dmfs.oauth2.client.http.decorators.BearerAuthenticatedRequest;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.optional.Optional;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tennisrockt.jsl.config.ValueSupplier;
import com.tennisrockt.jsl.exceptions.CriticalServerException;
import com.tennisrockt.jsl.utils.ServerUtils;

public class OpenIdRequestManager {
	private final HttpRequestExecutor executor = new HttpUrlConnectionExecutor();
	private OAuth2Client client;
	private OAuth2AccessToken accessToken;
	
	private final ValueSupplier<String> configUrl;
	private final ValueSupplier<String> username;
	private final ValueSupplier<String> ptw;
	
	private final Logger logger = LoggerFactory.getLogger(OpenIdRequestManager.class);
	
	
	
	public OpenIdRequestManager(ValueSupplier<String> configUrl, ValueSupplier<String> username, ValueSupplier<String> ptw) {
		this.configUrl = configUrl;
		this.username = username;
		this.ptw = ptw;
	}
	
	public synchronized void updateConfig() {
		try {
			logger.info("Updating config...");
			URL url = new URL(configUrl.value());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			JSONObject openIdConfig = ServerUtils.parseJSON(con.getInputStream());
			
			OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
			    URI.create(openIdConfig.getString("authorization_endpoint")),
			    URI.create(openIdConfig.getString("token_endpoint")),
			    new Duration(1,0,3600));
			OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(username.value(), ptw.value());
			client = new BasicOAuth2Client(provider, credentials, new LazyUri(new Precoded("http://localhost")));
			logger.info("Config successfully updated.");
		} catch (IOException e) {
			throw new CriticalServerException(e);
		}
	}
	private synchronized boolean checkUpdateTokens() {
		try {
			if(accessToken == null || accessToken.expirationDate().before(DateTime.now())) {
				logger.info("Tokens expired. Fetching new tokens...");
				accessToken = new ClientCredentialsGrant(client, new BasicScope("openid")).accessToken(executor);
				logger.info("Tokens successfully fetched.");
				return true;
			}
			else {
				return false;
			}
		} catch (ProtocolException | IOException | ProtocolError e) {
			throw new CriticalServerException(e);
		}
	} 
	public JSONObject doRequest(String url) {
		return doRequest(url, null);
	}
	public synchronized JSONObject doRequest(String url, JSONObject body) {
		if(client == null) {
			updateConfig();
		}
		HttpRequest<JSONObject> request = new HttpRequest<JSONObject>() {
			@Override
			public HttpMethod method() {
				return body == null ? HttpMethod.GET : HttpMethod.POST;
			}

			@Override
			public Headers headers() {
				return null;
			}

			@Override
			public HttpRequestEntity requestEntity() {
				if(body == null) {
					return null;
				}
				else {
					return new HttpRequestEntity() {
						
						@Override
						public void writeContent(OutputStream out) throws IOException {
							out.write(body.toString().getBytes());
						}
						
						@Override
						public Optional<MediaType> contentType() {
							return null;
						}
						
						@Override
						public Optional<Long> contentLength() {
							return null;
						}
					};
				}
			}

			@Override
			public HttpResponseHandler<JSONObject> responseHandler(HttpResponse response)
					throws IOException, ProtocolError, ProtocolException {
				return new HttpResponseHandler<JSONObject>() {

					@Override
					public JSONObject handleResponse(HttpResponse response) throws IOException, ProtocolError, ProtocolException {
						return ServerUtils.parseJSON(response.responseEntity().contentStream());
					}
				};
			}
		};
		try {
			checkUpdateTokens();
			return executor.execute(URI.create(url), new BearerAuthenticatedRequest<JSONObject>(request, accessToken));
		} catch (IOException | ProtocolError | ProtocolException e) {
			throw new CriticalServerException(e);
		}
	}
}
