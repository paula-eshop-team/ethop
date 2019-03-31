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
		//序列化行为:Always
		//对象的所有字段全部列入
		objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
		
		//取消默认转化timestamps形式
		//在Json序列化时, 默认是会把date转换为timestamps, 这样是两种形式, 故不希望做此转化
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		//忽略空Bean转json的错误
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		
		//所有的日期格式都统一为以下样式, 即"yyyy-MM-dd HH:mm:ss"
		objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
		
		//忽略 在jsson字符串中存在, 但是在java对象中不存在对应属性的情况, 以防止错误
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
	/*
	 * 获取格式化好的json
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
	/*
	 * 反序列化01-处理简单对象
	 */
	//如下, 第一个<T>标志此方法为泛型方法, 
	//        第二个<T>表示返回值类型
	//        第三个<T> 来限制Class的类型
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
	/*
	 * 反序列化02-可以处理复杂对象,包括 Map, List, Set
	 * TypeReference代表具体的类型
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
	
	/*
	 * 反序列化03-可以处理复杂对象,包括 Map, List, Set
	 * Class<?>... elementClasses 传多个class时可以传数组
	 * T代表的是List, ?代表具体的类, 如User
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
