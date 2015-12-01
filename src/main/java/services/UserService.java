package services;

import data.User;
import db.InstrumentDao;
import db.UserDao;
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
 * Service that handles reading and updating bank user information
 * @author nilstes
 */
@Path("users")
public class UserService extends SecureService  {
    
    private static final Logger log = Logger.getLogger(UserService.class.getName());

    private UserDao userDao = new UserDao();
    private InstrumentDao transactionDao = new InstrumentDao();

    @Context
    private HttpServletRequest request;

    @PUT
    @Path("/{email}")
    @Consumes("application/json")
    public void update(User user) {  
        log.info("UserService.update()");
        checkLogon();
        try {
            userDao.updateUser(user);
            log.info("Updated user!");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to update user", e);        
            throw new ServerErrorException("Failed to update user", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DELETE
    @Path("/{email}")
    @Consumes("application/json")
    public void delete(@PathParam("email") String email) {  
        log.log(Level.INFO, "UserService.delete(): {0}", email);
        checkLogon();
        try {
            userDao.deleteUser(email);
            log.info("Deleted user!");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to delete user", e);        
            throw new ServerErrorException("Failed to delete user", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
    
    @POST
    @Consumes("application/json")
    public void add(User user) {  
        log.info("UserService.add(): " + user);
        checkLogon();
        try {
            userDao.addUser(user);
            log.info("Added user!");        
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to add user", e);        
            throw new ServerErrorException("Failed to add user", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
    
    @GET
    @Path("/{email}")
    @Produces("application/json")
    public User get(@PathParam("email") String currentUserEmail) {
        checkLogon();
        try {
            return userDao.getUser(currentUserEmail);
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get user", e);        
            throw new ServerErrorException("Failed to get user", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
       
    @GET
    @Produces("application/json")
    public List<User> getUsers() {
        checkLogon();
        try {
            return userDao.getUsers();
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to get users", e);        
            throw new ServerErrorException("Failed to get users", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
