package net.unicon.cas.addons.ticket.registry;

import org.jasig.cas.authentication.ImmutableAuthentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HazelcastTicketRegistryTests {

    @Autowired
    HazelcastTicketRegistry hzTicketRegistry1;

    @Autowired
    HazelcastTicketRegistry hzTicketRegistry2;

    @Test
    public void basicOperationsAndClustering() throws Exception {
        this.hzTicketRegistry1.addTicket(newTestTgt());
        assertNotNull(this.hzTicketRegistry1.getTicket("TGT-TEST"));
        assertTrue(this.hzTicketRegistry2.deleteTicket("TGT-TEST"));
        assertFalse(this.hzTicketRegistry1.deleteTicket("TGT-TEST"));
        assertNull(this.hzTicketRegistry1.getTicket("TGT-TEST"));
        assertNull(this.hzTicketRegistry2.getTicket("TGT-TEST"));

        ServiceTicket st = newTestTgt().grantServiceTicket("ST-TEST", getService(), new NeverExpiresExpirationPolicy(), false);
        this.hzTicketRegistry2.addTicket(st);
        assertNotNull(this.hzTicketRegistry1.getTicket("ST-TEST"));
        assertNotNull(this.hzTicketRegistry2.getTicket("ST-TEST"));
        this.hzTicketRegistry1.deleteTicket("ST-TEST");
        assertNull(this.hzTicketRegistry1.getTicket("ST-TEST"));
        assertNull(this.hzTicketRegistry2.getTicket("ST-TEST"));
    }

    private TicketGrantingTicket newTestTgt() {
        return new TicketGrantingTicketImpl("TGT-TEST",
                new ImmutableAuthentication(new SimplePrincipal("test")),
                new NeverExpiresExpirationPolicy());
    }

    private Service getService() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("service", "test");
        return SimpleWebApplicationServiceImpl.createServiceFrom(request);
    }
}
