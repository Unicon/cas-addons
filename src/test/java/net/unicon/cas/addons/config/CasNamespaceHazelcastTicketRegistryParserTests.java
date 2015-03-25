package net.unicon.cas.addons.config;

import net.unicon.cas.addons.info.events.listeners.RedisStatsRecorderForServiceTicketValidatedEvents;
import net.unicon.cas.addons.info.events.listeners.RedisStatsRecorderForSsoSessionEstablishedEvents;
import net.unicon.cas.addons.ticket.registry.HazelcastTicketRegistry;
import org.jasig.cas.authentication.AuthenticationManager;
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
public class CasNamespaceHazelcastTicketRegistryParserTests {

    @Autowired
    ApplicationContext applicationContext;

    private static final String TICKET_REGISTRY_BEAN_NAME = "customTicketRegistryId";

    @Test
    public void hazelcastTicketRegistryBeanDefinitionCorrectlyParsed() {
        assertTrue(applicationContext.containsBean(TICKET_REGISTRY_BEAN_NAME));
        assertTrue(applicationContext.getBeansOfType(HazelcastTicketRegistry.class).size() == 1);
    }
}
