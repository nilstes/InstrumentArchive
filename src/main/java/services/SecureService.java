package services;

import data.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author nilstes
 */
public class SecureService {
    
    @Context
    private HttpServletRequest request;

    String checkLogon() throws NotAuthorizedException {
        // Check that we have a logged on user
        Session session = (Session)request.getSession().getAttribute("session");
        if(session == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.FORBIDDEN);
        }
        return session.getEmail();
    }
}
