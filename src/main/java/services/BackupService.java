package services;

import db.Db;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author nilstes
 */
@Path("backup")
public class BackupService extends SecureService {

    private static final Logger log = Logger.getLogger(BackupService.class.getName());

    @POST
    @Path("dump")
    @Produces("text/plain")
    public String dump(@FormParam("file") String filename) {
        checkLogon();
        
        if(filename == null || filename.length() == 0) {
            throw new ClientErrorException("Missing file parameter", Status.BAD_REQUEST);
        }
        try {           
            Db.instance().dump(filename);
            return "ok";
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to dump DB", e);        
            throw new ServerErrorException("Failed to dump DB", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @POST
    @Path("restore")
    @Produces("text/plain")
    public String restore(@FormParam("file") String filename) {
        checkLogon();

        if(filename == null || filename.length() == 0) {
            throw new ClientErrorException("Missing file parameter", Status.BAD_REQUEST);
        }
        try {
            Db.restore("c:\\temp\\dump.sql");
            return "ok";
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to restore DB", e);        
            throw new ServerErrorException("Failed to restore DB", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
