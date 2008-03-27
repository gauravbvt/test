package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * ...
 */
public class FilterPanel extends Panel {

    public FilterPanel( String id ) {
        super( id );

        add( new Label( "filter", "[Filter]" ) );
    }
}
