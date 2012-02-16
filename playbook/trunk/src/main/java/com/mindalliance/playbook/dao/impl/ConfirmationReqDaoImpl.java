package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.RedirectReq;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            "select c from ConfirmationReq as c where c.confirmation is null "
            + "and c.collaboration.play.playbook.account=:account" )
                .setParameter( "account", accountDao.getCurrentAccount() );

        return (List<ConfirmationReq>) query.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<ConfirmationReq> getIncomingRequests() {

        Query query = getSession().createQuery( 
            "select r from ConfirmationReq as r " 
            + "left join fetch r.collaboration as c " 
            + "where r.confirmation is null and r.redirect is null and c.with in (:contacts)" )
            .setParameterList( "contacts", contactDao.findAliases( accountDao.getCurrentAccount() )
                 );
   
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
}
