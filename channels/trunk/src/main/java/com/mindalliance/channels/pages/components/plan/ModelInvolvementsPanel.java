package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan scope panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 26, 2009
 * Time: 1:29:03 PM
 */
public class ModelInvolvementsPanel extends AbstractCommandablePanel implements Guidable {

    private static String[] ENTITY_CLASSES = {
            Actor.classLabel(),
            Function.classLabel(),
            InfoFormat.classLabel(),
            InfoProduct.classLabel(),
            Organization.classLabel(),
            Place.classLabel(),
            TransmissionMedium.classLabel(),
            MaterialAsset.classLabel()
    };

    private Class<? extends ModelEntity> selectedEntityClass = Organization.class;
    private boolean involvedOnly = false;
    private boolean uninvolvedOnly = false;
    private boolean expectedOnly = false;
    private ModelEntity selectedEntity;
    private ScopeIndexPanel scopeIndexPanel;
    private CheckBox involvedCheckBox;
    private CheckBox uninvolvedCheckBox;
    private CheckBox expectedCheckBox;
    private WebMarkupContainer entityContainer;
    private String newInvolvedName;
    private TextField addInvolvedField;

    private Assignments assignments;
    private Commitments commitments;


    public ModelInvolvementsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "involvements";
    }

    private void init() {
        addEntityClassSelector();
        addEntityClassLabel();
        addInvolvement();
        addExpectation();
        addToScope();
        addSelectedEntity();
        addScopeIndex();
    }

    public Assignments getAssignments() {
        if ( assignments == null ) {
            assignments = getQueryService().getAssignments();
        }
        return assignments;
    }

    public Commitments getCommitments() {
        if ( commitments == null ) {
            commitments = getQueryService().getAllCommitments();
        }
        return commitments;
    }

    private void addEntityClassSelector() {
        DropDownChoice<String> entityClassSelector = new DropDownChoice<String>(
                "entityClassChoice",
                new PropertyModel<String>( this, "selectedEntityClassName" ),
                Arrays.asList( ENTITY_CLASSES )
        );
        entityClassSelector.setOutputMarkupId( true );
        entityClassSelector.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                selectedEntity = null;
                init();
                target.add( ModelInvolvementsPanel.this );
            }
        } );
        addOrReplace( entityClassSelector );
    }

    public Class<? extends ModelEntity> getSelectedEntityClass() {
        return selectedEntityClass;
    }

    public void setSelectedEntityClassName( String val ) {
        selectedEntityClass = ModelEntity.classFromLabel( val);
    }

    public String getSelectedEntityClassName() {
        return ModelEntity.getPluralClassLabelOf( selectedEntityClass );
    }

    private void addEntityClassLabel() {
        String pluralClassLabel = ModelEntity.getPluralClassLabelOf( getSelectedEntityClass() );
        Label pluralLabel = new Label( "entityClassPlural", pluralClassLabel );
        pluralLabel.setOutputMarkupId( true );
        addOrReplace( pluralLabel );
    }

    private void addInvolvement() {
        // Involved only
        involvedCheckBox = new CheckBox(
                "involved",
                new PropertyModel<Boolean>( this, "involvedOnly" ) );
        involvedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        involvedCheckBox.setOutputMarkupId( true );
        addOrReplace( involvedCheckBox );
        // Uninvolved only
        uninvolvedCheckBox = new CheckBox(
                "uninvolved",
                new PropertyModel<Boolean>( this, "uninvolvedOnly" ) );
        uninvolvedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        uninvolvedCheckBox.setOutputMarkupId( true );
        addOrReplace( uninvolvedCheckBox );
    }

    private void addExpectation() {
        // expected only
        expectedCheckBox = new CheckBox(
                "expected",
                new PropertyModel<Boolean>( this, "expectedOnly" ) );
        expectedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        expectedCheckBox.setOutputMarkupId( true );
        addOrReplace( expectedCheckBox );
    }

    private void updateCheckBoxes( AjaxRequestTarget target ) {
        target.add( expectedCheckBox );
        target.add( involvedCheckBox );
        target.add( uninvolvedCheckBox );
    }

    private void addToScope() {
        WebMarkupContainer newInvolvedContainer = new WebMarkupContainer( "newInvolvedContainer" );
        newInvolvedContainer.setOutputMarkupId( true );
        addOrReplace( newInvolvedContainer );
        String singularClassLabel = ModelEntity.getSingularClassLabelOf( getSelectedEntityClass() );
        Label singularLabel = new Label( "entityClassSingular", singularClassLabel );
        singularLabel.setOutputMarkupId( true );
        newInvolvedContainer.setVisible( isPlanner() && getCollaborationModel().isDevelopment() );
        newInvolvedContainer.addOrReplace( singularLabel );
        addInvolvedField = new AutoCompleteTextField<String>(
                "newInvolved",
                new PropertyModel<String>( this, "newInvolvedName" ),
                getAutoCompleteSettings() ) {
            List<String> choices = getCandidateEntityNames();

            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        addInvolvedField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                involveNewEntity();
                newInvolvedName = null;
                addSelectedEntity();
                target.add( entityContainer );
                addScopeIndex();
                target.add( scopeIndexPanel );
                target.add( addInvolvedField );
            }
        } );
        String entityClassLabel = ModelEntity.getSingularClassLabelOf( getSelectedEntityClass() );
        addInputHint(
                addInvolvedField,
                "Enter the name of "
                        + (ChannelsUtils.startsWithVowel( entityClassLabel ) ? "an " : "a ")
                        + entityClassLabel
                        + " (then press enter)" );
        newInvolvedContainer.add( addInvolvedField );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getCandidateEntityNames() {
        List<ModelEntity> candidates = (List<ModelEntity>) CollectionUtils.select(
                listEntities( getSelectedEntityClass(), true ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        ModelEntity entity = (ModelEntity) obj;
                        return !entity.isUnknown() && !getQueryService().isInvolvementExpected( entity );
                    }
                }
        );
        return (List<String>) CollectionUtils.collect(
                candidates,
                TransformerUtils.invokerTransformer( "getName" )
        );
    }

    private void addSelectedEntity() {
        entityContainer = new WebMarkupContainer( "entityContainer" );
        entityContainer.setOutputMarkupId( true );
        makeVisible( entityContainer, selectedEntity != null );
        addOrReplace( entityContainer );
        Label nameInTitleLabel = new Label(
                "nameInTitle",
                new Model<String>(
                        selectedEntity != null
                                ? selectedEntity.getName()
                                : "" ) );
        entityContainer.add( nameInTitleLabel );
        Label nameLabel = new Label(
                "name",
                new Model<String>(
                        selectedEntity != null
                                ? selectedEntity.getName()
                                : "" ) );
        entityContainer.add( nameLabel );
        Label involvementLabel = new Label(
                "involvement",
                new Model<String>( getInvolvementTitle() )
        );
        entityContainer.add( involvementLabel );
        ModelObjectLink detailsLink = new ModelObjectLink(
                "detailsLink",
                new Model<ModelEntity>( selectedEntity != null ? selectedEntity : Actor.UNKNOWN ),
                new Model<String>( "See profile" ),
                "View the profile of the " + ( selectedEntity != null ? selectedEntity.getTypeName()  : ""),
                "window" );
        entityContainer.add( detailsLink );
        AjaxLink expectationActionLink = new AjaxLink( "expectationActionLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeExpectation();
                addScopeIndex();
                target.add( scopeIndexPanel );
                addSelectedEntity();
                target.add( entityContainer );
            }
        };
        // expectationActionLink.setVisible( isLockedByUser( Channels.ALL_INVOLVEMENTS ) );
        entityContainer.add( expectationActionLink );
        Label expectationLabel = new Label(
                "expectationAction",
                new Model<String>( getExpectationAction() )
        );
        addTipTitle( expectationLabel, new Model<String>( getExpectationActionHint() ) );
        expectationActionLink.add( expectationLabel );
        WebMarkupContainer involvementsContainer = new WebMarkupContainer( "involvementsContainer" );
        QueryService queryService = getQueryService();
        involvementsContainer.setVisible(
                selectedEntity != null
                        && queryService.isInvolved(
                        selectedEntity,
                        getAssignments(),
                        getCommitments() ) );
        entityContainer.add( involvementsContainer );
        InvolvementIndexPanel involvementIndexPanel = new InvolvementIndexPanel(
                "involvementIndex",
                new Model<ModelEntity>( selectedEntity ),
                null
        );
        involvementsContainer.add( involvementIndexPanel );
    }

    private String getExpectationAction() {
        if ( selectedEntity != null ) {
            if ( getQueryService().isInvolvementExpected( selectedEntity ) ) {
                return "Remove the expectation";
            } else {
                return "Add the expectation";
            }
        } else {
            return "";
        }
    }

    private String getExpectationActionHint() {
        if ( selectedEntity != null ) {
            String entityClassName = ModelEntity.getSingularClassLabelOf( getSelectedEntityClass() );
            if ( getQueryService().isInvolvementExpected( selectedEntity ) ) {
                return "Remove the expectation that the "
                        + entityClassName
                        + " should be involved";
            } else {
                return "Add the expectation that the "
                        + entityClassName
                        + "should be involved";
            }
        } else {
            return "";
        }
    }

    private void changeExpectation() {
        if ( selectedEntity != null ) {
            if ( getQueryService().isInvolvementExpected( selectedEntity ) ) {
                doCommand(
                        new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                                "involvements",
                                selectedEntity,
                                UpdateObject.Action.Remove ) );
                if ( getCommander().cleanup( getSelectedEntityClass(), selectedEntity.getName() ) )
                    selectedEntity = null;
            } else {
                doCommand(
                        new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                                "involvements",
                                selectedEntity,
                                UpdateObject.Action.AddUnique ) );
            }
        }
    }

    private void involveNewEntity() {
        if ( newInvolvedName != null && !newInvolvedName.trim().isEmpty() ) {
            ModelEntity entity = isActualEntityInvolvement()
                    ? getQueryService().safeFindOrCreateActual( // creates actual if new name
                    getSelectedEntityClass(),
                    newInvolvedName )
                    : getQueryService().safeFindOrCreateType( // creates type if new name
                    getSelectedEntityClass(),
                    newInvolvedName );
            selectedEntity = entity;
            doCommand(
                    new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                            "involvements",
                            entity,
                            UpdateObject.Action.AddUnique ) );
        }
    }

    private boolean isActualEntityInvolvement() {
        return selectedEntityClass == Actor.class
                || selectedEntityClass == Organization.class
                || selectedEntityClass == Place.class
                || selectedEntityClass == MaterialAsset.class;
    }

    private String getInvolvementTitle() {
        String s = "";
        if ( selectedEntity != null ) {
            QueryService queryService = getQueryService();
            boolean involved = queryService.isInvolved(
                    selectedEntity,
                    getAssignments(),
                    getCommitments() );
            boolean expected = queryService.isInvolvementExpected( selectedEntity );
            if ( involved && expected ) s = "is involved as expected.";
            else if ( involved && !expected ) s = "is involved even though it does not have to be.";
            else if ( !involved && expected ) s = "is not involved even though it is expected to be.";
        }
        return s;
    }

    private void addScopeIndex() {
        scopeIndexPanel = new ScopeIndexPanel(
                "scopeIndex",
                getModel(),
                null
        );
        scopeIndexPanel.setOutputMarkupId( true );
        addOrReplace( scopeIndexPanel );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isExpanded() && change.isForInstanceOf( getSelectedEntityClass() ) ) {
            if ( selectedEntity == null
                    || !selectedEntity.equals( change.getSubject( getCommunityService() ) ) ) {
                selectedEntity = (ModelEntity) change.getSubject( getCommunityService() );
                change.setType( Change.Type.Selected );
            } else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.isForInstanceOf( getSelectedEntityClass()) ) {
            addSelectedEntity();
            target.add( entityContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        assignments = null;
        commitments = null;
        super.refresh( target, change );
    }

    public boolean isInvolvedOnly() {
        return involvedOnly;
    }

    public void setInvolvedOnly( boolean val ) {
        this.involvedOnly = val;
        if ( val )
            uninvolvedOnly = false;
    }

    public boolean isUninvolvedOnly() {
        return uninvolvedOnly;
    }

    public void setUninvolvedOnly( boolean val ) {
        this.uninvolvedOnly = val;
        if ( val )
            involvedOnly = false;
    }

    public boolean isExpectedOnly() {
        return expectedOnly;
    }

    public void setExpectedOnly( boolean val ) {
        this.expectedOnly = val;
    }

    public String getNewInvolvedName() {
        return newInvolvedName;
    }

    public void setNewInvolvedName( String newInvolvedName ) {
        this.newInvolvedName = newInvolvedName;
    }

    @SuppressWarnings( "unchecked" )
    private List<? extends ModelEntity> getIndexedEntities( Class<? extends ModelEntity> modelEntityClass, boolean mustBeReferenced ) {
        List<ModelEntity> modelEntities = (List<ModelEntity>) CollectionUtils.select(
                listEntities( modelEntityClass, mustBeReferenced ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !( (ModelEntity) obj ).isUnknown();
                    }
                } );
        if ( involvedOnly || uninvolvedOnly ) {
            modelEntities = (List<ModelEntity>) CollectionUtils.select(
                    modelEntities,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            ModelEntity modelEntity = (ModelEntity) obj;
                            boolean involved = getQueryService().isInvolved( modelEntity,
                                    getAssignments(),
                                    getCommitments() );
                            return ( involvedOnly && involved )
                                    || ( uninvolvedOnly && !involved );
                        }
                    }
            );
        }
        if ( expectedOnly ) {
            modelEntities = (List<ModelEntity>) CollectionUtils.select(
                    modelEntities,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            ModelEntity modelEntity = (ModelEntity) obj;
                            boolean expected = getQueryService().isInvolvementExpected( modelEntity );
                            return !expectedOnly || expected;
                        }
                    }
            );
        }
        return modelEntities;
    }

    private List<? extends ModelEntity> listEntities( Class<? extends ModelEntity> modelEntityClass,
                                                      boolean mustBeReferenced ) {
        if ( modelEntityClass == Organization.class
                || modelEntityClass == Place.class
                || modelEntityClass == Actor.class
                || modelEntityClass == MaterialAsset.class )
            return getQueryService().listActualEntities( modelEntityClass, mustBeReferenced );
        else
            return getQueryService().listTypeEntities( modelEntityClass, mustBeReferenced );
    }

    /**
     * Scope index panel.
     */
    private class ScopeIndexPanel extends AbstractIndexPanel {

        private ScopeIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
            super( id, model, expansions );
        }

        @Override
        @SuppressWarnings( "unchecked" )
         protected List<Organization> findIndexedOrganizations() {
            if ( getSelectedEntityClass() == Organization.class )
                return (List<Organization>) getIndexedEntities( Organization.class, isMustBeReferenced() );
            else
                return new ArrayList<Organization>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<Function> findIndexedFunctions() {
            if ( getSelectedEntityClass() == Function.class )
                return (List<Function>) getIndexedEntities( Function.class, isMustBeReferenced() );
            else
                return new ArrayList<Function>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<MaterialAsset> findIndexedMaterialAssets() {
            if ( getSelectedEntityClass() == MaterialAsset.class )
                return (List<MaterialAsset>) getIndexedEntities( MaterialAsset.class, isMustBeReferenced() );
            else
                return new ArrayList<MaterialAsset>();
        }


        @Override
        @SuppressWarnings( "unchecked" )
        protected List<InfoFormat> findIndexedInfoFormats() {
            if ( getSelectedEntityClass() == InfoFormat.class )
                return (List<InfoFormat>) getIndexedEntities( InfoFormat.class, isMustBeReferenced() );
            else
                return new ArrayList<InfoFormat>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<InfoProduct> findIndexedInfoProducts() {
            if ( getSelectedEntityClass() == InfoProduct.class )
                return (List<InfoProduct>) getIndexedEntities( InfoProduct.class, isMustBeReferenced() );
            else
                return new ArrayList<InfoProduct>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<TransmissionMedium> findIndexedMedia() {
            if ( getSelectedEntityClass() == TransmissionMedium.class )
                return (List<TransmissionMedium>) getIndexedEntities( TransmissionMedium.class, isMustBeReferenced() );
            else
                return new ArrayList<TransmissionMedium>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<Place> findIndexedPlaces() {
            if ( getSelectedEntityClass() == Place.class )
                return (List<Place>) getIndexedEntities( Place.class, isMustBeReferenced() );
            else
                return new ArrayList<Place>();
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected List<Actor> findIndexedActors() {
            if ( getSelectedEntityClass() == Actor.class )
                return (List<Actor>) getIndexedEntities( Actor.class, isMustBeReferenced() );
            else
                return new ArrayList<Actor>();
        }
    }

    private class InvolvementIndexPanel extends AbstractIndexPanel {

        private InvolvementIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
            super( id, model, expansions );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            ModelEntity entity = (ModelEntity) getModel().getObject();
            if ( entity != null && entity instanceof Specable )
                return getQueryService().findAllPartsPlayedBy( (Specable)entity );
            else
                return new ArrayList<Part>();
        }

        @Override
        protected List<Flow> findIndexedFlows() {
            ModelEntity entity = (ModelEntity) getModel().getObject();
            if ( entity == null )
                return new ArrayList<Flow>();
            else
                return getQueryService().findAllFlowsInvolving( entity );
        }
    }
}
