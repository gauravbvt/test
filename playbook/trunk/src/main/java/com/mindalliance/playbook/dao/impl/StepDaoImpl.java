package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Ack;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Receive;
import com.mindalliance.playbook.model.Send;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.model.Subplay;
import com.mindalliance.playbook.model.Task;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

//        refresh( oldStep );
//        Play play = oldStep.getPlay();
//
//        play.removeStep( oldStep );
//        play.addStep( newStep );
//        playDao.save( play );
        
        delete( oldStep );
        save( newStep );

        return newStep;
    }

    @Override
    public boolean isConfirmable( Step step ) {
        if ( !step.isCollaboration() )
            return false;

        Collaboration collaboration = (Collaboration) step;
        if ( collaboration.getWith() == null || collaboration.getUsing() == null )
            return false;

        ConfirmationReq request = getLastRequest( collaboration );
        return request == null || request.getConfirmation() == null;
    }

    @Override
    public Status getStatus( Step step ) {
        if ( !step.isCollaboration() )
            return Status.CONFIRMED;

        Collaboration collaboration = (Collaboration) step;
        if ( collaboration.getWith() == null || collaboration.getUsing() == null )
            return Status.UNCONFIRMED;

        ConfirmationReq request = getLastRequest( collaboration );
        return request == null                   ? Status.UNCONFIRMED 
             : request.getConfirmation() == null ? Status.PENDING 
             : request.getConfirmation().isAck() ? Status.CONFIRMED 
                                                 : Status.REJECTED;
    }

    /**
     * Find last non-acknowledged confirmation request.
     * @param collaboration the step
     * @return null if none was found
     */
    private ConfirmationReq getLastRequest( Collaboration collaboration ) {
        Criteria criteria = getSession().createCriteria( ConfirmationReq.class )
            .add( Restrictions.eq( "collaboration", collaboration ) )
            .add( Restrictions.isNull( "confirmation" ) ) 
            .addOrder( Order.desc( "date" ) );

        List list = criteria.list();
        return list.isEmpty() ? null : (ConfirmationReq) list.get( 0 );
    }
}
