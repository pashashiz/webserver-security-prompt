package com.asg.test.security;

import java.security.Principal;

public class NamePasswordPrincipal implements Principal {

    private String name;
    private String password;

    public NamePasswordPrincipal(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
