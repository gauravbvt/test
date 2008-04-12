package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * ...
 */
abstract public class TabPanel extends Panel {

    public TabPanel( String id, ContainerModel model ) {
        super( id, model );
        setRenderBodyOnly( true );
        final WebMarkupContainer left = new WebMarkupContainer( "tab-left" );
        add( left );

        left.add( new FilterPanel( "filter", model ) );
        left.add( new TodoPanel( "todos", new RefPropertyModel( new Model( getSession() ), "participation" ) ) );

        add( new ContentPanel( "tab-right", model ) );

    }
}
