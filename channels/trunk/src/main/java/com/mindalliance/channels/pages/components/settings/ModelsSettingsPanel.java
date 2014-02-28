package com.mindalliance.channels.pages.components.settings;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.community.CommunityDefinitionManager;
import com.mindalliance.channels.core.dao.ModelDefinition;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.CollaborationModel;
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
 * Models settings panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/13
 * Time: 12:00 PM
 */
public class ModelsSettingsPanel extends AbstractCommandablePanel {

    private static final Logger LOG = LoggerFactory.getLogger( ModelsSettingsPanel.class );

    @SpringBean
    private ModelDefinitionManager modelDefinitionManager;

    @SpringBean
    private ModelManager modelManager;

    @SpringBean
    private CommunityDefinitionManager communityDefinitionManager;

    private WebMarkupContainer settingsContainer;

    private CollaborationModel selectedCollaborationModel;
    private String modelName;
    private String modelOwner;
    private String plannerSupportEmail;
    private String participantSupportEmail;
    private String newModelUri;
    private WebMarkupContainer modelPropertiesContainer;
    private TextField<String> newModelUriField;

    public ModelsSettingsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        initSettings();
    }

    private void resetAll() {
        modelName = null;
        modelOwner = null;
        plannerSupportEmail = null;
        participantSupportEmail = null;
    }

    private void initSettings() {
        resetAll();
        settingsContainer = new WebMarkupContainer( "settingsContainer" );
        settingsContainer.setOutputMarkupId( true );
        addOrReplace( settingsContainer );
        addModelSelection();
        addNewModel();
        addModelProperties();
        addModelActions();
        addCancelApply();
    }

    private void addModelProperties() {
        modelPropertiesContainer = new WebMarkupContainer( "modelProperties" );
        modelPropertiesContainer.setOutputMarkupId( true );
        settingsContainer.addOrReplace( modelPropertiesContainer );
        addConstantProperties();
        addModelEditableProperties();
    }


    private void addModelSelection() {
        DropDownChoice<CollaborationModel> modelDropDownChoice = new DropDownChoice<CollaborationModel>( "model-sel",
                new PropertyModel<CollaborationModel>( this, "selectedModel" ),
                new PropertyModel<List<CollaborationModel>>( this, "devModels" ),
                new ChoiceRenderer<CollaborationModel>() {
                    @Override
                    public Object getDisplayValue( CollaborationModel p ) {
                        return p.getName();
                    }
                } );
        modelDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setModel( getSelectedModel() );
                initSettings();
                target.add( settingsContainer );
            }
        } );
        modelDropDownChoice.setOutputMarkupId( true );
        settingsContainer.addOrReplace( modelDropDownChoice );
    }

    private void addNewModel() {
        // uri input
        newModelUriField = new TextField<String>(
                "newModelUri",
                new PropertyModel<String>( this, "newModelUri" ) );
        newModelUriField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        newModelUriField.setOutputMarkupId( true );
        addInputHint( newModelUriField, "a_unique_model_id" );
        settingsContainer.addOrReplace( newModelUriField );
        // button
        AjaxLink<String> addModelLink = new AjaxLink<String>( "addModel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean success = false;
                if ( modelDefinitionManager.isNewModelUriValid( newModelUri ) ) {
                    success = addModelWithUri( newModelUri );
                    if ( success ) {
                        newModelUri = null;
                        initSettings();
                        target.add( settingsContainer );
                        target.add( newModelUriField );
                    }
                }
                if ( !success )
                    update( target, Change.message( "Invalid model identifier - New collaboration model not added." ) );
            }
        };
        addModelLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( addModelLink );
    }

    private boolean addModelWithUri( String modelUri ) {
        if ( modelUri != null && !modelUri.trim().isEmpty() ) {
            try {
                String newModelName = "New Unnamed Model";
                ModelDefinition newModelDefinition = modelDefinitionManager.getOrCreate( modelUri, newModelName, "Unnamed" );
                getModelManager().assignModels();
                selectedCollaborationModel = modelManager.getModel( newModelDefinition.getUri(),
                        newModelDefinition.getDevelopmentVersion().getNumber() );
                return selectedCollaborationModel != null;
            } catch ( IOException e ) {
                LOG.error( "Unable to create collaboration model", e );
                return false;
            }
        } else {
            return false;
        }

    }

    private void addConstantProperties() {
        // uri
        Label modelUriLabel = new Label( "modelUri", getSelectedModel().getUri() );
        modelUriLabel.setOutputMarkupId( true );
        modelPropertiesContainer.addOrReplace( modelUriLabel );
        // community count
        int count = getCommunitiesCount();
        Label communitiesCountLabel = new Label( "communitiesCount", Integer.toString( count ) );
        communitiesCountLabel.setOutputMarkupId( true );
        addTipTitle(
                communitiesCountLabel,
                count == 0
                    ? "No community is based on this model. It can be deleted"
                    : "The model can not be deleted if one or more communities are based on it."
        );
        modelPropertiesContainer.addOrReplace( communitiesCountLabel );
        // dev version
        Label devVersionLabel = new Label( "devVersion", Integer.toString( getSelectedModel().getVersion() ) );
        devVersionLabel.setOutputMarkupId( true );
        modelPropertiesContainer.addOrReplace( devVersionLabel );
        // validity
        Label validityLabel = new Label( "validity", isDevelopmentVersionInvalid() ? "not valid" : "valid" );
        boolean invalid = isDevelopmentVersionInvalid();
        validityLabel.setOutputMarkupId( true );
        addTipTitle(
                validityLabel,
                invalid
                        ? "Validity issues remain. The development version should not be put in production."
                        : "There are no outstanding validity issues. The development version can be put into production.");
        modelPropertiesContainer.addOrReplace( validityLabel );
    }

    private int getCommunitiesCount() {
        return communityDefinitionManager.countCommunitiesFor( getSelectedModel().getUri() );
    }

    private void addModelEditableProperties() {
        // name
        TextField<String> modelNameField = new TextField<String>(
                "modelName",
                new PropertyModel<String>( this, "modelName" ) );
        modelNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        modelNameField.setOutputMarkupId( true );
        addInputHint( modelNameField, "One or more words" );
        modelPropertiesContainer.addOrReplace( modelNameField );
        // owner
        TextField<String> modelOwnerField = new TextField<String>(
                "modelOwner",
                new PropertyModel<String>( this, "modelOwner" ) );
        modelOwnerField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        addInputHint( modelOwnerField, "Who owns this collaboration model" );
        modelOwnerField.setOutputMarkupId( true );
        modelPropertiesContainer.addOrReplace( modelOwnerField );
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
        modelPropertiesContainer.addOrReplace( plannerSupportField );
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
        modelPropertiesContainer.addOrReplace( participantSupportField );
    }

    private void addModelActions() {
        // productize
        ConfirmedAjaxFallbackLink productizeLink = new ConfirmedAjaxFallbackLink(
                "productize",
                getProductizeConfirmationMessage() ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                CollaborationModel selectedDevCollaborationModel = getSelectedModel();
                getModelManager().productize( selectedDevCollaborationModel );
                CollaborationModel newDevCollaborationModel = getModelManager().getModel( selectedDevCollaborationModel.getUri(), selectedDevCollaborationModel.getVersion() + 1 );
                ModelsSettingsPanel.this.setModel( newDevCollaborationModel );
                initSettings();
                target.add( settingsContainer );
            }
        };
        productizeLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( productizeLink );
        // delete
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deleteModel",
                "Delete the selected collaboration model?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                List<CollaborationModel> collaborationModels = getModelManager().getDevelopmentModels();
                if ( collaborationModels.size() > 1 ) {
                    getModelManager().delete( getSelectedModel() );
                    ModelsSettingsPanel.this.setModel( getModelManager().getModels().get( 0 ) );
                    initSettings();
                    target.add( settingsContainer );
                }
            }
        };
        makeVisible( deleteLink, canBeDeletedModel( getSelectedModel() ) );
        deleteLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( deleteLink );
    }

    private String getProductizeConfirmationMessage() {
        StringBuilder sb = new StringBuilder();
        boolean developersOkToProductize = getModelManager().allDevelopersInFavorToPutInProduction( getSelectedModel() );
        boolean invalid = isDevelopmentVersionInvalid();
        sb.append( "Put in production the current version" );
        if ( invalid || !developersOkToProductize ) {
            sb.append( " even though");
        }
        if ( invalid ) {
            sb.append( " there are outstanding validity issues");
        }
        if ( !developersOkToProductize ) {
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

    private boolean canBeDeletedModel( CollaborationModel collaborationModel ) {
        return modelDefinitionManager.getSize() >= 1
                && communityDefinitionManager.countCommunitiesFor( collaborationModel.getUri() ) == 0;
    }

    public void setModel( CollaborationModel collaborationModel ) {
        selectedCollaborationModel = collaborationModel;
        AbstractChannelsWebPage page = (AbstractChannelsWebPage) getPage();
        page.setCollaborationModel( collaborationModel );
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
                updateModel();
                initSettings();
                target.add( settingsContainer );
                Change change = new Change( Change.Type.NeedsRefresh );
                change.setMessage( "Settings changed for model " + getCollaborationModel().getUri() );
                update( target, change );

            }
        };
        applyLink.setOutputMarkupId( true );
        settingsContainer.addOrReplace( applyLink );
    }

    private void updateModel() {
        setModel( getSelectedModel() );
        MultiCommand multiCommand = new MultiCommand( getUsername(), "Update model settings" );
        multiCommand.setChange( new Change( Change.Type.Updated, getSelectedModel() ) );
        multiCommand.makeUndoable( false );
        if ( !getModelName().equals( getSelectedModel().getName() ) ) {
            multiCommand.addCommand( new UpdateModelObject(
                    getUsername(),
                    getSelectedModel(),
                    "name",
                    modelName,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getModelOwner().equals( getSelectedModel().getClient() ) ) {
            multiCommand.addCommand( new UpdateModelObject(
                    getUsername(),
                    getSelectedModel(),
                    "client",
                    modelOwner,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getPlannerSupportEmail().equals(
                getSelectedModel().getPlannerSupportCommunity( modelManager.getDefaultSupportCommunity() ) ) ) {
            multiCommand.addCommand( new UpdateModelObject(
                    getUsername(),
                    getSelectedModel(),
                    "plannerSupportCommunity",
                    plannerSupportEmail,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !getPlannerSupportEmail().equals(
                getSelectedModel().getUserSupportCommunity( modelManager.getDefaultSupportCommunity() ) ) ) {
            multiCommand.addCommand( new UpdateModelObject(
                    getUsername(),
                    getSelectedModel(),
                    "userSupportCommunity",
                    participantSupportEmail,
                    UpdateObject.Action.Set
            ) );
        }
        if ( !multiCommand.isEmpty() )
            doCommand( multiCommand );
    }

    public List<CollaborationModel> getDevModels() {
        List<CollaborationModel> answer = new ArrayList<CollaborationModel>();
        for ( CollaborationModel collaborationModel : getModelManager().getModels() )
            if ( collaborationModel.isDevelopment() )
                answer.add( collaborationModel );
        return answer;
    }


    public String getParticipantSupportEmail() {
        return participantSupportEmail == null
                ? getSelectedModel().getUserSupportCommunity( modelManager.getDefaultSupportCommunity() )
                : participantSupportEmail;
    }

    public void setParticipantSupportEmail( String participantSupportEmail ) {
        this.participantSupportEmail = participantSupportEmail;
    }

    public ModelDefinitionManager getModelDefinitionManager() {
        return modelDefinitionManager;
    }

    public void setModelDefinitionManager( ModelDefinitionManager modelDefinitionManager ) {
        this.modelDefinitionManager = modelDefinitionManager;
    }

    public String getPlannerSupportEmail() {
        return plannerSupportEmail == null
                ? getSelectedModel().getPlannerSupportCommunity( modelManager.getDefaultSupportCommunity() )
                : plannerSupportEmail;
    }

    public void setPlannerSupportEmail( String plannerSupportEmail ) {
        this.plannerSupportEmail = plannerSupportEmail;
    }

    public String getModelName() {
        return modelName == null
                ? getSelectedModel().getName()
                : modelName;
    }

    public void setModelName( String modelName ) {
        this.modelName = modelName;
    }

    public String getModelOwner() {
        return modelOwner == null
                ? getSelectedModel().getClient()
                : modelOwner;
    }

    public void setModelOwner( String modelOwner ) {
        this.modelOwner = modelOwner;
    }

    public CollaborationModel getSelectedModel() {
        return selectedCollaborationModel == null
                ? modelManager.getDevelopmentModels().get( 0 )
                : selectedCollaborationModel;
    }

    public void setSelectedModel( CollaborationModel selectedCollaborationModel ) {
        this.selectedCollaborationModel = selectedCollaborationModel;
    }

    public String getNewModelUri() {
        return newModelUri;
    }

    public void setNewModelUri( String newModelUri ) {
        this.newModelUri = ChannelsUtils.sanitize( newModelUri );
    }
}
