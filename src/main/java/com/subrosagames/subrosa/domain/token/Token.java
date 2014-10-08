package com.subrosagames.subrosa.domain.token;

/**
 * Token used for security and identification.
 */
public interface Token {

    int getOwner();

    String getValue();

    TokenType getType();

}
