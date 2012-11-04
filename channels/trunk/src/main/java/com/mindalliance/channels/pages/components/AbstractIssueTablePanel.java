/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFISurveyService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract issue table panel.
 */
public abstract class AbstractIssueTablePanel extends AbstractUpdatablePanel implements Filterable {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( AbstractUpdatablePanel.class );

    protected static final String ALL = "All";

    /**
     * Category of issues to show.
     */
    private String issueType = ALL;

    /**
     * Model objects filtered on (show only where so and so is the actor etc.).
     */
    private ModelObject about;

    /**
     * Issues table.
     */
    private IssuesTable issuesTable;

    /**
     * Maximum number of rows in table.
     */
    private int maxRows;

    /**
     * Survey service.
     */

    @SpringBean(name="surveysDao")
    SurveysDAO surveysDAO;

    @SpringBean
    RFISurveyService rfiSurveyService;


    public AbstractIssueTablePanel( String id, IModel<? extends ModelObject> model, int maxRows ) {
        super( id, model );
        this.maxRows = maxRows;
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addIssueTypeChoice();
        addFilters();
        addIssuesTable();
    }

    private void addIssueTypeChoice() {
        DropDownChoice<String> issueTypeChoice = new DropDownChoice<String>( "issueType",
                new PropertyModel<String>( this,
                        "issueType" ),
                getIssueTypeChoices() );
        issueTypeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesTable();
                target.add( issuesTable );
            }
        } );
        issueTypeChoice.setOutputMarkupId( true );
        addOrReplace( issueTypeChoice );
    }

    private List<String> getIssueTypeChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ALL );
        choices.addAll( Arrays.asList( Issue.TYPES ) );
        return choices;
    }

    protected ModelObject getAbout() {
        return about;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType( String issueType ) {
        this.issueType = issueType;
    }

    /**
     * Add fields that augment the scope for issues.
     */
    protected abstract void addFilters();

    private void addIssuesTable() {
        issuesTable = new IssuesTable( "issuesTable", new PropertyModel<List<Issue>>( this, "issues" ) );
        issuesTable.setOutputMarkupId( true );
        addOrReplace( issuesTable );
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // only about is filtered; property is ignored
        about = identifiable == about ? null : (ModelObject) identifiable;
        addIssuesTable();
        target.add( issuesTable );
    }

    /**
     * Get all issues, possibly filtered on the model object they are about.
     *
     * @return a list of issues
     */
    public abstract List<? extends Issue> getIssues();

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return identifiable == about;
    }

    /**
     * Update issues table.
     *
     * @param target an ajax request target
     */
    protected void updateIssuesTable( AjaxRequestTarget target ) {
        addIssuesTable();
        target.add( issuesTable );
    }

    /**
     * Issues table.
     */
    public class IssuesTable extends AbstractTablePanel<Issue> {

        /**
         * Issue list model.
         */
        private IModel<List<Issue>> issuesModel;

        public IssuesTable( String id, IModel<List<Issue>> issuesModel ) {
            super( id, null, maxRows, null );
            this.issuesModel = issuesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn( "Kind", "detectorLabel", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "About",
                    "about",
                    "about.label",
                    EMPTY,
                    AbstractIssueTablePanel.this ) );
            columns.add( makeColumn( "Severity", "severity.negativeLabel", null, EMPTY, null, "severity.ordinal" ) );
            columns.add( makeColumn( "Description", "description", EMPTY ) );
            columns.add( makeColumn( "Remediation", "remediation", EMPTY ) );
            columns.add( makeColumn( "Reported by", "reportedBy", EMPTY ) );
            columns.add( makeColumn( "Waived", "waivedString", EMPTY ) );

            if ( getPlan().isDevelopment() ) {
                columns.add( makeSurveyColumn( "Survey" ) );
            }
            // provider and table
            add( new AjaxFallbackDefaultDataTable( "issues",
                    columns,
                    new SortableBeanProvider<Issue>( issuesModel.getObject(), "kind" ),
                    getPageSize() ) );
        }


        private AbstractColumn<Issue> makeSurveyColumn( String name ) {
            return new AbstractColumn<Issue>( new Model<String>( name ) ) {
                @Override
                public void populateItem( Item<ICellPopulator<Issue>> cellItem, String id, IModel<Issue> issueModel ) {
                    Issue issue = issueModel.getObject();
                    boolean surveyed = rfiSurveyService.findRemediationSurvey(
                            getPlan(),
                            issue,
                            getQueryService() ) != null;
                    SurveyLinkPanel surveyLinkPanel = new SurveyLinkPanel( id, surveyed, issue );
                    cellItem.add( surveyLinkPanel );
                }
            };
        }

    }

    private class SurveyLinkPanel extends AbstractUpdatablePanel {

        private SurveyLinkPanel( String id, boolean surveyed, final Issue issue ) {
            super( id );
            AjaxFallbackLink link = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                        RFISurvey survey = surveysDAO.getOrCreateRemediationSurvey(
                                getUsername(),
                                getPlan(),
                                getQueryService(),
                                issue );
                        // Open all surveys panel on this survey
                        Change change = new Change( Change.Type.Expanded, survey );
                        change.setId( RFISurvey.UNKNOWN.getId() );
                        update( target, change );
                }
            };
            add( link );
            WebMarkupContainer image = new WebMarkupContainer( "image" );
            image.add( new AttributeModifier( "src",
                    new Model<String>( surveyed ?
                            "images/survey_small.png" :
                            "images/survey_add_small.png" ) ) );
            image.add( new AttributeModifier( "alt",
                    new Model<String>( surveyed ? "View survey" : "Create new survey" ) ) );
            image.add( new AttributeModifier( "title",
                    new Model<String>( surveyed ? "View survey" : "Create new survey" ) ) );
            link.add( image );
        }
    }
}
