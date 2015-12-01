package data;

import java.io.Serializable;

/**
 * @author nilstes
 */
public class LoginData implements Serializable {
    private String email;
    private String password;

    public LoginData() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
