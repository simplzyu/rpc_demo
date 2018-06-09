package com.pku.gzy.utils;

import java.io.Serializable;
/**
 * provider和register之间自定义的数据传输格式
 * @author gzl
 *
 */
public class RegisterProviderInfo implements Serializable{
	
	/**
	 * type = 0; 发送的是注册信息
	 * type = 1; 发送的最后一个包
	 * type = 2; 保活
	 */
	int type;
	private String serviceInterfaceName;
	private String serviceInterfaceImplName;
	private String keepliving;
	
	public RegisterProviderInfo(int type, String keepliving){
		this.type = type;
		this.keepliving = keepliving;
	}
	public RegisterProviderInfo(int type,String serviceInterfaceName, String serviceInterfaceImplName) {
		super();
		this.type = type;
		this.serviceInterfaceName = serviceInterfaceName;
		this.serviceInterfaceImplName = serviceInterfaceImplName;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getServiceInterfaceName() {
		return serviceInterfaceName;
	}
	public void setServiceInterfaceName(String serviceInterfaceName) {
		this.serviceInterfaceName = serviceInterfaceName;
	}
	public String getServiceInterfaceImplName() {
		return serviceInterfaceImplName;
	}
	public void setServiceInterfaceImplName(String serviceInterfaceImplName) {
		this.serviceInterfaceImplName = serviceInterfaceImplName;
	}
	public String getKeepliving() {
		return keepliving;
	}
	public void setKeepliving(String keepliving) {
		this.keepliving = keepliving;
	}
}
