package com.subrosagames.gateway.token;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.subrosagames.gateway.auth.Account;

/**
 * CRUD operations for tokens.
 */
public interface TokenRepository extends CrudRepository<Token, String> {

    /**
     * Find specified token.
     *
     * @param token token string
     * @param type  token type
     * @return token entity
     */
    Token findByTokenAndType(String token, TokenType type);

    /**
     * Find all tokens associated with the given account.
     *
     * @param account account
     * @return account tokens
     */
    List<Token> findByAccount(Account account);

}
