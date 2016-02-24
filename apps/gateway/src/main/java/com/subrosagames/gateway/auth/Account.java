package com.subrosagames.gateway.auth;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.gateway.token.Token;
import com.subrosagames.gateway.token.TokenInvalidException;
import com.subrosagames.gateway.token.TokenRepository;
import com.subrosagames.gateway.token.TokenType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(name = "email", columnNames = Account.EMAIL),
                @UniqueConstraint(name = "account_cell_phone", columnNames = Account.CELL_PHONE),
                @UniqueConstraint(name = "account_username", columnNames = Account.USERNAME),
        }
)
public class Account implements com.subrosagames.subrosa.security.Account {

    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private transient TokenRepository tokenRepository;
    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private transient AccountRepository accountRepository;
    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private transient PasswordEncoder passwordEncoder;

    static final String CELL_PHONE = "cell_phone";
    static final String EMAIL = "email";
    static final String USERNAME = "username";

    @Id
    @SequenceGenerator(name = "accountSeq", sequenceName = "account_account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
    @Column(name = "account_id")
    private Integer id;

    @Column
    private String email;

    @Column(name = CELL_PHONE)
    private String phone;

    @Column
    private String username;

    @JsonIgnore
    @Column(length = 256)
    private String password;

    @Column
    private Boolean activated;

    @Column(name = "last_logged_in")
    private Date lastLoggedIn;

    @ElementCollection(targetClass = AccountRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles = EnumSet.noneOf(AccountRole.class);

    // TODO
//    @Column
//    private Date deleted;

    /**
     * Create an account with the specified password.
     *
     * @param userPassword password
     * @return created account
     */
    public Account create(String userPassword) throws ConflictException {
        setPassword(passwordEncoder.encode(userPassword));
        try {
            return accountRepository.save(this);
        } catch (JpaSystemException | DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage().toLowerCase();
            if (message.contains("unique constraint") && message.contains("email")) {
                throw new EmailConflictException("Email " + getEmail() + " already in use.", e);
            } else if (message.contains("unique constraint") && message.contains("phone")) {
                throw new PhoneConflictException("Phone " + getPhone() + " already in use.", e);
            } else {
                throw e;
            }
            //integrity constraint violation: unique constraint or index violation; UK_Q0UJA26QGU1ATULENWUP9RXYR table: ACCOUNT
        }
    }

    /**
     * Activates account using the given token.
     *
     * @param token activation token
     * @throws TokenInvalidException if token is not valid for activation
     */
    public void activate(String token) throws TokenInvalidException {
        Token retrieved = findToken(token, TokenType.EMAIL_VALIDATION);
        if (retrieved == null || !getId().equals(retrieved.getAccount().id)) {
            throw new TokenInvalidException("Token does not exist");
        }
        setActivated(true);
        accountRepository.save(this);
    }

    public Token findToken(String token, TokenType tokenType) {
        return tokenRepository.findByTokenAndType(token, tokenType);
    }

    public void storeToken(Token token) {
        tokenRepository.save(token);
    }

    public void deleteTokens() {
        List<Token> tokens = tokenRepository.findByAccount(this);
        tokenRepository.delete(tokens);
    }

    public void save() {
        accountRepository.save(this);
    }

    public Collection<? extends GrantedAuthority> grantedAuthorities() {
        return roles.stream()
                .map(AccountRole::name)
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
