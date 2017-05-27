package Entities;

/**
 * Created by carolina on 25/05/2017.
 */

public class Direccion {
    private String Address;
    private String City;
    private String State;
    private String Country;
    private String PostalCode;
    private String KnownName;

    public Direccion(String address, String city, String state, String country, String postalCode, String knownName) {
        Address = address;
        City = city;
        State = state;
        Country = country;
        PostalCode = postalCode;
        KnownName = knownName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getKnownName() {
        return KnownName;
    }

    public void setKnownName(String knownName) {
        KnownName = knownName;
    }
}
