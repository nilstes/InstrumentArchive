
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author nilstes
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        
        FileHandler fh;   
        fh = new FileHandler("debug.log");   
        logger.addHandler(fh); 

        System.out.println("Starting server...");
        int port = Integer.parseInt(System.getProperty("port", "8080"));
        String contextPath = System.getProperty("contextPath", "instruments");
        Server server = new Server(port);
        ProtectionDomain domain = Main.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/" + contextPath);
        webapp.setWar(location.toExternalForm());
        server.setHandler(webapp);
        server.start();
        System.out.println("Server started...");
        server.join();
    }
}
