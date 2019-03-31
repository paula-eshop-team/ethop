package com.eshop.utilities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.pojo.User;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Lists;

/**
 * 
 * @author Paula Lin
 *
 */
public class JsonUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> String obj2String(T obj) {
		if(obj == null) {
			return null;
		}
		try {
			return obj instanceof String? (String) obj : objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.warn("Parse object to String error", e);
			return null;
		}
	}
	 
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> String obj2StringPretty(T obj) {
		if(obj == null) {
			return null;
		}
		try {
			return obj instanceof String? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			logger.warn("Parse object to StringPretty error", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param clazz
	 * @return
	 */
	public static <T> T string2Obj(String str, Class<T> clazz) {
		if(StringUtil.isEmpty(str) || clazz == null) {
			return null;
		}
		try {
			return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
		} catch (IOException e) {
			logger.warn("Parse String to Object error", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param typeReference
	 * @return
	 */
	public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
		if(StringUtil.isEmpty(str) || typeReference == null) {
			return null;
		}
		
		try {
			return  typeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str, typeReference);
		} catch (IOException e) {
			logger.warn("Parse String to Object error", e);
			return null;
		}
	}
	
	/**
	 * @param str
	 * @param collectionClass
	 * @param elementClasses
	 * @return
	 */
	public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
		
		try {
			return  objectMapper.readValue(str, javaType);
		} catch (IOException e) {
			logger.warn("Parse String to Object error", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		User u1 = new User();
		u1.setId(1);
		u1.setEmail("paula@123.com");
		
		String user1Json = JsonUtil.obj2String(u1);
		
		String user1JsonPretty = JsonUtil.obj2StringPretty(u1);
		
		logger.info("user1Json:{}", user1Json);
		logger.info("user1JsonPretty:{}", user1JsonPretty);
		
		User user = JsonUtil.string2Obj(user1Json, User.class);
		
		User u2 = new User();
		u2.setId(2);
		u2.setEmail("paula2@123.com");
		List<User> userList = Lists.newArrayList();
		userList.add(u1);
		userList.add(u2);
		
		String userListStr = JsonUtil.obj2StringPretty(userList);//实际工作不用Pretty, Pretty涉及换行,空格等操作
		
		logger.info("=====================");
	    logger.info("userListJsonPretty: {}", userListStr);
	    
	    List<User> userListObj1 = JsonUtil.string2Obj(userListStr, List.class);//该方法并不能将List<T>对应的json转换为List<T>
	    List<User> userListObj2 = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>(){});
	    List<User> userListObj3 = JsonUtil.string2Obj(userListStr, List.class, User.class);
	    
	    System.out.println("end");
	}
}
