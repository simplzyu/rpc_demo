package com.pku.gzy.register;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.pku.gzy.utils.RegisterProviderInfo;

import redis.clients.jedis.Jedis;

/**
 * Register类，控制服务中心与服务提供者和客户端的通信
 * @author gzl
 */
public class Register {
	private static String redisURL = "localhost";
	private static Jedis redis = new Jedis(redisURL);
	private int port = 8088;

	private int portRegister = 8089;

	public static final ExecutorService executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static final ExecutorService executorRegister = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	

	/**
	 * 判断某个服务是否注册了
	 * @param serverInterface
	 * @return
	 */
	public static String isRegister(Class serverInterface) {
		String implName = redis.get(serverInterface.getName());
		if (!implName.equals("nil"))
			return implName;
		else
			return null;
	}

	/**
	 * 开启服务中心与服务提供者和客户端的通信
	 * @param
	 * @return
	 */
	public void start() {
		ServerSocket ss = null;
		
		RegisterThread rt = new RegisterThread();
		Thread t = new Thread(rt);
		t.start();
		try {
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(port));// 绑定端口号

			while (true) {
				Socket socket = ss.accept();
				executor.execute(new ConnThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 实现服务中心与服务提供者的socket通信
	 * @param
	 */
	public void register() {
		ServerSocket ssRegister = null;
		try {
			ssRegister = new ServerSocket();
			ssRegister.bind(new InetSocketAddress(portRegister));// 绑定端口号

			while (true) {
				Socket socket = ssRegister.accept();
				executorRegister.execute(new RegisterHandleThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 实现了Runnable接口来实现多线程，封装服务中心和客户端的通信过程
	 * @author ljl
	 *
	 */
	class ConnThread implements Runnable {

		Socket socket = null;

		public ConnThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			ObjectInputStream ois = null;
			ObjectOutputStream oos = null;

			try {
				ois = new ObjectInputStream(socket.getInputStream());
				String serviceInterfaceName = (String) ois.readObject();
				System.out.println(serviceInterfaceName);
				String res = redis.get(serviceInterfaceName);
				System.out.println(res);
				if (res == null || res.equals("nil")) {
					res = null;
				}

				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					oos.close();
					ois.close();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 实现服务中心与服务提供者的socket通信
	 * @author ljl
	 *
	 */
	class RegisterThread implements Runnable{

		@Override
		public void run() {
			register();
		}
		
	}
   
	/**
	 * 实现了Runnable接口来实现多线程，封装服务中心和服务提供者的通信过程
	 * @author ljl
	 *
	 */
	class RegisterHandleThread implements Runnable {
		Socket socket = null;

		public RegisterHandleThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			ObjectInputStream ois = null;
			try {
					ois = new ObjectInputStream(socket.getInputStream());
					while (true) {
					RegisterProviderInfo ri = (RegisterProviderInfo) ois.readObject();
					String serviceInterfaceName = ri.getServiceInterfaceName();
					String serviceInterfaceImplName = ri.getServiceInterfaceImplName();
					String ip = socket.getInetAddress().getHostAddress();
//					System.out.println(ip);
					redis.set(serviceInterfaceName, serviceInterfaceImplName+"/"+ip);
					if(ri.getType() == 1) break;
					System.out.println("开启第一个定时器");
//					timer1(socket);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	   /**
	    * 定时器，向客户端发送信息确认客户端是否还活着
	    * @param socket
	    */
		public static void timer1(Socket socket) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					ObjectOutputStream oos = null;
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						RegisterProviderInfo rpi = new RegisterProviderInfo(2,"isliving");
						System.out.println("想知道客户端是否活着！");
						oos.writeObject(rpi);
						timer2(socket, timer);
						System.out.println("开启第二个定时器");
					} catch (SocketException s) {
						s.printStackTrace();
						System.out.println("socket已经关闭");
						this.cancel();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},0, 2000);// 设定指定的时间time,此处为2000毫秒
		}

		/**
		 * 定时器，定时接受socket包来判断客户端是否活着
		 * @param socket
		 * @param timer1
		 */
		public static void timer2(Socket socket, Timer timer1) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					ObjectInputStream ois = null;
					try {
						ois = new ObjectInputStream(socket.getInputStream());
						RegisterProviderInfo rpi= (RegisterProviderInfo)ois.readObject();
						if(rpi.getType() == 2){
							String isliving = rpi.getKeepliving();
							if(!isliving.equals("yes")){
								System.out.println("enter");
								if(socket != null){
									System.out.println("客户端连接中断！");
									socket.close();
									System.out.println("关闭定时器1");
									timer1.cancel();
									System.out.println("关闭定时器2");
									this.cancel();
								}
							} else {
								System.out.println("客户端还活着");
							}
						}
					} catch (EOFException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			},0, 1000);// 设定指定的时间time,此处为2000毫秒
		}
}