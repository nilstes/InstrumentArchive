package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nilstes
 */
public class Musician implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private List<String> instruments = new ArrayList<String>();

    public Musician() {
    }

    public Musician(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<String> instruments) {
        this.instruments = instruments;
    }
}
