package cn.aotcloud.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentResult extends WeiXinResult {
	
	private static final long serialVersionUID = 1L;
	private String iscSecret;
	private String iscInterfaceSignKey;
	private String iscInterfaceSignPubKey;

	public String getIscInterfaceSignKey() {
		return iscInterfaceSignKey;
	}

	public void setIscInterfaceSignKey(String iscInterfaceSignKey) {
		this.iscInterfaceSignKey = iscInterfaceSignKey;
	}

	public String getIscInterfaceSignPubKey() {
		return iscInterfaceSignPubKey;
	}

	public void setIscInterfaceSignPubKey(String iscInterfaceSignPubKey) {
		this.iscInterfaceSignPubKey = iscInterfaceSignPubKey;
	}

	public String getIscSecret() {
		return iscSecret;
	}

	public void setIscSecret(String iscSecret) {
		this.iscSecret = iscSecret;
	}
}
