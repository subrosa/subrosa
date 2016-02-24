package com.subrosagames.gateway.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.gateway.auth.Account;
import lombok.Data;

/**
 * Token used for security and identification.
 */
@Data
@Entity
@Table(name = "token")
public class Token {

    @Id
    @SequenceGenerator(name = "tokenSeq", sequenceName = "token_token_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokenSeq")
    @Column(name = "token_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private String token;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private TokenType type;

}
