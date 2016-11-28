import javax.sound.sampled.spi.AudioFileReader;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/3/30.
 */
public class Client  {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private Socket thesocket;
    private PrintWriter datapw;
    private BufferedReader databr;
    private String dir;
    private  Scanner scanner;
    public Client() throws IOException{
        scanner=new Scanner(System.in);
        System.out.println("please enter the host address");
        Scanner scanner=new Scanner(System.in);
        String host=scanner.nextLine();
        System.out.println("please enter the port");
        String ports=scanner.nextLine();
        int port=Integer.parseInt(ports);
        socket=new Socket(host,port);
        System.out.println("successful connection");
        pw=getWriter(socket);
        br=getReader(socket);
        boolean filePosition=true;
        while (filePosition){
            System.out.println("please enter content which you want to store the resouce");
            scanner=new Scanner(System.in);
            dir=scanner.nextLine();
            File file=new File(dir+"//"+"test.txt");
            file.createNewFile();
            if (!file.exists()){
                System.out.println("this content is not exist");
            }else {
                file.delete();
                filePosition=false;
            }
        }
        System.out.println("please enter the filename you want to get");
        String filename=scanner.nextLine();
        pw.println("GET /"+filename+" HTTP/1.1");
        pw.println("Host:"+"localhost");
        pw.println("connection:keep-alive");
        pw.println();
        pw.flush();
        InputStream in = socket.getInputStream();
        String firstLineOfResponse = br.readLine();
        String secondLineOfResponse = br.readLine();
        String threeLineOfResponse = br.readLine();
        String fourLineOfResponse = br.readLine();
        if (firstLineOfResponse.equals("HTTP/1.1 200 OK")){
            if (filename.contains("html")){
                //对html文件单独处理，获取其中的每一个资源
                 int size=Integer.parseInt(br.readLine());
                String fileinHtml="";
                RandomAccessFile getfile;
                DataInputStream instream=null;
                int length;
                byte[] b = new byte[1024];
                 for (int i=0;i<size;i++){
                     usePESV();
                     fileinHtml=br.readLine();
                     System.out.println(fileinHtml);
                     getfile=new RandomAccessFile(dir+"\\"+fileinHtml,"rw");
                     instream=new DataInputStream(thesocket.getInputStream());
                     while((length=instream.read(b))!=-1)
                     {
                         getfile.write(b,0,length);
                     }
                     instream.close();
                     getfile.close();
                     System.out.println("OK");
                 }
            }else {
            byte[] b = new byte[1024];
            RandomAccessFile infile=new RandomAccessFile(dir+"//"+filename,"rw");
              InputStream instream = socket.getInputStream();
           int length;
            while((length=instream.read(b))!=-1)
            {
                infile.write(b,0,length);
            }
            br.close();
            infile.close();
            }
            System.out.println("success");
        }else{
            System.out.print(firstLineOfResponse+"\n"+secondLineOfResponse+"\n"+threeLineOfResponse+"\n"+fourLineOfResponse);
        }
    }
    private PrintWriter getWriter(Socket socket)throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }
    private BufferedReader getReader(Socket socket)throws IOException{
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }
    public static void main(String args[])throws IOException{
        new Client();
    }
    public void usePESV() throws IOException{
        String portaddress = br.readLine();
        String[] adress = portaddress.split(" ");
        int port_high = Integer.parseInt(adress[0]);
        int port_low = Integer.parseInt(adress[1]);
        int port = port_high * 256 + port_low;
        thesocket = new Socket("localhost", port);
        datapw=getWriter(thesocket);
        databr=getReader(thesocket);
    }
}
