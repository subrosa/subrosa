package com.subrosagames.subrosa.domain.token;

/**
 *
 */
public interface Token {

    int getOwner();
    String getValue();
    TokenType getType();

}
