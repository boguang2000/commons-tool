package cn.aotcloud.entity;

import java.io.Serializable;

public class Application implements Serializable {

	private static final long serialVersionUID = 1L;

	private String secret;

    private String agentSecret;

    private String admins;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAgentSecret() {
		return agentSecret;
	}

	public void setAgentSecret(String agentSecret) {
		this.agentSecret = agentSecret;
	}

	public String getAdmins() {
		return admins;
	}

	public void setAdmins(String admins) {
		this.admins = admins;
	}
    
}
