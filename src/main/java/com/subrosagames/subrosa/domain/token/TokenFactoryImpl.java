package com.subrosagames.subrosa.domain.token;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 *
 */
@Component
public class TokenFactoryImpl implements TokenFactory {

    @Autowired
    private TokenRepository tokenRepository;

    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateNewToken(int accountId, TokenType tokenType) {
        String token = new BigInteger(130, secureRandom).toString(32);
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setAccountId(accountId);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setToken(token);
        tokenRepository.storeToken(tokenEntity);
        return token;
    }

    @Override
    public Token getToken(String token, TokenType tokenType) {
        TokenEntity tokenEntity = tokenRepository.findToken(token);
        if (tokenType.equals(tokenEntity.getTokenType())) {
            return tokenEntity;
        }
        return null;
    }
}