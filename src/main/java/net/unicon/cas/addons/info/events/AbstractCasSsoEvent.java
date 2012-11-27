package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.Authentication;
import org.springframework.context.ApplicationEvent;

/**
 * Base Spring <code>ApplicationEvent</code> representing a abstract single sign on action executed within running CAS server.
 * <p/>
 * This event encapsulates {@link Authentication} that is associated with an SSO action executed in a CAS server.
 * <p/>
 * More concrete events are expected to subclass this abstract type.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public abstract class AbstractCasSsoEvent extends ApplicationEvent {

	private final Authentication authentication;

	public AbstractCasSsoEvent(Object source, Authentication authentication) {
		super(source);
		this.authentication = authentication;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" +
				"authentication=" + authentication +
				'}';
	}
}
