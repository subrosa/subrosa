package com.subrosagames.subrosa.api.dto;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import com.subrosagames.subrosa.domain.account.AccountRole;

/**
 * Encapsulates the necessary information to create or update an account.
 */
public class AccountDescriptor {

    private Integer id;
    private Optional<Boolean> activated;
    private Optional<String> username;
    private Optional<String> name;
    private Optional<String> email;
    private Optional<String> cellPhone;
    private Optional<Date> dateOfBirth;
    private Optional<Set<AccountRole>> accountRoles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Optional<Boolean> getActivated() {
        return activated;
    }

    public void setActivated(Optional<Boolean> activated) {
        this.activated = activated;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(Optional<String> email) {
        this.email = email;
    }

    public Optional<String> getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(Optional<String> cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Optional<Date> getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Optional<Date> dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Optional<Set<AccountRole>> getAccountRoles() {
        return accountRoles;
    }

    public void setAccountRoles(Optional<Set<AccountRole>> accountRoles) {
        this.accountRoles = accountRoles;
    }

}
