package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.commands.AddProducer;
import com.mindalliance.channels.command.commands.RemoveProducer;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan versions panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 14, 2009
 * Time: 4:30:09 PM
 */
public class PlanVersionsPanel extends AbstractCommandablePanel {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( PlanVersionsPanel.class );
    /**
     * Date format.
     */
    private SimpleDateFormat dateFormat;

    public PlanVersionsPanel(
            String id,
            IModel<? extends Identifiable> iModel,
            Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    private void init() {
        dateFormat = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" );
        addCurrentVersion();
        addProductionVersion();
        addDevelopmentVersion();
        addVotes();
    }

    private void addCurrentVersion() {
        Label versionLabel = new Label(
                "currentVersion", "" + getPlan().getVersion() );
        add( versionLabel );
    }

    private void addProductionVersion() {
        Plan prodPlan = getPlanManager().findProductionPlan( getPlan().getUri() );
        WebMarkupContainer prodContainer = new WebMarkupContainer( "prod" );
        add( prodContainer );
        Label versionLabel = new Label(
                "prodVersion",
                prodPlan == null ? "" : "" + prodPlan.getVersion() );
        prodContainer.add( versionLabel );
        Label dateLabel = new Label(
                "prodDate",
                prodPlan == null ? "" : "" + dateFormat.format( prodPlan.getWhenVersioned() ) );
        prodContainer.add( dateLabel );
        prodContainer.setVisible( prodPlan != null );
    }

    private void addDevelopmentVersion() {
        Plan devPlan = getPlanManager().findDevelopmentPlan( getPlan().getUri() );
        Label versionLabel = new Label(
                "devVersion", "" + devPlan.getVersion() );
        add( versionLabel );
        Label dateLabel = new Label(
                "devDate", dateFormat.format( devPlan.getWhenVersioned() ) );
        add( dateLabel );
    }

    private void addVotes() {
        WebMarkupContainer prodVotesContainer = new WebMarkupContainer( "prodVotes" );
        add( prodVotesContainer );
        ListView<Vote> voteList = new ListView<Vote>( "votes", getVotes() ) {
            protected void populateItem( ListItem<Vote> item ) {
                final Vote vote = item.getModelObject();
                boolean isCurrentUser = vote.getUsername().equals( User.current().getUsername() );
                Label nameLabel = new Label( "plannerName", vote.getPlannerName() );
                if ( isCurrentUser ) {
                    nameLabel.add(
                            new AttributeModifier(
                                    "style",
                                    true,
                                    new Model<String>( "font-weight: bold" ) ) );
                }
                item.add( nameLabel );
                WebMarkupContainer emailLink = new WebMarkupContainer( "plannerEmailLink" );
                emailLink.add(
                        new AttributeModifier(
                                "href",
                                true,
                                new Model<String>( "mailto:" + vote.getEmailAddress() ) ) );
                emailLink.setVisible( !isCurrentUser );
                item.add( emailLink );
                CheckBox voteCheckBox = new CheckBox( "plannerVote", new PropertyModel<Boolean>( vote, "inFavor" ) );
                voteCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        Change change = vote.getChange();
                        update( target, change );
                    }
                } );
                item.add( voteCheckBox );
                voteCheckBox.setEnabled( isCurrentUser );
            }
        };
        prodVotesContainer.add( voteList );
        prodVotesContainer.setVisible( getPlan().isDevelopment() );
    }

    public List<Vote> getVotes() {
        List<User> planners = getPlanManager().getPlanners( getPlan().getUri() );
        List<Vote> votes = new ArrayList<Vote>();
        for ( User planner : planners ) {
            votes.add( new Vote( planner ) );
        }
        return votes;
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isRecomposed() ) {
            PlanPage planPage = findParent( PlanPage.class );
            // skip directly to plan page since plan has changed
            planPage.updateWith( target, change, updated );
        }
        // else no need to update UI
    }

    /**
     * A vote to put the dev version into production.
     */
    public class Vote implements Serializable {
        /**
         * A user with planning privileges.
         */
        private User planner;
        /**
         * Change caused by change to vote.
         */
        private Change change;

        public Vote( User planner ) {
            this.planner = planner;
        }

        public User getPlanner() {
            return planner;
        }

        public boolean isInFavor() {
            return getPlan().getProducers().contains( planner.getUsername() );
        }

        public void setInFavor( boolean inFavor ) {
            Command command;
            if ( inFavor ) {
                command = new AddProducer( planner.getUsername() );
            } else {
                command = new RemoveProducer( planner.getUsername() );
            }
            try {
                change = getCommander().doCommand( command );
            } catch ( CommandException e ) {
                LOG.warn( "Voting failed", e );
            }
        }

        public Change getChange() {
            return change;
        }

        public String getEmailAddress() {
            return planner.getEmail();
        }

        public String getPlannerName() {
            return planner.getNormalizedFullName();
        }

        public String getUsername() {
            return planner.getUsername();
        }

    }

}
