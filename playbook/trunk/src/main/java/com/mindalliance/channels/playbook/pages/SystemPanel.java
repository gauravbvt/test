package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * ...
 */
public class SystemPanel extends TabPanel {

    public SystemPanel( String id, ContainerModel model ) {
        super( id, model );
        add( new Label( "content-title", new Model( "System" ) ) );
    }
}
