package cn.aotcloud.utils;

import java.io.File;

public class FileUtil {

	public static boolean createDir(File dir) {
		if(!dir.exists()) {
			return dir.mkdirs();
    	} else {
    		return true;
    	}
	}
}
