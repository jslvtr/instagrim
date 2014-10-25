package uk.ac.dundee.computing.aec.instagrim.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: jslvtr
 * Date: 23/10/2014
 * Time: 14:41
 */
public class FailureToPostException extends Exception {
    public FailureToPostException() {
        super();
    }

    public FailureToPostException(String message) {
        super(message);
    }
}
