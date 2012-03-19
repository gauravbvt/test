package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Data collection panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/12
 * Time: 1:11 PM
 */
public class DataCollectionPanel extends FloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private RFISurveysPanel rfisPanel;
    private QuestionnairesPanel questionnairesPanel;
    private IModel<RFISurvey> rfiSurveyModel;


    public DataCollectionPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    public DataCollectionPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        super( id );
        this.rfiSurveyModel = rfiSurveyModel;
        init();
    }

    private void init() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        getContentContainer().add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Surveys" ) ) {
            public Panel getPanel( String id ) {
                return new RFISurveysPanel( id, rfiSurveyModel );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Questionnaires" ) ) {
            public Panel getPanel( String id ) {
                return new QuestionnairesPanel( id );
            }
        } );

        return tabs;
   }


    @Override
    protected String getTitle() {
        return "Data collection";
    }

    @Override
    protected int getPadTop() {
        return PAD_TOP;
    }

    @Override
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    @Override
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    @Override
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    @Override
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    @Override
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, RFISurvey.UNKNOWN );
        update( target, change );
    }


}
