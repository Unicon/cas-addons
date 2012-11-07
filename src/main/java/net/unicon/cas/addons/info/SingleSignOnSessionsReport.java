package net.unicon.cas.addons.info;

import net.unicon.cas.addons.ticket.BulkRetrievalOfTicketsNotSupportedException;

import java.util.Collection;
import java.util.Map;

/**
 * An API to provide an aggregate view of CAS' <i>live</i> SSO sessions at run time i.e. a collection of
 * un-expired <code>TicketGrantingTicket</code>'s metadata and their associated <code>Authentication</code> data.
 * <p/>
 * Note that this view is just a snapshot of active sessions at the time of call, and might not represent a true
 * view of unexpired sessions by the time it is presented to clients.
 * <p/>
 * This API returns an un-typed view of this data in a form of <code>Map<String, Object</code> which adds a flexibility
 * to clients to render it however they choose. As a convenience, this interface also exposes an Enum of the map keys
 * it expects implementors to use.
 * <p/>
 * Note: this API is only intended to be called by CAS server code e.g. any custom CAS server overlay extension, etc.
 * <p/>
 * Concurrency semantics: implementations must be thread safe.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
public interface SingleSignOnSessionsReport {


	enum SsoSessionAttributeKeys {

		AUTHENTICATED_PRINCIPAL("authenticated_principal"),

		AUTHENTICATION_DATE("authentication_date"),

		TGT_ID("tgt_id"),

		NUMBER_OF_USES("number_of_uses");

		private String attributeKey;

		private SsoSessionAttributeKeys(String attributeKey) {
			this.attributeKey = attributeKey;
		}

		@Override
		public String toString() {
			return this.attributeKey;
		}
	}

	/**
	 * Get a collection of active (unexpired) CAS' SSO sessions (with their associated authentication and metadata).
	 * <p/>
	 * If there are no active SSO session, return an empty Collection and never return <strong>null</strong>
	 *
	 * @return a collection of SSO sessions (represented by Map of its attributes) OR and empty collection if there are
	 *         no active SSO sessions
	 */
	Collection<Map<String, Object>> getActiveSsoSessions() throws BulkRetrievalOfTicketsNotSupportedException;

}
