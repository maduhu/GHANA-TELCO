package org.motechproject.ghana.telco.tools.seed;

import org.motechproject.ghana.telco.repository.AllTelcoUsers;
import org.motechproject.ghana.telco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class TelcoUserSeed extends Seed {
    @Autowired
    private UserService userService;
    @Autowired
    private AllTelcoUsers allTelcoUsers;

    @Override
    protected void load() {
        allTelcoUsers.removeAll();
        userService.register("admin", "telco$987", asList("SUPERVISOR", "USER"));
        userService.register("display", "display$987", asList("USER"));
    }
}
