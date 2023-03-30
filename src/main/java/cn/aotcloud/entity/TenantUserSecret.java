package cn.aotcloud.entity;

import java.io.Serializable;

public class TenantUserSecret implements Serializable {
	
	private static final long serialVersionUID = 1L;

    //租户用户访问ID
    private String accessKeyId;
    
    //租户用户访问秘钥
    private String accessKeySecret;

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
    
}
