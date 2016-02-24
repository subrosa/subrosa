package com.subrosagames.gateway.auth;

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

import com.subrosagames.subrosa.security.ConnectionProvider;
import lombok.Data;

/**
 * Persisted user connection.
 */
@Data
@Entity
@Table(name = "user_connection")
public class UserConnection {

    @Id
    @SequenceGenerator(name = "userConnectionSeq", sequenceName = "user_connection_user_connection_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userConnectionSeq")
    @Column(name = "user_connection_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "provider_id")
    @Enumerated(EnumType.STRING)
    private ConnectionProvider provider;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "secret")
    private String secret;


}
