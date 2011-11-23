package com.subrosa.account;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

/**
 * Represent an account in the SubRosa application.
 */
@Entity
@Table(name = "account")
public class Account {

    @Id
    @SequenceGenerator(name = "accountSeq", sequenceName="account_account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
    @Column(name = "account_id")
    private int id;

    @Column
    private boolean activated;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    private String email;

    @Column(name = "cell_phone")
    private String cellPhone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ElementCollection(targetClass = AccountRole.class)
    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "account_role_id", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<AccountRole> accountRole;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public Set<AccountRole> getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(Set<AccountRole> accountRole) {
        this.accountRole = accountRole;
    }
}
