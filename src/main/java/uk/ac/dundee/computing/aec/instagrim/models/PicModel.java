package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;
import javax.imageio.ImageIO;

import static org.imgscalr.Scalr.*;

import com.jhlabs.image.FeedbackFilter;
import com.jhlabs.image.GainFilter;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.PointillizeFilter;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

public class PicModel {

    Cluster cluster;

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void deletePic(String user, java.util.UUID picid) {
        try {
            Session session = cluster.connect("instagrim_js");

            PreparedStatement psDeletePic = session.prepare("DELETE FROM pics WHERE picid = ?;");
            PreparedStatement psDeletePicFromUser = session.prepare("DELETE FROM userpiclist WHERE user = ? AND picid = ?");

            BoundStatement bsDeletePic = new BoundStatement(psDeletePic);
            BoundStatement bsDeletePicFromUser = new BoundStatement(psDeletePicFromUser);

            session.execute(bsDeletePic.bind(picid));
            session.execute(bsDeletePicFromUser.bind(user, picid));
        } catch(Exception e) {
            if(Constants.VERBOSE) {
                System.out.println("Error at PicModel#deletePic(String)");
            }
            if(Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void deletePicFromFilesystem(java.util.UUID picid) {
        try {
            Files.deleteIfExists(new File("/var/tmp/instagrim_js/" + picid).toPath());
        } catch(IOException ioe) {
            if(Constants.VERBOSE) {
                System.out.println("Error at PicModel#deletePicFromFilesystem(UUID)");
            }
            if(Constants.DEBUG) {
                ioe.printStackTrace();
            }
        }
    }

    public void insertPic(byte[] b, String type, String name, String user, String new_picid, String filterName) {
        try {
            String types[] = Converters.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid;
            if (new_picid.equals("")) {
                 picid = Converters.getTimeUUID();
            } else {
                picid = UUID.fromString(new_picid);
            }

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim_js/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim_js/" + picid));

            output.write(b);

            byte[] thumbb = picresize(picid.toString(), types[1]);
            int thumblength = thumbb.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
            byte[] processedb = picdecolour(picid.toString(), types[1], filterName);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            int processedlength = processedb.length;

            Session session = cluster.connect("instagrim_js");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf, processedbuf, user, DateAdded, length, thumblength, processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();

            deletePicFromFilesystem(picid);

        } catch(Exception ex) {
            System.out.println("Error --> " + ex);
            if(Constants.DEBUG) {
                ex.printStackTrace();
            }
        }
    }

    public byte[] picresize(String picid, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim_js/" + picid));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch(IOException et) {

        }
        return null;
    }

    public byte[] picdecolour(String picid, String type, String filterName) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim_js/" + picid));
            BufferedImage processed = createProcessed(BI, filterName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch(IOException et) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }

    public static BufferedImage createProcessed(BufferedImage img, String filterName) {
        int Width = img.getWidth() - 1;
        if(filterName.equals("Invert")) {
            InvertFilter invertFilter = new InvertFilter();
            BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            invertFilter.filter(img, out);
            return out;
        } else if(filterName.equals("GainDark")) {
            GainFilter gainFilter = new GainFilter();
            gainFilter.setGain(0.7f);
            BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            gainFilter.filter(img, out);
            return out;
        } else if(filterName.equals("GainLight")) {
            GainFilter gainFilter = new GainFilter();
            gainFilter.setGain(0.3f);
            BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            gainFilter.filter(img, out);
            return out;
        } else if(filterName.equals("Pointillize")) {
            PointillizeFilter pointillizeFilter = new PointillizeFilter();
            pointillizeFilter.setScale(10f);
            pointillizeFilter.setRandomness(0.1f);
            pointillizeFilter.setAmount(0.1f);
            pointillizeFilter.setFuzziness(0.1f);
            pointillizeFilter.setTurbulence(10f);
            pointillizeFilter.setGridType(PointillizeFilter.SQUARE);
            BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            pointillizeFilter.filter(img, out);
            return out;
        } else { //grayscale
            img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
            return pad(img, 4);
        }
    }

    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim_js");
        PreparedStatement ps = session.prepare("SELECT picid FROM userpiclist WHERE user = ?");
        if(Constants.VERBOSE) System.out.println("SELECT picid FROM userpiclist WHERE user = " + User);
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if(rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for(Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                Pics.add(pic);

            }
        }
        return Pics;
    }

    public Pic getPic(int image_type, java.util.UUID picid) {
        Session session = cluster.connect("instagrim_js");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        try {
            ResultSet rs = null;
            PreparedStatement ps = null;

            if(image_type == Converters.DISPLAY_IMAGE) {

                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if(image_type == Converters.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if(image_type == Converters.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            if(rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for(Row row : rs) {
                    if(image_type == Converters.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if(image_type == Converters.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                    } else if(image_type == Converters.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }

                    type = row.getString("type");

                }
            }
        } catch(Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        session.close();
        Pic p = new Pic();
        p.setPic(bImage, length, type);

        return p;

    }

}
