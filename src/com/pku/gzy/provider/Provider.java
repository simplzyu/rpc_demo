package com.pku.gzy.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.pku.gzy.utils.Token;

/**
 * 服务提供者类，该类可以向客户端提供自己的服务
 * @author gzl
 *
 */
public class Provider {

	public static final ExecutorService executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public static int port;

	public Provider(int port) {
		this.port = port;
	}

	/**
	 * 开启服务提供者的服务
	 * 
	 */
	public void start() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(port));//绑定端口号

			while (true) {
				Socket socket = ss.accept();
				executor.execute(new ConnThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭服务提供者的服务
	 */
	public void stop() {
		this.executor.shutdown();
	}

	/**
	 * 服务提供者与客户端通信的线程类
	 * @author gzl
	 *
	 */
	class ConnThread implements Runnable {

		Socket socket = null;

		public ConnThread(Socket socket) {
			this.socket = socket;
		}
		/**
		 * 调用代理方法得到结果并将结果返回
		 */
		@Override
		public void run() {
			ObjectInputStream ois = null;
			ObjectOutputStream oos = null;

			try {
				ois = new ObjectInputStream(socket.getInputStream());

				Token token = (Token) ois.readObject();
//				String serviceName = token.getServiceInterfaceName();//获取服务名
				String serviceInterfaceImplName = token.getServiceInterfaceImplName();
				String methodName = token.getMethodName();//获取方法名
				Class<?>[] parameterTypes = token.getParameterTypes();//获取参数类型列表
				Object[] args = token.getArgs();//获取参数对象列表

				Class<?> serviceClass = Class.forName(serviceInterfaceImplName); 
				Method method = serviceClass.getMethod(methodName, parameterTypes);
				Object res = method.invoke(serviceClass.newInstance(), args);

				oos = new ObjectOutputStream(socket.getOutputStream());
				Token token2 = new Token(2);
				token2.setRes(res);
				oos.writeObject(token2);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
