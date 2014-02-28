package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.AddProducer;
import com.mindalliance.channels.core.command.commands.RemoveProducer;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.ModelPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
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
public class ModelVersionsPanel extends AbstractCommandablePanel {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( ModelVersionsPanel.class );

    /**
     * Date format.
     */
    private SimpleDateFormat dateFormat;

    @SpringBean
    private UserRecordService userRecordService;
    private ConfirmedAjaxFallbackLink productizeLink;

    public ModelVersionsPanel(
            String id,
            IModel<? extends Identifiable> iModel,
            Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        dateFormat = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" );
        addCurrentVersion();
        addProductionVersion();
        addDevelopmentVersion();
        addVotes();
        addProductionButton();
    }

    private void addCurrentVersion() {
        Label versionLabel = new Label(
                "currentVersion", "" + getCollaborationModel().getVersion() );
        versionLabel.setOutputMarkupId( true );
        addOrReplace( versionLabel );
    }

    private void addProductionVersion() {
        CollaborationModel prodCollaborationModel = getModelManager().findProductionModel( getCollaborationModel().getUri() );
        WebMarkupContainer prodContainer = new WebMarkupContainer( "prod" );
        prodContainer.setOutputMarkupId( true );
        addOrReplace( prodContainer );
        Label versionLabel = new Label(
                "prodVersion",
                prodCollaborationModel == null ? "" : "" + prodCollaborationModel.getVersion() );
        prodContainer.add( versionLabel );
        Label dateLabel = new Label(
                "prodDate",
                prodCollaborationModel == null ? "" : "" + dateFormat.format( prodCollaborationModel.getWhenVersioned() ) );
        prodContainer.add( dateLabel );
        prodContainer.setVisible( prodCollaborationModel != null );
    }

    private void addDevelopmentVersion() {
        CollaborationModel devCollaborationModel = getModelManager().findDevelopmentModel( getCollaborationModel().getUri() );
        Label versionLabel = new Label(
                "devVersion", "" + devCollaborationModel.getVersion() );
        versionLabel.setOutputMarkupId( true );
        addOrReplace( versionLabel );
    }

    private void addVotes() {
        WebMarkupContainer prodVotesContainer = new WebMarkupContainer( "prodVotes" );
        prodVotesContainer.setOutputMarkupId( true );
        addOrReplace( prodVotesContainer );
        final List<Vote> votes = getVotes();
        ListView<Vote> voteList = new ListView<Vote>( "votes", votes ) {
            protected void populateItem( ListItem<Vote> item ) {
                final Vote vote = item.getModelObject();
                boolean isCurrentUser = vote.getUsername().equals( getUser().getUsername() );
                Label nameLabel = new Label( "developerName", vote.getDeveloperName() );
                if ( isCurrentUser ) {
                    nameLabel.add(
                            new AttributeModifier(
                                    "style",
                                    new Model<String>( "font-weight: bold" ) ) );
                }
                item.add( nameLabel );
                AjaxLink messageLink = new AjaxLink( "developerMessageLink" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Communicated, vote.getUsername() ) );
                    }
                };
                messageLink.setVisible( !isCurrentUser );
                item.add( messageLink );
                CheckBox voteCheckBox = new CheckBox( "developerVote", new PropertyModel<Boolean>( vote, "inFavor" ) );
                voteCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        addProductionButton();
                        target.add( productizeLink );
                        Change change = vote.getChange();
                        update( target, change );
                    }
                } );
                item.add( voteCheckBox );
                voteCheckBox.setEnabled( isCurrentUser );
                item.add( new AttributeModifier(
                        "class",
                        new Model<String>( itemCssClasses( item.getIndex(), votes.size() ) ) ) );

            }
        };
        prodVotesContainer.add( voteList );
        prodVotesContainer.setVisible( isLockedByUser( getCollaborationModel() ) );
    }

    private void addProductionButton() {
        productizeLink = new ConfirmedAjaxFallbackLink(
                "productize",
                getProductizeConfirmationMessage() ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                CollaborationModel selectedDevCollaborationModel = getCollaborationModel();
                getModelManager().productize( selectedDevCollaborationModel );
                CollaborationModel newDevCollaborationModel = getModelManager().getModel( selectedDevCollaborationModel.getUri(), selectedDevCollaborationModel.getVersion() + 1 );
                getUser().setCollaborationModel( newDevCollaborationModel );
                setResponsePage( ModelPage.class );
            }
        };
        productizeLink.setOutputMarkupId( true );
        boolean developerOkToProductize = getModelManager().allDevelopersInFavorToPutInProduction( getCollaborationModel() );
        makeVisible( productizeLink, developerOkToProductize && !isDevelopmentVersionInvalid() );
        addOrReplace( productizeLink );
    }

    private boolean isDevelopmentVersionInvalid() {
        return CollectionUtils.exists(
                getCommunityService().getDoctor().findAllUnwaivedIssues( getCommunityService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).isValidity();
                    }
                }
        );
    }

    private String getProductizeConfirmationMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Put in production the development version?" );
        return sb.toString();
    }


    private String itemCssClasses( int index, int count ) {
        String classes = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) classes += " last";
        return classes;
    }

    public List<Vote> getVotes() {
        List<Vote> votes = new ArrayList<Vote>();
        for ( ChannelsUser developer : userRecordService.getStrictlyDevelopers( getCollaborationModel().getUri() ) )
            votes.add( new Vote( developer ) );

        return votes;
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public CollaborationModel getCollaborationModel() {
        return (CollaborationModel) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isRecomposed() ) {
            ModelPage modelPage = findParent( ModelPage.class );
            // skip directly to plan page since plan has changed
            modelPage.updateWith( target, change, updated );
        }
        if ( change.isCommunicated() ) {
            super.updateWith( target, change, updated );
        }

    }

    /**
     * A vote to put the dev version into production.
     */
    public class Vote implements Serializable {
        /**
         * A user with planning privileges.
         */
        private ChannelsUser developer;
        /**
         * Change caused by change to vote.
         */
        private Change change;

        public Vote( ChannelsUser developer ) {
            this.developer = developer;
        }

        public ChannelsUser getDeveloper() {
            return developer;
        }

        public boolean isInFavor() {
            return getCollaborationModel().getProducers().contains( developer.getUsername() );
        }

        public void setInFavor( boolean inFavor ) {
            Command command;
            if ( inFavor ) {
                command = new AddProducer( getUser().getUsername(), developer.getUsername() );
            } else {
                command = new RemoveProducer( getUser().getUsername(), getCollaborationModel(), developer.getUsername() );
            }
            change = getCommander().doCommand( command );
        }

        public Change getChange() {
            return change;
        }

        public String getEmailAddress() {
            return developer.getEmail();
        }

        public String getDeveloperName() {
            return developer.getNormalizedFullName();
        }

        public String getUsername() {
            return developer.getUsername();
        }

    }

}
