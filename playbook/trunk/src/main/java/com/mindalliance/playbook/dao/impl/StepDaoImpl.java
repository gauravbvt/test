package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Receive;
import com.mindalliance.playbook.model.Send;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.model.Subplay;
import com.mindalliance.playbook.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation.
 */
@Repository
public class StepDaoImpl extends GenericHibernateDao<Step,Long> implements StepDao {
    
    @Autowired
    private PlayDao playDao;
    
    @Override
    public Step switchStep( Type stepType, Step oldStep ) {
        Step newStep;
        switch ( stepType ) {
        case SUBPLAY:
            newStep = new Subplay( oldStep );
            break;

        case RECEIVE:
            newStep = new Receive( oldStep );
            break;

        case SEND:
            newStep = new Send( oldStep );
            break;

        case TASK:
        default:
            newStep = new Task( oldStep );
            break;
        }

        refresh( oldStep );
        Play play = oldStep.getPlay();

        play.removeStep( oldStep );
        play.addStep( newStep );
        playDao.save( play );
        
        delete( oldStep );

        return newStep;
    }
}
