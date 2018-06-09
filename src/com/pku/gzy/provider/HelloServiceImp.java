package com.pku.gzy.provider;

import com.pku.gzy.allinterface.HelloService;
/**
 * HelloService接口的实现
 * @author gzl
 *
 */
public class HelloServiceImp implements HelloService{

	/**
	 * sayHi方法的实现...
	 * @param name
	 */
	@Override
	public String sayHi(String name) {
		return "hello "	+ name;
	}
	
}
