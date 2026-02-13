package com.leben.user.model.bean;

public class UserInfoEntity {
    private String nickName; // 昵称
    private String phone;    // 手机号
    private String img;      // 头像
    private String password;

    public UserInfoEntity(){

    }

    public UserInfoEntity(String nickName, String phone, String img, String password) {
        this.nickName = nickName;
        this.phone = phone;
        this.img = img;
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
