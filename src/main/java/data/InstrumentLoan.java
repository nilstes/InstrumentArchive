package data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author nilstes
 */
public class InstrumentLoan implements Serializable {
    private String musicianName;
    private Date outAt;
    private String outByUser;
    private Date inAt;
    private String inByUser;

    public InstrumentLoan() {
    }

    public InstrumentLoan(String musicianName, Date outAt, String outByUser, Date inAt, String inByUser) {
        this.musicianName = musicianName;
        this.outAt = outAt;
        this.outByUser = outByUser;
        this.inAt = inAt;
        this.inByUser = inByUser;
    }

    public String getMusicianName() {
        return musicianName;
    }

    public void setMusicianName(String musicianName) {
        this.musicianName = musicianName;
    }

    public Date getOutAt() {
        return outAt;
    }

    public void setOutAt(Date outAt) {
        this.outAt = outAt;
    }

    public String getOutByUser() {
        return outByUser;
    }

    public void setOutByUser(String outByUser) {
        this.outByUser = outByUser;
    }

    public Date getInAt() {
        return inAt;
    }

    public void setInAt(Date inAt) {
        this.inAt = inAt;
    }

    public String getInByUser() {
        return inByUser;
    }

    public void setInByUser(String inByUser) {
        this.inByUser = inByUser;
    }
}
