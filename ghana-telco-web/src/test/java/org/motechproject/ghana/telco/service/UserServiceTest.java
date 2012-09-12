package org.motechproject.ghana.telco.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.TelcoUser;
import org.motechproject.ghana.telco.repository.AllTelcoUsers;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.telco.utils.Encrypt.encrypt;

public class UserServiceTest {

    @Mock
    AllTelcoUsers mockAllTelcoUsers;
    UserService userService;

    @Before
    public void beforeSetup() {
        initMocks(this);
        userService = new UserService(mockAllTelcoUsers);
    }

    @Test
    public void shouldCreateTeloUser() {

        userService.register("uname", "paswed", asList("r1", "r2"));
        ArgumentCaptor<TelcoUser> telcoUserCaptor = ArgumentCaptor.forClass(TelcoUser.class);
        verify(mockAllTelcoUsers).add(telcoUserCaptor.capture());
        assertThat(telcoUserCaptor.getValue().getUserName(), is(encrypt("uname")));
        assertThat(telcoUserCaptor.getValue().getPassword(), is(encrypt("paswed")));
        assertThat(telcoUserCaptor.getValue().getRoles(), is(asList("r1", "r2")));
    }

    @Test
    public void shouldFindAllUserByNameAndPassword() {

        userService.findBy("uname", "password");
        verify(mockAllTelcoUsers).findBy(encrypt("uname"), encrypt("password"));
    }

    @Test
    public void shouldResetThePassword() {
        String username = "username";
        String oldpassword = "oldpassword";
        String newpassword = "newpassword";
        TelcoUser user = new TelcoUser();

        doReturn(user).when(mockAllTelcoUsers).findBy(encrypt(username), encrypt(oldpassword));
        ArgumentCaptor<TelcoUser> userCaptor = ArgumentCaptor.forClass(TelcoUser.class);

        userService.resetPassword(username, oldpassword, newpassword);
        verify(mockAllTelcoUsers).update(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword(), is(encrypt(newpassword)));
    }
}
