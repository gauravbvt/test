package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Channels settings panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/13
 * Time: 11:55 AM
 */
public class SettingsPanel extends AbstractUpdatablePanel {

    private AjaxTabbedPanel<ITab> tabbedPanel;

    public SettingsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addTabPanel();
    }

    private void addTabPanel() {
        tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.NeedsRefresh ) );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Collaboration templates" ) ) {
            public Panel getPanel( String id ) {
                return new PlansSettingsPanel( id );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Users" ) ) {
            public Panel getPanel( String id ) {
                return new UsersSettingsPanel( id );
            }
        } );
        return tabs;
    }



    public void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }
}
