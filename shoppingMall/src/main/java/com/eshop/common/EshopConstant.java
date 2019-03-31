package com.eshop.common;

public enum EshopConstant {
	/**
	 *Eshop User
	 * */
	REGISTER_SUCCESS("注册成功"),
	REGISTER_FAILED("注册失败"),
	USER_EXIST("用户名已存在"),
	USER_NOT_EXIST("用户不存在"),
	REGISTER_EMAIL_EXIST("email已存在"),
	REGISTER_PARMS_ERROR("参数错误"),
	REGISTER_PASSWORD_INFO_QUESTION_NULL("返回密码的问题是空的"),
	CHECK_ANSWER_ERROR("问题的答案错误"),
	PARMS_ERROR_NEED_TOKEN("参数错误,token需要传递"),
	TOKEN_INVALIDATE("token无效或者过期"),
	TOKEN_ERROR_NEED_REGET("token错误,请重新获取重置密码的token"),
	CHANGE_PASSWORD_SUCCESS("修改密码成功"),
	CHANGE_PASSWORD_FALED("修改密码失败"),
	PRE_PASSWORD_ERROR("旧密码错误"),
	UPDATE_PASSWORD_SUCCESS("密码更新成功"),
	UPDATE_PASSWORD_FAILED("密码更新失败"),
	PASSWORD_ERROR("密码错误"),
	LOGIN_SUCCESS("登录成功"),
	UPDATE_USER_INFO_SUCCESS("更新个人信息成功"),
	UPDATE_USER_INFO_FAILED("更新个人信息失败"),
	CANNOT_FIND_CURRENT_USER("找不到当前用户"),
	
	/**
	 * Eshop Category
	 * */
	ADD_CATEGORY_SUCCESS("添加品类成功"),
	ADD_CATEGORY_FAILED("添加品类失败"),
	ADD_CATEGORY_PARMS_ERROR("添加品类参数错误"),
	UPDATE_CATEGORY_SUCCESS("更新品类名字成功"),
	UPDATE_CATEGORY_FAILED("更新品类名字失败");
	
	private Integer value;
	
	private String string;
	
	private EshopConstant(Integer value) {
		this.value = value;
	}
	
	private EshopConstant(String string) {
		this.string = string;
	}
	
	/**
     * 覆盖
     */
    public Integer toInteger() {
    	return value;
    }
    
    public String toString() {
    	return String.valueOf(string);
    }
	
}
