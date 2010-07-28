package com.mindalliance.channels.pages.components.support;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 27, 2010
 * Time: 10:06:35 AM
 */
public class HelpPanel extends Panel {
    public HelpPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addHelpTabs();
    }

     private void addHelpTabs() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Active topics" ) ) {
            public Panel getPanel( String id ) {
                return new ActiveTopicsPanel( id );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Search topics" ) ) {
            public Panel getPanel( String id ) {
                return new SearchTopicsPanel( id );
            }
        } );
        return tabs;
    }

}
