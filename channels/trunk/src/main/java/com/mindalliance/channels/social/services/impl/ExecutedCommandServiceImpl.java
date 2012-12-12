package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.ExecutedCommand;
import com.mindalliance.channels.social.services.ExecutedCommandService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/3/12
 * Time: 10:48 AM
 */

@Repository
public class ExecutedCommandServiceImpl
        extends GenericSqlServiceImpl<ExecutedCommand,Long>
        implements ExecutedCommandService {

    private Map<String,Date> whenLastChanged = new HashMap<String, Date>();

    @Override
    public void started( Commander commander ) {
    }

    @Override
    @Transactional( readOnly = true)
    public Iterator<ExecutedCommand> getExecutedCommands( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", planCommunity.getPlanVersion() ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<ExecutedCommand> results = (List<ExecutedCommand>)criteria.list( );
        return results.iterator();
    }
   @Override
   @Transactional
    public void commandDone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Done, command, change);
    }

    @Override
    @Transactional
    public void commandUndone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Undone, command, change );
    }

    @Override
    @Transactional
    public void commandRedone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Redone, command, change );
    }

    @Override
    public Date getWhenLastChanged( String planUri ) {
        return whenLastChanged.get( planUri );
    }

    private void changed( String planUri ) {
        whenLastChanged.put( planUri, new Date() );
    }

    
    private void saveExecutedCommand(
            Commander commander,
            ExecutedCommand.Type type,
            Command command,
            Change change) {
        Plan plan = commander.getPlan();
        changed( plan.getVersionUri() );
        ExecutedCommand commandEvent = new ExecutedCommand( type, command, change, commander.getPlanCommunity() );
        save( commandEvent );
        
    }

}
