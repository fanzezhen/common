package com.github.fanzezhen.common.gateway.core.support.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thomason
 * @version 1.0
 * @since 2017/5/23 17:19
 */
public class ErrorInfo implements Serializable {
	/**
	 * 消息是否做过国际化
	 */
	private boolean internationalized = false;
	/**
	 * 消息code
	 */
	private String code;

	private String message;

	private List<Object> arguments;

	public ErrorInfo() {
	}

	public ErrorInfo(String code) {
		this.code = code;
	}

	public ErrorInfo(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ErrorInfo(Integer code, String message) {
		this.code = null != code ? code.toString() : null;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Object> getArguments() {
		return arguments;
	}

	public void setArguments(List<Object> arguments) {
		this.arguments = arguments;
	}

	public boolean isInternationalized() {
		return internationalized;
	}

	public void setInternationalized(boolean internationalized) {
		this.internationalized = internationalized;
	}
}
