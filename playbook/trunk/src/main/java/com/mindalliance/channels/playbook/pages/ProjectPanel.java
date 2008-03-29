package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
@AuthorizeInstantiation( { "ADMIN" })
public class ProjectPanel extends Panel {

    public ProjectPanel( String id, IModel model ) {
        super( id, model );
    }
}
