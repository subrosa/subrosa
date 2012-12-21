package com.subrosagames.subrosa.domain.token;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 *
 */
public interface TokenFactory {

    String generateNewToken(int accountId, TokenType tokenType);

    Token getToken(String token, TokenType tokenType);
}
