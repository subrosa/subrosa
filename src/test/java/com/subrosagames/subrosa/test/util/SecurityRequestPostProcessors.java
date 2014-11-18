/*
* Copyright 2002-2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
*/
package com.subrosagames.subrosa.test.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Uses a {@link RequestPostProcessor} to add a defined security context.
 */
public final class SecurityRequestPostProcessors {

    private SecurityRequestPostProcessors() {
    }

    /**
     * Establish a security context for a user with the specified username.
     * <p/>
     * The additional details are obtained from the {@link UserDetailsService}
     * declared in the {@link org.springframework.web.context.WebApplicationContext}.
     *
     * @param username username
     * @return servletRequest post processor
     */
    public static UserDetailsRequestPostProcessor userDetailsService(String username) {
        return new UserDetailsRequestPostProcessor(username);
    }


    /**
     * Support class for {@link RequestPostProcessor}s that establish a Spring Security context.
     */
    private abstract static class AbstractSecurityContextRequestPostProcessor {

        private SecurityContextRepository repository = new HttpSessionSecurityContextRepository();

        final void save(Authentication authentication, HttpServletRequest request) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            save(securityContext, request);
        }

        final void save(SecurityContext securityContext, final HttpServletRequest servletRequest) {
            HttpServletResponse response = new MockHttpServletResponse();

            HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(servletRequest, response);
            this.repository.loadContext(requestResponseHolder);

            HttpServletRequest request = requestResponseHolder.getRequest();
            response = requestResponseHolder.getResponse();

            this.repository.saveContext(securityContext, request, response);
        }
    }

    /**
     * Post processor that provides a user details service in the security context.
     */
    public static final class UserDetailsRequestPostProcessor
            extends AbstractSecurityContextRequestPostProcessor implements RequestPostProcessor {

        private final String username;

        private String userDetailsServiceBeanId;

        private UserDetailsRequestPostProcessor(String username) {
            this.username = username;
        }

        /**
         * Use this method to specify the bean id of the {@link UserDetailsService} to
         * use to look up the {@link UserDetails}.
         * <p/>
         * <p>By default a lookup of {@link UserDetailsService} is performed by type. This
         * can be problematic if multiple {@link UserDetailsService} beans are declared.
         *
         * @param beanId user details service bean id
         * @return {@code this}
         */
        public UserDetailsRequestPostProcessor userDetailsServiceBeanId(String beanId) {
            this.userDetailsServiceBeanId = beanId;
            return this;
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            UsernamePasswordAuthenticationToken authentication = authentication(request.getServletContext());
            save(authentication, request);
            return request;
        }

        private UsernamePasswordAuthenticationToken authentication(ServletContext servletContext) {
            ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            UserDetailsService userDetailsService = userDetailsService(context);
            UserDetails userDetails = userDetailsService.loadUserByUsername(this.username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }

        private UserDetailsService userDetailsService(ApplicationContext context) {
            if (userDetailsServiceBeanId == null) {
                return context.getBean(UserDetailsService.class);
            }
            return context.getBean(userDetailsServiceBeanId, UserDetailsService.class);
        }
    }

}
