package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Actor;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 23, 2009
 * Time: 2:15:22 PM
 */
public class ActorPanel extends ModelObjectPanel {

    public ActorPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model );
    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        moDetailsDiv.add(
                new TextField<String>( "job-title",                                            // NON-NLS
                        new PropertyModel<String>( this, "jobTitle" ) ) );

    }

    /**
     * Set the actor's job title if not null or empty.
     *
     * @param name a String
     */
    public void setJobTitle( String name ) {
        Actor actor = (Actor) mo;
        if ( name == null || name.trim().isEmpty() ) {
            actor.setJobTitle( "" );
        } else {
            actor.setJobTitle( name );
        }
    }

    /**
     * Get the actor's job title.
     *
     * @return a String
     */
    public String getJobTitle() {
        String jobTitle = ( (Actor) mo ).getJobTitle();
        return jobTitle == null ? "" : jobTitle;
    }

}
