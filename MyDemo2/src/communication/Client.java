package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		
		Scanner input = new Scanner(System.in);
		ExecutorService es = Executors.newSingleThreadExecutor();
		
		Socket socket = new Socket ("localhost", 12361 );
		System.out.println("���������ӳɹ�");
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		
		//����������͵�¼��Ϣ
		System.out.println("����������");
		String name = input.nextLine();
		Message msg = new Message(name, null, MessageType.TYPE_LOGIN, null);
		oos.writeObject(msg);
		msg = (Message)ois.readObject();
		System.out.println(msg.getInfo() + msg.getFrom());
		
		//������ȡ��Ϣ���߳�
		es.execute(new ReadInfoThread(ois));
		
		//ʹ�����̷߳�����Ϣ
		boolean flag = true;
		while(flag) {
			msg = new Message();
			System.out.println("To:");
			msg.setTo(input.nextLine());
			msg.setFrom(name);
			msg.setType(MessageType.TYPE_SEND);
			System.out.println("Info:");
			msg.setInfo(input.nextLine());
			oos.writeObject(msg);
			
		}
	}

}

//��ȡ��Ϣ
class ReadInfoThread implements Runnable{

	private ObjectInputStream in;
	private boolean flag = true;
	
	public ReadInfoThread(ObjectInputStream in) {
		this.in = in;
	}
	
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	
	@Override
	public void run() {
		try {
			while(flag) {
			
				Message message = (Message)in.readObject();
				System.out.println("["+ message.getFrom()+ "]����˵:"+message.getInfo()); 
			} 
			if(in != null)
				in.close();
		}catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} 
	}
	
}
