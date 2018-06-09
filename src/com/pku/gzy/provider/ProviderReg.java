package com.pku.gzy.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.pku.gzy.allinterface.HelloService;
import com.pku.gzy.allinterface.Laugh;
import com.pku.gzy.utils.RegisterProviderInfo;
/**
 * 服务提供者将服务向注册中心注册
 * @author gzl
 *
 */
public class ProviderReg {
	private static String url = "localhost";
	private static int port = 8089;

	public void start() {
		// 注册
		RegisterThread rt = new RegisterThread(url, port);
		Thread t = new Thread(rt);
		t.start();

		Provider provider = new Provider(8080);
		// Register.register(HelloService.class, Man.class);
		// Register.register(Laugh.class, Man.class);
		provider.start();
	}
}
/**
 * 
 * 服务提供者将服务向注册中心注册的线程类
 * @author gzl
 *
 */
class RegisterThread implements Runnable {
	Socket socket = null;
	String url = null;
	int port = 0;

	public RegisterThread(String url, int port) {
		this.url = url;
		this.port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		ObjectOutputStream oos = null;

		try {
			socket = new Socket("127.0.0.1", port);
			oos = new ObjectOutputStream(socket.getOutputStream());

			RegisterProviderInfo ri1 = new RegisterProviderInfo(0, HelloService.class.getName(), Man.class.getName());
			oos.writeObject(ri1);

			RegisterProviderInfo ri2 = new RegisterProviderInfo(1, Laugh.class.getName(), Man.class.getName());
			oos.writeObject(ri2);
			oos.writeObject(ri2);
			KeepLivingThread klt = new KeepLivingThread(socket);
			Thread t = new Thread(klt);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/**
 * 心跳机制的线程类
 * @author simplzy
 *
 */

class KeepLivingThread implements Runnable {

	Socket socket = null;

	public KeepLivingThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			while (true) {
				RegisterProviderInfo rpi = (RegisterProviderInfo) ois.readObject();
				int type = rpi.getType();
				if (type == 2) {
					String keepliving = rpi.getKeepliving();
					if (keepliving.equals("isliving")) {
						RegisterProviderInfo back = new RegisterProviderInfo(2,"yes");
						System.out.println("告诉服务器我还活着！");
						back.setKeepliving("yes");
						oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(back);
					}
				}
			}
		} catch (SocketException s) {
			System.out.println("socket已经关闭");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}