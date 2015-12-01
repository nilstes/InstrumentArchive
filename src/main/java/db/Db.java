package db;

import java.sql.Statement;
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
    private static final String DB_NAME = "instruments";
    private static Db instance = new Db();
    
    private Db() {
        try {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:~/" + DB_NAME, DB_NAME, "");
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS user ( email VARCHAR(256) PRIMARY KEY, password VARCHAR(256) )");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS instrument ( id VARCHAR(256) PRIMARY KEY, type VARCHAR(256), make VARCHAR(256), serial VARCHAR(256), product_no VARCHAR(256), description VARCHAR(256) )");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS musician ( id VARCHAR(256) PRIMARY KEY, first_name VARCHAR(256), last_name VARCHAR(256) )");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS instrument_type ( id VARCHAR(256) PRIMARY KEY, name VARCHAR(256) )");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS instrument_state ( instrument_id VARCHAR(256) PRIMARY KEY, date DATETIME, state LONGVARCHAR, state_by_user VARCHAR(256))");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS musician_instrument ( instrument_id VARCHAR(256), musician_id VARCHAR(256), out_at DATETIME, out_by_user VARCHAR(256), in_at DATETIME, in_by_user VARCHAR(256) )");
                
                statement.executeUpdate("MERGE INTO instrument_type values ('1', 'Kornett')");
                statement.executeUpdate("MERGE INTO instrument_type values ('2', 'Trompet')");
                statement.executeUpdate("MERGE INTO instrument_type values ('3', 'Klarinett')");
                statement.executeUpdate("MERGE INTO instrument_type values ('4', 'Klarinett (Bass)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('5', 'Valthorn')");
                statement.executeUpdate("MERGE INTO instrument_type values ('6', 'Tuba')");
                statement.executeUpdate("MERGE INTO instrument_type values ('7', 'Baryton')");
                statement.executeUpdate("MERGE INTO instrument_type values ('8', 'Horn')");
                statement.executeUpdate("MERGE INTO instrument_type values ('9', 'Horn (Alt)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('10', 'Horn (Tenor)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('11', 'Horn (Flygel)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('12', 'Obo')");
                statement.executeUpdate("MERGE INTO instrument_type values ('13', 'Fagott')");
                statement.executeUpdate("MERGE INTO instrument_type values ('14', 'Fløyte')");
                statement.executeUpdate("MERGE INTO instrument_type values ('15', 'Fløyte (Piccolo)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('16', 'Saksofon')");
                statement.executeUpdate("MERGE INTO instrument_type values ('17', 'Saksofon (Alt)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('18', 'Saksofon (Tenor)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('19', 'Trombone')");
                statement.executeUpdate("MERGE INTO instrument_type values ('20', 'Trombone (Alt)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('21', 'Trombone (Tenor)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('22', 'Perkusjon (Rytmisk)')");
                statement.executeUpdate("MERGE INTO instrument_type values ('23', 'Perkusjon (Melodisk)')");

                statement.close();
            } finally {
                connection.close();
            }
            log.info("DB initialized!");
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
        return DriverManager.getConnection("jdbc:h2:~/" + DB_NAME, DB_NAME, "");
    }
}