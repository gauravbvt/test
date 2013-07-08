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
        addNewPlan();
        initSettings();
    }

    private void resetAll() {
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
        addPlanProperties();
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
        add( newPlanUriField );
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
                    update( target, Change.failed( "Invalid uri; can't add new plan." ) );
            }
        };
        add( addPlanLink );
    }

    private boolean addPlanWithUri( String newPlanUri ) {
        if ( newPlanUri != null && !newPlanUri.trim().isEmpty() ) {
            try {
                String newPlanName = "New Plan " + newPlanUri;
                PlanDefinition newPlanDefinition = planDefinitionManager.getOrCreate( newPlanUri, newPlanName, "Unnamed" );
                getPlanManager().assignPlans();
                selectedPlan = planManager.getPlan( newPlanDefinition.getUri(),
                        newPlanDefinition.getDevelopmentVersion().getNumber() );
                return selectedPlan != null;
            } catch ( IOException e ) {
                LOG.error( "Unable to create plan", e );
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
        Label communitiesCountLabel = new Label( "communitiesCount", Integer.toString( getCommunitiesCount() ) );
        communitiesCountLabel.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( communitiesCountLabel );
        // dev version
        Label devVersionLabel = new Label( "devVersion", Integer.toString( getSelectedPlan().getVersion() ) );
        devVersionLabel.setOutputMarkupId( true );
        planPropertiesContainer.addOrReplace( devVersionLabel );
        addPlanActions();
    }

    private int getCommunitiesCount() {
        return communityDefinitionManager.countCommunitiesFor( getSelectedPlan().getUri() );
    }

    private void addPlanEditableProperties() {
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
        planPropertiesContainer.addOrReplace( productizeLink );
        // delete
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deletePlan",
                "Delete the selected plan?" ) {
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
        planPropertiesContainer.addOrReplace( deleteLink );
    }

    private String getProductizeConfirmationMessage() {
        StringBuilder sb = new StringBuilder();
        boolean plannersOkToProductize = getPlanManager().revalidateProducers( getSelectedPlan() );
        boolean invalid = CollectionUtils.exists(
                getAnalyst().findAllUnwaivedIssues( getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).isValidity();
                    }
                }
        );
        sb.append( "Put in prodocution the current version" );
        if ( invalid || !plannersOkToProductize ) {
            if ( invalid ) {
                sb.append( " even though validity issues are unresolved" );
            }
            if ( !plannersOkToProductize ) {
                if ( invalid ) sb.append( " and" );
                sb.append( " even though not all planners have voted to move into production" );
            }
        }
        sb.append( "?" );
        return sb.toString();
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
                change.setMessage( "Settings changed for plan " + getPlan().getUri() );
                update( target, change );

            }
        };
        applyLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( applyLink );
    }

    private void updatePlan() {
        setPlan( getSelectedPlan() );
        MultiCommand multiCommand = new MultiCommand( getUsername(), "Update plan settings" );
        multiCommand.setChange( new Change( Change.Type.Updated, getSelectedPlan() ) );
        multiCommand.makeUndoable( false );
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
        this.newPlanUri = newPlanUri;
    }
}
