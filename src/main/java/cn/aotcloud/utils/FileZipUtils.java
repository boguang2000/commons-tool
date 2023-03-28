package cn.aotcloud.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
 
public class FileZipUtils {
	
	protected static Logger logger = LoggerFactory.getLogger(FileZipUtils.class);

    /**
     * 压缩
     * @param sourceFile 压缩源文件，会在源文件所在目录下新建一个zip文件夹存放压缩后的文件
     * @param password   密码
     */
    @SuppressWarnings("resource")
	public static File compress(File sourceFile, String password) {
        // 文件名
        String fileName = sourceFile.getName();
        // 文件真实名（不含扩展名）
        String realName = fileName.indexOf(".")!=-1 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        File targetFile = new File(sourceFile.getParent() + File.separator + realName + ".zip");
 
        ZipParameters zipParameters = new ZipParameters();
        ZipFile zipFile = new ZipFile(targetFile);
        // 是否加密
        if (StringUtils.isNotBlank(password)) {
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipFile.setPassword(password.toCharArray());
        }
        try {
        	if(sourceFile.isDirectory()) {
        	    File[] files = sourceFile.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        zipFile.addFolder(file, zipParameters);
                    } else {
                        zipFile.addFile(file, zipParameters);
                    }
                }
        	} else {
        		zipFile.addFile(sourceFile, zipParameters);
        	}
        } catch (IOException e) {
        	logger.error("压缩文件异常：", e);
        }
        
        return targetFile;
    }
 
    /**
     * 解压
     *
     * @param sourceFile 解压源文件，会在源文件所在目录下新建一个unzip文件夹存放解压后的文件
     * @param password   密码
     */
    @SuppressWarnings("resource")
	public static void uncompress(File sourceFile, File targetDir, String password) {
        if(!FileUtil.exists(targetDir)) {
        	targetDir.mkdir();
        }
 
        ZipFile zipFile = new ZipFile(sourceFile);
        try {
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(targetDir.getPath());
        } catch (ZipException e) {
        	logger.error("解压缩文件异常：", e);
        }
    }
    
}