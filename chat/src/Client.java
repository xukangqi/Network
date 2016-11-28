import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.text.*;
/**
 * Created by Administrator on 2016/4/2.
 */
public class Client {
    //用于放置用户列表
    public static JComboBox comboBox=new JComboBox() ;
    //用于显示聊天内容
    public static  JTextPane textPane=new JTextPane();
    public ArrayList<ImageIcon> list;
    private JFrame frame;
    private Socket socket;
    private Socket thesocket;
    public static  JTextPane textPane_1=new JTextPane();
    private String name;
    public DataOutputStream dos;
    public DataInputStream dis;
    public DataOutputStream datados;
    public DataInputStream datadis;
    public Client() throws IOException,InterruptedException{
        socket=new Socket("localhost",8888);
        dos=new DataOutputStream(socket.getOutputStream());
        dis=new DataInputStream(socket.getInputStream());
        Login();

        initialize();
    }
   public void Login() throws IOException,InterruptedException{
       Login theLogin=new Login(socket);
       while(!theLogin.getJudge()){
           Thread.sleep(100);
       }
       name=Login.name;
       GetThread getThread=new GetThread(name,socket);
      try {
          dos.writeUTF("102");
          dos.flush();
      }catch (IOException e){
          System.out.println(e);
      }
   }

    public static void main(String args[])throws IOException,InterruptedException{
        new Client();
    }
    public void initialize() {
        frame = new JFrame(name);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("qq.jpg"));
        frame.setBounds(100, 100, 756, 638);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(0, 0, 740, 119);
        lblNewLabel.setIcon(new ImageIcon("back.jpg"));
        frame.getContentPane().add(lblNewLabel);

         textPane_1.setFont(new Font("Monospaced", Font.PLAIN, 15));
         textPane_1.setBounds(0, 455, 740, 110);
        frame.getContentPane().add(textPane_1);


        JButton btnSend = new JButton("");
        btnSend.addActionListener(new ActionListener() {
            //为发送消息的按钮添加监听事件
            public void actionPerformed(ActionEvent e) {
                try {
                    list=new ArrayList<ImageIcon>();
                    String msg="";
                    String from=Login.name;
                    String to=comboBox.getSelectedItem().toString();
                    Date date=new Date();
                  Document document=  textPane_1.getStyledDocument();
                   //获取输入框中所有表情
                    for(int i=0;i<document.getRootElements()[0].getElementCount();i++){

                        javax.swing.text.Element root = document.getRootElements()[0].getElement(i);

                        for(int j=0;j<root.getElementCount();j++){

                            ImageIcon icon = (ImageIcon) StyleConstants.getIcon(root.getElement(j).getAttributes());
                            if(icon!=null){

                                list.add(icon);
                            }
                        }
                    }
                    int k=0;
                    //往字符串msg中添加输入框中的内容，如果欲到表情，就用#路径#划分
                    for (int i=0;i<textPane_1.getText().length();i++){
                        if (textPane_1.getStyledDocument().getCharacterElement(i).getName().equals("icon")){
                            msg += "#"+list.get(k)+"#";
                            k++;
                        }else {

                            try {
                                msg += textPane_1.getStyledDocument().getText(i,1);
                            } catch (BadLocationException e5) {
                                e5.printStackTrace();
                            }
                        }
                    }
                    //在上方的消息显示栏中显示当前用户发送给其他用户的消息
                    Document docs = textPane.getDocument();
                    SimpleAttributeSet attrset = new SimpleAttributeSet();
                    StyleConstants.setFontSize(attrset, 15);
                    if (msg.contains(".gif")){
                        String str=date+" you send to "+to+" : ";
                        try {
                            docs.insertString(docs.getLength(), str, attrset);
                        } catch (BadLocationException e3) {
                            e3.printStackTrace();
                        }
                        String[] m=msg.split("#");
                        for (int i=0;i<m.length;i++){
                            if (m[i].contains(".gif")){
                                ImageIcon ico=new ImageIcon(m[i]);
                                textPane.setCaretPosition(docs.getLength());
                                textPane.insertIcon(ico);
                            }else {
                                str=m[i];
                                docs = textPane.getDocument();
                                attrset = new SimpleAttributeSet();
                                StyleConstants.setFontSize(attrset, 15);
                                try {
                                    docs.insertString(docs.getLength(), str, attrset);
                                } catch (BadLocationException e3) {
                                    e3.printStackTrace();
                                }
                            }

                        }
                        try {
                            docs.insertString(docs.getLength(), "\n", attrset);
                        } catch (BadLocationException e3) {
                            e3.printStackTrace();
                        }
                    }else {
                        String str=date+" you send to "+to+" : "+msg+"\n";
                        docs=textPane.getDocument();
                        attrset = new SimpleAttributeSet();
                        StyleConstants.setFontSize(attrset,15);
                        docs.insertString(docs.getLength(),str,attrset);
                    }
//                   将客户端发送的消息发送给服务器
                    dos.writeUTF("103,"+from+","+to+","+msg);
                    textPane_1.setText("");
                }catch (BadLocationException e4){
                    System.out.println(e4);
                }catch (IOException e1){
                    System.out.println(e1);
                }
            }
        });
        btnSend.setIcon(new ImageIcon("send.png"));
        btnSend.setBounds(672, 427, 29, 29);
        frame.getContentPane().add(btnSend);

        comboBox.setBounds(10, 427, 196, 29);
        frame.getContentPane().add(comboBox);

        JButton btnNewButton = new JButton("");
        btnNewButton.setIcon(new ImageIcon("file.png"));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //文件发送功能实现
                JFileChooser jfc=new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                try{
                    String from=Login.name;
                    String to=comboBox.getSelectedItem().toString();
                    dos.writeUTF("104,"+from+","+to+","+file.getName());
                    //开启数据端口用于发送文件
                    usePESV();
                    RandomAccessFile raf=new RandomAccessFile(file.getAbsolutePath(),"r");
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = raf.read(bytes)) != -1) {
                        datados.write(bytes, 0, length);
                    }
                    raf.close();
                    datados.close();
                    System.out.println("file is ok");
                }catch (IOException e2){
                    System.out.println(e2);
                }
            }
        });
        btnNewButton.setBounds(546, 427, 29, 29);
        frame.getContentPane().add(btnNewButton);

        JButton btnNewButton_1 = new JButton("");
        btnNewButton_1.setIcon(new ImageIcon("emoji.jpg"));
        btnNewButton_1.setBounds(611, 427, 29, 29);
        btnNewButton_1.addActionListener(new ActionListener() {
            //发送表情
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Emoji emoji=new Emoji();
                }catch (IOException E2){
                    System.out.println(E2);
                }
            }
        });
        frame.getContentPane().add(btnNewButton_1);

       textPane.setFont(new  Font("宋体", Font.PLAIN, 15));
        textPane.setBackground(SystemColor.menu);
        JScrollPane scrollPane=new JScrollPane();
        scrollPane.setBounds(0, 119, 740, 307);
        scrollPane.setViewportView(textPane);
        frame.getContentPane().add(scrollPane);

        frame.setVisible(true);
    }
    public void usePESV() throws IOException{
        thesocket = new Socket("localhost", 7777);
        datados=new DataOutputStream(thesocket.getOutputStream());
        datadis=new DataInputStream(thesocket.getInputStream());
    }
}
