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
     * Remove attached request and agreement of a collaboration, if necessary.
     * @param collaboration the collaboration
     */
    void deleteConfirmation( Collaboration collaboration );

    /**
     * Delete request attached to the step, if any.
     * @param collaboration the collaboration
     */
    void deleteRequest( Collaboration collaboration );

    /**
     * Status of a step.
     */
    enum Status {
        UNCONFIRMED,
        CONFIRMED,
        PENDING,
        REJECTED,
        AGREED
    }

    /**
     * Find all information for the collaboration status of a step.
     * 
     * @param id the id of the step
     * @return some extended information or null if there is no step matching the id
     */
    StepInformation getInformation( long id );

    /**
     * Find all information for the collaboration status of a step.
     *
     * @param step the step
     * @return some extended information
     */
    StepInformation getInformation( Step step );

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
}
