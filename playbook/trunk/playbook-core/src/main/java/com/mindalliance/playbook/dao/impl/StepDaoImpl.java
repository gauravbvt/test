package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.dao.StepInformation;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Ack;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Play;
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

import static com.mindalliance.playbook.model.Step.Type.*;

/**
 * Hibernate implementation.
 */
@Repository
public class StepDaoImpl extends GenericHibernateDao<Step,Long> implements StepDao {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AckDao ackDao;
    
    @Autowired
    private PlayDao playDao;

    @Autowired
    private ConfirmationReqDao reqDao;
    
    @SuppressWarnings( "unchecked" )
    @Override
    public List<Collaboration> getUnconfirmed() {
        return getSession().createQuery(
            "select c from Collaboration c " 
            + "left join c.request r " 
            + "left join c.agreement a " 
            + "where c.play.playbook.account = :account" 
            + " and r is null and a is null and c.with is not null and c.using is not null"
        )
            .setParameter( "account", accountDao.getCurrentAccount() )
            .list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Collaboration> getRejected() {
        return getSession().createQuery(
            "select n.request.collaboration from NAck n where n.request.collaboration.play.playbook.account = :account"
            )
            .setParameter( "account", accountDao.getCurrentAccount() )
            .list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Collaboration> getIncomplete() {
        return getSession().createQuery(
            "select c from Collaboration c where c.play.playbook.account = :account" 
            + " and ( c.with is null or c.using is null )"
        )
            .setParameter( "account", accountDao.getCurrentAccount() )
            .list();
    }

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
        save( newStep );
        delete( oldStep );
          
        return newStep;
    }

    private static Status getStatus( ConfirmationReq request ) {
        return request == null                   ? Status.UNCONFIRMED 
             : request.getConfirmation() == null ? Status.PENDING 
             : request.getConfirmation().isAck() ? Status.CONFIRMED 
                                                 : Status.REJECTED;
    }

    @Override
    public StepInformation getInformation( long id ) {
        Step step = load( id );
        return step == null ? null : getInformation( step );
    }

    @Override
    public StepInformation getInformation( Step step ) {
        ConfirmationReq req = null;
        ConfirmationAck confirmation = null;
        if ( step.isCollaboration() ) {
            req = getLastRequest( (Collaboration) step );
            if ( req != null )
                confirmation = req.getConfirmation();
        }

        return new StepInformationImpl( step, req, confirmation );
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

    /**
     * Deleting a collaboration step means having to properly delete the links
     * between confirmation requests and acknowledgement.
     * 
     * Hibernate does not support "on delete set null", unfortunately...
     * 
     * @param entity the step being deleted
     */
    @Override
    public void delete( Step entity ) {
        if ( entity.isCollaboration() )
            deleteConfirmation( (Collaboration) entity );
              
        super.delete( entity );
    }

    @Override
    public void deleteConfirmation( Collaboration collaboration ) {
        ConfirmationReq request = collaboration.getRequest();
        if ( request != null ) {
            ConfirmationAck confirmation = request.getConfirmation();
            request.setConfirmation( null );
            reqDao.save( request );
            if ( confirmation != null ) {
                confirmation.setRequest( null );
                ackDao.delete( confirmation );
            }
        }

        Ack agreement = collaboration.getAgreement();
        if ( agreement != null ) {
            ConfirmationReq req = agreement.getRequest();
            req.setConfirmation( null );
            reqDao.save( req );
            collaboration.setAgreement( null );
            ackDao.delete( agreement );
        }
    }

    @Override
    public void deleteRequest( Collaboration collaboration ) {
        ConfirmationReq request = collaboration.getRequest();
        if ( request != null ) {
            reqDao.delete( request );
        }

        Ack agreement = collaboration.getAgreement();
        if ( agreement != null ) {
            ConfirmationReq req = agreement.getRequest();
            req.setConfirmation( null );
            reqDao.save( req );
            collaboration.setAgreement( null );
            ackDao.delete( agreement );
        }
    }

    //=================================================================
    public static class StepInformationImpl implements StepInformation {

        private Step step;
        private ConfirmationReq req;
        private ConfirmationAck ack;

        public StepInformationImpl( Step step, ConfirmationReq req, ConfirmationAck ack ) {
            this.ack = ack;
            this.req = req;
            this.step = step;
        }

        @Override
        public long getPlayId() {
            return step.getPlay().getId();
        }

        @Override
        public ConfirmationAck getAck() {
            return ack;
        }

        @Override
        public Status getStatus() {
            if ( !step.isCollaboration() )
                return Status.CONFIRMED;

            Collaboration collaboration = (Collaboration) step;
            if ( collaboration.getWith() == null || collaboration.getUsing() == null )
                return Status.UNCONFIRMED;

            if ( collaboration.isAgreed() )
                return Status.AGREED;
            
            return req == null ? Status.UNCONFIRMED
                               : ack == null ? Status.PENDING
                                             : ack.isAck() ? Status.CONFIRMED
                                                           : Status.REJECTED;
        }

        @Override
        public boolean isConfirmable() {
            if ( !isCollaboration() || req != null && ack != null )
                return false;

            Collaboration collaboration = (Collaboration) step;
            Contact contact = collaboration.getWith();
            if ( contact == null || collaboration.getUsing() == null )
                return false;

            if ( collaboration.isAgreed() )
                return false;


            // TODO remove this when email invitations are enabled
            // Contact must be a registered playbook user for now...
            return true;
            //return accountDao.findByContact( contact ) != null;
        }

        @Override
        public boolean isCollaboration() {
            return step.isCollaboration();
        }

        @Override
        public String getAckMessage() {
            return ack == null ? null : ack.getReason();
        }

        @Override
        public ConfirmationReq getReq() {
            return req;
        }

        @Override
        public Step getStep() {
            return step;
        }

        @Override
        public Account getAccount() {
            return step.getPlay().getAccount();
        }
    }
}
