package db;

import data.Instrument;
import data.InstrumentFilter;
import data.InstrumentLoan;
import data.InstrumentType;
import data.InstrumentStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author nilstes
 */
public class InstrumentDao {

    private static final Logger log = Logger.getLogger(InstrumentDao.class.getName());

    public List<InstrumentType> getInstrumentTypes() throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM instrument_type order by name");   
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            List<InstrumentType> types = new ArrayList<InstrumentType>();
            while(rs.next()) {
                types.add(new InstrumentType(rs.getString("id"), rs.getString("name")));
            }               
            rs.close();
            statement.close();
            log.info("Found " + types.size() + " instrument types");
            return types;
        } finally {
            connection.close();
        }
    }

    public List<String> getInstrumentMakes() throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT distinct make FROM instrument order by make");        
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            List<String> types = new ArrayList<String>();
            while(rs.next()) {
                types.add(rs.getString("make"));
            }               
            rs.close();
            statement.close();
            log.info("Found " + types.size() + " instrument makes");
            return types;
        } finally {
            connection.close();
        }
    }

    public List<Instrument> getInstruments(InstrumentFilter filter) throws SQLException {
        log.info("getInstruments(" + filter + ")");
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM instrument i inner join instrument_type it on i.type=it.id");        
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            List<Instrument> instruments = new ArrayList<Instrument>();
            while(rs.next()) {
                Instrument i = new Instrument();
                i.setId(rs.getString("id"));
                i.setDescription(rs.getString("description"));
                i.setLentTo(null);
                i.setMake(rs.getString("make"));
                i.setProductNo(rs.getString("product_no"));
                i.setSerialNo(rs.getString("serial"));
                i.setType(rs.getString("name"));
                instruments.add(i);
            }               
            rs.close();
            statement.close();
            log.info("Found " + instruments.size() + " instruments");
            getCurrentLoans(connection, instruments);
            getLastStatuses(connection, instruments);
            return instruments;
        } finally {
            connection.close();
        }
    }
    
    public boolean addInstrument(Instrument instrument) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("INSERT INTO instrument (id,type,make,serial,product_no,description) VALUES(?,?,?,?,?,?)");
            s.setString(1, instrument.getId());
            s.setString(2, instrument.getType());
            s.setString(3, instrument.getMake());
            s.setString(4, instrument.getSerialNo());
            s.setString(5, instrument.getProductNo());
            s.setString(6, instrument.getDescription());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("Add instrument " + (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }
    
    public boolean addInstrumentState(InstrumentStatus status) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("INSERT INTO instrument_state (instrument_id,date,state,state_by_user) VALUES(?,?,?,?)");
            s.setString(1, status.getInstrumentId());
            s.setTimestamp(2, new Timestamp(status.getDate().getTime()));
            s.setString(3, status.getText());
            s.setString(4, status.getStatusByUser());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("Add instrument state " + (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }
    
    public boolean updateInstrument(Instrument instrument) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("UPDATE instrument SET type=?, make=?, serial=?, product_no=?, description=? WHERE id=?");
            s.setString(1, instrument.getType());
            s.setString(2, instrument.getMake());
            s.setString(3, instrument.getSerialNo());
            s.setString(4, instrument.getProductNo());
            s.setString(5, instrument.getDescription());
            s.setString(6, instrument.getId());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("Update instrument " + (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public void deleteInstrument(String id) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("DELETE FROM instrument where id=?");
            s.setString(1, id);
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();

            s = connection.prepareStatement("DELETE FROM instrument_state where instrument_id=?");
            s.setString(1, id);
            log.fine(s.toString());
            s.executeUpdate();
            s.close();

            s = connection.prepareStatement("DELETE FROM musician_instrument where instrument_id=?");
            s.setString(1, id);
            log.fine(s.toString());
            s.executeUpdate();
            s.close();

            log.info("Delete instrument " + (result == 1?"ok":"failed"));
        } finally {
            connection.close();
        }        
    }

    public Instrument getInstrument(String id) throws SQLException {
        log.info("getInstrument(" + id + ")");
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT i.*,it.name FROM instrument i inner join instrument_type it on i.type=it.id where i.id=?");
            statement.setString(1, id);
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            Instrument instrument = null;
            if(rs.next()) {
                instrument = getOneInstrument(rs);
            }               
            rs.close();
            statement.close();
            log.info("Get instrument " + (instrument != null?"ok":"failed"));
            if(instrument != null) {
                getCurrentLoans(connection, Arrays.asList(instrument));
                getLastStatuses(connection, Arrays.asList(instrument));
            }
            return instrument;
        } finally {
            connection.close();
        }
    }

    private void getLastStatuses(Connection connection, List<Instrument> instruments) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT max(date) as lastDate, state from instrument_state where instrument_id=? group by state");
        for(Instrument instrument : instruments) {
            statement.setString(1, instrument.getId());
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                log.info("Found state");
                instrument.setStatus(rs.getString("state"));
                instrument.setStatusDate(rs.getTimestamp("lastDate"));
            }
            rs.close();
        }
        statement.close();
        log.info("Get instrument last statuses ok");
    }

    private void getCurrentLoans(Connection connection, List<Instrument> instruments) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT first_name,last_name FROM musician_instrument mi inner join musician m on mi.musician_id=m.id where instrument_id=? and out_at is not null and in_at is null");
        for(Instrument instrument : instruments) {
            statement.setString(1, instrument.getId());
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                instrument.setLentTo(rs.getString("first_name") + " " + rs.getString("last_name"));
            }
            rs.close();
        }
        statement.close();
        log.info("Get current instrument loans ok");
    }

    public boolean hasCurrentLoan(String instrumentId) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT first_name FROM musician_instrument mi inner join musician m on mi.musician_id=m.id where instrument_id=? and out_at is not null and in_at is null");
            statement.setString(1, instrumentId);
            ResultSet rs = statement.executeQuery();
            boolean found = rs.next();
            statement.close();
            log.info("hasCurrentLoan " + found);
            return found;
        } finally {
            connection.close();
        }        
    }

    Instrument getOneInstrument(ResultSet rs) throws SQLException {
        Instrument i = new Instrument();
        i.setId(rs.getString("id"));
        i.setDescription(rs.getString("description"));
        i.setLentTo(null);
        i.setMake(rs.getString("make"));
        i.setProductNo(rs.getString("product_no"));
        i.setSerialNo(rs.getString("serial"));
        i.setType(rs.getString("name"));
        return i;
    }

    public boolean addLoan(String instrumentId, String musicianId, String loggedOnUser) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("INSERT INTO musician_instrument (instrument_id,musician_id,out_at,out_by_user) VALUES(?,?,?,?)");
            s.setString(1, instrumentId);
            s.setString(2, musicianId);
            s.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            s.setString(4, loggedOnUser);
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("Add instrument loan" + (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public boolean endLoan(String instrumentId, String loggedOnUser) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("UPDATE musician_instrument SET in_at=?, in_by_user=? where instrument_id=? and in_at is null");
            s.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            s.setString(2, loggedOnUser);
            s.setString(3, instrumentId);
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("End instrument loan #=" + result);
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public List<InstrumentLoan> getLoans(String instrumentId) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT first_name,last_name,out_at,out_by_user,in_at,in_by_user FROM musician_instrument mi inner join musician m on mi.musician_id=m.id where instrument_id=? order by out_at desc");
            statement.setString(1, instrumentId);
            ResultSet rs = statement.executeQuery();
            List<InstrumentLoan> loans = new ArrayList<InstrumentLoan>();
            while(rs.next()) {
                loans.add(new InstrumentLoan(rs.getString("first_name") + " " + rs.getString("last_name"), rs.getTimestamp("out_at"), rs.getString("out_by_user"), rs.getTimestamp("in_at"), rs.getString("in_by_user")));
            }
            rs.close();

            statement.close();
            log.info("Get instrument loans ok");
            return loans;
        } finally {
            connection.close();
        }    
    }

    public List<InstrumentStatus> getInstrumentStatuses(String instrumentId) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM instrument_state where instrument_id=? order by date desc");
            statement.setString(1, instrumentId);
            ResultSet rs = statement.executeQuery();
            List<InstrumentStatus> statuses = new ArrayList<InstrumentStatus>();
            while(rs.next()) {
                InstrumentStatus status = new InstrumentStatus();
                status.setDate(rs.getTimestamp("date"));
                status.setInstrumentId(instrumentId);
                status.setStatusByUser(rs.getString("state_by_user"));
                status.setText(rs.getString("state"));
                statuses.add(status);
            }
            rs.close();

            statement.close();
            log.info("Get instrument statuses ok");
            return statuses;
        } finally {
            connection.close();
        }    
    }
}
