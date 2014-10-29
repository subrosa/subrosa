package com.subrosagames.subrosa.domain.token;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 * Token factory implementation.
 */
@Component
public class TokenFactoryImpl implements TokenFactory {

    private static final int NUM_BITS = 130;

    @Autowired
    private TokenRepository tokenRepository;

    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateNewToken(int accountId, TokenType tokenType) {
        String token = new BigInteger(NUM_BITS, secureRandom).toString(32);
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setAccountId(accountId);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setToken(token);
        tokenRepository.storeToken(tokenEntity);
        return token;
    }

    @Override
    public Token findToken(String token, TokenType tokenType) {
        return tokenRepository.findToken(token, tokenType);
    }
}
