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

import com.tennisrockt.jsl.exceptions.ServerException;
import com.tennisrockt.jsl.utils.ServerUtils;

import express.utils.Status;

class TokensManagerUtils {

	private static final HttpRequestExecutor executor = new HttpUrlConnectionExecutor();
	private static OAuth2Client client;
	private static OAuth2AccessToken accessToken;
	
	public static void updateConfig(String configUrl, String username, String ptw) throws ServerException {
		try {
			URL url = new URL(configUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			JSONObject openIdConfig = ServerUtils.parseJSON(con.getInputStream());
			
			OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
			    URI.create(openIdConfig.getString("authorization_endpoint")),
			    URI.create(openIdConfig.getString("token_endpoint")),
			    new Duration(1,0,3600));
			OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(username, ptw);
			client = new BasicOAuth2Client(provider, credentials, new LazyUri(new Precoded("http://localhost")));
		} catch (IOException e) {
			throw new ServerException(e);
		}
	}
	private static boolean checkUpdateTokens() throws ServerException {
		try {
			if(accessToken == null || accessToken.expirationDate().before(DateTime.now())) {
				accessToken = new ClientCredentialsGrant(client, new BasicScope("openid")).accessToken(executor);
				return true;
			}
			else {
				return false;
			}
		} catch (ProtocolException | IOException | ProtocolError e) {
			throw new ServerException(e);
		}
	} 
	public static JSONObject doRequest(String configUrl, String username, String ptw, String url) throws ServerException {
		return doRequest(configUrl, username, ptw, url, null);
	}
	public static JSONObject doRequest(String configUrl, String username, String ptw, String url, JSONObject body) throws ServerException {
		if(client == null) {
			updateConfig(configUrl, username, ptw);
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
						try {
							return ServerUtils.parseJSON(response.responseEntity().contentStream());
						} catch (ServerException e) {
							return null;
						}
					}
				};
			}
		};
		try {
			checkUpdateTokens();
			JSONObject response = executor.execute(URI.create(url), new BearerAuthenticatedRequest<JSONObject>(request, accessToken));
			if(response == null) {
				throw new ServerException(Status._500, "Request failed!");
			}
			else {
				return response;
			}
		} catch (IOException | ProtocolError | ProtocolException e) {
			throw new ServerException(e);
		}
	}
}
