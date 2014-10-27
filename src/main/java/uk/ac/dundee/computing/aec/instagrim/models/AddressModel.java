package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.*;
import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.stores.AddressBean;
import uk.ac.dundee.computing.aec.instagrim.stores.CommentBean;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jslvtr
 * Date: 26/10/2014
 * Time: 13:32
 */
public class AddressModel {

    private Cluster cluster;

    public AddressModel(Cluster cluster) {
        this.cluster = cluster;
    }

    public LinkedList<AddressBean> getAddressesForUser(String login) {
        LinkedList<AddressBean> addressList = new LinkedList<>();
        Session session = cluster.connect("instagrim_js");
        PreparedStatement ps = session.prepare("SELECT addresses FROM userprofiles WHERE login = ?");

        if(Constants.VERBOSE) {
            System.out.println("SELECT addresses FROM userprofiles WHERE login = " + login);
        }

        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( boundStatement.bind(login) );
        if(rs.isExhausted()) {
            System.out.println("No addresses returned");
            return null;
        } else {
            Row row = rs.one();

            UserType addressUDT = cluster.getMetadata().getKeyspace("instagrim_js").getUserType("address");

            Map<String, UDTValue> addressMap = row.getMap("addresses", String.class, UDTValue.class);

            for(Map.Entry<String, UDTValue> entry:  addressMap.entrySet()) {
                System.out.println(entry);
                AddressBean ad = new AddressBean();
                ad.parse(entry.toString());
                if(Constants.VERBOSE) {
                    System.out.println(ad.toString());
                }
                addressList.add(ad);
            }
        }
        return addressList;
    }

    public String[] parseAddressList(String list) {
        String[] elements = list.split("}},");
        for(int i = 0; i < elements.length-1; i++) {
            elements[i] = elements[i] + "}}";
        }

        return elements;
    }

}
