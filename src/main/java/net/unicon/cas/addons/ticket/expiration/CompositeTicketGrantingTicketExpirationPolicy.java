package net.unicon.cas.addons.ticket.expiration;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.TicketState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This is a composite ticket expiration policy able to use
 * an {@link ExpirationPolicy} that is mapped to a {@link TicketExpirationPolicyEvaluator}.
 * The {@link TicketExpirationPolicyEvaluator} instance determines whether the policy is appropriate
 * for handling the given {@link TicketState}. If so, then its linked-to
 * {@link ExpirationPolicy} will be used.
 * 
 * <p>If none of the expiration policies satisfy the request, then the default policy will be used
 * that is set by {@link #setDefaultExpirationPolicy(ExpirationPolicy)}. If the default is not
 * explicitly set, the handling of the policy is delegated to {@link AlwaysExpiresExpirationPolicy}
 * which considers all tickets as expired.
 * @see IpAddressBasedExpirationPolicyEvalutor
 * @author Misagh Moayyed
 * @since 1.9
 */
public class CompositeTicketGrantingTicketExpirationPolicy implements ExpirationPolicy {
       
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final long serialVersionUID = 3021175146846182330L;

    private final Map<TicketExpirationPolicyEvaluator, ExpirationPolicy> evaluators;
    
    private ExpirationPolicy defaultExpirationPolicy = new AlwaysExpiresExpirationPolicy();
    
    /**
     * Init the policy with the given map of evaluators.
     * @param evaluators map of evaluators that are linked to expiration policies. 
     */
    public CompositeTicketGrantingTicketExpirationPolicy(final Map<TicketExpirationPolicyEvaluator, ExpirationPolicy> evaluators) {
        this.evaluators = evaluators;
    }
    
    public final void setDefaultExpirationPolicy(final ExpirationPolicy def) {
        this.defaultExpirationPolicy = def;
    }
    
    @Override
    public final boolean isExpired(final TicketState state) {
        final Set<TicketExpirationPolicyEvaluator> keys = this.evaluators.keySet();
        
        for (final TicketExpirationPolicyEvaluator eval : keys) {
            
            if (eval.doesSatisfyTicketExpirationPolicy(getRequest(), state)) {
                logger.debug("Expiration policy evaluator [{}] satisfies this request", eval);
                
                final ExpirationPolicy policy = this.evaluators.get(eval);
                final boolean expired = policy.isExpired(state);
          
                logger.debug("Delegated to mapped expiration policy [{}], which indicates the ticket has "
                        + (expired ? "" : "not ") + "expired", policy);
                
                return expired;
            }
        }
        
        logger.debug("Delegated to default expiration policy [{}]", this.defaultExpirationPolicy);
        return this.defaultExpirationPolicy.isExpired(state);
    }
    
    private HttpServletRequest getRequest() {
        final ServletRequestAttributes attrs = (ServletRequestAttributes) 
                RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest();
    }

    private class AlwaysExpiresExpirationPolicy implements ExpirationPolicy {

        private static final long serialVersionUID = -5505383542873474014L;

        @Override
        public boolean isExpired(final TicketState ticketState) {
            CompositeTicketGrantingTicketExpirationPolicy.this
                .logger.debug("Ticket is ALWAYS considered expired.");
            return true;
        }
        
    }
}
