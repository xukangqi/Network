import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Administrator on 2016/3/30.
 */
public class TaskThread extends Thread {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private Socket thesocket;
    private PrintWriter datapw;
    private BufferedReader databr;
    private Boolean isHtml;
    private ArrayList<String> innerElement;

    public TaskThread(Socket socket) throws IOException {
        this.socket = socket;
        start();
    }

    public void run() {
        try {
            innerElement = new ArrayList<String>();
            isHtml = false;
            br = getReader(socket);
            pw = getWriter(socket);
            String firstlineofRequst = "";
            firstlineofRequst = br.readLine();
            String uri = firstlineofRequst.split(" ")[1];
            File file = new File("D:/http" + uri);
            if (file.exists() && file.isFile()) {
                //对不同的文件进行响应
                pw.println("HTTP/1.1 200 OK");
                if (uri.contains("html") || uri.contains("htm")) {
                    pw.println("Content-Type:text/html");
                    isHtml = true;
                } else if (uri.contains("jpg")) {
                    pw.println("Content-Type:image/jpeg");
                } else if (uri.contains("wmv")) {
                    pw.println("Content-Type:video/x-ms-wmv");
                } else {
                    pw.println("Content-Type:application/octet-stream");
                }
                InputStream in = new FileInputStream("D:/http" + uri);
                pw.println("Content-length:" + in.available());
                pw.println();
                pw.flush();
                //对html网页单独分析
                if (isHtml) {
                    String str = uri;
                    innerElement.add(str.substring(1, str.length()));
                    RandomAccessFile infile = new RandomAccessFile("D:/http" + "//" + uri, "r");
                    String html = "";
                    String htmlcode="";
                    while ((html = infile.readLine()) != null) {
                          htmlcode=htmlcode+html;
                    }
                    //获取html网页中的所有内嵌资源
                    innerElement=getResource(htmlcode);
                    innerElement.add(str.substring(1, str.length()));
                    byte[] bytes;
                    DataOutputStream outline = null;
                    int length;
                    pw.println(innerElement.size());
                    //一次传递内嵌的每个资源
                    for (int j = 0; j < innerElement.size(); j++) {
                        openPESV();
                        pw.println(innerElement.get(j));
                        infile = new RandomAccessFile("D:/http" + "\\" + innerElement.get(j), "r");
                        System.out.println(innerElement.get(j));
                        bytes = new byte[1024];
                        outline = new DataOutputStream(thesocket.getOutputStream());
                        while ((length = infile.read(bytes)) != -1) {
                            outline.write(bytes, 0, length);
                        }
                        infile.close();
                        outline.close();
                    }

                } else {
                    RandomAccessFile infile = new RandomAccessFile("D:/http" + uri, "r");
                    byte[] bytes = new byte[1024];
                    OutputStream outline = socket.getOutputStream();
                    int length;
                    while ((length = infile.read(bytes)) != -1) {
                        outline.write(bytes, 0, length);
                    }
                    outline.close();

                }
            } else {
                pw.println("HTTP/1.1 404 Not Found");
                pw.println("Content-Type:text/plain");
                pw.println("Content-Length:7");
                pw.println();
                pw.print("NOTfound");
                pw.flush();
            }

        } catch (IOException e) {
            System.out.println(e);
        }


    }

    public void OpenSocket() throws IOException {
        ServerSocket ss = null;
        ss = new ServerSocket(8838);
        socket = ss.accept();
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }

        public static ArrayList<String> getResource(String htmlCode) {
            //利用正则表达式对html网页源代码进行解析
        ArrayList<String> imageSrcList = new ArrayList<String>();
        Pattern p = Pattern.compile("src=\"(.+?)\"");
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        while (m.find()) {
            quote = m.group(1);
            imageSrcList.add(quote);
        }
        p = Pattern.compile("href=\"(.+?)\"");
        Matcher m1 = p.matcher(htmlCode);
        while (m1.find()) {
             quote = m1.group(1);
             imageSrcList.add(quote);
        }
          return imageSrcList;
    }
    public void openPESV() throws  IOException{
        //开启数据端口用于传输多个文件
        ServerSocket ss=null;
        int port_high=0;
        int port_low=0;
        while (true){
            Random r=new Random();
            port_high = 1 + r.nextInt(20);
            port_low=100+r.nextInt(1000);
            try{
                ss=new ServerSocket(port_high*256+port_low);
                break;
            }catch (IOException e){
                continue;
            }
        }
        pw.println(port_high+" "+port_low);
        try{
            thesocket=ss.accept();
            datapw=getWriter(thesocket);
            databr=getReader(thesocket);
        }catch (IOException e){
            System.out.println(e);
        }finally {
            ss.close();
        }
    }
}
