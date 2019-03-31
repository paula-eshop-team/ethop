package com.eshop.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Paula Lin
 *
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//不要null.没有null
public class ServerResponse<T> implements Serializable {
	private int status;
	private String msg;
	private T data;
	
	/**
	 * @param status
	 */
	private ServerResponse(int status) {
		this.status = status;
	}
	
	/**
	 * @param status
	 * @param data
	 */
	private ServerResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}
	
	/**
	 * @param status
	 * @param msg
	 * @param data
	 */
	private ServerResponse(int status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	
	/**
	 * @param status
	 * @param msg
	 */
	private ServerResponse(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	/**
	 * @return
	 */
	@JsonIgnore
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	/**
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess() {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	
	/**
	 * @param msg
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess(T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	
	/**
	 * @param msg
	 * @param data
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess(String msg,T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}
	
	/**
	 * 
	 * @return
	 */
	public static <T> ServerResponse<T> createByError() {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
	}
	
	/**
	 * 
	 * @param errorMessage
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}
	
	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
		return new ServerResponse<T>(errorCode,errorMessage);
	}
}
