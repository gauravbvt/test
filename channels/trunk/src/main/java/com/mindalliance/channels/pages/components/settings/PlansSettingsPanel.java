package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.community.CommunityDefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.dao.PlanDefinitionManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plans settings panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/13
 * Time: 12:00 PM
 */
public class PlansSettingsPanel extends AbstractCommandablePanel {

    private static final Logger LOG = LoggerFactory.getLogger( PlansSettingsPanel.class );

    @SpringBean
    private PlanDefinitionManager planDefinitionManager;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private CommunityDefinitionManager communityDefinitionManager;

    private WebMarkupContainer settingsContainer;

    private Plan selectedPlan;
    private String planName;
    private String planOwner;
    private String plannerSupportEmail;
    private String participantSupportEmail;
    private String newPlanUri;
    private WebMarkupContainer planPropertiesContainer;
    private TextField<String> newPlanUriField;

    public PlansSettingsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        initSettings();
    }

    private void resetAll() {
        planName = null;
        planOwner = null;
        plannerSupportEmail = null;
        participantSupportEmail = null;
    }

    private void initSettings() {
        resetAll();
        settingsContainer = new WebMarkupContainer( "settingsContainer" );
        settingsContainer.setOutputMarkupId( true );
        addOrReplace( settingsContainer );
        addPlanSelection();
        addNewPlan();
        addPlanProperties();
        addPlanActions();
        addCancelApply();
    }

    private void addPlanProperties() {
        planPropertiesContainer = new WebMarkupContainer( "planProperties" );
        planPropertiesContainer.setOutputMarkupId( true );
        settingsContainer.addOrReplace( planPropertiesContainer );
        addConstantProperties();
        addPlanEditableProperties();
    }


    private void addPlanSelection() {
        DropDownChoice<Plan> planDropDownChoice = new DropDownChoice<Plan>( "plan-sel",
                new PropertyModel<Plan>( this, "selectedPlan" ),
                new PropertyModel<List<Plan>>( this, "devPlans" ),
                new ChoiceRenderer<Plan>() {
                    @Override
                    public Object getDisplayValue( Plan p ) {
                        return p.getName();
                    }
                } );
        planDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setPlan( getSelectedPlan() );
                initSettings();
                target.add( settingsContainer );
            }
        } );
        planDropDownChoice.setOutputMarkupId( true );
        settingsContainer.addOrReplace( planDropDownChoice );
    }

    private void addNewPlan() {
        // uri input
        newPlanUriField = new TextField<String>(
                "newPlanUri",
                new PropertyModel<String>( this, "newPlanUri" ) );
        newPlanUriField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        newPlanUriField.setOutputMarkupId( true );
        addInputHint( newPlanUriField, "a_unique_template_id" );
        settingsContainer.addOrReplace( newPlanUriField );
        // button
        AjaxLink<String> addPlanLink = new AjaxLink<String>( "addPlan" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean success = false;
                if ( planDefinitionManager.isNewPlanUriValid( newPlanUri ) ) {
                    success = addPlanWithUri( newPlanUri );
                    if ( success ) {
                        newPlanUri = null;
                        initSettings();
                        target.add( settingsContainer );
                        target.add( newPlanUriField );
                    }
                }
                if ( !success )
                    update( target, Change.message( "Invalid template identifier - New collaboration template not added." ) );
            }
        };
        addPlanLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( addPlanLink );
    }

    private boolean addPlanWithUri( String planUri ) {
        if ( planUri != null && !planUri.trim().isEmpty() ) {
            try {
                String newPlanName = "New Unnamed Template";
                PlanDefinition newPlanDefinition = planDefinitionManager.getOrCreate( planUri, newPlanName, "Unnamed" );
                getPlanManager().assignPlans();
                selectedPlan = planManager.getPlan( newPlanDefinition.getUri(),
                        newPlanDefinition.getDevelopmentVersion().getNumber() );
                return selectedPlan != null;
            } catch ( IOException e ) {
                LOG.error( "Unable to create collaboration template", e );
                return false;
            }
        } else {
            return false;
        }

    }

    private void addConstantProperties() {
        // uri
        Label planUriLabel = new Label( "planUri", getSelectedPlan().getUri() );
        planUriLabel.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( planUriLabel );
        // community count
        int count = getCommunitiesCount();
        Label communitiesCountLabel = new Label( "communitiesCount", Integer.toString( count ) );
        communitiesCountLabel.setOutputMarkupId( true );
        addTipTitle(
                communitiesCountLabel,
                count == 0
                    ? "No plan is based on this template. It can be deleted"
                    : "The template can not be deleted if one or more plans are based on it."
        );
        planPropertiesContainer.addOrReplace( communitiesCountLabel );
        // dev version
        Label devVersionLabel = new Label( "devVersion", Integer.toString( getSelectedPlan().getVersion() ) );
        devVersionLabel.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( devVersionLabel );
        // validity
        Label validityLabel = new Label( "validity", isDevelopmentVersionInvalid() ? "not valid" : "valid" );
        boolean invalid = isDevelopmentVersionInvalid();
        validityLabel.setOutputMarkupId( true );
        addTipTitle(
                validityLabel,
                invalid
                        ? "Validity issues remain. The development version should not be put in production."
                        : "There are no outstanding validity issues. The development version can be put into production.");
        planPropertiesContainer.addOrReplace( validityLabel );
    }

    private int getCommunitiesCount() {
        return communityDefinitionManager.countCommunitiesFor( getSelectedPlan().getUri() );
    }

    private void addPlanEditableProperties() {
        // name
        TextField<String> planNameField = new TextField<String>(
                "planName",
                new PropertyModel<String>( this, "planName" ) );
        planNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        planNameField.setOutputMarkupId( true );
        addInputHint( planNameField, "One or more words" );
        planPropertiesContainer.addOrReplace( planNameField );
        // owner
        TextField<String> planOwnerField = new TextField<String>(
                "planOwner",
                new PropertyModel<String>( this, "planOwner" ) );
        planOwnerField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        addInputHint( planOwnerField, "Who owns this collaboration template" );
        planOwnerField.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( planOwnerField );
        // planner support email
        TextField<String> plannerSupportField = new TextField<String>(
                "plannerSupportEmail",
                new PropertyModel<String>( this, "plannerSupportEmail" ) );
        plannerSupportField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        addInputHint( plannerSupportField, "An email address" );
        plannerSupportField.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( plannerSupportField );
        // participant support email
        TextField<String> participantSupportField = new TextField<String>(
                "participantSupportEmail",
                new PropertyModel<String>( this, "participantSupportEmail" ) );
        participantSupportField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        addInputHint( participantSupportField, "An email address" );
        participantSupportField.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( participantSupportField );
    }

    private void addPlanActions() {
        // productize
        ConfirmedAjaxFallbackLink productizeLink = new ConfirmedAjaxFallbackLink(
                "productize",
                getProductizeConfirmationMessage() ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Plan selectedDevPlan = getSelectedPlan();
                getPlanManager().productize( selectedDevPlan );
                Plan newDevPlan = getPlanManager().getPlan( selectedDevPlan.getUri(), selectedDevPlan.getVersion() + 1 );
                setPlan( newDevPlan );
                initSettings();
                target.add( settingsContainer );
            }
        };
        productizeLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( productizeLink );
        // delete
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deletePlan",
                "Delete the selected collaboration template?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                List<Plan> plans = getPlanManager().getDevelopmentPlans();
                if ( plans.size() > 1 ) {
                    getPlanManager().delete( getSelectedPlan() );
                    setPlan( getPlanManager().getPlans().get( 0 ) );
                    initSettings();
                    target.add( settingsContainer );
                }
            }
        };
        makeVisible( deleteLink, canBeDeletedPlan( getSelectedPlan() ) );
        deleteLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( deleteLink );
    }

    private String getProductizeConfirmationMessage() {
        StringBuilder sb = new StringBuilder();
        boolean plannersOkToProductize = getPlanManager().allDevelopersInFavorToPutInProduction( getSelectedPlan() );
        boolean invalid = isDevelopmentVersionInvalid();
        sb.append( "Put in production the current version" );
        if ( invalid || !plannersOkToProductize ) {
            sb.append( " even though");
        }
        if ( invalid ) {
            sb.append( " there are outstanding validity issues");
        }
        if ( !plannersOkToProductize ) {
            if ( invalid )
                sb.append( " and");
            sb.append( " not all developers have voted in favor of it" );
        }
        sb.append( "?" );
        return sb.toString();
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

    private boolean canBeDeletedPlan( Plan plan ) {
        return planDefinitionManager.getSize() >= 1
                && communityDefinitionManager.countCommunitiesFor( plan.getUri() ) == 0;
    }

    public void setPlan( Plan plan ) {
        selectedPlan = plan;
        AbstractChannelsWebPage page = (AbstractChannelsWebPage) getPage();
        page.setPlan( plan );
    }


    private void addCancelApply() {
        // reset
        AjaxLink<String> resetLink = new AjaxLink<String>( "reset" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                initSettings();
                target.add( settingsContainer );
            }
        };
        resetLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( resetLink );
        // apply
        AjaxLink<String> applyLink = new AjaxLink<String>( "apply" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                updatePlan();
                initSettings();
                target.add( settingsContainer );
                Change change = new Change( Change.Type.NeedsRefresh );
                change.setMessage( "Settings changed for template " + getPlan().getUri() );
                update( target, change );

            }
        };
        applyLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( applyLink );
    }

    private void updatePlan() {
        setPlan( getSelectedPlan() );
        MultiCommand multiCommand = new MultiCommand( getUsername(), "Update template settings" );
        multiCommand.setChange( new Change( Change.Type.Updated, getSelectedPlan() ) );
        multiCommand.makeUndoable( false );
        if ( !getPlanName().equals( getSelectedPlan().getName() ) ) {
            multiCommand.addCommand( new UpdatePlanObject(
                    getUsername(),
                    getSelectedPlan(),
                    "name",
                    planName,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getPlanOwner().equals( getSelectedPlan().getClient() ) ) {
            multiCommand.addCommand( new UpdatePlanObject(
                    getUsername(),
                    getSelectedPlan(),
                    "client",
                    planOwner,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getPlannerSupportEmail().equals(
                getSelectedPlan().getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() ) ) ) {
            multiCommand.addCommand( new UpdatePlanObject(
                    getUsername(),
                    getSelectedPlan(),
                    "plannerSupportCommunity",
                    plannerSupportEmail,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getPlannerSupportEmail().equals(
                getSelectedPlan().getUserSupportCommunity( planManager.getDefaultSupportCommunity() ) ) ) {
            multiCommand.addCommand( new UpdatePlanObject(
                    getUsername(),
                    getSelectedPlan(),
                    "userSupportCommunity",
                    participantSupportEmail,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !multiCommand.isEmpty() )
            doCommand( multiCommand );
    }

    public List<Plan> getDevPlans() {
        List<Plan> answer = new ArrayList<Plan>();
        for ( Plan plan : getPlanManager().getPlans() )
            if ( plan.isDevelopment() )
                answer.add( plan );
        return answer;
    }


    public String getParticipantSupportEmail() {
        return participantSupportEmail == null
                ? getSelectedPlan().getUserSupportCommunity( planManager.getDefaultSupportCommunity() )
                : participantSupportEmail;
    }

    public void setParticipantSupportEmail( String participantSupportEmail ) {
        this.participantSupportEmail = participantSupportEmail;
    }

    public PlanDefinitionManager getPlanDefinitionManager() {
        return planDefinitionManager;
    }

    public void setPlanDefinitionManager( PlanDefinitionManager planDefinitionManager ) {
        this.planDefinitionManager = planDefinitionManager;
    }

    public String getPlannerSupportEmail() {
        return plannerSupportEmail == null
                ? getSelectedPlan().getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() )
                : plannerSupportEmail;
    }

    public void setPlannerSupportEmail( String plannerSupportEmail ) {
        this.plannerSupportEmail = plannerSupportEmail;
    }

    public String getPlanName() {
        return planName == null
                ? getSelectedPlan().getName()
                : planName;
    }

    public void setPlanName( String planName ) {
        this.planName = planName;
    }

    public String getPlanOwner() {
        return planOwner == null
                ? getSelectedPlan().getClient()
                : planOwner;
    }

    public void setPlanOwner( String planOwner ) {
        this.planOwner = planOwner;
    }

    public Plan getSelectedPlan() {
        return selectedPlan == null
                ? planManager.getDevelopmentPlans().get( 0 )
                : selectedPlan;
    }

    public void setSelectedPlan( Plan selectedPlan ) {
        this.selectedPlan = selectedPlan;
    }

    public String getNewPlanUri() {
        return newPlanUri;
    }

    public void setNewPlanUri( String newPlanUri ) {
        this.newPlanUri = ChannelsUtils.sanitize( newPlanUri );
    }
}
