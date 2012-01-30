package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
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
     * Create a new play as a result of agreeing to a request.
     * @param request the request
     * @param title the new play title
     * @return the new play
     */
    Play createNewPlay( ConfirmationReq request, String title );
}
