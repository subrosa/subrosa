package com.subrosagames.subrosa.domain.token;

/**
 * Factory for the generation of identifying tokens.
 */
public interface TokenFactory {

    /**
     * Generate and persist a new token for a user.
     *
     * @param accountId account id
     * @param tokenType token type
     * @return token string
     */
    String generateNewToken(int accountId, TokenType tokenType);

    /**
     * Search for a token for the given token string and type.
     *
     * @param token token string
     * @param tokenType token type
     * @return token if found, or {@code null} if not found
     */
    Token findToken(String token, TokenType tokenType);
}
