import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/3.
 */
public class SendThread extends Thread {
    private Socket socket;
    private  String name;
    private ArrayList<Message> messages;
    public SendThread(Socket socket,String name){
        this.socket=socket;
        this.name=name;
        start();
    }
    public void run(){
        try {
            DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
            //该线程每过一秒从消息队列中获取发送给自己的消息，并且将获取的消息从消息队列中删除
            while (true){
                messages=MessageDAO.messageslist.getMymessage(name);
                StringBuffer buffer=new StringBuffer();
                for (int i=0;i<messages.size();i++){
                    Message m=messages.get(i);
                    buffer.append(m.getMessageType()+"@"+m.getFrom()+"@"+m.getContent()+"@"+m.getSendDate()+";");
                }
                if (buffer.length()>0){
                    String strMessage=buffer.substring(0,buffer.length()-1);
                    dos.writeUTF(strMessage);
                    dos.flush();
                }
                if (MessageDAO.messageslist.getSize()>0){
                    MessageDAO.messageslist.removeMymessage(messages);
                }
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println(e);
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }

    }

}
