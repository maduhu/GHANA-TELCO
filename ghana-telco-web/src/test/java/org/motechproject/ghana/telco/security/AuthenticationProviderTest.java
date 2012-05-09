package org.motechproject.ghana.telco.security;

import ch.lambdaj.Lambda;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.TelcoUser;
import org.motechproject.ghana.telco.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AuthenticationProviderTest {
    
    @Mock
    UserService userService;
    AuthenticationProvider authenticationProvider;

    @Before
    public void beforeSetup() {
        initMocks(this);
        authenticationProvider = new AuthenticationProvider(userService);
    }
    
    @Test
    public void shouldRetrieveUserBasedOnCredentialsProvided() {
        final String userName = "userName";
        final String password = "password";
        final List<String> roles = asList("role1", "role2");

        TelcoUser user = new TelcoUser(userName, password, roles);
        when(userService.findBy(userName, password)).thenReturn(user);

        final UserDetails actualUser = authenticationProvider.retrieveUser(userName, new UsernamePasswordAuthenticationToken(null, password));

        assertThat(actualUser.getUsername(), is(userName));
        assertThat(actualUser.getPassword(), is(password));
        assertThat(Lambda.extract(actualUser.getAuthorities(), on(GrantedAuthorityImpl.class).getAuthority()), is(roles));
    }

    @Test
    public void shouldThrowExceptionIfUserIsNotFound() {
        final String userName = "userName";
        final String password = "password";

        when(userService.findBy(userName, password)).thenReturn(null);
        try{
            authenticationProvider.retrieveUser(userName, new UsernamePasswordAuthenticationToken(null, password));
            junit.framework.Assert.fail("exception bad credentails exception");
        } catch(BadCredentialsException bce) {
            assertThat(bce.getMessage(), is("Invalid username or password"));
        }
    }
}
