package net.unicon.cas.addons.config;

import net.unicon.cas.addons.info.events.listeners.RedisStatsRecorderForServiceTicketValidatedEvents;
import net.unicon.cas.addons.info.events.listeners.RedisStatsRecorderForSsoSessionEstablishedEvents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CasNamespaceEventsRedisRecorderBeanDefinitionParserTests {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void eventsRedisRecorderBeanDefinitionCorrectlyParsed() {
        assertTrue(applicationContext.getBeansOfType(RedisStatsRecorderForSsoSessionEstablishedEvents.class).size() == 1);
        assertTrue(applicationContext.getBeansOfType(RedisStatsRecorderForServiceTicketValidatedEvents.class).size() == 1);
    }
}
