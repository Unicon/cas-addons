package net.unicon.cas.addons.info.events.listeners;

import net.unicon.cas.addons.info.events.CasServiceTicketValidatedEvent;
import net.unicon.cas.addons.info.events.CasSsoSessionEstablishedEvent;
import net.unicon.cas.addons.support.ThreadSafe;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * An event listener for <code>CasServiceTicketValidatedEvent</code>s that records daily counts for each event by atomically incrementing a value in
 * Redis server under a <i>cas:st-validated:yyyy-MM-dd</i> key.
 * <p/>
 * This class assumes a live Redis server running and depends on an instance of <code>org.springframework.data.redis.connection.jedis.JedisConnectionFactory</code>
 * of spring data redis module, from which it constructs an instance of <code>org.springframework.data.redis.core.StringRedisTemplate</code>.
 * <p/>
 * At runtime if a Redis server becomes unavailable or any other exceptions are thrown during server access, a WARN level log message logs the exception and execution path
 * of CAS server continues.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.9
 */
@ThreadSafe
public class RedisStatsRecorderForServiceTicketValidatedEvents implements
        ApplicationListener<CasServiceTicketValidatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RedisStatsRecorderForServiceTicketValidatedEvents.class);

    private StringRedisTemplate redisTemplate;

    public RedisStatsRecorderForServiceTicketValidatedEvents(JedisConnectionFactory connectionFactory) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
    }

    @Override
    public void onApplicationEvent(CasServiceTicketValidatedEvent event) {
        final String today = DateTime.now().toString("yyyy-MM-dd");
        try {
            logger.debug("Incrementing value for key 'cas:st-validated:{}' in Redis server...", today);
            this.redisTemplate.opsForValue().increment("cas:st-validated:" + today, 1L);
        }
        catch (Throwable e) {
            logger.warn("Unable to increment value for key 'cas:st-validated:'" + today + " in Redis. Caught the following exception: ", e);
        }
    }
}
