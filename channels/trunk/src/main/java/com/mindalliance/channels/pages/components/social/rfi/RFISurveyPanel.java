package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * RFI survey panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 9:58 AM
 */
public class RFISurveyPanel extends AbstractUpdatablePanel {

    public RFISurveyPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        super( id, rfiSurveyModel );
        init();
    }

    private void init() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Participation" ) ) {
            public Panel getPanel( String id ) {
                return new RFIsPanel( id, new Model<RFISurvey>( getRFISurvey() ) );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Results" ) ) {
            public Panel getPanel( String id ) {
                return new SurveyResultsPanel( id, new Model<RFISurvey>( getRFISurvey() ) );
            }
        } );
        return tabs;
    }

    private RFISurvey getRFISurvey() {
        return (RFISurvey)getModel().getObject();
    }


}
