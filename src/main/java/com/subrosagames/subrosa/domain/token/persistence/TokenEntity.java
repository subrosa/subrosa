package com.subrosagames.subrosa.domain.token.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.token.TokenType;

/**
 *
 */
@Entity
@Table(name = "token")
public class TokenEntity {

    @Id
    @SequenceGenerator(name = "tokenSeq", sequenceName = "token_token_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokenSeq")
    @Column(name = "token_id")
    private Integer id;

    @Column(name = "account_id")
    private Integer accountId;

    @Column
    private String token;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
