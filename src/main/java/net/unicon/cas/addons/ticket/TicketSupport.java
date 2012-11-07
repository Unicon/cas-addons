package net.unicon.cas.addons.ticket;

import org.jasig.cas.ticket.TicketGrantingTicket;

import java.util.List;

/**
 * Helper strategy API to ease manipulating CAS' <code>Ticket</code>'s related APIs and add convenience methods on top of them.
 * <p/>
 * <p>Note: this API is only intended to be called by CAS server code e.g. any custom CAS server overlay extension, etc.</p>
 * <p/>
 * <p>Concurrency semantics: implementations must be thread safe.</p>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
public interface TicketSupport {

	/**
	 * Convenience method that deletes a provided TGT from the underlying ticket store
	 * if that TGT is determined to be expired. If the provided ticket does not exist in a ticket store,
	 * or not a <code>TicketGrantingTicket</code>, simply returns
	 *
	 * @param ticketGrantingTicketId for which to delete the underlying ticket
	 *                               <strong>NOTE TO IMPLEMENTERS:</strong> this method should never throw any exceptions - runtime or otherwise
	 */
	void deleteExpiredTicketGrantingTicket(String ticketGrantingTicketId);

	/**
	 * Convenience method to determine if a TicketGrantingTicket represented by a given id exists in a underlying ticket store
	 * AND it is expired
	 *
	 * @param ticketGrantingTicketId representing TicketGrantingTicket
	 * @return true if both predicate conditions satisfied, false otherwise
	 *         <strong>NOTE TO IMPLEMENTERS:</strong> this method should never throw any exceptions - runtime or otherwise
	 */
	boolean ticketGrantingTicketExistsAndExpired(String ticketGrantingTicketId);

	/**
	 * Convenience method to return a collection of active (non-expired at the time of call) from CAS' underlying ticket store.
	 *
	 * @return a list of non-expired TGTs OR an empty list and NEVER <b>null</b>
	 *         <strong>NOTE TO IMPLEMENTERS:</strong> this method should never throw any exceptions other than
	 *         <code>BulkRetrievalOfTicketsNotSupportedException</code>
	 * @throws BulkRetrievalOfTicketsNotSupportedException
	 */
	List<TicketGrantingTicket> getNonExpiredTicketGrantingTickets() throws BulkRetrievalOfTicketsNotSupportedException;
}
