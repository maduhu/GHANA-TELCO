package org.motechproject.ghana.mtn.repository;

import org.junit.Test;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllMessagesTest extends BaseSpringTestContext {

    @Autowired
    AllMessages allMessages;

    @Test
    public void shouldFindMessagesByKey() {

        Message message = new Message("key1", "value1");
        Message message2 = new Message("key2", "value2");
        Message message3 = new Message("key3", "value3");
        Message message4 = new Message("key4", "value4");

        addAndMarkForDeletion(allMessages, message);
        addAndMarkForDeletion(allMessages, message2);
        addAndMarkForDeletion(allMessages, message3);
        addAndMarkForDeletion(allMessages, message4);

        assertEquals("value1", allMessages.findBy("key1").getContent());
        assertEquals("value2", allMessages.findBy("key2").getContent());
        assertEquals("value3", allMessages.findBy("key3").getContent());
        assertEquals("value4", allMessages.findBy("key4").getContent());
        
        assertEquals(null, allMessages.findBy("key5"));
        assertEquals(null, allMessages.findBy(null));
    }
}
