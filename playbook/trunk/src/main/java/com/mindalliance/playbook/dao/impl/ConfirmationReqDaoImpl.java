package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.RedirectReq;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Hibernate implementation.
 */
@Repository
public class ConfirmationReqDaoImpl extends GenericHibernateDao<ConfirmationReq,Long> implements ConfirmationReqDao {

    @Autowired
    AccountDao accountDao;
    
    @Autowired
    ContactDao contactDao;

    @SuppressWarnings( "unchecked" )
    @Override
    public List<ConfirmationReq> getOutgoingRequests() {
        Query query = getSession().createQuery(
            "select r from ConfirmationReq r " + "where r.confirmation is null and r.playbook = :playbook"
        )
                .setParameter( "playbook", accountDao.getCurrentAccount().getPlaybook() );

        return (List<ConfirmationReq>) query.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<ConfirmationReq> getIncomingRequests() {

        Set<Contact> aliases = accountDao.findAliases( accountDao.getCurrentAccount() );
        
        Query query = getSession().createQuery(
            "select r from ConfirmationReq as r " 
            + "left join r.collaboration c "
            + "left join r.redirect f "
            + "where ( r.redirect is null and r.confirmation is null ) " 
            + "and ( c.with in (:contacts) "
                + "or r.recipient in (:contacts) )" )
            .setParameterList( "contacts", aliases )
            ;
   
        return (List<ConfirmationReq>) query.list();
    }

    /**
     * Save a redirect request.
     * @param request the request
     * 
     */
    public void redirect( RedirectReq request ) {
        save( request );

        ConfirmationReq originalRequest = request.getOriginalRequest();
        originalRequest.setRedirect( request );
        save( originalRequest );
    }

    @Override
    public void delete( ConfirmationReq entity ) {
        if ( entity.isRedirect() ) {
            ConfirmationReq originalRequest = ((RedirectReq) entity).getOriginalRequest();
            originalRequest.setRedirect( null );
            save( originalRequest );            
        }
        
        super.delete( entity );
    }
}
