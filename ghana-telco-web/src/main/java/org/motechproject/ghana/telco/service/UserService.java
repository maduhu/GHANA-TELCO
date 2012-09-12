package org.motechproject.ghana.telco.service;

import org.motechproject.ghana.telco.domain.TelcoUser;
import org.motechproject.ghana.telco.repository.AllTelcoUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.ghana.telco.utils.Encrypt.encrypt;

@Component
public class UserService {

    AllTelcoUsers allTelcoUsers;

    @Autowired
    public UserService(AllTelcoUsers allTelcoUsers) {
        this.allTelcoUsers = allTelcoUsers;
    }

    public void register(String userName, String password, List<String> roles) {
        allTelcoUsers.add(new TelcoUser(encrypt(userName), encrypt(password), roles));
    }

    public TelcoUser findBy(String userName, String password) {
        return allTelcoUsers.findBy(encrypt(userName), encrypt(password));
    }

    public boolean resetPassword(String userName, String oldPassword, String newPassword) {
        TelcoUser telcoUser = findBy(userName, oldPassword);
        if(telcoUser != null) {
            telcoUser.setPassword(encrypt(newPassword));
            allTelcoUsers.update(telcoUser);
            return true;
        }
        return false;
    }
}
