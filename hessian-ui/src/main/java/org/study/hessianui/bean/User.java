package org.study.hessianui.bean;

import java.io.Serializable;

/**
 * @author chenyao
 * @date 2019/6/11 16:51
 * @description
 */
public class User implements Serializable {

    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
