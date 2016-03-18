package db;

import data.Instrument;
import data.Musician;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nilstes
 */
public class MusicianDao {

    private static final Logger log = Logger.getLogger(MusicianDao.class.getName());

    private InstrumentDao instrumentDao = new InstrumentDao();
    
    public List<Musician> getMusicians() throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM musician order by first_name, last_name");
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            List<Musician> musicians = new ArrayList<Musician>();
            while(rs.next()) {
                musicians.add(new Musician(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getBoolean("has_own_instrument")));
            }               
            rs.close();
            statement.close();
            log.info("Found " + musicians.size() + " musicians");
            getInstruments(connection, musicians);
            return musicians;
        } finally {
            connection.close();
        }
    }
    
    public Musician getMusician(String id) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM musician where id=?");
            statement.setString(1, id);
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            Musician musician = null;
            if(rs.next()) {
                musician = new Musician(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getBoolean("has_own_instrument"));
            }               
            rs.close();
            statement.close();
            log.info("Found musician " + musician==null?"ok":"failed");
            getInstruments(connection, Arrays.asList(musician));
            return musician;
        } finally {
            connection.close();
        }
    }
    
    public boolean addMusician(Musician musician) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("INSERT INTO musician (id, first_name, last_name, has_own_instrument) VALUES(?,?,?,?)");
            s.setString(1, musician.getId());
            s.setString(2, musician.getFirstName());
            s.setString(3, musician.getLastName());
            s.setBoolean(4, musician.isHasOwnInstrument());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.info("Add musician " + (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }
    
    public boolean updateMusician(Musician musician) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("UPDATE musician set first_name=?, last_name=?, has_own_instrument=? where id=?");
            s.setString(1, musician.getFirstName());
            s.setString(2, musician.getLastName());
            s.setBoolean(3, musician.isHasOwnInstrument());            
            s.setString(4, musician.getId());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.log(Level.INFO, "Update musician {0}", (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public boolean deleteMusician(String id) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("DELETE FROM musician where id=?");
            s.setString(1, id);
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.log(Level.INFO, "Delete musician {0}", (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    private void getInstruments(Connection connection, List<Musician> musicians) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT i.*,it.name FROM instrument i inner join instrument_type it on it.id=i.type inner join musician_instrument mi on mi.instrument_id=i.id where musician_id=? and out_at is not null and in_at is null");
        for(Musician musician : musicians) {
            statement.setString(1, musician.getId());
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                musician.getInstruments().add(instrumentDao.getOneInstrument(rs));
            }
            rs.close();
        }
        statement.close();
        log.info("Get instrument for musician ok");
    }
}
