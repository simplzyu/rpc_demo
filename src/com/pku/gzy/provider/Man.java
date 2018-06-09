package com.pku.gzy.provider;

import com.pku.gzy.allinterface.HelloService;
import com.pku.gzy.allinterface.Laugh;

/**
 * Man类实现了Laugh于HelloService接口
 * @author gzl
 *
 */
public class Man implements Laugh, HelloService {

	/**
	 * sayHi方法的实现...
	 * @param name
	 */
	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return "hello:" + name;
	}

	/**
	 * laugh方法的实现...
	 * @param str
	 */
	@Override
	public String laugh(String str) {
		// TODO Auto-generated method stub
		return "tom is lauging " + str;
	}

}
