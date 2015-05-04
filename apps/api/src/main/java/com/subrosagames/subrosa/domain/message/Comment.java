package com.subrosagames.subrosa.domain.message;

import java.util.Date;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Represents a comment on a {@link Post}.
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
        return timestamp == null ? null : new Date(timestamp.getTime());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp == null ? null : new Date(timestamp.getTime());
    }
}
