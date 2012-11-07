package net.unicon.cas.addons.ticket;

/**
 * Friendly specialization of <code>UnsupportedOperationException</code> to indicate <code>TicketRegistry#getTickets</code>
 * is not supported by underlying implementation.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
public class BulkRetrievalOfTicketsNotSupportedException extends UnsupportedOperationException {

	public BulkRetrievalOfTicketsNotSupportedException(String s) {
		super(s);
	}

	public BulkRetrievalOfTicketsNotSupportedException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
