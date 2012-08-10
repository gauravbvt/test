package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Ack;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.NAck;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Receive;
import com.mindalliance.playbook.model.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation.
 */
@Repository
public class AckDaoImpl extends GenericHibernateDao<ConfirmationAck, Long> implements AckDao {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private PlayDao playDao;

    @Autowired
    private StepDao stepDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ConfirmationReqDao reqDao;

    @Override
    public Play saveInPlay( Play existingPlay, ConfirmationReq request ) {
        Collaboration collaboration = request.getCollaboration();

        Contact privateContact = contactDao.privatize( collaboration.getOwner(), collaboration );
        boolean isSend = collaboration instanceof Send;
        
        Collaboration matchingStep = isSend ? new Receive( existingPlay ) 
                                            : new Send( existingPlay );

        matchingStep.setWith( privateContact );
        matchingStep.setUsing( privateContact.addMedium( collaboration.getUsing() ) );
        matchingStep.setTitle( request.getCollaboration().getTitle() );
        matchingStep.setSequence( 1 );

        ConfirmationAck ack = save( new Ack( request, (Collaboration) stepDao.save( matchingStep ) ) );
        request.setConfirmation( ack );
        reqDao.save( request );
        
        return existingPlay;
    }

    @Override
    public Play saveInPlay( String newPlay, ConfirmationReq request ) {

        return saveInPlay(
            playDao.save(
                new Play(
                    accountDao.getCurrentAccount().getPlaybook(),
                    newPlay == null || newPlay.trim().isEmpty() ? "Untitled play" : newPlay.trim() ) ), 
            request );
    }

    @Override
    public void refuse( NAck nAck ) {
        save( nAck );
        
        ConfirmationReq req = nAck.getRequest();
        req.setConfirmation( nAck );
        reqDao.save( req );
    }
}
