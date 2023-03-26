package cn.aotcloud.utils;

import org.apache.commons.lang3.StringUtils;

public class StringSafeUtil {

	public static String getString(String str) {
		if(StringUtils.isNotBlank(str)) {
			str = StringUtils.trim(str);
		}
		
		return str;
	}
	
}
