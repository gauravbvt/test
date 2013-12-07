/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.IssueDetectionWaiver;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Waivable;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.services.surveys.RFISurveyService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.components.menus.IssueActionsMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CollapsedIssuePanel extends AbstractCommandablePanel {

    /**
     * Survey service.
     */
    @SpringBean(name = "surveysDao")
    private SurveysDAO surveysDao;

    @SpringBean
    private RFISurveyService rfiSurveyService;

    /**
     * Issue in panel.
     */
    private IModel<Issue> model;

    public CollapsedIssuePanel( String id, IModel<Issue> model ) {
        super( id );
        this.model = model;
        init();
    }

    private void init() {
        this.setOutputMarkupId( true );
        final Issue issue = getIssue();
        addMenubar( issue );
        addSummary();
        addWaiving( issue );
        addSurveying( issue );
    }


    private void addSurveying( final Issue issue ) {
        WebMarkupContainer surveyLinkContainer = new WebMarkupContainer( "surveyLinkContainer" );
        surveyLinkContainer.setVisible( issue.isDetected() && !issue.isWaived( getCommunityService() ) );
        add( surveyLinkContainer );
        IndicatingAjaxFallbackLink surveyLink = new IndicatingAjaxFallbackLink( "surveyLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                RFISurvey survey = surveysDao.getOrCreateRemediationSurvey(
                        getUsername(),
                        getCommunityService(),
                        issue );
                // Open all surveys panel on this survey
                Change change = new Change( Change.Type.Expanded, survey );
                change.setId( RFISurvey.UNKNOWN.getId() );
                update( target, change );
            }
        };
        surveyLinkContainer.add( surveyLink );
        Label surveyActionLabel = new Label(
                "surveyAction",
                new Model<String>(
                        rfiSurveyService.findRemediationSurvey( getCommunityService(), issue ) != null
                                ? "View survey"
                                : "Create survey"
                ) );
        surveyLink.add( surveyActionLabel );
        surveyLink.setVisible( getPlan().isDevelopment() );
    }


    private void addMenubar( Issue issue ) {
        WebMarkupContainer menubar = new WebMarkupContainer( "menubar" );
        add( menubar );
        IssueActionsMenuPanel actionsMenu = new IssueActionsMenuPanel(
                "issueActionsMenu",
                new Model<Issue>( model.getObject() ),
                true );
        menubar.add( actionsMenu );
        makeVisible( menubar, !issue.isDetected() );
    }

    private void addWaiving( final Issue issue ) {
        WebMarkupContainer waivedContainer = new WebMarkupContainer( "waived-container" );
        makeVisible( waivedContainer, issue.canBeWaived() );
        add( waivedContainer );
        CheckBox waiveCheckBox = new CheckBox(
                "waived",
                new PropertyModel<Boolean>( this, "waived" ) );
        waiveCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change(
                        Change.Type.Updated,
                        issue.getAbout(),
                        "waivedIssueDetections" ) );
            }
        } );
        Identifiable about = issue.getAbout();
        waivedContainer.setVisible( isLockedByUserIfNeeded( about ) );
        waivedContainer.add( waiveCheckBox );
    }

    private void addSummary() {
        final Issue issue = getIssue();
        WebMarkupContainer summary = new WebMarkupContainer( "summary" );
        summary.setOutputMarkupId( true );
        summary.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                if ( !issue.isDetected() )
                    update( target, new Change( Change.Type.Expanded, getIssue() ) );
            }
        } );
        if ( !issue.isDetected() ) {
            summary.add( new AttributeModifier( "class", new Model<String>( "pointer" ) ) );
        }
        addOrReplace( summary );
        Label label;
        Label suggestion;
        if ( issue.isDetected() ) {
            label = new Label( "issue-label", new PropertyModel( issue, "label" ) );
            suggestion = new Label( "issue-suggestion", new PropertyModel( this, "remediation" ) );
        } else {
            label = new Label( "issue-label", new AbstractReadOnlyModel() {

                public Object getObject() {
                    return issue.getLabel( IssuesPanel.MAX_LENGTH, getCommunityService() );
                }
            } );
            suggestion = new Label( "issue-suggestion", new AbstractReadOnlyModel() {

                public Object getObject() {
                    return StringUtils.abbreviate(
                            issue.getRemediation().replaceAll( "\n", " " ),
                            IssuesPanel.MAX_LENGTH );
                }
            } );
        }
        summary.add( label );
        summary.add( suggestion );
    }

    private Issue getIssue() {
        return model.getObject();
    }

    /**
     * Whether this kind of issue is waived for this model object.
     *
     * @return a boolean
     */
    public boolean isWaived() {
        return getIssue().isWaived( getCommunityService() );
    }

    /**
     * get remediation with ending period.
     *
     * @return a string
     */
    public String getRemediation() {
        String remediation = getIssue().getRemediation();
        if ( !remediation.endsWith( "." ) ) remediation += ".";
        return remediation;
    }

    /**
     * Waive or unwaive this kind of issue for this model object.
     *
     * @param waive a boolean
     */
    public void setWaived( boolean waive ) {
        Identifiable about = getIssue().getAbout();
        Command command;
        if ( about instanceof Waivable ) {
            if ( about instanceof ModelObject ) { // the identifiable is persisted, store the waived detector name in it
                command = UpdateObject.makeCommand(
                        getUser().getUsername(),
                        getIssue().getAbout(),
                        "waivedIssueDetections",
                        getIssue().getKind(),
                        waive ? UpdateObject.Action.AddUnique : UpdateObject.Action.Remove );
            } else { // the identifiable is not persistent so store the waiver in the plan community
                command = UpdateObject.makeCommand(
                        getUser().getUsername(),
                        getPlanCommunity(),
                        "issueDetectionWaivers",
                        new IssueDetectionWaiver( about, getIssue().getKind() ),
                        waive ? UpdateObject.Action.AddUnique : UpdateObject.Action.Remove );
            }
            doCommand( command );
        }
    }
}
