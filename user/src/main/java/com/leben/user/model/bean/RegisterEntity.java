package com.leben.user.model.bean;

import java.io.Serializable;

public class RegisterEntity implements Serializable {

    public String account;    // 账号
    public String password;   // 密码
    public String nickName;   // 昵称 (对应后端 nick_name)
    public String phone;      // 手机号
    public String gender;     // 性别
    public String img;        // 头像路径/URL

}