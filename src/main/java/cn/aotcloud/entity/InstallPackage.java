package cn.aotcloud.entity;

import java.io.Serializable;


public class InstallPackage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//安装包是否进行安全扫描
    private String secretScan;
    
    //安装包秘钥泄露情况
    private byte[] secretResource;

	public String getSecretScan() {
		return secretScan;
	}

	public void setSecretScan(String secretScan) {
		this.secretScan = secretScan;
	}

	public byte[] getSecretResource() {
		return secretResource;
	}

	public void setSecretResource(byte[] secretResource) {
		this.secretResource = secretResource;
	}
	
}
