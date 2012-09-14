package org.motechproject.ghana.telco.controller;

import org.motechproject.ghana.telco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.motechproject.ghana.telco.handler.TelcoAuthenticationSuccessHandler.PRINCIPAL;

@Controller
@RequestMapping(value = "/password")
public class PasswordController {

    @Autowired
    private UserService userService;
    private static final String FAILURE = "Reset password failed. Please retry again";
    private static final String SUCCESS = "Password Successfully reset";
    private static final String STATUS = "status";

    @RequestMapping(method = RequestMethod.POST, value = "/reset")
    public ModelAndView resetPassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("resetPassword");
        String userName = ((User) session.getAttribute(PRINCIPAL)).getUsername();
        boolean resetSuccess = userService.resetPassword(userName, oldPassword, newPassword);

        if (resetSuccess) {
            modelAndView.getModel().put(STATUS, SUCCESS);
        } else {
            modelAndView.getModel().put(STATUS, FAILURE);
        }
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/page")
    public ModelAndView getPasswordResetForm() {
        ModelAndView modelAndView = new ModelAndView("resetPassword");
        modelAndView.getModel().put("STATUS", "");
        return modelAndView;
    }

}
