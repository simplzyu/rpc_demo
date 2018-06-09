package com.pku.gzy.test;

import com.pku.gzy.provider.ProviderReg;

/**
 * 服务提供者测试类，服务提供者向服务中心注册请求，并接受来自客户端的请求
 * @author gzl
 *
 */
public class ProviderTest {
	public static void main(String[] args){
		ProviderReg pr = new ProviderReg();
		pr.start();
		
	}
}
