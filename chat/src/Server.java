import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/4/2.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;

    public Server() throws IOException {
        System.out.println("Welcome to use chat server");
        serverSocket=new ServerSocket(8888);
        while (true){
            socket=serverSocket.accept();
            TaskThread newThread=new TaskThread(socket);
        }

    }
    public static void main(String args[])throws IOException{
        new Server();
    }
}