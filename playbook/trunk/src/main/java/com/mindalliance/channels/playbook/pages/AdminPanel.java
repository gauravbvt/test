package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * ...
 */
@AuthorizeInstantiation( { "ADMIN" })
public class AdminPanel extends Panel {

    public AdminPanel( String id ) {
        super( id );
    }
}
