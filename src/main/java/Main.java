
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author nilstes
 */
public class Main {

    public static void main(String[] args) throws Exception {
        
        Logger logger = initLogging();

        logger.info("Starting server...");
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
        logger.info("Server started...");
        server.join();
    }

    private static Logger initLogging() throws IOException, SecurityException {
        File logDirectory = new File("log");
        if(!logDirectory.exists()) {
            logDirectory.mkdir();
        }
        
        // Get root logger
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINER);
        
        // Remove console handlers
        Handler[] handlers = logger.getHandlers();
        if (handlers[0] instanceof Handler) {
            logger.removeHandler(handlers[0]);
        }

        // Add new file handlers
        FileHandler debugHandler = new FileHandler("log/debug.log", 1048576, 5, true);
        debugHandler.setFormatter(new SimpleFormatter());   
        debugHandler.setLevel(Level.FINER);
        logger.addHandler(debugHandler);
        
        FileHandler auditHandler = new FileHandler("log/audit.log", 1048576, 5, true);   
        auditHandler.setFormatter(new SimpleFormatter());
        auditHandler.setLevel(Level.INFO);
        logger.addHandler(auditHandler);
        
        FileHandler errorHandler = new FileHandler("log/error.log", 1048576, 5, true);   
        errorHandler.setFormatter(new SimpleFormatter());
        errorHandler.setLevel(Level.SEVERE);
        logger.addHandler(errorHandler);
        
        return logger;
    }
}
