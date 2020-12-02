package no.hvl.past.webui.security;

import no.hvl.past.webui.transfer.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.Collection;
import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    UserService userService;

    public CustomAuthenticationProvider(@Autowired UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (authentication.getCredentials() == null) {
            throw new InsufficientAuthenticationException("no password");
        }
        String passwordToken = authentication.getCredentials().toString();
        if (this.userService.authenticate(username, passwordToken)) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, passwordToken, Collections.emptyList());
            return token;
        } else {
            throw new BadCredentialsException("Wrong username/password combination");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
