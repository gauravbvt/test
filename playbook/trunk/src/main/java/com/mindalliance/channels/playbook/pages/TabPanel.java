package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * ...
 */
abstract public class TabPanel extends Panel {

    private FilterPanel filterPanel;

    public TabPanel( String id, ContainerModel model ) {
        super( id, model );
        filterPanel = new FilterPanel( "filter", model );
        add( filterPanel );
        add( new ContentPanel( "contents", model ) );

        add( new TodoPanel( "todos", new RefPropertyModel( new Model( getSession() ), "participation" ) ) );

    }
}
