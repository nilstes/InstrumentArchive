package data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nilstes
 */
public class Status implements Serializable {
    private String instrumentId;
    private Date date;
    private String text;
    private String statusByUser;

    public Status() {
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatusByUser() {
        return statusByUser;
    }

    public void setStatusByUser(String statusByUser) {
        this.statusByUser = statusByUser;
    }
}
