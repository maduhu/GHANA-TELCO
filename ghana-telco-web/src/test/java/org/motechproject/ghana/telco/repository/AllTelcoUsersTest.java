package org.motechproject.ghana.telco.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.telco.BaseSpringTestContext;
import org.motechproject.ghana.telco.domain.TelcoUser;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AllTelcoUsersTest extends BaseSpringTestContext {

    @Autowired
    private AllTelcoUsers allTelcoUsers;

    @Test
    public void shouldFindTelcoByUserNameAndPassword() {
        final String userName = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        final String password = "4632a5a8a5e1a5bd79a47dac97f3108ebe95ad1ea73773bc0c6bcfaaedd806cf";

        final TelcoUser user1 = new TelcoUser(userName, password, asList("some role", "role2"));
        final TelcoUser user2 = new TelcoUser(password, password, asList("some role", "role2"));
        allTelcoUsers.add(user1);
        allTelcoUsers.add(user2);

        assertThat(allTelcoUsers.findBy(userName, password).getId(), is(user1.getId()));
        assertThat(allTelcoUsers.findBy(password, password).getId(), is(user2.getId()));
        assertNull(allTelcoUsers.findBy(password, userName));
    }

    @After
    public void destroy() {
        allTelcoUsers.removeAll();
    }
}
