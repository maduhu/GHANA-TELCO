package org.motechproject.ghana.telco.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TelcoAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String PRINCIPAL = "PRINCIPAL";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletRequest.getSession().setAttribute(PRINCIPAL, authentication.getPrincipal());
        super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }
}
