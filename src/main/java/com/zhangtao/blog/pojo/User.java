package com.zhangtao.blog.pojo;

public class User {
    private String name;
    private String password;
    private int age;
    private String gender;
    private Hourse hourse;

    public void setHourse(Hourse hourse) {
        this.hourse = hourse;
    }

    public User(){

    }

    public User(String name, int age, String gender, Hourse hourse) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hourse = hourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Hourse getHourse() {
        return hourse;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
