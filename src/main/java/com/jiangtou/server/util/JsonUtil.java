package com.jiangtou.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 阿里FastJSON封装类 防止解析时异常
 * @author 陆迪
 */
public class JsonUtil {

	/**
     * 判断json格式是否正确
	 */
	public static boolean isJsonCorrect(String s) {

        return s != null && !"[]".equals(s)
                && !"{}".equals(s) && !"".equals(s) && !"[null]".equals(s) && !"{null}".equals(s) && !"null".equals(s);
    }

	/**
     * 获取有效的json
	 */
	public static String getCorrectJson(String json) {
		return isJsonCorrect(json) ? json : "";
	}

    /**
     * json转JSONObject
     */
	public static JSONObject parseObject(Object obj) {
		return parseObject(toJSONString(obj));
	}
	public static JSONObject parseObject(String json) {
		try {
			return JSON.parseObject(getCorrectJson(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * JSONObject转实体类
	 */
	public static <T> T parseObject(JSONObject object, Class<T> clazz) {
		return parseObject(toJSONString(object), clazz);
	}
	public static <T> T parseObject(String json, Class<T> clazz) {
		try {
			return JSON.parseObject(getCorrectJson(json), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJSONString(Object obj) {
		if (obj instanceof String) {
			return (String) obj;
		}
		try {
			return JSON.toJSONString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray parseArray(String json) {
		try {
			return JSON.parseArray(getCorrectJson(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> List<T> parseArray(String json, Class<T> clazz) {
		try {
			return JSON.parseArray(getCorrectJson(json), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
     * 格式化，显示更好看
	 */
	public static String format(Object object) {
		try {
			return JSON.toJSONString(object, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
     * 判断是否为JSONObject
	 * @param obj instanceof String ? parseObject
	 */
	public static boolean isJSONObject(Object obj) {
		if (obj instanceof JSONObject) {
			return true;
		}
		if (obj instanceof String) {
			try {
				JSONObject json = parseObject((String) obj);
				return json != null && !json.isEmpty();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	/**判断是否为JSONArray
	 * @param obj instanceof String ? parseArray
	 */
	public static boolean isJSONArray(Object obj) {
		if (obj instanceof JSONArray) {
			return true;
		}
		if (obj instanceof String) {
			try {
				JSONArray json = parseArray((String) obj);
				return json != null && !json.isEmpty();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

}
