package cn.aotcloud.entity;

import java.io.Serializable;
import java.util.Arrays;

public class Platforms implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * 支持平台(1-安卓,2-IOS)
     */
    protected String[] platforms;
    
    public String[] getPlatforms() {
		return platforms != null ? Arrays.copyOf(platforms, platforms.length) : null;
	}

	public void setPlatforms(String[] platforms) {
		this.platforms = platforms != null ? Arrays.copyOf(platforms, platforms.length) : null;
	}
	
}
