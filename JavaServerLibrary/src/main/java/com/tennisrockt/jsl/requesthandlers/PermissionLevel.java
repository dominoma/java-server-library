package com.tennisrockt.jsl.requesthandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tennisrockt.jsl.exceptions.ForbiddenException;

public enum PermissionLevel {

	USER	(),
	PREMIUM	(USER),
	CLUB	(USER),
	ADMIN	(PREMIUM, CLUB);
	
	private final List<PermissionLevel> ranges;
	
	PermissionLevel(PermissionLevel... kinds) {
		ranges = kinds == null ? new ArrayList<>() : Arrays.asList(kinds);	
	}
	
	private boolean hasPermissionLevel(PermissionLevel permission) {
		if(permission == null || this == permission) {
			return true;
		}
		else {
			for(PermissionLevel range : ranges) {
				if(range.hasPermissionLevel(permission)) {
					return true;
				}
			}
			return false;
		}
	}
	public boolean hasPermissionLevels(PermissionLevel... permissions) {
		for(PermissionLevel permission : permissions) {
			if(hasPermissionLevel(permission)) {
				return true;
			}
		}
		return false;
	}
	
	public void checkPermissions(PermissionLevel... required) throws ForbiddenException {
		if(!hasPermissionLevels(required)) {
			throw new ForbiddenException("User has no permission for this operation! (level: '"+toString()+"', required: '"+required.toString()+"')");
		}
	}
	
}
