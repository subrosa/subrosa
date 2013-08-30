package com.subrosagames.subrosa.api;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.test.util.MockWebApplication;
import com.subrosagames.subrosa.test.util.MockWebApplicationContextLoader;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.security.SubrosaUser;
import junit.framework.Assert;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Nullable;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link ApiAccountController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "/test-context.xml" })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    /**
     * Test registration and authentication of an account.
     * @throws Exception everything.
     */
    @Test
    public void testRegistrationAndAuthentication() throws Exception {
        Registration registration = new Registration();
        Account account = new Account();
        account.setEmail("jimmy@icanhazemail.com");
        registration.setAccount(account);
        registration.setPassword("password");

        mockMvc.perform(
                post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jimmy@icanhazemail.com"));

        AuthenticationManager authenticationManager = applicationContext.getBean("org.springframework.security.authenticationManager",
                AuthenticationManager.class);
        Authentication credentials = new UsernamePasswordAuthenticationToken("jimmy@icanhazemail.com", "password");
        Authentication authentication = authenticationManager.authenticate(credentials);
        SubrosaUser principal = (SubrosaUser) authentication.getPrincipal();
        Assert.assertEquals("jimmy@icanhazemail.com", principal.getAccount().getEmail());
    }

}
