package net.unicon.cas.addons.ticket.registry;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.registry.AbstractDistributedTicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Hazelcast-based implementation of a distributed <code>TicketRegistry</code>
 *
 * This implementation just wraps the Hazelcast's <code>IMap</code> which is an extension of the
 * standard Java's <code>ConcurrentMap</code>. The heavy lifting of distributed data partitioning,
 * network cluster discovery and join, data replication, etc. is done by Hazelcast's Map implementation.
 *
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.9
 */
public class HazelcastTicketRegistry extends AbstractDistributedTicketRegistry {

    private final IMap<String, Ticket> ticketsMap;

    private static final Logger logger = LoggerFactory.getLogger(HazelcastTicketRegistry.class);

    /**
     *  @param hz an instance of <code>HazelcastInstance</code> configured on each node
     *  from which it creates an instance of a cluster-aware Map - a main data structure
     *  where tickets are stored and seamlessly replicated across nodes in the cluster by Hazelcast.
     */
    public HazelcastTicketRegistry(final HazelcastInstance hz) {
        logger.debug("Constructing TicketRegistry from HazelcastInstance: {}", hz);
        this.ticketsMap = hz.getMap("tickets");
    }

    @Override
    protected void updateTicket(Ticket ticket) {
        this.ticketsMap.set(ticket.getId(), ticket);
    }

    @Override
    public void addTicket(Ticket ticket) {
        this.ticketsMap.set(ticket.getId(), ticket);
    }

    @Override
    public Ticket getTicket(String ticketId) {
        final Ticket t = this.ticketsMap.get(ticketId);
        return t == null ? null : getProxiedTicketInstance(t);
    }

    @Override
    public boolean deleteTicket(String ticketId) {
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
}
