package uk.ac.dundee.computing.aec.instagrim.lib;

import com.datastax.driver.core.*;
import uk.ac.dundee.computing.aec.instagrim.Constants;

/**
 * Class to create the Cassandra Keyspace and tables.
 *
 * @author jslvtr
 * @author Andy Cobley
 *
 * @version 1.0
 * @since 14 Oct 2014
 */
public final class Keyspaces {

    /**
     * Method that creates the keyspace and tables for the Cassandra cluster.
     * Does not raise exceptions, but will print an error message out.
     *
     * @param c: the Cassandra cluster to create the keyspace and tables in.
     */
    public static void SetUpKeySpaces(Cluster c) {
        try {
            String CreateKeyspace = "CREATE KEYSPACE if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreatePicTable = "CREATE TABLE if not exists instagrim.Pics ("
                    + " user varchar,"
                    + " picid uuid PRIMARY KEY, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + " processedlength int,"
                    + " type  varchar,"
                    + " name  varchar"
                    + ")";
            String CreateUserPicList = "CREATE TABLE if not exists instagrim.userpiclist (\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "pic_added timestamp,\n"
                    + "PRIMARY KEY (user,picid,pic_added)\n"
                    + ") WITH CLUSTERING ORDER BY (picid desc, pic_added desc);";
            String CreateAddressType = "CREATE TYPE if not exists instagrim.address (\n"
                    + "      street text,\n"
                    + "      city text,\n"
                    + "      zip int\n"
                    + "  );";
            String CreateUserProfile = "CREATE TABLE if not exists instagrim.userprofiles (\n"
                    + "      login text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      first_name text,\n"
                    + "      last_name text,\n"
                    + "      email set<text>,\n"
                    + "      addresses  map<text, frozen <address>>,\n"
                    + "      profile_content text\n"
                    + "  );";

            // Connects to the Cassandra cluster, `c`.
            Session session = c.connect();

            // Creates the Keyspace.
            try {
                PreparedStatement statement = session.prepare(CreateKeyspace);
                BoundStatement boundStatement = new BoundStatement(statement);
                if(Constants.VERBOSE) System.out.println("Executing Keyspace prepared statement...");
                session.execute(boundStatement);
                if(Constants.VERBOSE) System.out.println("Created instagrim Keyspace.");
            } catch (Exception et) {
                if(Constants.VERBOSE) System.out.println("Can't create instagrim: " + et);
            }

            /*
             * Create the TABLEs in Cassandra cluster, `c`.
             */

            // Create the `Pic` table.
            if(Constants.DEBUG && Constants.VERBOSE) System.out.println("" + CreatePicTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreatePicTable);
                if(Constants.VERBOSE) System.out.println("Executing CreatePicTable query...");
                session.execute(cqlQuery);
            } catch (Exception et) {
                if(Constants.VERBOSE) System.out.println("Can't create tweet table: " + et);
                if(Constants.DEBUG) et.printStackTrace();
            }

            // Create the `userpiclist` table.
            if(Constants.DEBUG && Constants.VERBOSE) System.out.println("" + CreateUserPicList);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserPicList);
                if(Constants.VERBOSE) System.out.println("Executing CreateUserPicList query...");
                session.execute(cqlQuery);
            } catch (Exception et) {
                if(Constants.VERBOSE) System.out.println("Can't create user pic list table: " + et);
                if(Constants.DEBUG) et.printStackTrace();
            }

            // Create the `AddressType` table.
            if(Constants.DEBUG && Constants.VERBOSE) System.out.println("" + CreateAddressType);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateAddressType);
                if(Constants.VERBOSE) System.out.println("Executing CreateAddressType query...");
                session.execute(cqlQuery);
            } catch (Exception et) {
                if(Constants.VERBOSE) System.out.println("Can't create Address type: " + et);
                if(Constants.DEBUG) et.printStackTrace();
            }

            // Create the `UserProfile` table.
            if(Constants.DEBUG && Constants.VERBOSE) System.out.println("" + CreateUserProfile);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserProfile);
                if(Constants.VERBOSE) System.out.println("Executing CreateUserProfile query...");
                session.execute(cqlQuery);
            } catch (Exception et) {
                if(Constants.VERBOSE) System.out.println("Can't create Address Profile: " + et);
                if(Constants.DEBUG) et.printStackTrace();
            }

            session.close();

        } catch (Exception et) {
            if(Constants.VERBOSE) System.out.println("Other keyspace or coulm definition error" + et);
            if(Constants.DEBUG) et.printStackTrace();
        }

    }
}
