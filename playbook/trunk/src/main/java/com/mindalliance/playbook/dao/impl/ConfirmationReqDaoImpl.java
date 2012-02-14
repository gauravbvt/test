package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.ConfirmationReq;
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
            "select c from ConfirmationReq as c " 
            + "left join fetch c.collaboration as d " 
            + "where c.confirmation is null and d.with in (:contacts)" )
            .setParameterList( "contacts", contactDao.findAliases( accountDao.getCurrentAccount() )
                 );
   
        return (List<ConfirmationReq>) query.list();
    }

}
