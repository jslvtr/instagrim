package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.*;
import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.stores.CommentBean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jslvtr
 * Date: 23/10/2014
 * Time: 14:29
 */
public class CommentModel {

    private Cluster cluster;

    public CommentModel(Cluster cluster) {
        this.cluster = cluster;
    }

    public boolean addComment(UUID threadID, String content, String user) {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("insert into comments (thread_id,date,user,content) Values(?,?,?,?)");

            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(threadID, new Date(), user, content));
            //We are assuming this always works.  Also a transaction would be good here !

            return true;
        } catch (Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at User addComent method ----\n\n");
                e.printStackTrace();
            }
        }

        return false;
    }

    public LinkedList<CommentBean> getCommentsForThread(UUID threadID) {
        LinkedList<CommentBean> commentList = new LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT content, user, date FROM comments WHERE thread_id = ?");

        if(Constants.VERBOSE) {
            System.out.println("SELECT content, user, date FROM comments WHERE thread_id = " + threadID.toString());
        }

        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( boundStatement.bind(threadID) );
        if(rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for(Row row : rs) {
                CommentBean comment = new CommentBean();
                String user = row.getString("user");
                Date date = row.getDate("date");
                String content = row.getString("content");
                if(Constants.VERBOSE) {
                    System.out.println("User: " + user);
                    System.out.println("Date: " + date.toString());
                    System.out.println("Content: " + content);
                }
                comment.setUser(user);
                comment.setDate(date);
                comment.setContent(content);

                commentList.add(comment);
            }
        }
        return commentList;
    }
}
