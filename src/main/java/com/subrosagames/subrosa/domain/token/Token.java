package com.subrosagames.subrosa.domain.token;

/**
 * Token used for security and identification.
 */
public interface Token {

    /**
     * Get token owner id.
     *
     * @return owner id
     */
    int getOwner();

    /**
     * Get token value.
     *
     * @return token value
     */
    String getValue();

    /**
     * Get token type.
     *
     * @return token type
     */
    TokenType getType();

}
