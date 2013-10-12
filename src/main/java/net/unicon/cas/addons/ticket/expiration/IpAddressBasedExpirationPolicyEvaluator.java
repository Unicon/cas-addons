package net.unicon.cas.addons.ticket.expiration;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.conn.util.InetAddressUtils;
import org.jasig.cas.ticket.TicketState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of the {@link TicketExpirationPolicyEvaluator} that is able to
 * determine whether the remote address of the incoming request matches a particular IP pattern.
 * The pattern may be specified as regular expression that is compiled and run against the remote address.
 * The retrieval of the remote IP address may be provided via IPv6 or IPv4 syntax.
 * 
 * <p><strong>NOTE:</strong> If you prefer to configure the pattern by IPv4 syntax only,
 * add the <code>-Djava.net.preferIPv4Stack=true</code> flag to your <code>JAVA_OPTS</code>
 * environment variable prior to restarting the container.
 * @author Misagh Moayyed
 * @since 1.9
 * @see CompositeTicketGrantingTicketExpirationPolicy
 */
public class IpAddressBasedExpirationPolicyEvaluator implements TicketExpirationPolicyEvaluator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
         
    private Pattern ipAddressPattern;
    
    public IpAddressBasedExpirationPolicyEvaluator(final String ipPattern) {
        this.ipAddressPattern = Pattern.compile(ipPattern);
    }
    
    @Override
    public boolean doesSatisfyTicketExpirationPolicy(final HttpServletRequest request, final TicketState state) {
        final String currentIp = request.getRemoteAddr();
        if (InetAddressUtils.isIPv6Address(currentIp)) {
            logger.debug("Remote IP [{}] is a valid standard (non-compressed) IPv6 address", currentIp);
        } else if (InetAddressUtils.isIPv6HexCompressedAddress(currentIp)) {
            logger.debug("Remote IP [{}] is a valid IPv6 address (including compressed).", currentIp);
        } else if (InetAddressUtils.isIPv6StdAddress(currentIp)) {
            logger.debug("Remote IP [{}] is a valid compressed IPv6 address", currentIp);
        } else if (InetAddressUtils.isIPv4Address(currentIp)) {
            logger.debug("Remote IP [{}] is a valid IPv4 address ", currentIp);
        } else {
            logger.debug("Remote IP [{}] does not match a known IP syntax", currentIp);
        }
        
        return this.ipAddressPattern.matcher(currentIp).find();
    }

}
