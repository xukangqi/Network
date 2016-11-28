import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import   java.awt.event.*;
/**
 * Created by Administrator on 2016/4/5.
 */
public class Emoji  {
    private JFrame jFrame;
    GridLayout   gridLayout1   =   new   GridLayout(9,15);
    JLabel[]   ico=new   JLabel[135];
    public Emoji() throws IOException{
        init();
    }
    public void init() throws IOException{
        //表情窗口
        jFrame=new JFrame("emoji");
        jFrame.setBounds(200,200,28*18,28*10);
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setLayout(gridLayout1);
        String fileName = "";
        for(int i=0;i <ico.length;i++){
            //获取表情图片，并显示在屏幕上
            fileName= "qq\\"+i+".gif";
            ico[i] =new   JLabel(new ImageIcon(fileName));
            ico[i].setBorder(BorderFactory.createLineBorder(new Color(225,225,225), 1));
            ico[i].addMouseListener(new   MouseAdapter(){
                public   void   mouseClicked(MouseEvent  e) {
                    if (e.getButton() == 1) {
                        JLabel cubl = (JLabel) (e.getSource());
                        Icon icon=cubl.getIcon();
                        Client.textPane_1.insertIcon(icon);
                        jFrame.setVisible(false);
                    }
                    }
                    @Override
                    public void mouseEntered (MouseEvent e){
                        ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                    @Override
                    public void mouseExited (MouseEvent e){
                        ((JLabel) e.getSource()).setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
                    }

            });
            p.add(ico[i]);
        }
        jFrame.add(p);
        jFrame.setVisible(true);
    }
}
