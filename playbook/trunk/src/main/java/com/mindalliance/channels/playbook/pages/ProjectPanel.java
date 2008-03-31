package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

/**
 * ...
 */
@AuthorizeInstantiation( { "ADMIN" })
public class ProjectPanel extends TabPanel {

    public ProjectPanel( String id, ContainerModel model ) {
        super( id, model );
        add( new Label( "content-title", new RefPropertyModel( model.getTarget(), "name" ) ) );
    }
}
