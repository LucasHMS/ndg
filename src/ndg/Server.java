package ndg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public Server(int n){
		try {
			ServerSocket server = new ServerSocket(1818);
			Object monitor = 1;
			int i=0;
			while(i++<n){
				Socket client = server.accept();
				client.setTcpNoDelay(true);
				handleClient(client,monitor);
				System.out.println(i);
			}

			Thread.sleep(500);
		
			synchronized(monitor) {
				monitor.notifyAll();
			}

			server.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleClient(Socket client, Object mutex){
		new Thread(() ->{
			try {
				ObjectOutputStream oss = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream oin = new ObjectInputStream(client.getInputStream());

				oin.readObject();
				synchronized(mutex){
					mutex.wait();
				}
				System.out.println(client.getInetAddress().getHostAddress()+" recording");

				oss.writeObject("rec");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();	
	}

	public static void main(String [] args){
		Server s = new Server(Integer.parseInt(args[0]));
	}
}
