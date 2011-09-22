/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Surveys panel.
 */
public class SurveysPanel extends FloatingCommandablePanel implements Filterable {

    /**
     * Pad top on move.
     */
    private static final int PAD_TOP = 68;

    /**
     * Pad left on move.
     */
    private static final int PAD_LEFT = 5;

    /**
     * Pad bottom on move and resize.
     */
    private static final int PAD_BOTTOM = 5;

    /**
     * Pad right on move and resize.
     */
    private static final int PAD_RIGHT = 6;

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private static final String NOT_YET_LAUNCHED = "not yet launched";

    private static final String LAUNCHED = "launched";

    private static final String CLOSED = "closed";

    private static final String RELEVANT = "relevant";

    private static final String IRRELEVANT = "no longer relevant";

    /**
     * Status choices.
     */
    private static final String[] StatusChoices = { NOT_YET_LAUNCHED, LAUNCHED, CLOSED };

    /**
     * Relevance choices.
     */
    private static final String[] RelevanceChoices = { RELEVANT, IRRELEVANT };

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_SURVEY_ROWS = 10;

    /**
     * Whether to filter on status and relevance.
     */
    private boolean stateFiltered = false;

    /**
     * What status to filter surveys on.
     */
    private String statusFilter = StatusChoices[0];

    /**
     * What relevance to filter surveys on.
     */
    private String relevanceFilter = RelevanceChoices[0];

    /**
     * Status filter choice.
     */
    private DropDownChoice<String> statusChoice;

    /**
     * Relevance filter choice.
     */
    private DropDownChoice<String> relevanceChoice;

    /**
     * Surveys table.
     */
    private SurveysTable surveysTable;

    /**
     * Selected survey.
     */
    private Survey selectedSurvey = Survey.UNKNOWN;

    /**
     * Survey container.
     */
    private WebMarkupContainer surveyContainer;

    /**
     * Title label.
     */
    private Label titleLabel;

    /**
     * Survey service.
     */
    @SpringBean
    private SurveyService surveyService;

    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();

    public SurveysPanel( String id, Survey survey, Set<Long> readOnlyExpansions ) {
        super( id, null, readOnlyExpansions );
        selectedSurvey = survey;
        init();
    }

    @Override
    protected String getTitle() {
        return "Surveys";
    }

    @Override
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Survey.UNKNOWN );
        update( target, change );
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

    private void init() {
        addStatusAndRelevanceFilters();
        addTableTitle();
        addSurveysTable();
        addSurveyContainer();
    }

    private void addStatusAndRelevanceFilters() {
        addFilterCheckBox();
        addStatusChoices();
        addRelevanceChoices();
        updateEnabled();
    }

    private void updateEnabled() {
        statusChoice.setEnabled( stateFiltered );
        relevanceChoice.setEnabled( stateFiltered );
    }

    private void addFilterCheckBox() {
        CheckBox filteredCheckBox = new CheckBox( "filtered", new PropertyModel<Boolean>( this, "stateFiltered" ) );
        filteredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateEnabled();
                target.addComponent( statusChoice );
                target.addComponent( relevanceChoice );
                target.addComponent( titleLabel );
                addSurveysTable();
                target.addComponent( surveysTable );
            }
        } );
        add( filteredCheckBox );
    }

    public boolean isStateFiltered() {
        return stateFiltered;
    }

    public void setStateFiltered( boolean val ) {
        stateFiltered = val;
    }

    private void addTableTitle() {
        titleLabel = new Label( "tableTitle", new PropertyModel<String>( this, "tableTitle" ) );
        titleLabel.setOutputMarkupId( true );
        add( titleLabel );
    }

    public String getTableTitle() {
        String title = "All surveys";
        if ( stateFiltered ) {
            title = title + " that are " + getStatusFilter() + " and are " + getRelevanceFilter();
        }
        return title;
    }

    private void addStatusChoices() {
        statusChoice = new DropDownChoice<String>( "statusChoice",
                                                   new PropertyModel<String>( this, "statusFilter" ),
                                                   Arrays.asList( StatusChoices ) );
        statusChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( titleLabel );
                addSurveysTable();
                target.addComponent( surveysTable );
            }
        } );
        add( statusChoice );
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter( String statusFilter ) {
        this.statusFilter = statusFilter;
    }

    private void addRelevanceChoices() {
        relevanceChoice = new DropDownChoice<String>( "relevanceChoice",
                                                      new PropertyModel<String>( this, "relevanceFilter" ),
                                                      Arrays.asList( RelevanceChoices ) );
        relevanceChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( titleLabel );
                addSurveysTable();
                target.addComponent( surveysTable );
            }
        } );
        add( relevanceChoice );
    }

    public String getRelevanceFilter() {
        return relevanceFilter;
    }

    public void setRelevanceFilter( String relevanceFilter ) {
        this.relevanceFilter = relevanceFilter;
    }

    private void addSurveysTable() {
        surveysTable = new SurveysTable( "surveysTable", new PropertyModel<List<Survey>>( this, "surveyWrappers" ) );
        surveysTable.setOutputMarkupId( true );
        addOrReplace( surveysTable );
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addSurveysTable();
        target.addComponent( surveysTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    /**
     * Get all unfiltered surveys.
     *
     * @return a list of surveys
     */
    @SuppressWarnings( "unchecked" )
    public List<SurveyWrapper> getSurveyWrappers() {
        List<Survey> surveys = (List<Survey>) CollectionUtils.select( surveyService.getSurveys(), new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                Survey survey = (Survey) obj;
                return !stateFiltered || matchesRelevance( survey ) && matchesStatus( survey );
            }
        } );
        List<SurveyWrapper> wrappers = new ArrayList<SurveyWrapper>();
        for ( Survey survey : surveys ) {
            wrappers.add( new SurveyWrapper( survey ) );
        }
        return (List<SurveyWrapper>) CollectionUtils.select( wrappers, new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                return !isFilteredOut( (SurveyWrapper) obj );
            }
        } );
    }

    private boolean isFilteredOut( SurveyWrapper wrapper ) {
        ModelObject about = filters.get( "about" );
        if ( about != null && !about.equals( wrapper.getAbout() ) )
            return true;
        Segment segment = (Segment) filters.get( "segment" );
        return segment != null && !segment.equals( wrapper.getSegment() );
    }

    private boolean matchesStatus( Survey survey ) {
        return survey.isLaunched() && statusFilter.equals( LAUNCHED )
               || survey.isClosed() && statusFilter.equals( CLOSED ) || survey.canBeLaunched() && statusFilter.equals(
                NOT_YET_LAUNCHED );
    }

    private boolean matchesRelevance( Survey survey ) {
        boolean relevant = surveyService.isRelevant( survey, getQueryService() );
        return relevant && relevanceFilter.equals( RELEVANT ) || !relevant && relevanceFilter.equals( IRRELEVANT );
    }

    private void addSurveyContainer() {
        surveyContainer = new WebMarkupContainer( "surveyContainer" );
        surveyContainer.setOutputMarkupId( true );
        add( surveyContainer );
        addSurveyPanel();
        updateVisibility();
    }

    private void updateVisibility() {
        makeVisible( surveyContainer, !selectedSurvey.isUnknown() );
    }

    private void addSurveyPanel() {
        Component surveyPanel;
        if ( !selectedSurvey.isUnknown() ) {
            surveyPanel = new SurveyPanel( "survey", new Model<Survey>( selectedSurvey ) );
        } else {
            surveyPanel = new Label( "survey", "" );
        }
        surveyContainer.addOrReplace( surveyPanel );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( Survey.class ) ) {
            if ( change.isExpanded() ) {
                selectedSurvey = (Survey) change.getSubject( getQueryService() );
            }
            if ( change.isCollapsed() || change.isRemoved() ) {
                selectedSurvey = Survey.UNKNOWN;
            }
        }
        super.changed( change );
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Survey.class ) ) {
            if ( change.isUpdated() || change.isRemoved() ) {
                addSurveysTable();
                target.addComponent( surveysTable );
            }
            if ( selectedSurvey.isUnknown() && !change.isRemoved() )
                super.updateWith( target, change, updated );
            else {
                addSurveyPanel();
                updateVisibility();
                target.addComponent( surveyContainer );
            }
        } else
            super.updateWith( target, change, updated );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, String Aspect ) {
        if ( change.isForInstanceOf( Survey.class ) ) {
            addSurveysTable();
            target.addComponent( surveysTable );
        }
    }

    public class SurveyWrapper implements Serializable {

        private Survey survey;

        public SurveyWrapper( Survey survey ) {
            this.survey = survey;
        }

        public Survey getSurvey() {
            return survey;
        }

        public Date getCreationDate() {
            return survey.getCreationDate();
        }

        public String getFormattedCreationDate() {
            return survey.getFormattedCreationDate();
        }

        public String getIssuer() {
            return survey.getIssuer();
        }

        public String getTitle() {
            return survey.getTitle();
        }

        public int getContactedCount() {
            return survey.getContactedCount();
        }

        public Identifiable getAbout() {
            return survey.getAbout( surveyService.getAnalyst(), getQueryService() );
        }

        public Segment getSegment() {
            Identifiable about = getAbout();
            if ( about instanceof SegmentObject ) {
                return ( (SegmentObject) about ).getSegment();
            } else {
                return null;
            }
        }

        public String getStatus() {
            return survey.getStatus().getLabel();
        }

        public String getSurveyType() {
            return survey.getSurveyType();
        }
    }

    /**
     * Surveys table.
     */
    public class SurveysTable extends AbstractTablePanel<Survey> {

        private IModel<List<Survey>> surveysModel;

        public SurveysTable( String s, IModel<List<Survey>> model ) {
            super( s, null, null );
            this.surveysModel = model;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Type", "surveyType", EMPTY ) );
            columns.add( makeColumn( "created on", "formattedCreationDate", null, EMPTY, null, "creationDate" ) );
            columns.add( makeColumn( "by", "issuer", EMPTY ) );
            columns.add( makeColumn( "titled", "title", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "about", "about", "about.name", EMPTY, SurveysPanel.this ) );
            columns.add( makeFilterableLinkColumn( "in plan segment",
                                                   "segment",
                                                   "segment.name",
                                                   EMPTY,
                                                   SurveysPanel.this ) );
            columns.add( makeColumn( "# surveyed", "contactedCount", EMPTY ) );
            if ( !stateFiltered ) {
                columns.add( makeColumn( "status", "status", EMPTY ) );
            }
            columns.add( makeExpandLinkColumn( "", "survey", "more" ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable( "surveys",
                                                   columns,
                                                   new SortableBeanProvider<Survey>( surveysModel.getObject(),
                                                                                     "creationDate" ),
                                                   MAX_SURVEY_ROWS ) );
        }
    }
}
