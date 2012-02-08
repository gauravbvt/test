package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Receive;
import com.mindalliance.playbook.model.Send;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.model.Subplay;
import com.mindalliance.playbook.model.Task;
import org.apache.wicket.spring.injection.annot.SpringBean;
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
    private AccountDao accountDao;

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
       
        delete( oldStep );
        save( newStep );

        return newStep;
    }

    @Override
    public boolean isConfirmable( Step step ) {
        if ( !step.isCollaboration() )
           return false;

        Collaboration collaboration = (Collaboration) step;
        Contact contact = collaboration.getWith();
        if ( contact == null || collaboration.getUsing() == null )
            return false;


        if ( collaboration.isAgreed() )
            return false;

        ConfirmationReq request = getLastRequest( collaboration );
        if ( request != null && request.getConfirmation() != null )
            return false;

        // Contact must be a register playbook user for now...
        // TODO remove this when email invitations are enabled

        for ( String email : contact.getEmails() )
            if ( accountDao.findByEmail( email ) != null )
                    return true;

        return false;
    }

    @Override
    public Status getStatus( Step step ) {
        if ( !step.isCollaboration() )
            return Status.CONFIRMED;

        Collaboration collaboration = (Collaboration) step;
        if ( collaboration.getWith() == null || collaboration.getUsing() == null )
            return Status.UNCONFIRMED;
        
        if ( collaboration.isAgreed() )
            return Status.AGREED;

        return getStatus( getLastRequest( collaboration ) );
    }

    private static Status getStatus( ConfirmationReq request ) {
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
            .addOrder( Order.desc( "date" ) )
            .setMaxResults( 1 );

        List list = criteria.list();
        return list.isEmpty() ? null : (ConfirmationReq) list.get( 0 );
    }
}
