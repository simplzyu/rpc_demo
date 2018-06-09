package com.pku.gzy.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import com.pku.gzy.utils.Token;
/**
 * rpc 客户端类，在该类中可以得到代理对象
 * @author gzl
 * 
 */
public class RPCClient<T> {
	
	private static Socket socket = null;
	
	private static String register_url = "localhost"; //注册中心的ip地址
	
	private static int port = 8088; //服务提供者的端口号
	
	public static String client_provider_url = "localhost"; //注册中心的ip地址
	/**
	 * 通过反射机制得到远程的对象
	 * @param serviceInterface
	 * @param serviceInterfaceImplName
	 * @param addr
	 * @return 远程代理对象
	 */
	public static <T> T getRemoteProxyImp(final Class<?> serviceInterface, String serviceInterfaceImplName, final InetSocketAddress addr){
		
		InvocationHandler h = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// TODO Auto-generated method stub
				Socket socket = null;
				ObjectInputStream ois = null;
				ObjectOutputStream oos = null;
				
				try {
					socket = new Socket();
					socket.connect(addr);
					oos = new ObjectOutputStream(socket.getOutputStream());
					
					Token tokenRequest = new Token(0,serviceInterface.getName(),serviceInterfaceImplName,method.getName(),method.getParameterTypes(),args);
					
					oos.writeObject(tokenRequest);
					
					ois = new ObjectInputStream(socket.getInputStream());
					Token token = (Token) ois.readObject();
					Object res = null;
					if(token.getType() == 2)
						res = token.getRes();
					
					return res;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					Thread.sleep(10000);//10秒后停止并关闭socket
					System.out.println("客户端关闭socket连接");
					socket.close();
				}
				return null;
			}
		};
		return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, h);
	}
	
	/**
	 * 检查服务是否注册
	 * @param c
	 * @return 远程接口实现对象的名
	 */
	public static String isRegister(Class c){
		ObjectOutputStream os = null;
		ObjectInputStream is = null;
		try {
			socket = new Socket(register_url,port);
			os = new ObjectOutputStream(socket.getOutputStream());
			os.writeObject(c.getName());
			is = new ObjectInputStream(socket.getInputStream());
			String str = (String) is.readObject();
			if(!str.equals("null")){
				return str;
			}
			String[] res = str.split("/");
			System.out.println(res[1]);
			client_provider_url = res[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}


