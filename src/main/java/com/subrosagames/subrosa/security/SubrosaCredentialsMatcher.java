package com.subrosagames.subrosa.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: jgore
 * Date: 4/24/12
 * Time: 6:21 午後
 * To change this template use File | Settings | File Templates.
 */
public class SubrosaCredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaCredentialsMatcher.class);

    @Override
    protected Object getCredentials(AuthenticationInfo info) {
        String credentials = ((String) info.getCredentials()).substring(128);
        LOG.debug("Credentials section: " + credentials);
        return credentials;
    }

    @Override
    protected Object hashProvidedCredentials(AuthenticationToken token, AuthenticationInfo info) {
        LOG.debug("Hashing provided credentials");
        try {
            String credentials = String.valueOf(((char[]) token.getCredentials()));
            String storedCredentials = (String) info.getCredentials();
            LOG.debug("Credentials: " + storedCredentials);
            MessageDigest hashedPw = MessageDigest.getInstance(getHashAlgorithmName());
            String saltSection = storedCredentials.substring(0, 128);
            LOG.debug("Salt section: " + saltSection);
            hashedPw.update(saltSection.getBytes());
            hashedPw.update(credentials.getBytes());
            String hashedProvidedCredentials = Hex.encodeToString(hashedPw.digest());
            LOG.debug("Hash provided: " + hashedProvidedCredentials);
            return hashedProvidedCredentials;
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Failed create digest for algorithm {}", getHashAlgorithmName(), e);
            throw new IllegalStateException("JDK does not support hash algorithm", e);
        }
    }
}
