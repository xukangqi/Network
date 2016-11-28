import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * Created by Administrator on 2016/3/27.
 */
public class TaskThread extends Thread {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private PrintWriter datapw;
    private BufferedReader databr;
    public String cmd;
    public  String parament;
    private  String parament2;
    private  String dir;//目录路径
    private  Socket thesocket;
    private  String incompleteFile;
    private HashMap<String,Long>map;
    public TaskThread(Socket socket){
        this.socket=socket;
        start();
    }
    public void  run(){
        try {
            pw = getWriter(socket);
            br = getReader(socket);
            map=new HashMap<String,Long>();
            int length;
            while (true){
                String s=br.readLine();
                System.out.println(s);
                String[] str=s.split(" ");
                if (str.length==1){
                    cmd=str[0];
                    parament="";
                }else if (str.length==2){
                    cmd=str[0];
                    parament=str[1];

                }else {
                    cmd=str[0];
                    parament=str[1];
                    parament2=str[2];
                }

                if (cmd.equals("USER")){
                    if (parament.equals("xukangqi")){
                        pw.println("success");pw.flush();
                    }
                    else {
                        pw.println("nobody") ;pw.flush();
                    }
                }else if (cmd.equals("PASS")){
                    if (parament.equals("12345")){
                        pw.println("success");pw.flush();}
                    else {pw.println("error") ;pw.flush();}
                }else if(cmd.equals("PASV")) {
                    //开启被动模式
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
                    InetAddress i=InetAddress.getLocalHost();
                    pw.println(i+" "+port_high+" "+port_low);
                    try{
                        thesocket=ss.accept();
                        datapw=getWriter(thesocket);
                        databr=getReader(thesocket);
                    }catch (IOException e){
                        System.out.println(e);
                    }finally {
                        ss.close();
                    }
                }else if (cmd.equals("SIZE")){
                    //返回当前目录下指定文件大小
                    String fileaddress=dir+"\\"+parament;
                   File file=new File(fileaddress);
                    if ((!file.exists())||!file.isFile()){
                       pw.println("0");
                        pw.flush();
                    }else{
                    pw.println(file.length());
                    System.out.println(file.length());
                    pw.flush();
                    }
                }else if (cmd.equals("REST")){
                    //将需要断点续传的文件和文件指针的位置加入HashMap
                    incompleteFile=parament;
                    Long filePointer=Long.parseLong(parament2);
                    map.put(incompleteFile,filePointer);
                }else if (cmd.equals("RETR")){
                    //文件下载功能
                    String uploadfilename=parament;
                    File file=new File(dir+"\\"+uploadfilename);
                    if (file.isFile()){
                    RandomAccessFile outfile = new RandomAccessFile(dir+"\\"+uploadfilename,"r");
                        //首先判断是否需要断点续传，需要就从文件指针处开始
                        if (map.containsKey(uploadfilename)){
                            outfile.seek(map.get(uploadfilename));
                            System.out.println("RSRT");
                        }
                    OutputStream outline=thesocket.getOutputStream();
                    byte uploadbytebuffer[]= new byte[1024];
                    while ((length = outfile.read(uploadbytebuffer)) != -1){
                        outline.write(uploadbytebuffer,0,length);
                    }
                    outline.close();
                    outfile.close();
                    System.out.println("success");
                    pw.println("226 Transfer OK");
                    pw.flush();
                    }
                    else if (file.isDirectory()){
                        //依次传输每一个文件
                        File[] filelist=file.listFiles();
                        pw.println(filelist.length);
                        pw.flush();
                        for (int i=0;i<filelist.length;i++){
                            pw.println(filelist[i].getName());
                            RandomAccessFile outfile = new RandomAccessFile(dir+"\\"+uploadfilename+"\\"+filelist[i].getName(),"r");
                            OutputStream outline=thesocket.getOutputStream();
                            byte uploadbytebuffer[]= new byte[1024];
                            while ((length = outfile.read(uploadbytebuffer)) != -1){
                                outline.write(uploadbytebuffer,0,length);
                            }
                            System.out.println("OK");
                            outfile.close();
                            outline.close();
                            openPESV();
                        }
                    } else { pw.println("error");
                    }


                }else if (cmd.equals("STOR")){
                    //上传文件功能
                    //如果包含扩展名，则为单一文件
                    if (parament.contains(".")){
                    String downloadfilename=parament;
                        RandomAccessFile infile=new RandomAccessFile(dir+"\\"+downloadfilename,"rw");
                        InputStream inline=thesocket.getInputStream();
                        //当文件存在时，就将文件指针移动文件的末尾
                        if (new File(dir+"\\"+downloadfilename).exists()){
                            infile.seek(new File(dir+"\\"+downloadfilename).length());
                        }
                    byte downloadbytebuffer[]= new byte[1024];
                    while ((length=inline.read(downloadbytebuffer))!=-1){
                        infile.write(downloadbytebuffer,0,length);
                    }
                        inline.close();
                        infile.close();
                        pw.println("226 Transfer OK");
                        pw.flush();
                    }else{
                        //新建文件夹，依次传输每个文件
                        File file = new File(dir + "\\" + parament);
                        file.mkdir();
                        int sizeofdirectory=Integer.parseInt(parament2);
                        String name = "";
                        for (int i = 0; i <sizeofdirectory; i++) {
                            name = br.readLine();
                            RandomAccessFile infile2 = new RandomAccessFile(dir + "\\" +parament + "\\" + name, "rw");
                            InputStream inline2 = thesocket.getInputStream();
                            byte downloadbytebuffer2[] = new byte[1024];
                            while ((length = inline2.read(downloadbytebuffer2)) != -1) {
                                infile2.write(downloadbytebuffer2, 0, length);
                            }
                            infile2.close();
                            inline2.close();
                            openPESV();
                        }
                    }

                }else if (cmd.equals("QUIT")){
                    pw.println("221 Goodbye");
                    pw.flush();
                    try{
                        Thread.currentThread();
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        System.out.println(e);
                    }

                }else {
                    if (cmd.equals("CWD")) {
                        dir = parament;
                        //判断目录
                        String addressstr = dir + "\\" + "test.txt";
                        File file = new File(addressstr);
                        file.createNewFile();
                        if ((!file.exists()) || (!file.isFile())) {
                            pw.println("550 CWD failed");
                            pw.flush();
                        } else {
                            file.delete();
                            pw.println("success");
                            pw.flush();
                        }
                    } else {
                        if (cmd.equals("LIST")) {
                            //显示目录下所有文件，文件夹
                            StringBuilder fileinformation=new StringBuilder();
                            fileinformation.append("the file in server:\n ");
                            File file = new File(dir);
                            File[] templist = file.listFiles();
                            for (int i = 0; i < templist.length; i++) {

                                Path path = Paths.get(templist[i].getAbsoluteFile().toURI());
                                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                                FileTime creationTime = attributes.creationTime();
                                fileinformation.append("Name: " + templist[i].getName() + " Size：" + templist[i].length() + "B CreationTime："
                                        + creationTime + " Path: " + path + "\n");
                            }
                            datapw.println(fileinformation.toString());
                            datapw.flush();
                            thesocket.close();
                        } else {
                            pw.println("500 Syntax error, command unrecognized.");
                            pw.flush();
                        }
                    }
                }
            }
        }catch (IOException e){
            System.out.println(e);
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
    public void openPESV() throws  IOException{
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
        InetAddress i=InetAddress.getLocalHost();
        pw.println(i+" "+port_high+" "+port_low);
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
