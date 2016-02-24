package com.subrosagames.gateway.token;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.gateway.auth.Account;

/**
 * Factory for the generation of identifying tokens.
 */
@Component
public class TokenFactory {

    int NUM_BITS = 130;

    @Autowired
    private TokenRepository tokenRepository;

    private SecureRandom secureRandom = new SecureRandom();

    public String generateNewToken(Account account, TokenType type) {
        String str = new BigInteger(NUM_BITS, secureRandom).toString(32);
        Token token = new Token();
        token.setAccount(account);
        token.setType(type);
        token.setToken(str);
        tokenRepository.save(token);
        return str;
    }

    public Token findToken(String token, TokenType tokenType) {
        return tokenRepository.findByTokenAndType(token, tokenType);
    }
}
