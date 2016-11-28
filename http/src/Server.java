import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/3/30.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    public Server() throws IOException {
        System.out.println("Welcome to use http server");
        try {
            serverSocket=new ServerSocket(7777);
        }catch (IOException e){
            System.out.println(e);
        }
        while (true){
            socket=serverSocket.accept();
            TaskThread newThread=new TaskThread(socket);
        }

    }
    public static void main(String args[])throws IOException{
        new Server();
    }
}
