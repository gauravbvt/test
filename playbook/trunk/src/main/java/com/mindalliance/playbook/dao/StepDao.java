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
     * Change the type of a step.
     *
     * @param stepType the new type
     * @param oldStep the step
     * @return the new step
     */
    @Transactional
    Step switchStep( Type stepType, Step oldStep );
}
