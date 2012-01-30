package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import com.mindalliance.playbook.model.Receive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Hibernate implementation.
 */
@Repository
public class AckDaoImpl extends GenericHibernateDao<ConfirmationAck,Long> implements AckDao {

    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private PlayDao playDao;

    @Autowired
    private StepDao stepDao;
    
    @Autowired
    private AccountDao accountDao;
        
    @Override
    public Play createNewPlay( ConfirmationReq request, String title ) {
        
        // First, add new contact info, if required
        Account account = accountDao.getCurrentAccount();
        Contact myContact = getContact( request, account );
        Play play = playDao.save( new Play( account.getPlaybook(), title ) );

        Receive step = new Receive();
        step.setTitle( "Answer " + myContact.getGivenName() );
        step.setWith( myContact );
        step.setUsing( myContact.findMedium( request.getCollaboration().getUsing() ) );
        play.addStep( step );
        
        stepDao.save( step );

        return play;
    }

    private Contact getContact( ConfirmationReq request, Account account ) {
        Playbook theirPlaybook = request.getCollaboration().getPlay().getPlaybook();
        Contact theirContact = theirPlaybook.getMe();
        String theirEmail = theirPlaybook.getAccount().getEmail();
        List<Contact> contacts = contactDao.findByEmail( theirEmail );
        
        if ( contacts.isEmpty() )
            return newContactFrom( request, theirEmail, theirContact, account );
        else
            return contacts.get( 0 );
        
    }

    private Contact newContactFrom( ConfirmationReq request, String theirEmail, Contact theirContact, Account account ) {
        Contact result = new Contact( account, theirEmail );
        
        // TODO filter by what is needed
        result.merge( theirContact );
        
        return result;
    }
}
