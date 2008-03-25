package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * ...
 */
public class ScenariosPanel extends Panel {



    public ScenariosPanel( String s ) {
        super( s );
        add( new ScenarioChooser( "scenario-chooser") );
        add( new ContentPanel( "contents" ) );
    }
}
