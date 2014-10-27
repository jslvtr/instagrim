package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

import javax.servlet.ServletException;

/**
 * Class to store and access information about Users.
 *
 * @author Administrator
 * @author jslvtr
 *
 * @since 16 Oct 2014
 */
public class User {
    Cluster cluster;

    /**
     * Method to add an user and password to the database.
     * Will not throw an exception, even if the Cassandra cluster is unavailable.
     *
     * @param username the username to add to the database.
     * @param password the password for the username we're adding.
     * @return true if the username was added, false otherwise.
     */
    public boolean RegisterUser(String username, String password, java.util.UUID user_id) {
        String EncodedPassword;

        try {
            EncodedPassword = AeSimpleSHA1.SHA1(password);
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }

        try {

            if(IsValidUser(username, password) != null) {
                return false;
            } else {

                Session session = cluster.connect("instagrim_js");
                PreparedStatement ps = session.prepare("INSERT INTO userprofiles (login,userid,password) VALUES (?,?,?) IF NOT EXISTS");

                BoundStatement boundStatement = new BoundStatement(ps);
                session.execute(boundStatement.bind(username, user_id, EncodedPassword));
                //We are assuming this always works.  Also a transaction would be good here !

                if(IsValidUser(username, password) != null) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at User RegisterUser method ----\n\n");
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Method to check is user is valid or not.
     * Will throw a ServletException if Cassandra Cluster is unavailable.
     * It's up to the Servlet to catch that and redirect user to an error page.
     *
     * @param username username to check the database for.
     * @param password password to check whether it's right or not.
     * @return true if the user is valid, false otherwise.
     */
    public UUID IsValidUser(String username, String password) throws NullPointerException {
        String EncodedPassword;

        try {
            EncodedPassword = AeSimpleSHA1.SHA1(password);
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return null;
        }

        try {
            Session session = cluster.connect("instagrim_js");
            PreparedStatement ps = session.prepare("select userid, password from userprofiles where login = ?");
            ResultSet rs;
            BoundStatement boundStatement = new BoundStatement(ps);

            rs = session.execute(boundStatement.bind(username));

            if(rs.isExhausted()) {
                System.out.println("User doesn't exist!");
                return null;
            } else {
                for(Row row : rs) {
                    String StoredPass = row.getString("password");
                    if(StoredPass.compareTo(EncodedPassword) == 0) {
                        return row.getUUID("userid");
                    }
                }
            }
        } catch(NullPointerException e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at User IsValidUser method ----\n\n");
                e.printStackTrace();
            }
            throw new NullPointerException("Cassandra cluster unavailable.");
        }


        return null;
    }

    public String getUsernameForID(UUID userID) {
        try {
            Session session = cluster.connect("instagrim_js");
            PreparedStatement ps = session.prepare("select login from userprofiles where userid = ? LIMIT 1");
            ResultSet rs;
            BoundStatement boundStatement = new BoundStatement(ps);

            rs = session.execute(boundStatement.bind(userID));

            if(rs.isExhausted()) {
                System.out.println("User doesn't exist!");
                return null;
            } else {
                return rs.one().getString("login");
            }
        } catch(NullPointerException e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at User IsValidUser method ----\n\n");
                e.printStackTrace();
            }
            throw new NullPointerException("Cassandra cluster unavailable.");
        }
    }

    /**
     * Sets the Cassandra cluster for this instance to be the parameter.
     *
     * @param cluster the Cassandra cluster to set this instance's cluster to be.
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }


}
