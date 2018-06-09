package com.pku.gzy.utils;

import java.io.Serializable;
/**
 * provider和client之间自定义的数据传输格式
 * @author gzl
 *
 */
public class Token implements Serializable{
	
	/**
	 * type=0 代表传输的参数是serviceInterfaceName，serviceInterfaceImplName,methodName，parameterTypes，args；
	 * type=2 代表传输的参数是res；
	 */
	int type; 
	String serviceInterfaceName;
	String serviceInterfaceImplName;
	
	String methodName;
	Class<?>[] parameterTypes;
	Object[] args;
	String keepliving;
	Object res;
	
	public Token(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Token(int type, String serviceInterfaceName, String serviceInterfaceImplName, String methodName,
			Class<?>[] parameterTypes, Object[] args) {
		super();
		this.type = type;
		this.serviceInterfaceName = serviceInterfaceName;
		this.serviceInterfaceImplName = serviceInterfaceImplName;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}

	public String getServiceInterfaceName() {
		return serviceInterfaceName;
	}
	public void setServiceInterfaceName(String serviceInterfaceName) {
		this.serviceInterfaceName = serviceInterfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String getKeepliving() {
		return keepliving;
	}
	public void setKeepliving(String keepliving) {
		this.keepliving = keepliving;
	}
	public Object getRes() {
		return res;
	}
	public void setRes(Object res) {
		this.res = res;
	}		
	public String getServiceInterfaceImplName() {
		return serviceInterfaceImplName;
	}
	public void setServiceInterfaceImplName(String serviceInterfaceImplName) {
		this.serviceInterfaceImplName = serviceInterfaceImplName;
	}
}