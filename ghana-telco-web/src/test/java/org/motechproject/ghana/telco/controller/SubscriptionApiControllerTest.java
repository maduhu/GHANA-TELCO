package org.motechproject.ghana.telco.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.ghana.telco.parser.RegisterProgramMessageParser;
import org.motechproject.ghana.telco.repository.AllProgramTypes;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.motechproject.ghana.telco.service.SubscriptionService;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionApiControllerTest {
    @InjectMocks
    private SubscriptionApiController controller = new SubscriptionApiController();
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private RegisterProgramMessageParser registerProgramParser;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private SMSHandler handler;
    @Mock
    private VelocityEngine velocityEngine;
    @Mock
    private Template template;
    @Mock
    private AllProgramTypes allProgramTypes;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRegisterAndSendSuccessJsonResponse() throws JSONException {
        RegisterProgramSMS registerSms = new RegisterProgramSMS("message", null);
        String subscriptionNo = "1234566889";
        doReturn(registerSms).when(registerProgramParser).parse("P 12", subscriptionNo);
        String jsonResponse = controller.registerJson(subscriptionNo, "P", "12");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1234566889\",\"status\":\"Success\"}"));
    }

    @Test
    public void shouldSendErrorJsonResponseWhenTheStartTimeIsInvalid() throws JSONException {
        String subscriptionNo = "1234566889";
        doReturn(null).when(registerProgramParser).parse("C 13", subscriptionNo);
        String jsonResponse = controller.registerJson(subscriptionNo, "P", "12");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1234566889\",\"reason\":\"Start Time is not valid\",\"status\":\"Failed\"}"));
    }

    @Test
    public void shouldRegisterAndSetSuccessResponseForXmlTemplate() throws JSONException {
        RegisterProgramSMS registerSms = new RegisterProgramSMS("message", null);
        String subscriptionNo = "1234566889";
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        doReturn(registerSms).when(registerProgramParser).parse("P 12", subscriptionNo);
        controller.registerXml(subscriptionNo, "P", "12");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Success"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is(subscriptionNo));
    }

    @Test
    public void shouldSendErrorResponseForXmlTemplateWhenTheStartTimeIsInvalid() throws JSONException {
        String subscriptionNo = "1234566889";
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        doReturn(null).when(registerProgramParser).parse("C 13", subscriptionNo);
        controller.registerXml(subscriptionNo, "P", "12");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Failed"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is(subscriptionNo));
        assertThat((String) contextCaptor.getValue().get("reason"), is("Start Time is not valid"));
    }

    @Test
    public void shouldSearchAndSendSuccessJsonResponse() throws JSONException {
        List<Subscription> subscriptions = Arrays.asList(new Subscription[]{
                new Subscription(new Subscriber("1"), new ProgramType().setProgramKey("Pregnancy").setShortCodes(Arrays.asList("P")), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime()),
        });
        String subscriptionNo = "1";
        doReturn(subscriptions).when(allSubscriptions).getAll();
        String jsonResponse = controller.searchJson(subscriptionNo, "P");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1\",\"status\":\"Success\",\"program\":\"Pregnancy\",\"userStatus\":\"ACTIVE\"}"));
    }

    @Test
    public void shouldSendErrorJsonResponseWhenTheSubscriberIsNotRegistered() throws JSONException {
        String subscriptionNo = "1234566889";
        doReturn(new ArrayList()).when(allSubscriptions).getAll();
        String jsonResponse = controller.searchJson(subscriptionNo, "P");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1234566889\",\"reason\":\"Not registered\",\"status\":\"Failed\"}"));
    }

    @Test
    public void shouldSearchAndSetSuccessResponseForXmlTemplate() throws JSONException {
        List<Subscription> subscriptions = Arrays.asList(new Subscription[]{
                new Subscription(new Subscriber("1"), new ProgramType().setProgramKey("Pregnancy").setShortCodes(Arrays.asList("P")), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime()),
        });
        String subscriptionNo = "1";
        doReturn(subscriptions).when(allSubscriptions).getAll();
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        controller.searchXml(subscriptionNo, "P");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Success"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is(subscriptionNo));
        assertThat((String) contextCaptor.getValue().get("program"), is("Pregnancy"));
        assertThat((String) contextCaptor.getValue().get("userStatus"), is("ACTIVE"));
    }

    @Test
    public void shouldSendErrorResponseForXmlTemplateWhenTheSubscriberIsNotRegistered() throws JSONException {
        doReturn(new ArrayList()).when(allSubscriptions).getAll();
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        controller.searchXml("1", "P");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Failed"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is("1"));
        assertThat((String) contextCaptor.getValue().get("reason"), is("Not registered"));
    }

    @Test
    public void shouldUnRegisterAndSendSuccessJsonResponse() throws JSONException {
        Subscription subscription = new Subscription(new Subscriber("1"), new ProgramType().setProgramKey("Pregnancy").setShortCodes(Arrays.asList("P")), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime());
        String subscriptionNo = "1";
        doReturn(subscription).when(allSubscriptions).findActiveSubscriptionFor(subscriptionNo, "Pregnancy");
        doReturn(new ProgramType().setProgramKey("Pregnancy")).when(allProgramTypes).findByCampaignShortCode("P");
        String jsonResponse = controller.unRegisterJson(subscriptionNo, "P");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1\",\"status\":\"Success\"}"));
    }

    @Test
    public void shouldSendErrorJsonResponseWhenTheSubscriberIsNotRegisteredAndTryToUnRegister() throws JSONException {
        String subscriptionNo = "1234566889";
        doReturn(new ProgramType().setProgramKey("Pregnancy")).when(allProgramTypes).findByCampaignShortCode("P");
        String jsonResponse = controller.unRegisterJson(subscriptionNo, "P");
        assertThat(jsonResponse, is("{\"phoneNumber\":\"1234566889\",\"reason\":\"Not registered\",\"status\":\"Failed\"}"));
    }

    @Test
    public void shouldUnRegisterAndSetSuccessResponseForXmlTemplate() throws JSONException {
        Subscription subscription = new Subscription(new Subscriber("1"), new ProgramType().setProgramKey("Pregnancy").setShortCodes(Arrays.asList("P")), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime());
        String subscriptionNo = "1";
        doReturn(subscription).when(allSubscriptions).findActiveSubscriptionFor(subscriptionNo, "Pregnancy");
        doReturn(new ProgramType().setProgramKey("Pregnancy")).when(allProgramTypes).findByCampaignShortCode("P");
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        controller.unRegisterXml(subscriptionNo, "P");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Success"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is(subscriptionNo));
    }

    @Test
    public void shouldSendErrorResponseForXmlTemplateWhenTheSubscriberIsNotRegisteredAndTryToUnRegister() throws JSONException {
        String subscriptionNo = "1";
        doReturn(new ProgramType().setProgramKey("Pregnancy")).when(allProgramTypes).findByCampaignShortCode("P");
        doReturn(template).when(velocityEngine).getTemplate("/templates/responses/xmlResponse.vm");
        controller.unRegisterXml(subscriptionNo, "P");
        ArgumentCaptor<VelocityContext> contextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        ArgumentCaptor<Writer> writer = ArgumentCaptor.forClass(Writer.class);
        verify(template).merge(contextCaptor.capture(), writer.capture());
        assertThat((String) contextCaptor.getValue().get("status"), is("Failed"));
        assertThat((String) contextCaptor.getValue().get("reason"), is("Not registered"));
        assertThat((String) contextCaptor.getValue().get("phoneNumber"), is(subscriptionNo));
    }
}
