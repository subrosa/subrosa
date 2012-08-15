package com.subrosagames.subrosa.domain.message;

import com.subrosagames.subrosa.domain.account.Account;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 8:57 午後
 * To change this template use File | Settings | File Templates.
 */
public class Comment {

    private Account account;
    private String comment;
    private Date timestamp;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
