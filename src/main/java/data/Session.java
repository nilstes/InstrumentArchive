package data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nilstes
 */
public class Session implements Serializable {
    private String email;
    private Date loggedOn;

    public Session() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(Date loggedOn) {
        this.loggedOn = loggedOn;
    }
}
