package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.Releaseable;
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

/**
 * Data collection panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/12
 * Time: 1:11 PM
 */
public class DataCollectionPanel extends FloatingCommandablePanel implements Releaseable {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    public static final String QUESTIONNAIRES = "Questionnaires";
    public static final String SURVEYS = "Surveys";

    private QuestionnairesPanel questionnairesPanel;
    private IModel<RFISurvey> rfiSurveyModel;
    private RFISurveysPanel rfiSurveysPanel;

    public DataCollectionPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        this( id, rfiSurveyModel, "" );
    }

    public DataCollectionPanel( String id, IModel<RFISurvey> rfiSurveyModel, String initialTab ) {
        super( id );
        this.rfiSurveyModel = rfiSurveyModel;
        init( initialTab );
    }

    private void init( String initialTab ) {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                clearSelections();
                super.onAjaxUpdate( target );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        tabbedPanel.setSelectedTab( initialTab.equals( QUESTIONNAIRES ) ? 1 : 0 );
        getContentContainer().add( tabbedPanel );
    }

    private void clearSelections() {
        if ( questionnairesPanel != null ) {
            questionnairesPanel.clearSelectionWith( this );
        }
        if ( rfiSurveysPanel != null ) {
            rfiSurveysPanel.clearSelectionWith( this );
        }
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( SURVEYS ) ) {
            public Panel getPanel( String id ) {
                rfiSurveysPanel = new RFISurveysPanel( id, rfiSurveyModel );
                return rfiSurveysPanel;
            }
        } );

        tabs.add( new AbstractTab( new Model<String>( QUESTIONNAIRES ) ) {
            public Panel getPanel( String id ) {
                questionnairesPanel = new QuestionnairesPanel( id );
                return questionnairesPanel;
            }
        } );

        return tabs;
    }


    @Override
    protected String getTitle() {
        return "Surveys";
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
        clearSelections();
        Change change = new Change( Change.Type.Collapsed, RFISurvey.UNKNOWN );
        update( target, change );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void releaseAnyLockOn( Identifiable identifiable ) {
        getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void requestLockOn( Identifiable identifiable ) {
        getCommander().requestLockOn( getUser().getUsername(), identifiable );
    }

}
