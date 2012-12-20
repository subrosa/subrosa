package com.subrosagames.subrosa.domain.token;

/**
 *
 */
public interface TokenFactory {

    String generateNewToken(TokenType tokenType, int accountId);
}
