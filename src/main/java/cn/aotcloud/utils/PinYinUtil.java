package cn.aotcloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 */
public class PinYinUtil {

	private static Logger logger = LoggerFactory.getLogger(PinYinUtil.class);
	
	private final static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

	static {
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	/**
	 * 将字符串转成拼音
	 * 
	 * @param chinese
	 * @return
	 */
	public static String getPinYin(String chinese) {
		char[] spell = chinese.toCharArray();
		String pinYin = "";
		for (int i = 0; i < spell.length; i++) {
			if ((spell[i] >= 0x4e00) && (spell[i] <= 0x9fbb)) { // 判断是否是汉字（不包含中文字符）
				try {
					pinYin += PinyinHelper.toHanyuPinyinStringArray(spell[i], defaultFormat)[0];
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					logger.error("获取汉字拼音时发生BadHanyuPinyinOutputFormatCombination异常");
				}
			} else {
				pinYin += spell[i];
			}
		}
		return pinYin;
	}
	
	/**
	 * 获取中文的拼音首字母
	 * 
	 * @author ZSQ
	 * @param chinese
	 * @return
	 */
	public static String getFirstPinYin(String chinese) {
		return getPinYin(chinese).substring(0,1);
	}
	
	/**
	 * 获取中文的大写拼音首字母
	 * 
	 * @author ZSQ
	 * @param chinese
	 * @return
	 */
	public static String getFirstUpPinYin(String chinese) {
		return getFirstPinYin(chinese).toUpperCase();
	}

	// 判断是否是汉字（包含中文字符）
	@SuppressWarnings("unused")
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 将字符串的首字母转大写
	 * @param str 需要转换的字符串
	 * @return
	 */
	public static String captureName(String str) {
		// 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
		char[] cs=str.toCharArray();
		cs[0]-=32;
		return String.valueOf(cs);
	}
}
