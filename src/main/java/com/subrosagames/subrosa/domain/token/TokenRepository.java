package com.subrosagames.subrosa.domain.token;

import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 *
 */
public interface TokenRepository {

    TokenEntity findToken(String token, TokenType tokenType);

    void storeToken(TokenEntity tokenEntity);
}
