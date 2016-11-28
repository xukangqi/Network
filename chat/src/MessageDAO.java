import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MessageDAO {
    public static MessageDAO messageslist=new MessageDAO();
    public ArrayList<Message> messages;
    public  MessageDAO(){
        messages=new ArrayList<Message>();
    }
    public void add(Message message){
        messages.add(message);
    }
    public ArrayList<Message> getMymessage(String name){
        Message m;
        ArrayList<Message> mymessage=new ArrayList<Message>();
        for (int i=0;i<messages.size();i++){
            m=messages.get(i);
            if (m.getTo().equals(name)){
                mymessage.add(m);
            }
        }

        return mymessage;
    }
    public void  removeMymessage(ArrayList<Message> mymessage){
          Message m;
        if (mymessage.size()==0){
            ;
        }else{
        String name=mymessage.get(0).getTo();
         for (int i=0;i<mymessage.size();i++){
            m=mymessage.get(i);
             messages.remove(m);
         }
        }
    }
    public int getSize(){
        return messages.size();
    }
}
