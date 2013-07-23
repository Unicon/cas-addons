package net.unicon.cas.addons.ticket.internal;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.util.DefaultLongNumericGenerator;
import org.jasig.cas.util.DefaultRandomStringGenerator;
import org.jasig.cas.util.NumericGenerator;
import org.jasig.cas.util.RandomStringGenerator;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * An implementation of {@link UniqueTicketIdGenerator} that is able auto-configure
 * the suffix based on the underlying host name, if one isn't specified.
 *
 * <p>In order to assist with multi-node deployments, in scenarios where CAS configuration
 * and specially <code>cas.properties</code> file is externalized, it would be ideal to simply just have one set
 * of configuration files for all nodes, such that there would for instance be one <code>cas.properties</code> file
 * for all nodes. This would remove the need to copy/sync config files over across nodes, again in a
 * situation where they are externalized.
 * <p> The current drawback is that in keeping only one <code>cas.properties</code> file, we'd lose the ability
 * to define unique <code>host.name</code> property values for each node as the suffix, which would assist with troubleshooting
 * and diagnostics. To provide a remedy, this ticket generator is able to retrieve the host.name value directly from
 * the actual node name, rather than relying on the configuration, only if one isn't specified in
 * the <code>cas.properties</code> file. </p>
 * @author Misagh Moayyed (mmoayyed@unicon.net)
 * @since 1.7
 */
public final class HostNameBasedUniqueTicketIdGenerator implements UniqueTicketIdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostNameBasedUniqueTicketIdGenerator.class);

    /** The numeric generator to generate the static part of the id. */
    private final NumericGenerator numericGenerator;

    /** The RandomStringGenerator to generate the secure random part of the id. */
    private final RandomStringGenerator randomStringGenerator;

    private String suffix;

    public HostNameBasedUniqueTicketIdGenerator() {
        this(null);
    }

    public HostNameBasedUniqueTicketIdGenerator(final int maxLength) {
        this(maxLength, null);
    }

    public HostNameBasedUniqueTicketIdGenerator(final String suffix) {
        this.numericGenerator = new DefaultLongNumericGenerator(1);
        this.randomStringGenerator = new DefaultRandomStringGenerator();
        prepareTicketSuffix(suffix);
    }

    public HostNameBasedUniqueTicketIdGenerator(final int maxLength, final String suffix) {
        this.numericGenerator = new DefaultLongNumericGenerator(1);
        this.randomStringGenerator = new DefaultRandomStringGenerator(maxLength);
        prepareTicketSuffix(suffix);
    }

    public String getNewTicketId(final String prefix) {
        final String number = this.numericGenerator.getNextNumberAsString();
        final StringBuilder buffer = new StringBuilder(prefix.length() + 2
                + (this.suffix != null ? this.suffix.length() : 0) + this.randomStringGenerator.getMaxLength()
                + number.length());

        buffer.append(prefix);
        buffer.append("-");
        buffer.append(number);
        buffer.append("-");
        buffer.append(this.randomStringGenerator.getNewString());

        if (this.suffix != null) {
            buffer.append(this.suffix);
        }

        return buffer.toString();
    }

    private void prepareTicketSuffix(final String suffix) {
        this.suffix = null;

        if (StringUtils.isEmpty(suffix)) {

            try {
                final String hostName = InetAddress.getLocalHost().getCanonicalHostName();
                if (hostName.indexOf(".") > 0) {
                    this.suffix = hostName.substring(0, hostName.indexOf("."));
                } else {
                    this.suffix = hostName;
                }

                LOGGER.debug("Automatically determined ticket suffix to be [{}].", this.suffix);
            } catch (final UnknownHostException e) {
                LOGGER.debug("Host name could not be determined automatically for the ticket suffix.", e);
            }
        } else {
            this.suffix = "-" + suffix;
        }
    }
}
