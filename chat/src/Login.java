/**
 * Created by Administrator on 2016/4/2.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Login extends JPanel {
    public static String name;

    private JFrame frame;
    private JTextField textField_1;
    private JPasswordField textField;
    private String userName ;
    private  String passwrod ;
    private  boolean judge;
    private Socket thesocket;
   private  DataOutputStream dos;
    private DataInputStream dis;

    public Login(Socket socket) throws IOException{
        thesocket=socket;
        dos=new DataOutputStream(thesocket.getOutputStream());
        dis=new DataInputStream(thesocket.getInputStream());
        judge=false;
        frame = new JFrame();
        frame.setBackground(Color.CYAN);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("qq.jpg"));
        frame.setBounds(100, 100, 429, 609);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("");

        textField = new JPasswordField();
        textField.setBackground(new Color(135, 206, 250));
        textField.setFont(new Font("Aparajita", Font.ITALIC, 26));
        textField.setBounds(169, 426, 155, 36);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setFont(new Font("Aparajita", Font.ITALIC, 26));
        textField_1.setBackground(new Color(135, 206, 250));
        textField_1.setBounds(169, 371, 155, 36);
        frame.getContentPane().add(textField_1);
        textField_1.setColumns(10);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(135, 206, 250));
        btnLogin.setFont(new Font("Aparajita", Font.ITALIC, 23));
        btnLogin.setIcon(null);
        btnLogin.setBounds(123, 495, 135, 28);
        frame.getContentPane().add(btnLogin);
        btnLogin.addActionListener(new loginListener());

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(new Color(60, 179, 113));
        lblUsername.setFont(new Font("Aparajita", Font.ITALIC, 26));
        lblUsername.setBounds(49, 367, 128, 39);
        frame.getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(new Color(60, 179, 113));
        lblPassword.setFont(new Font("Aparajita", Font.ITALIC, 26));
        lblPassword.setBounds(49, 419, 100, 50);
        frame.getContentPane().add(lblPassword);
        lblNewLabel.setIcon(new ImageIcon("background.jpg"));
        lblNewLabel.setBounds(102, 44, 222, 215);
        frame.getContentPane().add(lblNewLabel);

        JLabel label = new JLabel("");
        label.setIcon(new ImageIcon("back.jpg"));
        label.setBounds(0, 0, 413, 571);
        frame.getContentPane().add(label);
        frame.setVisible(true);
    }
    class loginListener implements  ActionListener{
        public void actionPerformed(ActionEvent e) {
            userName=textField_1.getText();
            passwrod=String.valueOf(textField.getPassword());

            try{
                //发送给服务器验证
                dos.writeUTF("101,"+userName+","+passwrod);
                int answer=Integer.parseInt(dis.readUTF());
                if (answer==1) {
                    JOptionPane.showMessageDialog(null, "登录成功", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    name=userName;
                    frame.setVisible(false);
                    judge=true;
                } else {
                    JOptionPane.showMessageDialog(null, "请重新输入", "错误",
                            JOptionPane.ERROR_MESSAGE);
                    textField.setText("");
                    textField_1.setText("");
                }
            }catch (IOException E){
                System.out.println(E);
            }
        }
    }
    public boolean getJudge(){
        return judge;
    }

}