package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String lentToName;
    private List<Status> statuses = new ArrayList<Status>();

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
        return lentToName;
    }

    public void setLentTo(String lentToName) {
        this.lentToName = lentToName;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
    
    
}
