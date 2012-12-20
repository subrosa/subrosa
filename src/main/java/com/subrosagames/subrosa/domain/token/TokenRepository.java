package com.subrosagames.subrosa.domain.token;

import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 *
 */
public interface TokenRepository {

    TokenEntity findToken(String token);

    void storeToken(TokenEntity tokenEntity);
}
