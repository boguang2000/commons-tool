package cn.aotcloud.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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
        File targetFile = new File(String.format("%s%s%s.%s", targetPath, File.separator, FilenameUtils.normalize(sourceFile.getName()), "zip"));

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
    
    public static void compressNoParentZip(File sourceFile, ZipOutputStream zos, String name) throws IOException {
        byte[] buf = new byte[1024];
        if(sourceFile.isFile()){
            // 压缩单个文件，压缩后文件名为当前文件名
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 空文件夹的处理(创建一个空ZipEntry)
                zos.putNextEntry(new ZipEntry(""));
                zos.closeEntry();
            } else {
                // 递归压缩文件夹下的文件
                for (File file : listFiles) {
                	compressNoParentZip(file, zos, file.getName());
                }
            }
        }
    }
    
    /**
     * 解压 zip 文件
     * @throws Exception
     */
	@SuppressWarnings("deprecation")
	public static List<String> unZip(InputStream input, String fileId, String scanDir) throws IOException {
    	int BUFFER_SIZE = 1024;
        File destFile = FileUtil.newFile(scanDir, FilenameUtils.normalize(fileId));
        if(FileUtil.exists(destFile)) {
        	destFile.delete();
        }
        FileUtil.mkdirs(destFile);
        
        ZipArchiveInputStream is = null;
        BufferedInputStream bis = null;
        List<String> fileNames = new ArrayList<String>();

        try {
        	bis = new BufferedInputStream(input, BUFFER_SIZE);
        	is = isWindows() ? new ZipArchiveInputStream(bis, "GBK") :  new ZipArchiveInputStream(bis);
            //is = new ZipArchiveInputStream(bis, "UFT-8");
            ZipArchiveEntry entry = null;
            while ((entry = is.getNextZipEntry()) != null) {
                fileNames.add(entry.getName());
                if (entry.isDirectory()) {
                    File directory = FileUtil.newFile(destFile, FilenameUtils.normalize(entry.getName()));
                    FileUtil.mkdirs(directory);
                } else {
                	FileOutputStream fos = null;
                    OutputStream os = null;
                    try {
                    	File file = FileUtil.newFile(destFile, FilenameUtils.normalize(entry.getName()));
                    	FileUtil.mkdirs(file.getParentFile());
                    	fos = new FileOutputStream(file);
                        os = new BufferedOutputStream(fos, BUFFER_SIZE);
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                        IOUtils.closeQuietly(fos);
                    }
                }
            }
        } catch(IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
        	IOUtils.closeQuietly(bis);
        	IOUtils.closeQuietly(is);
        }

        return fileNames;
    }
	
    private static boolean isWindows() {
		Properties prop = System.getProperties();
		String osName = prop.getProperty("os.name");
		return StringUtils.startsWithIgnoreCase(osName, "win");
	}
}
