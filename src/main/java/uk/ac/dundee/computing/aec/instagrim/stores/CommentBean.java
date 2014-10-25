package uk.ac.dundee.computing.aec.instagrim.stores;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jslvtr
 * Date: 23/10/2014
 * Time: 14:29
 */
public class CommentBean implements Serializable {

    private String content;
    private Date date;
    private String user;

    public CommentBean() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
