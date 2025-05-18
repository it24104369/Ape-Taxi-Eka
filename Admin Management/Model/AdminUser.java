package com.taxi.admin.model;

public class AdminUser extends user {
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePicturePath;
    private String permission;

    public AdminUser() {
    }

    public AdminUser(String username, String password, String email, String role,
                     String firstName, String lastName,String phone,String profilePicturePath,String permission) {
        super(username, password, email);
        this.role = role;
        this.firstName=firstName;
        this.lastName=lastName;
        this.phone=phone;
        this.profilePicturePath=profilePicturePath;
        this.permission=permission;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String toFileFormat() {
        return getUsername() + " , " + getPassword() + " , " + getEmail() + " , " + getRole() + " , "
                + getFirstName() + " , " + getLastName() + " , " + getPhone() + " , " + getProfilePicturePath() + " , " +getPermission();
    }

    public static AdminUser fromFileFormat(String line){
        String[] parts = line.split(" , ");
        if(parts.length<9){
            throw new IllegalArgumentException("Invalid admin data: "+line);
        }

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        return new AdminUser(parts[0],parts[1],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8]);
    }



}
