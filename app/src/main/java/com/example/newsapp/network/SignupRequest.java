package com.example.newsapp.network;

public class SignupRequest {
    private String name;
    private String phone;
    private String email;
    private String password;
    private String age;
    
    public SignupRequest(String name, String phone, String email, String password, String age) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.age = age;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAge() {
        return age;
    }
    
    public void setAge(String age) {
        this.age = age;
    }
}
