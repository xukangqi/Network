import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Administrator on 2016/4/2.
 */
public class TaskThread extends Thread{
    private Socket socket;
    private  Socket thesocket;
    public HashMap<String,String> userlist;
    private ArrayList<String> alluser;
    private String cmd;
    public  String parament;
    private  String parament2;
    private  String name;
    private DataInputStream dis;
    private  DataOutputStream dos;
    private DataInputStream datadis;
    private  DataOutputStream datados;
    public TaskThread(Socket socket){
        this.socket=socket;

        start();
    }
    public void run(){
        try{
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
            userlist=new HashMap<String, String>();
            alluser=new ArrayList<String>();
            adduser();
           while (true){
               String s=dis.readUTF();
               String[] array=s.split(",");
               if (array.length==1){
                   cmd=array[0];
               }else if (array.length==3){
                   cmd=array[0];
                   parament=array[1];
                   parament2=array[2];
               }else{
                   cmd=array[0];
               }

               if (cmd.equals("101")){
                   if (userlist.containsKey(parament)){
                       String password=userlist.get(parament);
                       if (parament2.equals(password)){
                           dos.writeUTF("1");
                           name=parament;
                           //登录成功，，服务器创建一个线程给这个客户端发消息
                           SendThread sendThread=new SendThread(socket,parament);
                       }else{
                           dos.writeUTF("0");
                       }
                   }else {
                       dos.writeUTF("0");
                   }
               }else if (cmd.equals("102")){
                       StringBuffer sb=new StringBuffer();
                       for (int i=0;i<alluser.size();i++){
                           sb.append(alluser.get(i)+",");
                       }
                   //新建消息，添加到消息队列中
                   String User=sb.substring(0,sb.length()-1);
                   Message m=new Message();
                   m.setFrom("server");
                   m.setTo(name);
                   m.setContent(User);
                   m.setMessageType("102");
                   m.setSendDate(new Date());
                   MessageDAO.messageslist.add(m);
               }else if (cmd.equals("103")){
                   Message m=new Message();
                   m.setFrom(array[1]);
                   m.setTo(array[2]);
                   m.setContent(array[3]);
                   m.setMessageType("103");
                   m.setSendDate(new Date());
                   MessageDAO.messageslist.add(m);
               }else if (cmd.equals("104")){
                   Message m=new Message();
                   m.setFrom(array[1]);
                   m.setTo(array[2]);
                   m.setContent(array[3]);
                   m.setMessageType("104");
                   m.setSendDate(new Date());
                   MessageDAO.messageslist.add(m);
                   //开启数据端口，接受客户端发过来的文件
                   openPESV();
                   RandomAccessFile infile=new RandomAccessFile("D:/chat"+"\\"+array[3],"rw");
                   byte[] bytes = new byte[1024];
                   int length;
                   while ((length = datadis.read(bytes)) != -1) {
                       infile.write(bytes, 0, length);
                   }
                   infile.close();
                   datadis.close();
                   System.out.println("file is ok");
               }else if (cmd.equals("105")){
                   //开启数据端口，发送文件给消息的接受方
                   openPESV();
                   RandomAccessFile outfile=new RandomAccessFile("D:/chat"+"\\"+array[1],"r");
                   byte[] bytes = new byte[1024];
                   int length;
                   while ((length = outfile.read(bytes)) != -1) {
                       datados.write(bytes, 0, length);
                   }
                   System.out.println("ok");
                   outfile.close();
                   datadis.close();
               }
           }
        }catch (IOException e){
            System.out.println(e);
        }


    }
    public void adduser(){
        userlist.put("Bob","1234");
        userlist.put("xukangqi","123");
        userlist.put("alice","12");
        userlist.put("lucy","1");
        alluser.add("Bob");
        alluser.add("xukangqi");
        alluser.add("alice");
        alluser.add("lucy");
    }
    public void openPESV() throws  IOException{
        //开启数据端口
        ServerSocket ss=null;
        while (true){
            try{
                ss=new ServerSocket(7777);
                break;
            }catch (IOException e){
                continue;
            }
        }
        try{
            thesocket=ss.accept();
            datados=new DataOutputStream(thesocket.getOutputStream());
            datadis=new DataInputStream(thesocket.getInputStream());
        }catch (IOException e){
            System.out.println(e);
        }finally {
            ss.close();
        }
    }
}
