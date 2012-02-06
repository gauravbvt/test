package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

/**
 * Direct accessor to a play steps.
 */
@Secured( "ROLE_USER" )
public interface StepDao extends GenericDao<Step,Long> {

    /**
     * Status of a step.
     */
    public enum Status {
        UNCONFIRMED,
        CONFIRMED,
        PENDING,
        REJECTED        
    }

    /**
     * Change the type of a step.
     *
     * @param stepType the new type
     * @param oldStep the step
     * @return the new step
     */
    @Transactional
    Step switchStep( Type stepType, Step oldStep );

    /**
     * Test if a step is confirmable. 
     * 
     * @param step the step
     * @return true if step is a collaboration that has valid contact and medium information
     * that has not been confirmed or rejected.
     */
    @Transactional
    boolean isConfirmable( Step step );

    /**
     * Return a descriptive text of the status of a step.
     * 
     * @param step the step
     * @return either "Confirm", "Pending" or "Rejected"
     */
    @Transactional
    Status getStatus( Step step );
}
