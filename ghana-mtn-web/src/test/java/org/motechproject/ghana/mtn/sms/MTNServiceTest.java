package org.motechproject.ghana.mtn.sms;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DefaultDateTimeSource;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MTNServiceTest {

    MTNService service;
    @Mock
    SmsService smsService;

    @Before
    public void setUp() {
        initMocks(this);
        service = new MTNService(smsService);
    }

    @Test
    public void shouldSendSMSToTheGateWay() {
        String mobileNum = "987654321";
        String message = "Test Message";

        service.send(mobileNum, message, null);
        verify(smsService).sendSMS(mobileNum, message);
    }

    @Test
    public void shouldVerifyTheDeliveryTime() {
        String mobileNum = "987654321";
        String message = "Test Message";
        Time deliveryTime = new Time(10, 30);

        mockCurrentDate(new DateTime(2011, 12, 21, 15, 30, 0, 0));
        service.send(mobileNum, message, deliveryTime);
        DateTime deliveryDateTime = DateUtil.now().withHourOfDay(deliveryTime.getHour()).withMinuteOfHour(deliveryTime.getMinute()).plusDays(1);
        verify(smsService).sendSMS(mobileNum, message, deliveryDateTime);

        mockCurrentDate(new DateTime(2011, 12, 21, 9, 30, 0, 0));
        service.send(mobileNum, message, deliveryTime);
        deliveryDateTime = DateUtil.now().withHourOfDay(deliveryTime.getHour()).withMinuteOfHour(deliveryTime.getMinute());
        verify(smsService).sendSMS(mobileNum, message, deliveryDateTime);
    }

    @After
    public void tearDown() {
        DateTimeSourceUtil.SourceInstance = new DefaultDateTimeSource();
    }

    private void mockCurrentDate(final DateTime currentDate) {
        DateTimeSourceUtil.SourceInstance = new DateTimeSource() {

            @Override
            public DateTimeZone timeZone() {
                return currentDate.getZone();
            }

            @Override
            public DateTime now() {
                return currentDate;
            }

            @Override
            public LocalDate today() {
                return currentDate.toLocalDate();
            }
        };
    }
}
