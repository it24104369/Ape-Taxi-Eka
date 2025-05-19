package com.taxi.user.model;

public class Passenger extends User {
    private String phone;
    private String fullname = "";
    private String address = "";
    private boolean isVerified;

    // Constructor
    public Passenger(String username, String email, String password, String phone, String fullname, String address, boolean isVerified) {
        super(username, email, password);
        this.phone = phone;
        this.fullname = fullname;
        this.address = address;
        this.isVerified = isVerified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname != null ? fullname : "";
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address : "";
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
