package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.ModelObjectPanel;
import com.mindalliance.channels.pages.components.PlaybookPanel;
import com.mindalliance.channels.pages.components.DirectoryPanel;
import com.mindalliance.channels.pages.Project;

/**
 * Actor page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 2:31:40 PM
 */
public class ActorPage extends WebPage {
    /**
     * The actor 'id' parameter in the URL.
     */
    static final String ID_PARM = "id";                                                   // NON-NLS

    public ActorPage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Actor not found", e );
        }
    }

    private void init( PageParameters parameters ) throws NotFoundException {
        // setVersioned( false );
        // setStatelessHint( true );
        Actor actor = findActor( parameters );
        assert actor != null;
        add( new Label( "title", new Model<String>( "Actor: " + actor.getName() ) ) );
        add( new IssuesPanel<Actor>( "issues", new Model<Actor>( actor ) ) );
        add( new ModelObjectPanel( "actor-form", new Model<Actor>( actor ) ) );
        add( new PlaybookPanel( "playbook", new Model<Actor>( actor ) ) );
        add( new DirectoryPanel( "directory", new Model<Actor>( actor ) ) );
    }

    private Actor findActor( PageParameters parameters ) throws NotFoundException {
        Actor actor = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            Dao dao = Project.getProject().getDao();
            actor = dao.findActor( parameters.getLong( ID_PARM ) );
        }
        return actor;
    }
}
