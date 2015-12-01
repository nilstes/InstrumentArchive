package services;

import data.Musician;
import db.MusicianDao;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


/**
 * Service handling user transactions in the bank
 * @author nilstes
 */
@Path("musicians")
public class MusicianService extends SecureService {
    
    private static final Logger log = Logger.getLogger(MusicianService.class.getName());

    private static SecureRandom random = new SecureRandom();

    private MusicianDao musicianDao = new MusicianDao();

    @Context
    private HttpServletRequest request;

    @POST
    @Consumes("application/json")
    public void add(Musician musician) {       
        checkLogon();
        
        if(musician.getId() == null) {
            musician.setId(new BigInteger(256, random).toString(36));
        }

        try {
            musicianDao.addMusician(musician);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to add musician", e);        
            throw new ServerErrorException("Failed add musician", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PUT
    @Consumes("application/json")
    public void update(Musician musician) {       
        checkLogon();

        try {
            musicianDao.updateMusician(musician);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to add musician", e);        
            throw new ServerErrorException("Failed add musician", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {       
        checkLogon();
        
        try {
            musicianDao.deleteMusician(id);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to delete musician", e);        
            throw new ServerErrorException("Failed delete musician", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Path("/{id}")
    public Musician getMusician(@PathParam("id") String id) {
        checkLogon();
        
        try {
            return musicianDao.getMusician(id);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get musician", e);        
            throw new ServerErrorException("Failed get musician", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Produces("application/json")
    public List<Musician> getMusicians() {       
        checkLogon();
        
        try {
            return musicianDao.getMusicians();
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get musicians", e);        
            throw new ServerErrorException("Failed get musicians", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
