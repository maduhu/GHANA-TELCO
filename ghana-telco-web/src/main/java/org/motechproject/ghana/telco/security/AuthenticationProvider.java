package org.motechproject.ghana.telco.security;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ghana.telco.domain.TelcoUser;
import org.motechproject.ghana.telco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    UserService userService;

    @Autowired
    public AuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        TelcoUser telcoUser = userService.findBy(userName, password);
        if (telcoUser != null) {
            return new User(userName, password, true, true, true, true, authorities(telcoUser));
        }
        throw new BadCredentialsException("Invalid username or password");
    }

    private Collection<GrantedAuthority> authorities(TelcoUser user) {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            for (String role : user.getRoles()) {
                authorities.add(new GrantedAuthorityImpl(role));
            }
        }
        return authorities;
    }
}
