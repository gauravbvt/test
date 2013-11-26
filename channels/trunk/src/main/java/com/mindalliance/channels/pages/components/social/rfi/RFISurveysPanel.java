package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.RFISurveyService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RFI surveys panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/6/12
 * Time: 2:36 PM
 */
public class RFISurveysPanel extends AbstractCommandablePanel implements Filterable, Guidable {

    private static final String ANYTHING = "anything";

    @SpringBean
    private QuestionnaireService questionnaireService;

    @SpringBean
    private RFISurveyService rfiSurveyService;

    @SpringBean
    private RFIService rfiService;

    @SpringBean(name = "surveysDao")
    private SurveysDAO surveysDAO;

    private String about = null;
    private boolean onlyLaunched = false;
    private boolean onlyAnswered = false;
    private RFISurvey selectedRFISurvey;
    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();
    private WebMarkupContainer rfiSurveyContainer;
    private static final int MAX_ROWS = 5;
    private RFISurveyTable rfiSurveyTable;


    public RFISurveysPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        super( id, rfiSurveyModel );
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

    @Override
    // Use the domain community
    public CommunityService getCommunityService() {
        return getCommunityService( getDomainPlanCommunity() );
    }


    private void init() {
        RFISurvey rfiSurvey = getRFISurvey();
        if ( !rfiSurvey.isUnknown() )
            selectedRFISurvey = rfiSurvey;
        addFilters();
        addRFISurveyTable();
        addRFISurvey();
    }

    private void addFilters() {
        // launched
        AjaxCheckBox activeCheckBox = new AjaxCheckBox(
                "launched",
                new PropertyModel<Boolean>( this, "onlyLaunched" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            }
        };
        add( activeCheckBox );
        // about
        DropDownChoice<String> aboutChoice = new DropDownChoice<String>(
                "aboutChoice",
                new PropertyModel<String>( this, "about" ),
                getAboutChoices() );
        aboutChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            }
        } );
        add( aboutChoice );
        // answered
        AjaxCheckBox answeredCheckBox = new AjaxCheckBox(
                "answered",
                new PropertyModel<Boolean>( this, "onlyAnswered" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            }
        };
        add( answeredCheckBox );
    }

    private List<String> getAboutChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ANYTHING );
        choices.addAll( ModelObject.CLASS_LABELS );
        return choices;
    }


    private void addRFISurveyTable() {
        rfiSurveyTable = new RFISurveyTable(
                "rfiSurveys",
                new PropertyModel<List<RFISurveyWrapper>>( this, "rfiSurveyWrappers" )
        );
        rfiSurveyTable.setOutputMarkupId( true );
        addOrReplace( rfiSurveyTable );
    }

    public List<RFISurveyWrapper> getRfiSurveyWrappers() {
        List<RFISurveyWrapper> wrappers = new ArrayList<RFISurveyWrapper>();
        CommunityService communityService = getCommunityService();
        List<RFISurvey> rfiSurveys = rfiSurveyService.select(
                communityService,
                onlyLaunched, // restrict to open surveys or not
                about );
        for ( RFISurvey rfiSurvey : rfiSurveys ) {
            if ( !rfiSurvey.isObsolete( communityService ) ) {
                if ( !onlyAnswered || getAnsweredCount( rfiSurvey ) > 0 )
                    if ( !isFilteredOut( rfiSurvey ) ) {
                        rfiSurvey = rfiSurveyService.load( rfiSurvey.getUid() );
                        wrappers.add( new RFISurveyWrapper( rfiSurvey ) );
                    }
            }
        }
        return wrappers;
    }

    private void addRFISurvey() {
        CommunityService communityService = getCommunityService();
        rfiSurveyContainer = new WebMarkupContainer( "rfiSurveyContainer" );
        rfiSurveyContainer.setOutputMarkupId( true );
        makeVisible( rfiSurveyContainer, selectedRFISurvey != null );
        rfiSurveyContainer.add( new Label(
                "rfiSurveyLabel",
                new PropertyModel<String>( this, "rfiSurveyLabel" ) ) );
        // activate
        AjaxLink<String> activateButton = new AjaxLink<String>( "activate" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                rfiSurveyService.toggleActivation( selectedRFISurvey );
                selectedRFISurvey = rfiSurveyService.load( selectedRFISurvey.getUid() );
                addRFISurveyTable();
                addRFISurvey();
                target.add( rfiSurveyTable );
                target.add( rfiSurveyContainer );
            }
        };
        activateButton.add( new AttributeModifier(
                "value",
                new Model<String>( selectedRFISurvey == null
                        || selectedRFISurvey.isObsolete( communityService )
                        ? ""
                        : selectedRFISurvey.isClosed()
                        ? "Activate this survey"
                        : "Retire this survey" ) ) );
        activateButton.setVisible( selectedRFISurvey != null
                && !selectedRFISurvey.isObsolete( communityService ) );
        rfiSurveyContainer.add( activateButton );
        // can forward
        AjaxCheckBox canForwardCheckBox = new AjaxCheckBox(
                "canForward",
                new PropertyModel<Boolean>( this, "canBeForwarded" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                //do nothing
            }
        };
        rfiSurveyContainer.add( canForwardCheckBox );
        // preview
        AjaxLink<String> previewLink = new AjaxLink<String>( "preview" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                openRFIPreview( target );
            }
        };
        rfiSurveyContainer.add( previewLink );
        // rfi panel
        rfiSurveyContainer.add( selectedRFISurvey == null
                ? new Label( "rfiSurvey", "" )
                : new RFISurveyPanel(
                "rfiSurvey",
                new Model<RFISurvey>( selectedRFISurvey ) ) );

        addOrReplace( rfiSurveyContainer );
    }

    private void openRFIPreview( AjaxRequestTarget target ) {
        RFI transientRFI = new RFI();
        transientRFI.setRfiSurvey( selectedRFISurvey );
        // open in dialog
        SurveyAnswersPanel surveyAnswersPanel = new SurveyAnswersPanel(
                getModalableParent().getModalContentId(),
                new Model<RFI>( transientRFI ),
                true ); // readOnly
        getModalableParent().showDialog(
                "Survey preview",
                600,
                800,
                surveyAnswersPanel,
                RFISurveysPanel.this,
                target
        );
    }

    public String getRfiSurveyLabel() {
        return selectedRFISurvey == null
                ? ""
                : selectedRFISurvey.getSurveyLabel( getCommunityService() );
    }

    public RFISurvey getSelectedRFISurvey() {
        return selectedRFISurvey;
    }

    public void select( RFISurvey rfiSurvey ) {
        setSelectedRFISurvey( rfiSurvey == null || rfiSurvey.isUnknown()
                ? null
                : rfiSurvey );
    }


    public void setSelectedRFISurvey( RFISurvey rfiSurvey ) {
        unlockRFISurvey();
        selectedRFISurvey = rfiSurvey == null || rfiSurvey.isUnknown() ? null : rfiSurvey;
        lockRFISurvey();
        refreshSelected();
    }

    private void unlockRFISurvey() {
        if ( selectedRFISurvey != null ) {
            releaseAnyLockOn( selectedRFISurvey );
        }
    }

    private void lockRFISurvey() {
        if ( selectedRFISurvey != null ) {
            requestLockOn( selectedRFISurvey );
        }
    }

    private void refreshSelected() {
        if ( selectedRFISurvey != null )
            selectedRFISurvey = rfiSurveyService.load( selectedRFISurvey.getUid() );
    }


    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addRFISurvey();
        target.add( rfiSurveyContainer );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    private boolean isFilteredOut( RFISurvey rfiSurvey ) {
        Segment segment = (Segment) filters.get( "segment" );
        return segment != null && !segment.equals( rfiSurvey.getSegment( getCommunityService() ) );
    }

    public boolean isOnlyLaunched() {
        return onlyLaunched;
    }

    public void setOnlyLaunched( boolean onlyLaunched ) {
        this.onlyLaunched = onlyLaunched;
    }

    public boolean isOnlyAnswered() {
        return onlyAnswered;
    }

    public void setOnlyAnswered( boolean onlyAnswered ) {
        this.onlyAnswered = onlyAnswered;
    }

    public String getAbout() {
        return ( about == null || about.isEmpty() ) ? ANYTHING : about;
    }

    public void setAbout( String val ) {
        this.about = val.equals( ANYTHING )
                ? null
                : val;
    }

    private int getAnsweredCount( RFISurvey rfiSurvey ) {
        return surveysDAO.findAnsweringRFIs( getCommunityService(), rfiSurvey ).size();
    }

    public boolean isCanBeForwarded() {
        return selectedRFISurvey != null && selectedRFISurvey.isCanBeForwarded();
    }

    public void setCanBeForwarded( boolean val ) {
        if ( selectedRFISurvey != null ) {
            selectedRFISurvey.setCanBeForwarded( val );
            rfiSurveyService.save( selectedRFISurvey );
        }
    }

    private RFISurvey getSelectedSurvey() {
        return selectedRFISurvey;
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RFISurveyWrapper.class ) ) {
            RFISurveyWrapper fw = (RFISurveyWrapper) change.getSubject( getCommunityService() );
            if ( selectedRFISurvey != null ) rfiSurveyService.refresh( selectedRFISurvey );
            RFISurvey rfiSurvey = fw.getRfiSurvey();
            if ( change.isExpanded() ) {
                if ( selectedRFISurvey != null && rfiSurvey.equals( selectedRFISurvey ) ) {
                    setSelectedRFISurvey( null );
                } else {
                    if ( !isLockedByOtherUser( rfiSurvey ) )
                        setSelectedRFISurvey( rfiSurveyService.refresh( rfiSurvey ) );  // acquires lock
                }
            }
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( RFISurveyWrapper.class ) && change.isExpanded() ) {
            RFISurveyWrapper fw = (RFISurveyWrapper) change.getSubject( getCommunityService() );
            RFISurvey rfiSurvey = fw.getRfiSurvey();
            if ( isLockedByOtherUser( rfiSurvey ) ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            } else {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
                addRFISurvey();
                target.add( rfiSurveyContainer );
            }
        } else if ( change.isForInstanceOf( RFISurvey.class ) ) {
            addRFISurveyTable();
            target.add( rfiSurveyTable );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    private RFISurvey getRFISurvey() {
        return (RFISurvey) getModel().getObject();
    }

    public void clearSelectionWith( Releaseable releaseable ) {
        if ( selectedRFISurvey != null ) {
            releaseable.releaseAnyLockOn( selectedRFISurvey );
        }
    }

    public class RFISurveyWrapper implements Identifiable {

        private RFISurvey rfiSurvey;
        private Map<String, Integer> metrics;

        private RFISurveyWrapper( RFISurvey rfiSurvey ) {
            this.rfiSurvey = rfiSurvey;
        }

        public RFISurvey getRfiSurvey() {
            return rfiSurvey;
        }

        @Override
        public long getId() {
            return rfiSurvey.getId();
        }

        @Override
        public String getDescription() {
            return rfiSurvey.getDescription();
        }

        @Override
        public String getTypeName() {
            return rfiSurvey.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return rfiSurvey.isModifiableInProduction();
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getKindLabel() {
            return getTypeName();
        }

        @Override
        public String getName() {
            return rfiSurvey.getName();
        }

        public Date getCreated() {
            return rfiSurvey.getCreated();
        }

        public String getAbout() {
            return StringUtils.capitalize( getQuestionnaire().getAbout() );
        }

        public ModelObject getModelObject() {
            return rfiSurvey.getModelObject( getCommunityService() );
        }

        public Questionnaire getQuestionnaire() {
            return questionnaireService.load( rfiSurvey.getQuestionnaireUid() );
        }

        public Segment getSegment() {
            return rfiSurvey.getSegment( getCommunityService() );
        }

        public String getMoLabel() {
            ModelObject mo = getModelObject();
            if ( mo == null ) {
                return null;
            } else {
                return StringUtils.capitalize( mo.getTypeName() )
                        + " \""
                        + mo.getLabel()
                        + "\"";
            }
        }

        public String getStatusLabel() {
            return rfiSurvey.getStatusLabel( getCommunityService() );
        }

        public int getSentToCount() {
            return rfiService.select( getCommunityService(), rfiSurvey ).size();
        }

        public String getShortResponseMetrics() {
            if ( rfiSurvey != null ) {
                Map<String, Integer> metrics = getResponseMetrics();
                Integer[] values = new Integer[3];
                values[0] = metrics.get( "completed" );
                values[1] = metrics.get( "declined" );
                values[2] = metrics.get( "incomplete" );
                return MessageFormat.format( "{0}c {1}d {2}i", values );
            } else
                return null;
        }

        public String getLongResponseMetrics() {
            if ( rfiSurvey != null ) {
                Map<String, Integer> metrics = getResponseMetrics();
                Integer[] values = new Integer[3];
                values[0] = metrics.get( "completed" );
                values[1] = metrics.get( "declined" );
                values[2] = metrics.get( "incomplete" );
                return MessageFormat.format( "{0} completed, {1} declined and {2} incomplete", values );
            } else
                return null;
        }

        private Map<String, Integer> getResponseMetrics() {
            if ( metrics == null ) {
                metrics = surveysDAO.findResponseMetrics( getCommunityService(), rfiSurvey );
            }
            return metrics;
        }

        public String getExpandLabel() {
            RFISurvey selected = getSelectedSurvey();
            return selected != null && selected.equals( rfiSurvey )
                    ? "Close"
                    : isLockedByOtherUser( rfiSurvey )
                    ? ( getUserFullName( getLockOwner( rfiSurvey ) ) + " modifying" )
                    : "Open";
        }


    }

    public class RFISurveyTable extends AbstractTablePanel<RFISurveyWrapper> {

        private IModel<List<RFISurveyWrapper>> rfiSurveysModel;

        public RFISurveyTable( String id, IModel<List<RFISurveyWrapper>> rfiSurveysModel ) {
            super( id );
            this.rfiSurveysModel = rfiSurveysModel;
            initialize();
        }

        @SuppressWarnings("unchecked")
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Questionnaire", "questionnaire.name", EMPTY ) );
            columns.add( makeColumn( "About", "about", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "Applied to", "modelObject", "moLabel", EMPTY, RFISurveysPanel.this ) );
            columns.add( makeFilterableLinkColumn( "In segment", "segment", "segment.name", EMPTY, RFISurveysPanel.this ) );
            columns.add( makeColumn( "Status", "statusLabel", EMPTY ) );
            columns.add( makeColumn( "Sent to", "sentToCount", EMPTY ) );
            columns.add( makeColumn( "Responses", "shortResponseMetrics", null, EMPTY, "longResponseMetrics" ) );
            columns.add( makeExpandLinkColumn( "", "", "@expandLabel" ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "rfiSurveys",
                    columns,
                    new SortableBeanProvider<RFISurveyWrapper>( rfiSurveysModel.getObject(),
                            "created" ),
                    MAX_ROWS ) );

        }
    }

}
