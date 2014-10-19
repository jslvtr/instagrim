package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class LoggedIn {
    boolean loggedIn = false;
    String username = null;

    /**
     * Sets the username of this LoggedIn instance.
     * @param username the username to set to.
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets the username of this LoggedIn instance.
     * @return the username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Sets the status to "logged in" (true).
     */
    public void setLoggedIn(){
        this.loggedIn = true;
    }

    /**
     * Sets the status to "logged out" (false).
     */
    public void setLoggedOut(){
        this.loggedIn = false;
    }

    /**
     * Sets the status to either true or false.
     * @param loggedIn whether the user is logged in or not.
     */
    public void setLoginState(boolean loggedIn){
        this.loggedIn = loggedIn;
    }

    /**
     * Gets the current status of the instance, whether it is logged in or not.
     * @return true if logged in, false otherwise.
     */
    public boolean getLoggedIn(){
        return this.loggedIn;
    }
}
