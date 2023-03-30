package cn.aotcloud.entity;

import java.io.Serializable;

public class RoleMenuPermissionParam implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String roleType;

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
}
