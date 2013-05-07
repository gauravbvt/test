package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data collection panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/12
 * Time: 1:11 PM
 */
public class AllSurveysPanel extends AbstractFloatingMultiAspectPanel implements Releaseable {

    public static final String QUESTIONNAIRES = "Questionnaires";
    public static final String SURVEYS = "Surveys";

    private static final String[] ASPECTS = {SURVEYS, QUESTIONNAIRES};

    public AllSurveysPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        this( id, rfiSurveyModel, SURVEYS );
    }

    public AllSurveysPanel( String id, IModel<RFISurvey> rfiSurveyModel, String aspect ) {
        super( id, rfiSurveyModel, null, aspect );
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return new ArrayList<String>(  );  // survey aspect panels take care of locking, unlocking
    }


 /*   private void clearSelections() {
        if ( questionnairesPanel != null ) {
            questionnairesPanel.clearSelectionWith( this );
        }
        if ( rfiSurveysPanel != null ) {
            rfiSurveysPanel.clearSelectionWith( this );
        }
    }*/

    @Override
    protected String getCssClass() {
        return "surveys";
    }

    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return null;
    }

    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
       if ( aspect.equals( QUESTIONNAIRES ) ) {
            return getQuestionnairesPanel();
        } else {
            return getRFISurveysPanel();
        }
    }

    private RFISurveysPanel getRFISurveysPanel() {
        return new RFISurveysPanel( "aspect", (IModel<RFISurvey>)getModel() );
    }

    private QuestionnairesPanel getQuestionnairesPanel() {
        return new QuestionnairesPanel( "aspect" );
    }

    @Override
    protected String getMapTitle() {
        return "";
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        return new ArrayList<GeoLocatable>(  );
    }

    @Override
    protected String getTitle() {
        return "Surveys and questionnaires";
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        // clearSelections();
        Change change = new Change( Change.Type.Collapsed, RFISurvey.UNKNOWN );
        update( target, change );
    }

}
