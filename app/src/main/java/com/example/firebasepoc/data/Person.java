package com.example.firebasepoc.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by robin on 2/10/16.
 */
public class Person implements Serializable {

    public String firstname, lastname, zip;

    public Long dob;

    private String key = null;

    public String getFullName() {
        return firstname + " " + lastname;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getBirthDate() {
        if(dob == null) {
            return null;
        }

        return new Date(this.dob);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Long getDob() {
        return dob;
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }
}
