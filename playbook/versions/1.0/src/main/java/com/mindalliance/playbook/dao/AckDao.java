package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.NAck;
import com.mindalliance.playbook.model.Play;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

/**
 * Request acknowledgement accessor.
 */
@Secured( "ROLE_USER" )
@Transactional
public interface AckDao extends GenericDao<ConfirmationAck,Long> {

    /**
     * Add a matching step in a play in this account from a collaboration step in another account.
     * 
     *
     *
     * @param existingPlay the local play
     * @param request
     * @return the existing play
     */
    Play saveInPlay( Play existingPlay, ConfirmationReq request );

    /**
     * Add a matching step in a new play in this account from a collaboration step in another account.
     *
     * @param newPlay the new local play name. If null, a default title will be used.
     * @param request
     * @return the existing play
     */
    Play saveInPlay( String newPlay, ConfirmationReq request );

    /**
     * Add a refusal notice.
     * @param nAck details of the refusal
     * 
     */
    void refuse( NAck nAck );
}
