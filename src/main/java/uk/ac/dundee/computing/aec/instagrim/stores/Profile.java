package uk.ac.dundee.computing.aec.instagrim.stores;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class to store and send a user's profile through our session.
 *
 * @author jslvtr
 * @since 21/10/2014
 */
public class Profile implements Serializable {

    private String content;
    private String username;
    private UUID userID;

    /**
     * Nullary constructor to match the JavaBean standard.
     */
    public Profile() {

    }

    /**
     * Sets the profile content in this instance.
     * @param content the content of the profile for this instance.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the content of the profile in this instance.
     * @return a String, the content of the profile.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the username for this profile instance.
     * @return a String, the username of this profile.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username for this profile instance.
     * @param username the username for this profile.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    /**
     * Converts the instance to a string, in the format: `Profile[username=%s,content=%s]`.
     * @return a String the instance can be converted to.
     */
    public String toString() {
        return String.format("Profile[username=%s,content=%s]", this.username, this.content);
    }

}
