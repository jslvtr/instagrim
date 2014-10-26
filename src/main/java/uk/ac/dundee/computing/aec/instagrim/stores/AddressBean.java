package uk.ac.dundee.computing.aec.instagrim.stores;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: jslvtr
 * Date: 26/10/2014
 * Time: 12:10
 */
public class AddressBean implements Serializable {

    private String street, city;
    private int zip;
    private String name;

    public AddressBean() {

    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void parse(String in) {
        String csv = in.replaceAll("[\\{\\}]*","");

        this.name = csv.split("=")[0];

        String[] elems = csv.split("=")[1].split(":");

        this.street = elems[1].split(",")[0].split("'")[1];
        this.city = elems[2].split(",")[0].split("'")[1];
        this.zip = Integer.parseInt(elems[3].replaceAll(" ", ""));
    }

    public String toString() {
        return String.format("{%s={street: '%s',city: '%s',zip: %d}}", this.name, this.street, this.city, this.zip);
    }
}
