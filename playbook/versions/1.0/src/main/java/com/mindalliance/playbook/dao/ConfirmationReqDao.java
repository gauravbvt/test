package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.RedirectReq;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Confirmation request manipulator/finder.
 */
@Secured( "ROLE_USER" )
@Transactional
public interface ConfirmationReqDao extends GenericDao<ConfirmationReq,Long> {

    /**
     * @return pending outgoing confirmation requests for current user.
     */
    List<ConfirmationReq> getOutgoingRequests();

    /**
     * @return pending incoming confirmation requests for current user.
     */
    List<ConfirmationReq> getIncomingRequests();

    /**
     * Save a redirect request.
     * @param request the request
     */
    void redirect( RedirectReq request );
}
