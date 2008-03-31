package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * ...
 */
public class ResourcesPanel extends TabPanel {

    public ResourcesPanel( String id, ContainerModel model ) {
        super( id, model );
        add( new Label( "content-title", new Model( "Resources" ) ) );
    }
}
