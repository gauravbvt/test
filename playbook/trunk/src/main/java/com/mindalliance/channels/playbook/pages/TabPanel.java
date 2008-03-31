package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.ContainerModel;
import org.apache.wicket.markup.html.panel.Panel;

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
    }
}
