package com.example.guitartraina.activities.account;

public class User {
    private String userName;
    private Email email;
    private EncryptedPassword encryptedPassword;
    private String Rol;
    public String plainTextPassword;

    public User(Email email,String userName, EncryptedPassword encryptedPassword, String rol) {
        this.email = email;
        this.userName=userName;
        this.encryptedPassword = encryptedPassword;
        Rol = rol;
    }
    public User(Email email) {
        this.email = email;
    }

    public User(Email email, EncryptedPassword encryptedPassword, String rol) {
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        Rol = rol;
    }

    public User(Email email, EncryptedPassword encryptedPassword) {
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.Rol="";
    }

    public User() {
        this.email = new Email();
        this.encryptedPassword=new EncryptedPassword();
        this.Rol="";
    }

    public EncryptedPassword getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(EncryptedPassword encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    public String getRol() {
        return Rol;
    }

    public void setRol(String rol) {
        Rol = rol;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
}
