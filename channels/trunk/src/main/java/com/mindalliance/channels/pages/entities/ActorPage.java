package com.mindalliance.channels.pages.entities;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.ModelObjectPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

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
        final Actor actor = findActor( parameters );
        assert actor != null;
        add( new Label( "title", new Model<String>( "Actor: " + actor.getName() ) ) );
        add( new Label( "header-title", new PropertyModel<String>( actor, "name" ) ) );
        add ( new ExternalLink("index", "index.html"));
        add( new ModelObjectPanel( "actor-form", new Model<Actor>( actor ) ) );
        add( new ProfileLink( "profile-link",
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return ( ResourceSpec.with( actor )) ;
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return "View profile";
                            }
                        }
                )  );
    }

    private Actor findActor( PageParameters parameters ) throws NotFoundException {
        Actor actor = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            actor = getService().find( Actor.class, parameters.getLong( ID_PARM ) );
        }
        return actor;
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }


}
