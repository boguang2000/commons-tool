package cn.aotcloud.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

public class FileUtil extends FileUtils {

	public static File newFile(final String pathname) {
		return new File(pathname);
	}
	
	public static File newFile(final String parent, final String child) {
		mkdirs(parent);
		return new File(parent, child);
	}
	
	public static File newFile(final File parent, final String child) {
		mkdirs(parent);
		return new File(parent, child);
	}
	
	public static boolean mkdirs(final File file) {
		if(!FileUtil.exists(file)) {
			return file.mkdirs();
    	}
		return false;
	}
	
	public static boolean mkdirs(final String pathname) {
		return newFile(pathname).mkdirs();
	}
	
	public static boolean mkdir(final File file) {
		return file.mkdir();
	}
	
	public static boolean mkdir(final String pathname) {
		return newFile(pathname).mkdir();
	}
	
	public static File[] listFiles(File searchFile, FileFilter filter) {
		return searchFile.listFiles(filter);
	}
	
	public static File[] listFiles(File searchFile, FilenameFilter filter) {
		return searchFile.listFiles(filter);
	}
	
	public static boolean exists(final File file) {
		return file != null && file.exists();
	}
	
	public static boolean exists(final String pathname) {
		return newFile(pathname).exists();
	}
	
	public static boolean isDirectory(final File file) {
		return file.isDirectory();
	}
	
	public static boolean isFile(final File file) {
		return file.isFile();
	}
	
	public static boolean canRead(final String pathname) {
		return newFile(pathname).canRead();
	}
	
	public static boolean canWrite(final String pathname) {
		return newFile(pathname).canWrite();
	}
	
	public static boolean canExecute(final String pathname) {
		return newFile(pathname).canExecute();
	}
	
	public static boolean canRead(final File file) {
		return file.canRead();
	}
	
	public static boolean canWrite(final File file) {
		return file.canWrite();
	}
	
	public static boolean canExecute(final File file) {
		return file.canExecute();
	}
	
	public static String getParent(final String pathname) {
		return newFile(pathname).getParent();
	}
	
	public static String getName(final String pathname) {
		return newFile(pathname).getName();
	}
	
	public static String getAbsolutePath(final String pathname) {
		return newFile(pathname).getAbsolutePath();
	}
	
	public static long length(final String pathname) {
		return newFile(pathname).length();
	}
	
	public static String getPath(final String parent, final String child) {
		return FileUtil.newFile(parent, child).getPath();
	}
	
	public static String readFileContent(String pathname) throws IOException {
		StringBuilder sb = new StringBuilder();
		List<String> list = org.apache.commons.io.FileUtils.readLines(FileUtil.newFile(pathname), "UTF-8");
		list.forEach((line) -> {sb.append(line).append(System.getProperty("line.separator"));});
		return sb.toString();
	}
	
	public static void writeFileContent(String pathname, String content) throws IOException {
		org.apache.commons.io.FileUtils.write(FileUtil.newFile(pathname), content, "UTF-8");
	}
	
	@SuppressWarnings("deprecation")
	public static void write(InputStream in, String pathname) throws IOException {
		try (FileOutputStream out = new FileOutputStream(pathname)) {
			StreamUtils.copy(in, out);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static int copy(String inPathname, String outPathname) throws IOException {
		return FileCopyUtils.copy(newFile(inPathname), newFile(outPathname));
	}
	
	public static int copy(InputStream in, String outPathname) throws IOException {
		return FileCopyUtils.copy(in, new FileOutputStream(outPathname));
	}
	
	public static String getFilenameFromPath(String path) {
		String firstPathCandidate = path.substring(path.lastIndexOf("/") + 1);
		String secondPathCandidate = path.substring(path.lastIndexOf("\\") + 1);
		return path.equals(firstPathCandidate) ? secondPathCandidate : firstPathCandidate;
	}
	
	public static boolean createDir(File dir) {
		if(!dir.exists()) {
			return dir.mkdirs();
    	} else {
    		return true;
    	}
	}
}
