package data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nilstes
 */
public class Instrument implements Serializable {
    private String id;
    private String type;
    private String make;
    private String productNo;
    private String serialNo;
    private String description;
    private String lentTo;
    private String status;
    private Date statusDate;

    public Instrument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getLentTo() {
        return lentTo;
    }

    public void setLentTo(String lentTo) {
        this.lentTo = lentTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }


}
