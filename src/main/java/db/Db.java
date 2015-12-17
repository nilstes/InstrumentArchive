package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton for DB creation and connection creation
 * @author nilstes
 */
public class Db {

    private static final Logger log = Logger.getLogger(Db.class.getName());
    private static Db instance = new Db();
    private static String dbUrl;
    
    private Db() {
        try {
            dbUrl = System.getProperty("db.url", "jdbc:h2:~/instruments;USER=instruments");
            String driver = "org.h2.Driver";
            if(dbUrl.contains("mysql")) {
                driver = "com.mysql.jdbc.Driver";
            } 
            Class.forName(driver);

            log.info("DB initialized using driver " + driver);
        } catch (Exception exception) {
            log.log(Level.SEVERE, "Failed to start DB", exception);
        }
    }
    
    public static Db instance() {
        return instance;
    }
    
    Connection getConnection() throws SQLException {
        // todo FIXME This is not a good way to get connections
        // Use a pool instead
        return DriverManager.getConnection(dbUrl);
    }
}