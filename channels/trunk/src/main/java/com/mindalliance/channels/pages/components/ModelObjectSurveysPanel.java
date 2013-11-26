package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.RFISurveyService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import com.mindalliance.channels.pages.components.social.rfi.AllSurveysPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/12/12
 * Time: 12:29 PM
 */
public class ModelObjectSurveysPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    @SpringBean
    private RFISurveyService rfiSurveyService;

    @SpringBean
    private RFIService rfiService;

    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;

    @SpringBean
    private QuestionnaireService questionnaireService;

    private SurveyWrapper selectedSurvey;

    public ModelObjectSurveysPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel, null );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "learning";
    }

    @Override
    public String getHelpTopicId() {
        return "launch-survey";
    }

    private void init() {
        addHeader();
        addModelObjectSurveysTable();
    }

    private void addHeader() {
        Label header = new Label( "header", getHeader() );
        header.setOutputMarkupId( true );
        getContentContainer().addOrReplace( header );
    }

    private String getHeader() {
        return "Surveys about "
                + getModelObject().getTypeName()
                + " \""
                + getModelObject().getLabel()
                + "\"";
    }

    private void addModelObjectSurveysTable() {
        getContentContainer().addOrReplace( new SurveysTable( "surveysTable", getSurveyWrappers() ) );
    }

    private List<SurveyWrapper> getSurveyWrappers() {
        List<SurveyWrapper> surveyWrappers = new ArrayList<SurveyWrapper>();
        Set<Questionnaire> questionnairesUsed = new HashSet<Questionnaire>();
        for ( RFISurvey survey : rfiSurveyService.select( getCommunityService(), getModelObject() ) ) {
            Questionnaire questionnaire = findQuestionnaire( survey );
            if ( !questionnaire.isIssueRemediation() ) {
                surveyWrappers.add( new SurveyWrapper( survey ) );
                questionnairesUsed.add( questionnaire );
            }
        }
        for ( Questionnaire questionnaire : questionnaireService.findApplicableQuestionnaires(
                getCommunityService(),
                getModelObject() ) ) {
            if ( !questionnairesUsed.contains( questionnaire ) )
                surveyWrappers.add( new SurveyWrapper( questionnaire ) );
        }
        return surveyWrappers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isUnknown() || change.isModified() || change.isRefresh() ) {
            init();
            target.add( this );
        }
    }


    private ModelObject getModelObject() {
        return (ModelObject) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getModelObject(), "surveys" );
        update( target, change );
    }


    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return "Surveys for "
                + getModelObject().getTypeName()
                + " \""
                + getModelObject().getLabel()
                + "\"";
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( SurveyWrapper.class ) && change.isExpanded() ) {
            selectedSurvey = (SurveyWrapper) change.getSubject( getCommunityService() );
            if ( change.isForProperty( "launch" ) ) {
                if ( selectedSurvey.launch() ) {
                    change.setSubject( selectedSurvey.getRfiSurvey() );
                    change.setId( RFISurvey.UNKNOWN.getId() );
                    change.setProperty( AllSurveysPanel.SURVEYS );
                }
            } else if ( change.isForProperty( "view" ) ) {
                RFISurvey survey = rfiSurveyService.refresh( selectedSurvey.getRfiSurvey() );
                assert survey != null;
                change.setSubject( survey );
                change.setId( RFISurvey.UNKNOWN.getId() );
                change.setProperty( AllSurveysPanel.SURVEYS );
            }
        }
        super.changed( change );
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        addModelObjectSurveysTable();
        target.add( this );
        super.updateWith( target, change, updated );
    }

    private Questionnaire findQuestionnaire( RFISurvey rfiSurvey ) {
       return questionnaireService.load( rfiSurvey.getQuestionnaireUid() );
    }

    public class SurveyWrapper implements Identifiable {

        private Questionnaire questionnaire;
        private RFISurvey rfiSurvey;
        private Map<String,Integer> metrics;

        private SurveyWrapper( RFISurvey rfiSurvey ) {
            this.rfiSurvey = rfiSurvey;
            questionnaire = findQuestionnaire( rfiSurvey );
        }

        private SurveyWrapper( Questionnaire questionnaire ) {
            this.questionnaire = questionnaire;
        }

        public Questionnaire getQuestionnaire() {
            return questionnaire;
        }

        public RFISurvey getRfiSurvey() {
            return rfiSurvey;
        }

        public String getLaunchDate() {
            if ( rfiSurvey != null ) {
                Date launchDate = rfiSurvey.getCreated();
                return getDateFormat().format( launchDate );
            } else {
                return null;
            }
        }

        public String getStatus() {
            if ( rfiSurvey != null ) {
                return rfiSurvey.isClosed()
                        ? "Closed"
                        : rfiSurvey.isOngoing( getCommunityService() )
                        ? "Launched"
                        : "Closed";
            } else {
                return null;
            }
        }

        public String getShortResponseMetrics() {
            if ( rfiSurvey != null ) {
                Map<String,Integer> metrics = getResponseMetrics();
                Integer[] values = new Integer[3];
                values[0] = metrics.get( "completed" );
                values[1] = metrics.get( "declined" );
                values[2] = metrics.get( "incomplete" );
                return MessageFormat.format( "{0}c {1}d {2}i", values );
            }
            else
                return null;
        }

        public String getLongResponseMetrics() {
            if ( rfiSurvey != null ) {
                Map<String,Integer> metrics = getResponseMetrics();
                Integer[] values = new Integer[3];
                values[0] = metrics.get( "completed" );
                values[1] = metrics.get( "declined" );
                values[2] = metrics.get( "incomplete" );
                return MessageFormat.format( "{0} completed, {1} declined and {2} incomplete", values );
            }
            else
                return null;
        }


        private Map<String,Integer> getResponseMetrics() {
            if ( metrics == null ) {
                metrics = surveysDAO.findResponseMetrics( getCommunityService(), rfiSurvey );
            }
            return metrics;
        }

        public String getAction() {
            return rfiSurvey != null
                    ? "view"
                    : "launch";
        }

        public boolean launch() {
            assert rfiSurvey == null;
            rfiSurvey = rfiSurveyService.launch(
                    getCommunityService(),
                    getUsername(),
                    getQuestionnaire(),
                    getModelObject() );
            return rfiSurvey != null;
        }

        // identifiable

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public String getTypeName() {
            return getClass().getSimpleName();
        }

        @Override
        public String getKindLabel() {
            return getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return true;
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getName() {
            return getQuestionnaire().getName();
        }


    }

    private class SurveysTable extends AbstractFilterableTablePanel {

        private final List<SurveyWrapper> surveys;

        private SurveysTable( String id, List<SurveyWrapper> surveys ) {
            super( id );
            this.surveys = surveys;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn( "Survey", "questionnaire.name", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeColumn( "Launched on", "launchDate", EMPTY ) );
            columns.add( makeColumn( "Responses", "shortResponseMetrics", null, EMPTY, "longResponseMetrics" ) );
            columns.add( makeFlexibleExpandLinkColumn( "", "", "action", "" ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "surveys",
                    columns,
                    new SortableBeanProvider<SurveyWrapper>(
                            surveys,
                            "questionnaire.name" ),
                    getPageSize() ) );

        }

        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }


    }


}
