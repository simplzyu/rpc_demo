package com.pku.gzy.test;

import com.pku.gzy.register.Register;

/**
 * 服务中心测试类，打开服务中心，接受来自服务提供者注册服务请求，来自客户端的服务请求
 * @author gzl
 *
 */
public class RegisterTest {
	public static void main(String[] args){
		Register r = new Register();
		r.start();
	} 
}