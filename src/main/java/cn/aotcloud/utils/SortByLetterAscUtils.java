package cn.aotcloud.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

public class SortByLetterAscUtils {

	/**
	 * @param list 		需要排序的集合
	 * @param field		排序的字段
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static <T> Map<String, List<T>> sortByLetterAsc(List<T> list, String field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//判断是否为空集合
		if(list== null || list.isEmpty()) return Collections.emptyMap();
		
		Map<String, List<T>> groupMap = new HashMap<String, List<T>>();
		List<T> otherList = new ArrayList<>();
		// 对map进行分组
		for (T t : list) {
			String key = PinYinUtil.getFirstPinYin(BeanUtils.getProperty(t, field)).toUpperCase();
			if(key.matches("[A-Z]")) {
				if(!groupMap.containsKey(key)) {
					groupMap.put(key, new ArrayList<T>());
				}
				groupMap.get(key).add(t);
			}else {
				otherList.add(t);
			}
		}
		
		Map<String, List<T>> result = new LinkedHashMap<>(groupMap.size());
		groupMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		
		//#放map最后
		if(!otherList.isEmpty()) {
			result.put("#", otherList);
		}
		return result;
	}

	/**
	 *
	 * @param object 一个集合
	 * @param clazz  集合存放的类
	 * @param field  要排序的类的字段,字段的getXyz() X要大写
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, ArrayList<Object>> sortByLetterAsc(Object object, Class<?> clazz, String field)
			throws Exception {

		if (object instanceof List) {
			List<Object> list = (List<Object>) object;
			Class<?> c = Class.forName(clazz.getName());
			Object o = c.newInstance();
			// 按拼音首字母表排序
			Map<String, ArrayList<Object>> letterMap = new TreeMap<String, ArrayList<Object>>(
					new MapSortUtil().getMapKeyComparator());
			if (!list.isEmpty()) {
				for (Object t : list) {
					o = t;
					String pinYinFirstLetter = getFieldValue(field, o);
					if (!letterMap.containsKey(pinYinFirstLetter) && pinYinFirstLetter.matches("[A-Z]")) {
						letterMap.put(pinYinFirstLetter, new ArrayList<Object>());
					}
				}
				Iterator<Entry<String, ArrayList<Object>>> entries = letterMap.entrySet().iterator();
				while (entries.hasNext()) {
					Entry<String, ArrayList<Object>> next = entries.next();
					ArrayList<Object> listTemp = new ArrayList<Object>();
					for (Object t : list) {
						o = t;
						String pinYinFirstLetter = getFieldValue(field, o);
						if (StringUtils.equals(next.getKey(), pinYinFirstLetter)) {
							listTemp.add(t);
						}
					}
					next.getValue().addAll(listTemp);
				}
			}

			ArrayList<Object> listTemp2 = new ArrayList<Object>();
			if (!list.isEmpty()) {
				for (Object t : list) {
					o = t;
					String pinYinFirstLetter = getFieldValue(field, o);
					if (!pinYinFirstLetter.matches("[A-Z]")) {
						listTemp2.add(t);
					}
				}
				if (!listTemp2.isEmpty()) {
					letterMap.put("#", listTemp2);
				}
			}

			return letterMap;
		} else {
			return null;
		}

	}

	/**
	 * 获取传递字段的属性值
	 * 
	 * @param field 字段名 要大写 比如"Name"
	 * @param o
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static String getFieldValue(String field, Object o)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String name = field.substring(0, 1).toUpperCase() + field.substring(1);
		Method method = o.getClass().getMethod("get" + name);
		// 获取字段属性值
		String value = (String) method.invoke(o);
		// 取首字母大写返回
		String pinYinFirstLetter = PinYinUtil.getPinYin(value).substring(0, 1).toUpperCase();
		return pinYinFirstLetter;
	}

}
