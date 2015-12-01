package services;

import data.Instrument;
import data.InstrumentFilter;
import data.InstrumentType;
import data.Status;
import db.InstrumentDao;
import db.UserDao;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

/**
 * Service handling user transactions in the bank
 * @author nilstes
 */
@Path("instruments")
public class InstrumentService extends SecureService {
    
    private static final Logger log = Logger.getLogger(InstrumentService.class.getName());

    private static SecureRandom random = new SecureRandom();
    
    private UserDao userDao = new UserDao();
    private InstrumentDao instrumentDao = new InstrumentDao();

    @POST
    @Consumes("application/json")
    public void add(Instrument instrument) {       
        checkLogon();
        
        if(instrument.getId() == null) {
            instrument.setId(new BigInteger(256, random).toString(36));
        }
        
        // Add instrument
        try {
            instrumentDao.addInstrument(instrument);
            log.info("Added instrument");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to add instrument", e);        
            throw new ServerErrorException("Failed to add transaction", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @POST
    @Path("/{id}/states")
    @Consumes("application/json")
    public void addState(Status status) {       
        checkLogon();
        
        // Add instrument state
//        try {
//            instrumentDao.addInstrumentState(status);
//            log.info("Added instrument state");        
//        } catch(SQLException e) {
//            log.log(Level.SEVERE, "Failed to add instrument state", e);        
//            throw new ServerErrorException("Failed to add instrument state", Response.Status.INTERNAL_SERVER_ERROR, e);
//        }
    }

    @POST
    @Path("/{id}/loan")
    @Consumes("text/plain")
    public void addLoan(@PathParam("id") String instrumentId, String musicianId) {       
        String loggedOnUser = checkLogon();
        
        // Add loan
        try {
            if(instrumentDao.hasCurrentLoan(instrumentId)) {
                throw new ForbiddenException("Instrument is already on loan");
            }           
            instrumentDao.addLoan(instrumentId, musicianId, loggedOnUser);
            log.info("Added instrument loan");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to add instrument loan", e);        
            throw new ServerErrorException("Failed to add instrument loan", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DELETE
    @Path("/{id}/loan")
    @Consumes("text/plain")
    public void endLoan(@PathParam("id") String instrumentId) {       
        String loggedOnUser = checkLogon();
        
        // Add loan
        try {
            if(!instrumentDao.hasCurrentLoan(instrumentId)) {
                throw new ForbiddenException("Instrument is not on loan");
            }          
            instrumentDao.endLoan(instrumentId, loggedOnUser);
            log.info("Ended instrument loan");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to end instrument loan", e);        
            throw new ServerErrorException("Failed to end instrument loan", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @PUT
    @Consumes("application/json")
    public void update(Instrument instrument) {       
        checkLogon();
        
        // Add instrument
        try {
            instrumentDao.updateInstrument(instrument);
            log.info("Updated instrument");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to update instrument", e);        
            throw new ServerErrorException("Failed to update instrument", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Instrument getInstrument(@PathParam("id") String id) {       
        checkLogon();

        try {
            return instrumentDao.getInstrument(id);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get instrument", e);        
            throw new ServerErrorException("Failed to get instrument", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    public void deleteInstrument(@PathParam("id") String id) {       
        checkLogon();

        try {
            if(instrumentDao.hasCurrentLoan(id)) {
                throw new ForbiddenException("Instrument is on loan");
            }

            instrumentDao.deleteInstrument(id);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to delete instrument", e);        
            throw new ServerErrorException("Failed to delete instrument", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Produces("application/json")
    public List<Instrument> getInstruments(@QueryParam("filter") InstrumentFilter filter) {
        checkLogon();
        
        try {
            return instrumentDao.getInstruments(filter);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get instruments", e);        
            throw new ServerErrorException("Failed to instruments", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Path("/types")
    @Produces("application/json")
    public List<InstrumentType> getInstrumentTypes() {
        checkLogon();
        
        try {
            return instrumentDao.getInstrumentTypes();
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get instrument types", e);        
            throw new ServerErrorException("Failed to instrument types", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GET
    @Path("/makes")
    @Produces("application/json")
    public List<String> getInstrumentMakes() {
        checkLogon();
        
        try {
            return instrumentDao.getInstrumentMakes();
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get instrument makes", e);        
            throw new ServerErrorException("Failed to instrument makes", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
