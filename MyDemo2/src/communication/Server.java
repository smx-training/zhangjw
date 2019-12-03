package communication;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	
	public static void main(String[] args) throws IOException {
		
		//����ͻ��˴�����߳�
		Vector<UserThread> vector = new Vector<>();
		ExecutorService es = Executors.newFixedThreadPool(5);
		
		//�����������˵�Socket
		ServerSocket server = new ServerSocket(12361); 
		System.out.println("�����������������ڵȴ����ӡ�����������");
		while(true) {
			Socket socket = server.accept();
			UserThread user = new UserThread(socket, vector);
			es.execute(user);
			
		}
		
		
	}

}

//�ͻ��˴�����߳�
class UserThread implements Runnable{
	
	private String name;
	private Socket socket;
	private Vector<UserThread> vector;
	private boolean flag = true;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public UserThread(Socket socket, Vector<UserThread> vector) {
		this.socket = socket;
		this.vector = vector;
		vector.add(this);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			System.out.println("�ͻ���" + socket.getInetAddress().getHostAddress()+"������");
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			while(flag) {
				//��ȡ��Ϣ����
				Message msg = (Message)ois.readObject();
				int type = msg.getType();
				switch(type) {
					case MessageType.TYPE_SEND:
						String to = msg.getTo();
						UserThread ut;
						for(int i = 0; i < vector.size(); ++i) {
							ut = vector.get(i);
							if(to.equals(ut.name) && ut != this) {
								ut.oos.writeObject(msg);
								break;
							}
						}
						break;
						
						
					case MessageType.TYPE_LOGIN:
						name = msg.getFrom();
						msg.setInfo("��ӭ��:");
						oos.writeObject(msg);
						break;
					}
			}
			oos.close();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
