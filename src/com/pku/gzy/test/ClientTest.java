package com.pku.gzy.test;

import java.net.InetSocketAddress;
import com.pku.gzy.allinterface.HelloService;
import com.pku.gzy.client.RPCClient;

/**
 * 客户端测试类，向服务中心请求HelloService服务，然后像在本地调用方法一样调用方法sayHi
 * @author gzl
 */
public class ClientTest{

	public static void main(String[] args) {
		String implName = RPCClient.isRegister(HelloService.class);
		if(implName != null){
			HelloService hs = RPCClient.getRemoteProxyImp(HelloService.class, implName.split("/")[0], new InetSocketAddress(RPCClient.client_provider_url, 8080));
			String res = hs.sayHi("Tom");
			System.out.println(res);			
		}else{
			System.out.println("服务不存在");
		}
	}
	
	
}