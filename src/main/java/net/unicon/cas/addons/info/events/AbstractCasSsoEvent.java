package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.Authentication;
import org.springframework.context.ApplicationEvent;

/**
 * Base Spring <code>ApplicationEvent</code> representing a abstract single sign on action executed within running CAS server.
 * <p/>
 * This event encapsulates {@link Authentication} that is associated with an SSO action executed in a CAS server and an SSO session
 * token in the form of ticket granting ticket id.
 * <p/>
 * More concrete events are expected to subclass this abstract type.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public abstract class AbstractCasSsoEvent extends ApplicationEvent {

	private final Authentication authentication;

	private final String ticketGrantingTicketId;

	public AbstractCasSsoEvent(Object source, Authentication authentication, String ticketGrantingTicketId) {
		super(source);
		this.authentication = authentication;
		this.ticketGrantingTicketId = ticketGrantingTicketId;
	}

	public Authentication getAuthentication() {
		return this.authentication;
	}

	public String getTicketGrantingTicketId() {
		return this.ticketGrantingTicketId;
	}

	@Override
	public String toString() {
		return "AbstractCasSsoEvent{" +
				"authentication=" + authentication +
				", ticketGrantingTicketId='" + ticketGrantingTicketId + '\'' +
				'}';
	}
}
