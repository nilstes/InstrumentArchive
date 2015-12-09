package db;

import data.User;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nilstes
 */
public class UserDao {
    
    private static final Logger log = Logger.getLogger(UserDao.class.getName());
    private static final Random random = new SecureRandom();
        
    String generateSalt() {
        return new BigInteger(128, random).toString(36);
    }
    
    byte[] makeHash(String salt, String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update((salt + password).getBytes());
        return messageDigest.digest();
    }
    
    public User getUser(String email) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE email=?");
            statement.setString(1, email);
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            User user = null;
            if(rs.next()) {
                log.log(Level.INFO, "Found user {0}", email);
                user = new User();
                user.setEmail(rs.getString("email"));
                user.setPassword("****");       
            } else {
                log.log(Level.INFO, "Could not find user {0}", email);
            }
            rs.close();
            statement.close();
            return user;
        } finally {
            connection.close();
        }
    }

    public boolean isPasswordOk(String email, String password) throws SQLException, NoSuchAlgorithmException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT salt,password FROM user WHERE email=?");
            statement.setString(1, email);
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            boolean ok = false;
            if(rs.next()) {
                log.log(Level.INFO, "Found user {0}", email);
                String salt = rs.getString("salt");
                log.log(Level.INFO, "salt:" + salt);
                log.log(Level.INFO, "password:" + password);
                byte[] dbPassword = rs.getBytes("password");
                byte[] givenPassword = makeHash(salt, password);
                log.log(Level.INFO, "db:" + new String(dbPassword) + ", given:" + new String(givenPassword), email);
                ok = Arrays.equals(dbPassword, givenPassword);     
            } else {
                log.log(Level.INFO, "Could not find user {0}", email);
            }
            rs.close();
            statement.close();
            return ok;
        } finally {
            connection.close();
        }
    }
    
    public boolean addUser(User user) throws SQLException, NoSuchAlgorithmException {
        Connection connection = Db.instance().getConnection();
        try {
            return doAddUser(connection, user);
        } finally {
            connection.close();
        }        
    }

    private boolean doAddUser(Connection connection, User user) throws SQLException, NoSuchAlgorithmException {
        PreparedStatement s = connection.prepareStatement("INSERT INTO user (email,salt,password) VALUES(?,?,?)");
        String salt = generateSalt();
        s.setString(1, user.getEmail());
        s.setString(2, salt);
        s.setBytes(3, makeHash(salt, user.getPassword()));
        log.fine(s.toString());
        int result = s.executeUpdate();
        s.close();
        log.log(Level.INFO, "Add user {0}", (result == 1?"ok":"failed"));
        return result == 1;
    }
    
    public boolean updateUser(User user) throws SQLException, NoSuchAlgorithmException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("UPDATE user set salt=?, password=? where email=?");
            String salt = generateSalt();
            s.setString(1, salt);
            s.setBytes(2, makeHash(salt, user.getPassword()));
            s.setString(3, user.getEmail());
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.log(Level.INFO, "Update user {0}", (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public boolean deleteUser(String email) throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("DELETE FROM user where email=?");
            s.setString(1, email);
            log.fine(s.toString());
            int result = s.executeUpdate();
            s.close();
            log.log(Level.INFO, "Delete user {0}", (result == 1?"ok":"failed"));
            return result == 1;
        } finally {
            connection.close();
        }        
    }

    public User insertFirstUserIfEmpty(User user) throws SQLException, NoSuchAlgorithmException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) from user");
            log.fine(s.toString());
            ResultSet rs = s.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            s.close();
            
            if(count == 0) {
                doAddUser(connection, user);
                log.info("Inserted first user");
                return user;
            } else {
                return null;
            }
        } finally {
            connection.close();
        }        
    }

    public List<User> getUsers() throws SQLException {
        Connection connection = Db.instance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user");
            log.fine(statement.toString());
            ResultSet rs = statement.executeQuery();
            List<User> users = new ArrayList<User>();
            while(rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setPassword("****");      
                users.add(user);
            }
            rs.close();
            statement.close();
            return users;
        } finally {
            connection.close();
        }
    }
}
