package cn.aotcloud.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具类
 */
public class CompressUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(CompressUtil.class);

    private static final int BUFFER_SIZE = 1024;
    
    /**
     * 压缩为zip格式，支持文件、文件夹的压缩
     *
     * @param sourcePath 被压缩文件地址
     * @param targetPath 压缩文件保存地址
     */
    public static String compressToZip(String sourcePath, String targetPath) {
        File sourceFile = validateSourcePath(sourcePath);
        return compressToZip(sourceFile, targetPath);
    }

    /**
     * 压缩为zip格式，支持文件、文件夹的压缩
     *
     * @param sourceFile 被压缩文件
     * @param targetPath 压缩文件保存地址
     */
    public static String compressToZip(File sourceFile, String targetPath) {
        validateTargetPath(targetPath);
        //输入文件路径包含文件名
        File targetFile = new File(String.format("%s%s%s.%s", targetPath, File.separator, sourceFile.getName(), "zip"));

        //1.使用try-with-resource优雅关闭流
        //2.使用CRC32进行文件校验
        //logger.debug("start to compress file to zip, file name:{}", sourceFile.getName());
        //long start = System.currentTimeMillis();
        try (FileOutputStream fileOut = new FileOutputStream(targetFile);
    		CheckedOutputStream cos = new CheckedOutputStream(fileOut, new CRC32());
    		ZipOutputStream zipOut = new ZipOutputStream(cos)) {
            String baseDir = "";
            compressToZip(sourceFile, zipOut, baseDir);
            return targetFile.getName();
        } catch (FileNotFoundException e) {
        	logger.error("compress file to zip throw exception:{}", e);
        } catch (IOException e) {
        	logger.error("compress file to zip throw exception:{}", e);
        }
        //logger.debug("finish compress file to zip, file name:{}, cost:{} ms", sourceFile.getName(), System.currentTimeMillis() - start);
        return null;
    }

    /**
     * 真正文件/文件夹的压缩部分
     *
     * @param sourceFile 待压缩文件
     * @param zipOut     压缩流
     * @param baseDir
     */
    private static void compressToZip(File sourceFile, ZipOutputStream zipOut, String baseDir) throws IOException {
        //文件夹的压缩
        if (sourceFile.isDirectory()) {
            compressDirectoryToZip(sourceFile, zipOut, baseDir);
        } else {
            //文件的压缩
            compressFileToZip(sourceFile, zipOut, baseDir);
        }
    }

    /**
     * 文件夹的压缩
     *
     * @param sourceFile 待压缩文件
     * @param zipOut     压缩流
     * @param basePath   基本路径
     */
    private static void compressDirectoryToZip(File sourceFile, ZipOutputStream zipOut, String basePath) throws IOException {
        File[] files = sourceFile.listFiles();
        for (File file : files) {
            compressToZip(file, zipOut, basePath + sourceFile.getName() + File.separator);
        }
    }

    /**
     * 文件的压缩
     *
     * @param sourceFile 待压缩文件
     * @param zipOut     压缩流
     * @param basePath   基本路径
     */
    private static void compressFileToZip(File sourceFile, ZipOutputStream zipOut, String basePath) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
            ZipEntry entry = new ZipEntry(basePath + sourceFile.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                zipOut.write(data, 0, count);
            }
        }
    }

    public static String unpackExportZipEntry(ZipFile zipFile, ZipEntry entry, String targetPath) throws IOException {
    	File targetDir = CompressUtil.validateTargetPath(targetPath);
    	String entryName = entry.getName();
    	entryName = StringUtils.replace(entryName, "/", File.separator);
    	entryName =	StringUtils.substringAfter(entryName, File.separator);
        // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
        File tempFile = new File(targetDir, entryName);
        // 保证这个文件的父文件夹必须要存在
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        tempFile.createNewFile();

        // 将压缩文件内容写入到这个文件中
        try (InputStream is = zipFile.getInputStream(entry);
        	FileOutputStream fos = new FileOutputStream(tempFile)) {
            int len;
            byte[] buf = new byte[BUFFER_SIZE];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        }
        
        unpackZip(targetPath + File.separator + entryName, targetPath);
        tempFile.delete();
        
        return targetPath + File.separator + StringUtils.substringBeforeLast(entryName, ".");
    }
    
    /**
     * 解压zip格式的压缩包
     *
     * @param sourcePath 待解压文件路径
     * @param targetPath 解压路径
     */
    public static void unpackZip(String sourcePath, String targetPath) {
        File sourceFile = validateSourcePath(sourcePath);
        unpackZip(sourceFile, targetPath);
    }

    public static void unpackZip(File sourceFile, String targetPath) {
        //校验解压地址是否存在
        validateTargetPath(targetPath);

        //logger.debug("start to unpack zip file, file name:{}", sourceFile.getName());
        //long start = System.currentTimeMillis();
        try (ZipFile zipFile = new ZipFile(sourceFile)) {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = targetPath + File.separator + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File tempFile = new File(targetPath + File.separator + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!tempFile.getParentFile().exists()) {
                        tempFile.getParentFile().mkdirs();
                    }
                    tempFile.createNewFile();

                    // 将压缩文件内容写入到这个文件中
                    try (InputStream is = zipFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(tempFile)) {
                        int len;
                        byte[] buf = new byte[BUFFER_SIZE];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                }

            }
            //logger.debug("finish unpack zip file, file name:{}, cost:{} ms", sourceFile.getName(), System.currentTimeMillis() - start);
        } catch (Exception e) {
        	logger.error("unpack zip throw exception:{}", e);
        }
    }

    /**
     * 源文件路径判断
     *
     * @param sourcePath 待解压文件路径
     * @return
     */
    public static File validateSourcePath(String sourcePath) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
        	logger.error("the source file is not exist, source path: {}", sourceFile.getAbsolutePath());
            throw new RuntimeException("the source file is not exist");
        }
        return sourceFile;
    }

    /**
     * 解压路径存在判断
     *
     * @param targetPath
     * @return
     */
    public static File validateTargetPath(String targetPath) {
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
        	//logger.debug("the target file is not exist, target path: {}. create", targetFile.getAbsolutePath());
            targetFile.mkdirs();
        }

        return targetFile;
    }
}
