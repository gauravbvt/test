package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.services.QuestionnaireService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.commons.lang.StringUtils;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/6/12
 * Time: 2:37 PM
 */
public class QuestionnairesPanel extends AbstractUpdatablePanel {

    private static final String ANYTHING = "anything";
    private static final String ALL = "all";


    @SpringBean
    private QuestionnaireService questionnaireService;

    @SpringBean
    private RFIService rfiService;

    @SpringBean
    private RFISurveyService rfiSurveyService;

    private Questionnaire selectedQuestionnaire;

    private String about;
    private String status;
    private boolean usedInSurveysOnly = false;
    private boolean remediation = false;
    private WebMarkupContainer questionnaireContainer;

    private static final int MAX_ROWS = 10;
    private QuestionnaireTable questionnaireTable;


    public QuestionnairesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addFilters();
        addQuestionnaireTable();
        addNewQuestionnaireButton();
        addQuestionnaireContainer();
    }

    private void addFilters() {
        // status choice
        DropDownChoice<String> statusChoice = new DropDownChoice<String>(
                "statusChoice",
                new PropertyModel<String>( this, "status" ),
                getStatusChoices() );
        statusChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionnaireTable();
                target.add( questionnaireTable );
            }
        } );
        add( statusChoice );
        // about choice
        DropDownChoice<String> aboutChoice = new DropDownChoice<String>(
                "aboutChoice",
                new PropertyModel<String>( this, "about" ),
                getAboutChoices() );
        aboutChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionnaireTable();
                target.add( questionnaireTable );
            }
        } );
        add( aboutChoice );
        // remediation or not
        AjaxCheckBox remediationCheckBox = new AjaxCheckBox(
                "remediation",
                new PropertyModel<Boolean>( this, "remediation" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionnaireTable();
                target.add( questionnaireTable );
            }
        };
        add( remediationCheckBox );
        // used in surveys
        AjaxCheckBox usedCheckBox = new AjaxCheckBox(
                "usedOnly",
                new PropertyModel<Boolean>( this, "usedInSurveysOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionnaireTable();
                target.add( questionnaireTable );
            }
        };
        add( usedCheckBox );
    }

    private List<String> getStatusChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ALL );
        for ( Questionnaire.Status value : Questionnaire.Status.values() ) {
            choices.add( value.name().toLowerCase() );
        }
        return choices;
    }

    private List<String> getAboutChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ANYTHING );
        choices.addAll( ModelObject.CLASS_LABELS );
        return choices;
    }

    private void addQuestionnaireTable() {
        questionnaireTable = new QuestionnaireTable(
                "questionnaires",
                new PropertyModel<List<QuestionnaireWrapper>>( this, "filteredQuestionnaires" ) );
        questionnaireTable.setOutputMarkupId( true );
        addOrReplace( questionnaireTable );
    }

    private void addQuestionnaireContainer() {
        questionnaireContainer = new WebMarkupContainer( "questionnaire" );
        questionnaireContainer.setOutputMarkupId( true );
        makeVisible(
                questionnaireContainer,
                selectedQuestionnaire != null );
        // Label
        addQuestionnaireLabel();
        // Questions
        questionnaireContainer.add(
                selectedQuestionnaire == null
                        ? new Label( "questionnaireManager", "" )
                        : new QuestionnaireManagerPanel(
                        "questionnaireManager",
                        new PropertyModel<Questionnaire>( this, "selectedQuestionnaire" ) ) );
        // delete
        ConfirmedAjaxFallbackLink<String> deleteButton = new ConfirmedAjaxFallbackLink<String>(
                "deleteQuestionnaire",
                "Delete this questionnaire?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                deleteQuestionnaire();
                addQuestionnaireTable();
                target.add( questionnaireTable );
                addQuestionnaireContainer();
                target.add( questionnaireContainer );
            }
        };
        questionnaireContainer.add( deleteButton );
        // activate
        ConfirmedAjaxFallbackLink<String> activateButton = new ConfirmedAjaxFallbackLink<String>(
                "activateQuestionnaire",
                "Activate this questionnaire?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                activateQuestionnaire();
                addQuestionnaireTable();
                target.add( questionnaireTable );
                addQuestionnaireContainer();
                target.add( questionnaireContainer );
            }
        };
        activateButton.setVisible( selectedQuestionnaire != null && !selectedQuestionnaire.isActive() );
        questionnaireContainer.add( activateButton );
        // retire
        ConfirmedAjaxFallbackLink<String> retireButton = new ConfirmedAjaxFallbackLink<String>(
                "retireQuestionnaire",
                "Deactivate this questionnaire?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                retireQuestionnaire();
                addQuestionnaireTable();
                target.add( questionnaireTable );
                addQuestionnaireContainer();
                target.add( questionnaireContainer );
            }
        };
        retireButton.setVisible( selectedQuestionnaire != null && selectedQuestionnaire.isActive() );
        questionnaireContainer.add( retireButton );
        addOrReplace( questionnaireContainer );
    }

    private void addQuestionnaireLabel() {
        Label label = new Label( "questionnaireLabel",
                selectedQuestionnaire == null
                        ? ""
                        : selectedQuestionnaire.toString() );
        label.setOutputMarkupId( true );
        questionnaireContainer.addOrReplace( label );
    }

    private void deleteQuestionnaire() {
        if ( selectedQuestionnaire != null )
            questionnaireService.delete( selectedQuestionnaire );
        selectedQuestionnaire = null;
    }

    private void activateQuestionnaire() {
        if ( selectedQuestionnaire != null ) {
            selectedQuestionnaire.setStatus( Questionnaire.Status.ACTIVE );
            questionnaireService.save( selectedQuestionnaire );
        }
    }

    private void retireQuestionnaire() {
        if ( selectedQuestionnaire != null ) {
            selectedQuestionnaire.setStatus( Questionnaire.Status.INACTIVE );
            questionnaireService.save( selectedQuestionnaire );
        }
    }

    public List<QuestionnaireWrapper> getFilteredQuestionnaires() {
        List<QuestionnaireWrapper> wrappers = new ArrayList<QuestionnaireWrapper>();
        List<Questionnaire> questionnaires = questionnaireService.select(
                getPlan(),
                getAbout().equals( ANYTHING ) ? null : getAbout(),
                getStatus().equals( ALL ) ? null : Questionnaire.Status.valueOf( getStatus().toUpperCase() ),
                isRemediation()
        );
        for ( Questionnaire questionnaire : questionnaires ) {
            if ( !questionnaire.isObsolete( getQueryService(), getAnalyst() ) ) {
                QuestionnaireWrapper wrapper = new QuestionnaireWrapper( questionnaire );
                if ( isRemediation() == questionnaire.isIssueRemediation() )
                    if ( !isUsedInSurveysOnly() || wrapper.getSurveyCount() > 0 )
                        wrappers.add( wrapper );
            }
        }
        return wrappers;
    }


    public void select( Questionnaire questionnaire ) {
        selectedQuestionnaire = questionnaire.isUnknown()
                ? null
                : selectedQuestionnaire;
        refreshSelected();
    }


    public String getAbout() {
        return about == null ? ANYTHING : about;
    }

    public String getDefaultAbout() {
        return about == null ? ModelObject.CLASS_LABELS.get( 0 ) : about;
    }

    public void setAbout( String about ) {
        this.about = about.equals( ANYTHING ) ? null : about;
    }

    public String getStatus() {
        return ( status == null || status.isEmpty() ) ? ALL : status;
    }

    public void setStatus( String status ) {
        this.status = status.equals( ALL ) ? null : status;
    }

    public boolean isUsedInSurveysOnly() {
        return usedInSurveysOnly;
    }

    public void setUsedInSurveysOnly( boolean usedInSurveysOnly ) {
        this.usedInSurveysOnly = usedInSurveysOnly;
    }

    public boolean isRemediation() {
        return remediation;
    }

    public void setRemediation( boolean remediation ) {
        this.remediation = remediation;
    }

    private void refreshSelected() {
        if ( selectedQuestionnaire != null ) questionnaireService.refresh( selectedQuestionnaire );
    }

    public Questionnaire getSelectedQuestionnaire() {
        return selectedQuestionnaire;
    }

    public void setSelectedQuestionnaire( Questionnaire questionnaire ) {
        selectedQuestionnaire = questionnaire;
    }

    private void addNewQuestionnaireButton() {
        AjaxLink newQuestionnaireButton = new AjaxLink( "newQuestionnaire" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                selectedQuestionnaire = new Questionnaire( getPlan(), getUsername() );
                selectedQuestionnaire.setAbout( getDefaultAbout() );
                questionnaireService.save( selectedQuestionnaire );
                addQuestionnaireTable();
                target.add( questionnaireTable );
                addQuestionnaireContainer();
                target.add( questionnaireContainer );
            }
        };
        add( newQuestionnaireButton );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( QuestionnaireWrapper.class ) && change.isExpanded() ) {
            QuestionnaireWrapper qw = (QuestionnaireWrapper) change.getSubject( getQueryService() );
            setSelectedQuestionnaire( qw.getQuestionnaire() );
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( QuestionnaireWrapper.class ) && change.isExpanded() ) {
            addQuestionnaireContainer();
            target.add( questionnaireContainer );
        } else {
            if ( change.isUpdated() && change.isForInstanceOf( Questionnaire.class ) ) {
                addQuestionnaireTable();
                target.add( questionnaireTable );
                addQuestionnaireLabel();
                target.add( questionnaireContainer );
            } else {
                super.updateWith( target, change, updated );
            }
        }
        target.add( this );
    }


    public class QuestionnaireWrapper implements Identifiable {

        private final Questionnaire questionnaire;

        public QuestionnaireWrapper( Questionnaire questionnaire ) {
            this.questionnaire = questionnaire;
        }

        public Questionnaire getQuestionnaire() {
            return questionnaire;
        }

        @Override
        public long getId() {
            return questionnaire.getId();
        }

        @Override
        public String getDescription() {
            return questionnaire.getDescription();
        }

        @Override
        public String getTypeName() {
            return questionnaire.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return questionnaire.isModifiableInProduction();
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getName() {
            return questionnaire.getName();
        }

        public String getAbout() {
            return StringUtils.capitalize( questionnaire.getAbout() );
        }

        public String getStatus() {
            return questionnaire.getStatus().name();
        }

        public String getAuthor() {
            return getUserFullName( questionnaire.getUsername() );
        }

        public String getCreatedOn() {
            return getDateFormat().format( questionnaire.getCreated() );
        }

        public String getLastModifiedOn() {
            Date lastModified = questionnaire.getLastModified();
            return lastModified == null ? null : getDateFormat().format( questionnaire.getLastModified() );
        }

        public int getRfiCount() {
            return rfiService.getRFICount( getPlan(), questionnaire );
        }

        public int getSurveyCount() {
            return rfiSurveyService.findSurveys( getPlan(), questionnaire ).size();
        }
    }

    public class QuestionnaireTable extends AbstractTablePanel<QuestionnaireWrapper> {

        private IModel<List<QuestionnaireWrapper>> questionnairesModel;

        public QuestionnaireTable( String id, IModel<List<QuestionnaireWrapper>> questionnairesModel ) {
            super( id );
            this.questionnairesModel = questionnairesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Questionnaire", "name", EMPTY ) );
            columns.add( makeColumn( "About", "about", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeColumn( "Created on", "createdOn", EMPTY ) );
            columns.add( makeColumn( "By", "author", EMPTY ) );
            columns.add( makeColumn( "Last modified on", "lastModifiedOn", EMPTY ) );
            columns.add( makeColumn( "# of surveys", "surveyCount", EMPTY ) );
            columns.add( makeColumn( "# of RFIs", "rfiCount", EMPTY ) );
            columns.add( makeExpandLinkColumn( "", "", "edit..." ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "questionnaires",
                    columns,
                    new SortableBeanProvider<QuestionnaireWrapper>( questionnairesModel.getObject(),
                            "createdOn" ),
                    MAX_ROWS ) );

        }
    }
}

