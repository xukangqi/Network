
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/3/27.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;

    public Server() throws IOException{
        System.out.println("Welcome to use ftp server");
        serverSocket=new ServerSocket(7777);
        while (true){
            socket=serverSocket.accept();
            TaskThread newThread=new TaskThread(socket);
        }

    }
    public static void main(String args[])throws IOException{
        new Server();
    }
}
