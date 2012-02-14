package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Direct accessor to a play steps.
 */
@Secured( "ROLE_USER" )
@Transactional
public interface StepDao extends GenericDao<Step,Long> {

    /**
     * Status of a step.
     */
    public enum Status {
        UNCONFIRMED,
        CONFIRMED,
        PENDING,
        REJECTED,
        AGREED
    }

    /**
     * Find all unconfirmed steps in any play of current user.
     * @return a list of collaborations
     */
    List<Collaboration> getUnconfirmed();

    /**
     * Find all steps of current user that were rejected.
     * @return a list of collaborations
     */
    List<Collaboration> getRejected();

    /**
     * Find all steps that are missing contact information.
     * @return a list of steps
     */
    List<Collaboration> getIncomplete();

    /**
     * Change the type of a step.
     *
     * @param stepType the new type
     * @param oldStep the step
     * @return the new step
     */
    Step switchStep( Type stepType, Step oldStep );

    /**
     * Test if a step is confirmable. 
     * 
     * @param step the step
     * @return true if step is a collaboration that has valid contact and medium information
     * that has not been confirmed or rejected.
     */
    boolean isConfirmable( Step step );

    /**
     * Return a descriptive text of the status of a step.
     * 
     * @param step the step
     * @return either "Confirm", "Pending" or "Rejected"
     */
    Status getStatus( Step step );
}
