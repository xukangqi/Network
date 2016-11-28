import javax.security.auth.login.LoginContext;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**15
 * Created by Administrator on 2016/3/27.
 */
public class Client {
    private Socket socket;
    private Socket thesocket;
    private PrintWriter pw;
    private BufferedReader br;
    private PrintWriter datapw;
    private BufferedReader databr;
    private String dir;
    public Client() throws IOException{
        String str="";
        String respose="";
        int length;
        if (connect()) {
            Login();
            while (true) {
                windows();
                Scanner scanner = new Scanner(System.in);
                int i = scanner.nextInt();
                String filename = "";
                switch (i) {
                    case 1:
                        //开启被动模式
                        pw.println("PASV");
                        String portaddress = br.readLine();
                        String[] adress = portaddress.split(" |/");
                        InetAddress inetAddress = InetAddress.getByName(adress[1]);
                        int port_high = Integer.parseInt(adress[2]);
                        int port_low = Integer.parseInt(adress[3]);
                        int port = port_high * 256 + port_low;
                        thesocket = new Socket(inetAddress, port);
                        databr = getReader(thesocket);
                        datapw = getWriter(thesocket);
                        System.out.print("Now you can transfer data\n");
                        break;
                    case 2:
                        //获取指定文件大小
                        System.out.println("please enter the file name:");
                        scanner = new Scanner(System.in);
                        filename = scanner.nextLine();
                        pw.println("SIZE " + filename);
                        pw.flush();
                        String fileSize = br.readLine() + "B";
                        System.out.println("Size of " + filename + " is " + fileSize);
                        break;
                    case 3:
                        //下载文件
                        System.out.println("please choose file or directory you want to download(1.file 2. directory)");
                        scanner = new Scanner(System.in);
                        String choice = scanner.nextLine();
                        int j = Integer.parseInt(choice);
                        switch (j) {
                            case 1:
                                System.out.println("please enter the file name you want to download:");
                                scanner = new Scanner(System.in);
                                filename = scanner.nextLine();
                                RandomAccessFile infile;
                                if (new File(dir + "\\" + filename).exists()){
                                    //如果本地存在该文件，则先发送给服务器文件名和偏移量，然后进行传输
                                    infile = new RandomAccessFile(dir + "\\" + filename, "rws");
                                    Long filePointer= infile.getFilePointer();
                                    infile.seek(filePointer);
                                    pw.println("REST " + filename + " " + filePointer);
                                    pw.println("RETR " + filename);
                                    pw.flush();
                                    System.out.println("断点续传");
                                }else {
                                    infile = new RandomAccessFile(dir + "\\" + filename, "rw");
                                    pw.println("RETR " + filename);
                                    pw.flush();
                                }
                                InputStream inline = thesocket.getInputStream();
                                byte downloadbytebuffer[] = new byte[1024];
                                while ((length = inline.read(downloadbytebuffer)) != -1) {
                                    infile.write(downloadbytebuffer, 0, length);
                                }
                                System.out.println(br.readLine());
                                inline.close();
                                infile.close();
                                break;
                            case 2:
                                //对文件夹中的每个文件都要进行传输
                                System.out.println("please enter the directory name you want to download:");
                                scanner = new Scanner(System.in);
                                String directory = scanner.nextLine();
                                pw.println("RETR " + directory);
                                pw.flush();
                                int sizeofdirectory = Integer.parseInt(br.readLine());
                                File file = new File(dir + "\\" + directory);
                                file.mkdir();
                                String name = "";
                                for (i = 0; i < sizeofdirectory; i++) {
                                    name = br.readLine();
                                    RandomAccessFile infile2 = new RandomAccessFile(dir + "\\" + directory + "\\" + name, "rw");
                                    InputStream inline2 = thesocket.getInputStream();
                                    byte downloadbytebuffer2[] = new byte[1024];
                                    while ((length = inline2.read(downloadbytebuffer2)) != -1) {
                                        infile2.write(downloadbytebuffer2, 0, length);
                                    }
                                    infile2.close();
                                    inline2.close();
                                    usePESV();
                                }
                                break;
                        }
                        break;
                    case 4:
                        System.out.println("please enter the file name you want to upload:(1.file 2. directory)");
                        scanner = new Scanner(System.in);
                        choice = scanner.nextLine();
                        j = Integer.parseInt(choice);
                        switch (j) {
                            case 1:
                                System.out.println("please enter the file name you want to upload:");
                                scanner = new Scanner(System.in);
                                filename = scanner.nextLine();
                                RandomAccessFile outfile = new RandomAccessFile(dir + "\\" + filename, "r");
                                OutputStream outline = thesocket.getOutputStream();
                                //获取服务器中该文件的大小
                                pw.println("SIZE "+filename);
                                String information=br.readLine();
                                System.out.println(information);
                                Long filesize=Long.parseLong(information);
                                pw.println("STOR " + filename);
                                pw.flush();
                                //如果文件大于0，则进行断点续传
                                if (filesize>0){
                                     outfile.seek(filesize);
                                    System.out.println("断点续传");
                                }
                                byte uploadbytebuffer[] = new byte[1024];
                                while ((length = outfile.read(uploadbytebuffer)) != -1) {
                                    outline.write(uploadbytebuffer, 0, length);
                                }
                                outline.close();
                                outfile.close();
                                System.out.println(br.readLine());

                                break;
                            case 2:
                                //传递文件夹中每个文件
                                System.out.println("please enter the directory name you want to upload:");
                                scanner = new Scanner(System.in);
                                String diectoryname = scanner.nextLine();
                                File directory = new File(dir + "\\" + diectoryname);
                                File[] filelist = directory.listFiles();
                                pw.println("STOR "+diectoryname+" "+filelist.length);
                                pw.flush();
                                for (i = 0; i < filelist.length; i++) {
                                    pw.println(filelist[i].getName());
                                    RandomAccessFile outfile2 = new RandomAccessFile(dir + "\\" + diectoryname + "\\" + filelist[i].getName(), "r");
                                    OutputStream outline2 = thesocket.getOutputStream();
                                    byte uploadbytebuffer2[] = new byte[1024];
                                    while ((length = outfile2.read(uploadbytebuffer2)) != -1) {
                                        outline2.write(uploadbytebuffer2, 0, length);
                                    }
                                    outfile2.close();
                                    outline2.close();
                                    usePESV();
                                }
                                break;
                        }

                        break;
                    case 5:
                        pw.println("QUIT");
                        System.exit(0);
                        break;
                    case 6:
                        //设置下载和上传目录
                        while (true) {
                            System.out.println("Enter dowload/upload content in your computer");
                            scanner = new Scanner(System.in);
                            dir = scanner.nextLine();
                            String addressstr = dir + "\\" + "test.txt";
                            File file = new File(addressstr);
                            file.createNewFile();
                            if ((file.exists()) && (file.isFile())) {
                                file.delete();
                                break;
                            }
                        }
                        respose = "550 CWD failed";
                        while (respose.equals("550 CWD failed")) {
                            System.out.println("Enter dowload/upload content in server");
                            scanner = new Scanner(System.in);
                            str = scanner.nextLine();
                            System.out.println(str);
                            pw.println("CWD " + str);
                            pw.flush();
                            respose = br.readLine();
                        }
                        break;
                    case 7:
                        pw.println("LIST");
                        pw.flush();
                        while ((respose = databr.readLine())!=null){
                            System.out.println(respose);
                        }
                        thesocket.close();
                        break;
                }
            }
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

    public  boolean  connect() {
        try {
            socket=new Socket("localhost",7777);
            pw = getWriter(socket);
            br = getReader(socket);
        }catch (IOException error){
            return false;
        }
        return true;
    }
    public void usePESV() throws IOException{
        String portaddress = br.readLine();
        String[] adress = portaddress.split(" |/");
        InetAddress inetAddress = InetAddress.getByName(adress[1]);
        int port_high = Integer.parseInt(adress[2]);
        int port_low = Integer.parseInt(adress[3]);
        int port = port_high * 256 + port_low;
        thesocket = new Socket(inetAddress, port);
    }

    public void  Login(){
        String username="";
        String password="";
        String response="o";

        BufferedReader lineread=new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!response.equals("success")){
                System.out.println("please enter username:");
                username=lineread.readLine();
                pw.println("USER "+username);
                pw.flush();
                response=br.readLine();
            }

            response="";
            while (!response.equals("success")){
                System.out.println("please enter password:");
                password=lineread.readLine();
                pw.println("PASS "+password);
                pw.flush();
                response=br.readLine();
            }
        }catch (IOException  error){
            System.out.println("error");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void windows(){
        System.out.println("Welcome to use FTP server");
        System.out.println("please choose the server you want to use");
        System.out.println("1.use PASV");
        System.out.println("2.size of file ");
        System.out.println("3.download file");
        System.out.println("4.upload file");
        System.out.println("5.quit the program");
        System.out.println("6.set the dowload/upload content ");
        System.out.println("7.list all the files in server");

    }
    public static void main(String args[])throws IOException{
       new Client();
    }

}
