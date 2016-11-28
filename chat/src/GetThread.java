import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Created by Administrator on 2016/4/3.
 */
public class GetThread extends Thread {
    private Socket socket;
    private Socket thesocket;
    private String name;
    private DataInputStream dis;
    private DataOutputStream dos;
    private DataInputStream datadis;
    private DataOutputStream datados;
    private InputStream in;
    public GetThread(String name,Socket socket){
        this.socket=socket;
        this.name=name;
        start();
    }
    public void run(){
        try {
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
            while (true){
                String message=dis.readUTF();
                String []m1=message.split(";");
                for (int i=0;i<m1.length;i++){
                    String[] m2=m1[i].split("@");
                    if (m2[0].equals("102")){
                        String[] m3=m2[2].split(",");
                        Client.comboBox.removeAllItems();
                        for (int j=0;j<m3.length;j++){
                           if (name.equals(m3[j])){
                               ;
                           }else{
                            Client.comboBox.addItem(m3[j]);
                           }
                        }
                    }
                    else if (m2[0].equals("103")){
                        if (m2[2].contains(".gif")){
                            //如果消息中包含表情，就先分割字符串，根据表情路径获取表情，并且显示在消息框中
                            String str = m2[3] + " " + m2[1] + " send you: " + "\n";
                            Document docs = Client.textPane.getDocument();
                            SimpleAttributeSet attrset = new SimpleAttributeSet();
                            StyleConstants.setFontSize(attrset, 15);
                            StyleConstants.setForeground(attrset,Color.RED);
                            try {
                                docs.insertString(docs.getLength(), str, attrset);
                            } catch (BadLocationException e3) {
                                e3.printStackTrace();
                            }
                            String[]m3=m2[2].split("#");
                            for (i=0;i<m3.length;i++){
                                if (m3[i].contains(".gif")){
                                    ImageIcon ico=new ImageIcon(m3[i]);
                                    Client.textPane.setCaretPosition(docs.getLength());
                                    Client.textPane.insertIcon(ico);

                                }else {
                                    str=m3[i];
                                    docs = Client.textPane.getDocument();
                                    attrset = new SimpleAttributeSet();
                                    StyleConstants.setFontSize(attrset, 15);
                                    StyleConstants.setForeground(attrset,Color.RED);
                                    try {
                                        docs.insertString(docs.getLength(), str, attrset);
                                    } catch (BadLocationException e3) {
                                        e3.printStackTrace();
                                    }
                                }

                            }
                            try {
                                docs.insertString(docs.getLength(),"\n", attrset);
                            } catch (BadLocationException e3) {
                                e3.printStackTrace();
                            }
                        }else {
                            //对没有表情的消息进行显示
                            String str = m2[3] + " " + m2[1] + " send you: " + "\n" + m2[2] + "\n";
                            Document docs = Client.textPane.getDocument();
                            SimpleAttributeSet attrset = new SimpleAttributeSet();
                            StyleConstants.setFontSize(attrset, 15);
                            StyleConstants.setForeground(attrset,Color.RED);
                            try {
                                docs.insertString(docs.getLength(), str, attrset);
                            } catch (BadLocationException e3) {
                                e3.printStackTrace();
                            }
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException E) {
                                System.out.println(E);
                            }
                        }
                    }else if (m2[0].equals("104")){
                        //先让用户选择要保存文件的目录，然后开启数据端口传递文件
                        Document docs=Client.textPane.getDocument();
                        SimpleAttributeSet attrset = new SimpleAttributeSet();
                        StyleConstants.setFontSize(attrset,15);
                        StyleConstants.setForeground(attrset,Color.RED);
                        try {
                            docs.insertString(docs.getLength(),m2[3]+" "+m2[1]+" send you a file.please choose the directory you want to store this file\n",attrset);//对文本进行追加
                        } catch (BadLocationException e3) {
                            e3.printStackTrace();
                        }
                        dos.writeUTF("105,"+m2[2]);
                        usePESV();
                        JFileChooser jfc=new JFileChooser();
                        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        jfc.showDialog(new JLabel(), "");
                        File file=jfc.getSelectedFile();
                        RandomAccessFile infile=new RandomAccessFile(file.getAbsolutePath()+"\\"+m2[2],"rw");
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = datadis.read(bytes)) != -1) {
                            infile.write(bytes, 0, length);
                        }
                        infile.close();
                        datadis.close();
                    }
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }

    }

    public void usePESV() throws IOException{
        thesocket = new Socket("localhost",7777);
        datados=new DataOutputStream(thesocket.getOutputStream());
        datadis=new DataInputStream(thesocket.getInputStream());
    }
}
