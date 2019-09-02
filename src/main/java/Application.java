import com.sun.net.httpserver.HttpServer;

public class Application {

    public static void main(String[] args)
    {
        HttpServer server = Server.getServerObject();
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }
}
