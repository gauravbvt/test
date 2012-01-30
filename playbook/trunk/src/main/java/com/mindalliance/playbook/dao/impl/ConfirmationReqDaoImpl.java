package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
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
            "select c from ConfirmationReq as c where c.pending = 'true' "
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
            + "where c.pending = 'true' and d.with in (:contacts)" )
            .setParameterList( "contacts", contactDao.findAliases( accountDao.getCurrentAccount() )
                 );
//        .setParameterList( "contacts", getContactIds(), LongType.INSTANCE );

        return (List<ConfirmationReq>) query.list();
    }

    private Collection<Long> getContactIds() {
        Collection<Long> result = new ArrayList<Long>();
        for ( Contact alias : contactDao.findAliases( accountDao.getCurrentAccount() ) )
            result.add( alias.getId() );
        
        return result;
    }
}
