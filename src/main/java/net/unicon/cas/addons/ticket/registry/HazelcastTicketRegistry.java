package net.unicon.cas.addons.ticket.registry;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.AbstractDistributedTicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Hazelcast-based implementation of a distributed <code>TicketRegistry</code>
 * <p/>
 * This implementation just wraps the Hazelcast's <code>IMap</code> which is an extension of the
 * standard Java's <code>ConcurrentMap</code>. The heavy lifting of distributed data partitioning,
 * network cluster discovery and join, data replication, etc. is done by Hazelcast's Map implementation.
 * <p/>
 * The logic for tgt and st timeout settings and dynamically determining the ticket type is borrowed
 * from CAS' <code>MemCacheTicketRegistry</code>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.9
 */
public class HazelcastTicketRegistry extends AbstractDistributedTicketRegistry {

    private final IMap<String, Ticket> ticketsMap;

    private static final Logger logger = LoggerFactory.getLogger(HazelcastTicketRegistry.class);

    private final long serviceTicketTimeoutInSeconds;

    private final long ticketGrantingTicketTimeoutInSeconds;

    /**
     * @param hz an instance of <code>HazelcastInstance</code> configured on each node
     * @param ticketGrantingTicketTimeoutInSeconds for TGT Hazelcast Map entries TTL
     * @param serviceTicketTimeoutInSeconds for ST Hazelcast Map entries TTL
     * from which it creates an instance of a cluster-aware Map - a main data structure
     * where tickets are stored and seamlessly replicated across nodes in the cluster by Hazelcast.
     */
    public HazelcastTicketRegistry(final HazelcastInstance hz, long ticketGrantingTicketTimeoutInSeconds, long serviceTicketTimeoutInSeconds) {
        logger.info("Constructing TicketRegistry from HazelcastInstance: {}", hz);
        logger.info("TicketGrantingTicket timeout is used for Hazelcast TGT entries (in seconds): [{}]", ticketGrantingTicketTimeoutInSeconds);
        logger.info("ServiceTicket timeout is used for Hazelcast ST entries (in seconds): [{}]", serviceTicketTimeoutInSeconds);
        this.ticketsMap = hz.getMap("tickets");
        this.ticketGrantingTicketTimeoutInSeconds = ticketGrantingTicketTimeoutInSeconds;
        this.serviceTicketTimeoutInSeconds = serviceTicketTimeoutInSeconds;
    }

    @Override
    protected void updateTicket(Ticket ticket) {
        addTicket(ticket);
    }

    @Override
    public void addTicket(Ticket ticket) {
        final long ticketTimeout = getTimeout(ticket);
        logger.debug("Adding Ticket[{}] to the Hazelcast IMap with a TTL of [{}] seconds", ticket.getId(), ticketTimeout);
        this.ticketsMap.set(ticket.getId(), ticket, getTimeout(ticket), TimeUnit.SECONDS);
    }

    @Override
    public Ticket getTicket(String ticketId) {
        final Ticket t = this.ticketsMap.get(ticketId);
        logger.debug("Returning Ticket[{}] from the Hazelcast IMap", t == null ? "null" : t.getId());
        return t == null ? null : getProxiedTicketInstance(t);
    }

    @Override
    public boolean deleteTicket(String ticketId) {
        logger.debug("Removing Ticket[{}] from the Hazelcast IMap", ticketId);
        return this.ticketsMap.remove(ticketId) != null;
    }

    @Override
    public Collection<Ticket> getTickets() {
        return this.ticketsMap.values();
    }

    @Override
    protected boolean needsCallback() {
        return false;
    }

    private long getTimeout(final Ticket t) {
        if (t instanceof TicketGrantingTicket) {
            return this.ticketGrantingTicketTimeoutInSeconds;
        }
        else if (t instanceof ServiceTicket) {
            return this.serviceTicketTimeoutInSeconds;
        }
        throw new IllegalArgumentException("Invalid ticket type");
    }
}