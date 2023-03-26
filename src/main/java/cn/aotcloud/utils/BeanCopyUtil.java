package cn.aotcloud.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanCopyUtil extends BeanUtils {

	private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtil.class);

	public static void copyProperties(Object dest, Object src) {
		try {
			BeanUtils.copyProperties(dest, src);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			logger.warn("对象复制时发生异常：{}", e.getMessage());
		}
	}
}
