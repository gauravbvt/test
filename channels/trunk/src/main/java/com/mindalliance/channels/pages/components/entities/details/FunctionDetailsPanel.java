package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Objective;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Function details panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/21/13
 * Time: 5:00 PM
 */
public class FunctionDetailsPanel extends EntityDetailsPanel implements Guidable {

    private static final String RISK = "Risk";
    private static final String GAIN = "Gain";
    private static final String[] RISK_RO_GAIN = {RISK, GAIN};

    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;
    private WebMarkupContainer objectivesContainer;

    private String addingRiskOrGain = "Risk";
    private DropDownChoice<Goal.Category> goalCategoryChoice;
    private WebMarkupContainer infoNeededContainer;
    private WebMarkupContainer infoAcquiredContainer;

    public FunctionDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-function";
    }

    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addObjectives();
        addInfoNeeded();
        addInfoAcquired();
    }

    private void addObjectives() {
        objectivesContainer = new WebMarkupContainer( "objectivesContainer" );
        objectivesContainer.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( objectivesContainer );
        addObjectivesList();
        addNewObjective();
    }

    private void addObjectivesList() {
        ListView<Objective> objectivesList = new ListView<Objective>(
                "objectives",
                getFunction().getObjectives()
        ) {
            @Override
            protected void populateItem( ListItem<Objective> item ) {
                final Objective objective = item.getModelObject();
                // inclusion checkbox
                AjaxCheckBox includedCheckBox = new AjaxCheckBox(
                        "included",
                        new Model<Boolean>( true )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        excludeObjective( objective );
                        addObjectivesList();
                        target.add( objectivesContainer );
                        Change change = new Change( Change.Type.Updated, getFunction() );
                        update( target, change );
                    }
                };
                includedCheckBox.setEnabled( isLockedByUser( getFunction() ) );
                item.add( includedCheckBox );
                // objective label
                item.add( new Label( "objectiveLabel", objective.getLabel() ) );
            }
        };
        objectivesList.setOutputMarkupId( true );
        objectivesContainer.addOrReplace( objectivesList );
    }

    private void addNewObjective() {
        addRiskOrGain();
        addGoalCategoryChoice();
    }

    private void addRiskOrGain() {
        DropDownChoice<String> riskOrGainChoice = new DropDownChoice<String>(
                "riskOrGain",
                new PropertyModel<String>( this, "addingRiskOrGain" ),
                Arrays.asList( RISK_RO_GAIN )
        );
        riskOrGainChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addGoalCategoryChoice();
                target.add( goalCategoryChoice );
            }
        } );
        riskOrGainChoice.setEnabled( isLockedByUser( getFunction() ) );
        objectivesContainer.add( riskOrGainChoice );
    }

    private void addGoalCategoryChoice() {
        goalCategoryChoice = new DropDownChoice<Goal.Category>(
                "goalCategory",
                new PropertyModel<Goal.Category>( this, "addingGoalCategory" ),
                getGoalCategoryChoices(),
                new ChoiceRenderer<Goal.Category>() {
                    @Override
                    public Object getDisplayValue( Goal.Category goalCategory ) {
                        return goalCategory.getLabel( addingRiskOrGain.equals( GAIN ) );
                    }

                    @Override
                    public String getIdValue( Goal.Category object, int index ) {
                        return Integer.toString( index );
                    }
                }

        );
        goalCategoryChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addObjectivesList();
                target.add( objectivesContainer );
                Change change = new Change( Change.Type.Updated, getFunction() );
                update( target, change );
            }
        } );
        goalCategoryChoice.setOutputMarkupId( true );
        goalCategoryChoice.setEnabled( isLockedByUser( getFunction() ) );
        objectivesContainer.addOrReplace( goalCategoryChoice );
    }

    private List<Goal.Category> getGoalCategoryChoices() {
        return Arrays.asList( Goal.Category.values() );
    }

    private void excludeObjective( Objective objective ) {
        Command command = new UpdatePlanObject(
                getUsername(),
                getFunction(),
                "objectives",
                objective,
                UpdateObject.Action.Remove
        );
        this.doCommand( command );
    }

    private void addInfoNeeded() {
        infoNeededContainer = new WebMarkupContainer( "infoNeededContainer" );
        infoNeededContainer.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( infoNeededContainer );
        infoNeededContainer.add( new InformationSpecPanel(
                "infoNeeded",
                "infoNeeded"
        ) );
    }

    private void addInfoAcquired() {
        infoAcquiredContainer = new WebMarkupContainer( "infoAcquiredContainer" );
        infoAcquiredContainer.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( infoAcquiredContainer );
        infoAcquiredContainer.add( new InformationSpecPanel(
                "infoAcquired",
                "infoAcquired"
        ) );
    }

    public String getAddingRiskOrGain() {
        return addingRiskOrGain;
    }

    public void setAddingRiskOrGain( String addingRiskOrGain ) {
        this.addingRiskOrGain = addingRiskOrGain;
    }

    public Goal.Category getAddingGoalCategory() {
        return null;
    }

    public void setAddingGoalCategory( Goal.Category goalCategory ) {
        Objective objective = new Objective( goalCategory, addingRiskOrGain.equals( GAIN ) );
        if ( !getFunction().getObjectives().contains( objective ) ) {
            Command command = new UpdatePlanObject(
                    getUsername(),
                    getFunction(),
                    "objectives",
                    objective,
                    UpdateObject.Action.AddUnique
            );
            this.doCommand( command );
        }
    }

    public Function getFunction() {
        return (Function) getEntity();
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        String property = change.getProperty();
        if ( property != null ) {
            if ( property.equals( "infoNeeded" ) ) {
                addInfoNeeded();
                target.add( infoNeededContainer );
            } else if ( property.equals( "infoAcquired" ) ) {
                addInfoAcquired();
                target.add( infoAcquiredContainer );
            }
        }
        super.updateWith( target, change, updated );
    }

    /**
     * Information (needed, acquired) panel.
     */
    private class InformationSpecPanel extends AbstractCommandablePanel {

        private String property;
        private WebMarkupContainer infoContainer;
        private Information expanded;

        public InformationSpecPanel( String id, String property ) {
            super( id );
            this.property = property;
            initPanel();
        }

        private void initPanel() {
            addInformation();
        }

        private void addInformation() {
            infoContainer = new WebMarkupContainer( "infoContainer" );
            infoContainer.setOutputMarkupId( true );
            addOrReplace( infoContainer );
            addInformationList();
            addNewInformation();
        }

        @SuppressWarnings("unchecked")
        private void addInformationList() {
            ListView<Information> infoList = new ListView<Information>(
                    "infoList",
                    getInformationList()
            ) {
                @Override
                protected void populateItem( ListItem<Information> item ) {
                    final Information info = item.getModelObject();
                    // included
                    AjaxCheckBox includedCheckBox = new AjaxCheckBox(
                            "included",
                            new Model<Boolean>( true )
                    ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            excludeInfo( info );
                            Change change = new Change( Change.Type.Updated, getFunction() );
                            change.setProperty( property );
                            update( target, change );
                        }
                    };
                    includedCheckBox.setEnabled( isLockedByUser( getFunction() ) );
                    item.add( includedCheckBox );
                    // name
                    final TextField<String> infoNameField = new TextField<String>(
                            "name",
                            new Model<String>( info.getName() )
                    );
                    infoNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            updateNameOfInfo( info, infoNameField.getConvertedInput() );
                            addInformationList();
                            target.add( infoContainer );
                            Change change = new Change( Change.Type.Updated, getFunction() );
                            change.setProperty( property );
                            update( target, change );
                        }
                    } );
                    infoNameField.setEnabled( isLockedByUser( getFunction() ) && info.getInfoProduct() == null );
                    if ( info.getInfoProduct() != null ) {
                        addTipTitle( infoNameField, "Access the info product profile to change its name" );
                    }
                    addInputHint( infoNameField, "The name of the information" );
                    item.add( infoNameField );
                    // as info product
                    final CheckBox isInfoProductCheckBox = new CheckBox(
                            "infoProduct",
                            new Model<Boolean>( info.getInfoProduct() != null )
                    );
                    isInfoProductCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            toggleInfoProduct( info, isInfoProductCheckBox.getConvertedInput() );
                            addInformationList();
                            target.add( infoContainer );
                            Change change = new Change( Change.Type.Updated, getFunction() );
                            change.setProperty( property );
                            update( target, change );
                        }
                    } );
                    item.add( isInfoProductCheckBox );
                    // info product link
                    ModelObjectLink infoProductLink = new ModelObjectLink(
                            "profile",
                            new Model<InfoProduct>( info.getInfoProduct() ),
                            new Model<String>( "info product" ) );
                    item.add( infoProductLink );
                    // add eois summary
                    List<String> eoiNames = info.getEffectiveEoiNames();
                    String eoisString = eoiNames.size() == 0
                            ? "(none)"
                            : ChannelsUtils.listToString( eoiNames, ", ", " and " );
                    item.add( new Label( "eoisLabel", "Elements: " + eoisString ) );

                    // show/hide EOIs
                    AjaxLink<String> showHideEoisLink = new AjaxLink<String>(
                            "showHideEois"
                    ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            toggleInfoExpansion( info );
                            addInformationList();
                            target.add( infoContainer );
                            if ( expanded == null ) {
                                Change change = new Change( Change.Type.Updated, getFunction() );
                                change.setProperty( property );
                                update( target, change );
                            }
                        }
                    };
                    item.add( showHideEoisLink );
                    showHideEoisLink.add( new Label(
                            "moreOrLess",
                            isExpanded( info )
                                    ? "Less"
                                    : "More"
                    ) );
                    // EOIs
                    Component eoisPanel;
                    if ( isExpanded( info ) ) {
                        eoisPanel = new InfoElementsPanel(
                                "eois",
                                property,
                                info
                        );
                    } else {
                        eoisPanel = new Label( "eois", "" );
                        eoisPanel.setOutputMarkupId( true );
                        makeVisible( eoisPanel, false );
                    }
                    item.addOrReplace( eoisPanel );
                }
            };
            infoList.setOutputMarkupId( true );
            infoContainer.addOrReplace( infoList );
        }

        private void toggleInfoProduct( Information info, Boolean isInfoProduct ) {
            int index = getInformationList().indexOf( info );
            if ( index >= 0 ) {
                if ( isInfoProduct ) {
                    if ( !info.getName().trim().isEmpty() ) {
                        MultiCommand multi = new MultiCommand( getUsername(), "Set info's info product" );
                        Change change = new Change( Change.Type.Updated, getFunction() );
                        change.setProperty( property );
                        multi.setChange( change );
                        multi.addCommand( new CreateEntityIfNew(
                                getUsername(),
                                InfoProduct.class,
                                info.getName(),
                                ModelEntity.Kind.Type ) );
                        multi.addCommand( new UpdatePlanObject(
                                getUsername(),
                                getFunction(),
                                property + "[" + index + "].infoProduct",
                                getQueryService().findOrCreateType( InfoProduct.class, info.getName() ),
                                UpdateObject.Action.Set
                        ) );
                        doCommand( multi );
                    }
                } else {
                    doCommand(
                            new UpdatePlanObject(
                                    getUsername(),
                                    getFunction(),
                                    property + "[" + index + "].infoProduct",
                                    null,
                                    UpdateObject.Action.Set
                            ) );
                }
            }
        }

        private void updateNameOfInfo( Information info, String newName ) {
            if ( newName != null
                    && !newName.isEmpty()
                    && !isRedundantInfo( newName )
                    && info.getInfoProduct() == null ) {
                int index = getInformationList().indexOf( info );
                if ( index >= 0 ) {
                    Command command = new UpdatePlanObject(
                            getUsername(),
                            getFunction(),
                            property + "[" + index + "].name",
                            newName,
                            UpdateObject.Action.Set
                    );
                    doCommand( command );
                }
            }
        }

        private boolean isExpanded( Information info ) {
            return expanded != null && expanded.equals( info );
        }

        private void excludeInfo( Information info ) {
            Command command = new UpdatePlanObject(
                    getUsername(),
                    getFunction(),
                    property,
                    info,
                    UpdateObject.Action.Remove
            );
            doCommand( command );
        }

        private void addNewInformation() {
            TextField<String> newInfoField = new TextField<String>(
                    "newInfo",
                    new PropertyModel<String>( InformationSpecPanel.this, "newInfo" )
            );
            newInfoField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    addInformationList();
                    target.add( infoContainer );
                    Change change = new Change( Change.Type.Updated, getFunction() );
                    update( target, change );
                }
            } );
            addInputHint( newInfoField, "The name of the information" );
            infoContainer.add( newInfoField );
        }

        public String getNewInfo() {
            return null;
        }

        public void setNewInfo( String value ) {
            if ( value != null && !value.trim().isEmpty() && !isRedundantInfo( value ) ) {
                Information newInfo = new Information( value );
                Command command = new UpdatePlanObject(
                        getUsername(),
                        getFunction(),
                        property,
                        newInfo,
                        UpdateObject.Action.AddUnique
                );
                doCommand( command );
            }
        }

        private boolean isRedundantInfo( final String value ) {
            return CollectionUtils.exists(
                    getInformationList(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return Matcher.same( ( (Information) object ).getName(), value );
                        }
                    } );
        }

        private void toggleInfoExpansion( Information info ) {
            if ( isExpanded( info ) ) {
                expanded = null;
            } else {
                expanded = info;
            }
        }

        @SuppressWarnings("unchecked")
        private List<Information> getInformationList() {
            return (List<Information>) ChannelsUtils.getProperty(
                    getFunction(),
                    property,
                    new ArrayList<Information>() );
        }
    }

    private class InfoElementsPanel extends AbstractCommandablePanel {

        private WebMarkupContainer eoisContainer;
        private String property;
        private Information info;

        public InfoElementsPanel( String id, String property, Information info ) {
            super( id );
            this.property = property;
            this.info = info;
            initPanel();
        }

        private void initPanel() {
            eoisContainer = new WebMarkupContainer( "eoisContainer" );
            eoisContainer.setOutputMarkupId( true );
            addOrReplace( eoisContainer );
            addEoisList();
            addNewEoi();
        }

        private void addEoisList() {
            ListView<ElementOfInformation> eoisList = new ListView<ElementOfInformation>(
                    "eoisList",
                    info.getEffectiveEois()
            ) {
                @Override
                protected void populateItem( ListItem<ElementOfInformation> item ) {
                    final ElementOfInformation eoi = item.getModelObject();
                    // included
                    AjaxCheckBox includedCheckBox = new AjaxCheckBox(
                            "included",
                            new Model<Boolean>( true )
                    ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            excludeEoi( eoi );
                            Change change = new Change( Change.Type.Updated, getFunction() );
                            change.setProperty( property );
                            update( target, change );
                        }
                    };
                    boolean local = info.isLocalEoi( eoi );
                    includedCheckBox.setEnabled( local && isLockedByUser( getFunction() ) );
                    item.add( includedCheckBox );
                    // name
                    final TextField<String> eoiNameField = new TextField<String>(
                            "name",
                            new Model<String>( local
                                    ? eoi.getContent()
                                    : ( eoi.getContent() + " (from " + info.getInfoProduct().getName() + ")" )
                            )
                    );
                    eoiNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            updateNameOfEoi( info, eoi, eoiNameField.getConvertedInput() );
                            addEoisList();
                            target.add( eoisContainer );
                        }
                    } );
                    eoiNameField.setEnabled( local && isLockedByUser( getFunction() ) );
                    addInputHint( eoiNameField, "An element of information" );
                    item.add( eoiNameField );
                    // question
                    final TextField<String> eoiQuestionField = new TextField<String>(
                            "question",
                            new Model<String>( eoi.getDescription() )
                    );
                    eoiQuestionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            updateQuestionOfEoi( info, eoi, eoiQuestionField.getConvertedInput() );
                            addEoisList();
                            target.add( eoisContainer );
                        }
                    } );
                    eoiQuestionField.setEnabled( local && isLockedByUser( getFunction() ) );
                    addInputHint( eoiQuestionField, "The question the element answers" );
                    item.add( eoiQuestionField );
                }
            };
            eoisList.setOutputMarkupId( true );
            eoisContainer.addOrReplace( eoisList );
        }

        private void updateNameOfEoi( Information info, ElementOfInformation eoi, String value ) {
            if ( value != null
                    && !value.isEmpty()
                    && !isRedundantEoi( value )
                    && info.isLocalEoi( eoi ) ) {
                int infoIndex = getInformationList().indexOf( info );
                int eoiIndex = info.getEois().indexOf( eoi );
                if ( infoIndex >= 0 && eoiIndex >= 0 ) {
                    Command command = new UpdatePlanObject(
                            getUsername(),
                            getFunction(),
                            property + "[" + infoIndex + "].eois[" + eoiIndex + "].content",
                            value,
                            UpdateObject.Action.Set
                    );
                    doCommand( command );
                }
            }
        }

        private void updateQuestionOfEoi( Information info, ElementOfInformation eoi, String value ) {
            if ( info.isLocalEoi( eoi ) ) {
                int infoIndex = getInformationList().indexOf( info );
                int eoiIndex = info.getEois().indexOf( eoi );
                if ( infoIndex >= 0 && eoiIndex >= 0 ) {
                    Command command = new UpdatePlanObject(
                            getUsername(),
                            getFunction(),
                            property + "[" + infoIndex + "].eois[" + eoiIndex + "].description",
                            value,
                            UpdateObject.Action.Set
                    );
                    doCommand( command );
                }
            }
        }

        private boolean isRedundantEoi( final String value ) {
            return CollectionUtils.exists(
                    info.getEois(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return Matcher.same( ( (ElementOfInformation) object ).getContent(), value );
                        }
                    }
            );
        }

        private void excludeEoi( ElementOfInformation eoi ) {
            int index = getInformationList().indexOf( info );
            if ( index >= 0 ) {
                Command command = new UpdatePlanObject(
                        getUsername(),
                        getFunction(),
                        property + "[" + index + "].eois",
                        eoi,
                        UpdateObject.Action.Remove
                );
                doCommand( command );
            }

        }

        @SuppressWarnings("unchecked")
        private List<Information> getInformationList() {
            return (List<Information>) ChannelsUtils.getProperty(
                    getFunction(),
                    property,
                    new ArrayList<Information>() );
        }


        private void addNewEoi() {
            TextField<String> newEoiField = new TextField<String>(
                    "newEoi",
                    new PropertyModel<String>( InfoElementsPanel.this, "newEoi" )
            );
            newEoiField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    addEoisList();
                    target.add( eoisContainer );
                    Change change = new Change( Change.Type.Updated, getFunction() );
                    update( target, change );
                }
            } );
            addInputHint( newEoiField, "An element of information" );
            eoisContainer.add( newEoiField );
        }

        public String getNewEoi() {
            return null;
        }

        public void setNewEoi( String value ) {
            if ( value != null && !value.trim().isEmpty() && !isRedundantEoi( value ) ) {
                ElementOfInformation newEoi = new ElementOfInformation( value );
                int index = getInformationList().indexOf( info );
                if ( index >= 0 ) {
                    Command command = new UpdatePlanObject( getUsername(),
                            getFunction(),
                            property + "[" + index + "].eois",
                            newEoi,
                            UpdateObject.Action.AddUnique
                    );
                    doCommand( command );
                }
            }
        }


    }
}
