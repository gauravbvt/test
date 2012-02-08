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
    public Play saveInPlay( Play existingPlay, Collaboration collaboration, ConfirmationReq request ) {

        Contact privateContact = contactDao.privatize( collaboration.getPlay().getPlaybook().getMe(), collaboration );
        boolean isSend = collaboration instanceof Send;
        
        Collaboration matchingStep = isSend ? new Receive( existingPlay ) 
                                            : new Send( existingPlay );

        matchingStep.setWith( privateContact );
        matchingStep.setUsing( privateContact.addPrivate( collaboration.getUsing() ) );
        matchingStep.setTitle( ( isSend ? "Answer " : "Call " ) + privateContact.getGivenName() );
        matchingStep.setSequence( 1 );

        ConfirmationAck ack = save( new Ack( request, (Collaboration) stepDao.save( matchingStep ) ) );
        request.setConfirmation( ack );
        reqDao.save( request );
        
        return existingPlay;
    }

    @Override
    public Play saveInPlay( String newPlay, Collaboration collaboration, ConfirmationReq request ) {

        Play play = new Play(
            accountDao.getCurrentAccount().getPlaybook(),
            newPlay == null || newPlay.trim().isEmpty() ? "Untitled play" : newPlay.trim() );

        playDao.save( play );

        return saveInPlay( play, collaboration, request );
    }
}
