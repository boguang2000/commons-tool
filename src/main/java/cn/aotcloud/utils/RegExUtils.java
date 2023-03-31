package cn.aotcloud.utils;

import org.apache.commons.lang3.StringUtils;

public class RegExUtils {

	public static boolean matchesLike(String text, String query) {
		if(StringUtils.isAnyBlank(text, query)) {
			return false;
		} else {
			return text.matches("(.*"+query+".*)");
		}
	}
}
