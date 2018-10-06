package com.tennisrockt.jsl.requesthandlers;

import com.tennisrockt.jsl.exceptions.AuthenticationException;
import com.tennisrockt.jsl.exceptions.RequestException;
import com.tennisrockt.jsl.exceptions.ServerException;
import com.tennisrockt.jsl.keymanager.IKeyManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public abstract class JWTAuthenticatedRequestHandler extends RequestHandler {

	private Claims claims = null;
	
	private PermissionLevel permissionLevel;
	
	@Override
	public void preHandle() throws ServerException {
		if(getRequest().getAuthorization().size() != 1) {
			throw new AuthenticationException(); 
		}
		String jws = getRequest().getAuthorization().get(0).getData();
		Jws<Claims> jwt;
		try {
			jwt = Jwts.parser().setSigningKeyResolver(getKeyManager().getKeyResolver()).parseClaimsJws(jws);
		}
		catch(Exception e) {
			throw new AuthenticationException();
		}
		claims = jwt.getBody();
		String perm = (String) claims.get("permissionlevel");
		if(perm == null) {
			permissionLevel = PermissionLevel.ADMIN;
			//throw new RequestException("Permission level claim is missing!");
		}
		else {
			permissionLevel = PermissionLevel.valueOf(perm.toUpperCase());
			if(permissionLevel == null) {
				throw new RequestException("Permission level '"+perm+"' is unknown. (Supported levels: 'USER', 'CLUB', 'PREMIUM', 'ADMIN')");
			}
		}
	}
	public Claims getClaims() {
		return claims;
	}
	public PermissionLevel getPermissions() {
		return permissionLevel;
	}
	
	public abstract IKeyManager getKeyManager();
}