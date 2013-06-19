package com.mindalliance.channels.db.services.activities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.activities.ExecutedCommand;
import com.mindalliance.channels.db.data.activities.QExecutedCommand;
import com.mindalliance.channels.db.repositories.ExecutedCommandRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:38 PM
 */
@Component
public class ExecutedCommandServiceImpl extends AbstractDataService<ExecutedCommand> implements ExecutedCommandService {

    @Autowired
    private ExecutedCommandRepository repository;

    private Map<String, Date> whenLastChanged = new HashMap<String, Date>();

    @Override
    public void save( ExecutedCommand executedCommand ) {
        repository.save( executedCommand );
    }

    @Override
    public ExecutedCommand load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public Iterator<ExecutedCommand> getExecutedCommands( PlanCommunity planCommunity ) { // todo - paginate?
        QExecutedCommand qExecutedCommand = QExecutedCommand.executedCommand;
        return toList(
                repository.findAll(
                        qExecutedCommand.classLabel.eq( ExecutedCommand.class.getSimpleName() )
                                .and( qExecutedCommand.communityUri.eq( planCommunity.getUri() ) )
                                .and( qExecutedCommand.planVersion.eq( planCommunity.getPlanVersion() ) ),
                        qExecutedCommand.created.desc()
                )
        ).iterator();
    }

    @Override
    public void started( Commander commander ) {
        // do nothing
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Done, command, change );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Undone, command, change );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        saveExecutedCommand( commander, ExecutedCommand.Type.Redone, command, change );
    }

    @Override
    public Date getWhenLastChanged( String planCommunityUri ) {
        return whenLastChanged.get( planCommunityUri );
    }

    private void saveExecutedCommand(
            Commander commander,
            ExecutedCommand.Type type,
            Command command,
            Change change ) {
        PlanCommunity planCommunity = commander.getPlanCommunity();
        changed( planCommunity.getUri() );
        ExecutedCommand commandEvent = new ExecutedCommand( type, command, change, planCommunity );
        save( commandEvent );

    }


    private void changed( String planCommunityUri ) {
        whenLastChanged.put( planCommunityUri, new Date() );
    }

}
