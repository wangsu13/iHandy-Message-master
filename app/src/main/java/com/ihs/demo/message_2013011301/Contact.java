package com.ihs.demo.message_2013011301;

import com.ihs.contacts.api.IPhoneContact.HSContactContent;

import java.util.Date;

public class Contact extends HSContactContent {
    String mid;
    String name;


    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact(String content, String label, String contactId, int type, boolean isFriend) {
        super(content, label, contactId, type, isFriend);
    }

    Date last;

    long lastTime;

    public Long getTime(){return lastTime;}


}
