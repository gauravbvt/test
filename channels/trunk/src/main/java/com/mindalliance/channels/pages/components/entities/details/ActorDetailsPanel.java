package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Available;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 23, 2009
 * Time: 2:15:22 PM
 */
public class ActorDetailsPanel extends EntityDetailsPanel implements Guidable {

    private final static String AT_MOST_ONE = "At most one";
    private final static String ANY_NUMBER = "Any number of";
    private final static String NO_MORE_THAN = "No more than";
    private final static String[] CARD_OPTIONS = {AT_MOST_ONE, ANY_NUMBER, NO_MORE_THAN};


    /**
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    //Container to add components to.
    private WebMarkupContainer moDetailsDiv;

    private WebMarkupContainer languagesContainer;
    private NumberTextField<Integer> cardinalityField;
    private Label participantLabel;
    private String cardinalityChoice;

    public ActorDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-agent";
    }


    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addParticipationFields();
        addIsSystem();
        addContactInfo();
        addAvailabilityPanel();
        addLanguages();
        addClearances();
    }


    private void addContactInfo() {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contact" );
        moDetailsDiv.add( contactContainer );
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( getActor() ) ) );
        contactContainer.setVisible( getActor().isActual() );
    }

    private void addLanguages() {
        final List<String> choices = allLanguageChoices();
        languagesContainer = new WebMarkupContainer( "languagesContainer" );
        languagesContainer.setOutputMarkupId( true );
        ListView<String> languageList = new ListView<String>(
                "languages",
                new PropertyModel<List<String>>( this, "languages" ) ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<String> item ) {
                addLanguageNameCell( item, choices );
                addDeleteLanguageCell( item );
            }
        };
        languagesContainer.add( languageList );
        moDetailsDiv.addOrReplace( languagesContainer );
    }

    private void addLanguageNameCell( final ListItem<String> item, final List<String> choices ) {
        String language = item.getModelObject();
        Label nameLabel = new Label( "language", language );
        nameLabel.setVisible( !language.isEmpty() );
        item.add( nameLabel );
        AutoCompleteTextField<String> newLanguageField = new AutoCompleteTextField<String>(
                "newLanguage",
                new PropertyModel<String>( this, "language" ),
                getAutoCompleteSettings() ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        newLanguageField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // addLanguages();
                target.add( languagesContainer );
                update( target, new Change( Change.Type.Updated, getActor(), "languages" ) );
            }
        } );
        newLanguageField.setOutputMarkupId( true );
        makeVisible( newLanguageField, language.isEmpty() && isLockedByUser( getActor() ) );
        addInputHint( newLanguageField, "A language spoken and understood" );
        item.add( newLanguageField );
    }

    private void addDeleteLanguageCell( ListItem<String> item ) {
        final String language = item.getModelObject().toLowerCase();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deleteLanguage",
                "Remove language?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(),
                        "languages",
                        language,
                        UpdateObject.Action.Remove ) );
                addLanguages();
                target.add( languagesContainer );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getActor(),
                                "languages"
                        ) );
            }
        };
        makeVisible( deleteLink, isLockedByUser( getEntity() ) && !language.isEmpty() );
        item.add( deleteLink );
    }

    private List<String> allLanguageChoices() {
        Set<String> choices = new HashSet<String>();
        for ( Actor actor : getQueryService().list( Actor.class ) ) {
            choices.addAll( actor.getLanguages() );
        }
        choices.removeAll( getActor().getLanguages() );
        return new ArrayList<String>( choices );
    }

    public String getLanguage() {
        return "";
    }

    public void setLanguage( String name ) {
        if ( name != null && !name.isEmpty() ) {
            doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(),
                    "languages",
                    name.toLowerCase(),
                    UpdateObject.Action.Add ) );
        }
    }


    public List<String> getLanguages() {
        List<String> languages = new ArrayList<String>();
        for ( String language : getActor().getLanguages() ) {
            languages.add( StringUtils.capitalize( language ) );
        }
        Collections.sort( languages );
        languages.add( "" );
        return languages;
    }

    private void addAvailabilityPanel() {
        moDetailsDiv.add( new AvailabilityPanel(
                "availability",
                new Model<Available>( getActor() ) ) );
    }

    private void addClearances() {
        WebMarkupContainer clearancesContainer = new WebMarkupContainer( "clearancesContainer" );
        moDetailsDiv.add( clearancesContainer );
        clearancesContainer.add( new ClassificationsPanel(
                "clearances",
                new Model<Identifiable>( getActor() ),
                "clearances",
                isLockedByUser( getActor() )
        )
        );
        clearancesContainer.setVisible( getActor().isActual() );
    }

    private void addParticipationFields() {
        WebMarkupContainer participationContainer = new WebMarkupContainer( "participationConstraints" );
        participationContainer.setVisible( getActor().isActual() );
        moDetailsDiv.add( participationContainer );
        addOpenParticipationCheckBox( participationContainer );
        addParticipationCardinality( participationContainer );
        addSameEmployerParticipation( participationContainer );
        addSupervisedParticipation( participationContainer );
        addAnonymousParticipation( participationContainer );
    }

    private void addOpenParticipationCheckBox( WebMarkupContainer participationContainer ) {
        CheckBox openParticipationCheckBox = new CheckBox(
                "isParticipationOpen",
                new PropertyModel<Boolean>( this, "openParticipation" ) );
        openParticipationCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "openParticipation" ) );
            }
        } );
        participationContainer.add( openParticipationCheckBox );
        openParticipationCheckBox.setEnabled( isLockedByUser( getActor() ) );
    }

    private void addParticipationCardinality( WebMarkupContainer participationContainer ) {
        addCardinalityChoice( participationContainer );
        addCardinalityField( participationContainer );
        addParticipantLabel( participationContainer );
    }

    private void addCardinalityChoice( final WebMarkupContainer participationContainer ) {
        int maxParticipation = getActor().getMaxParticipation();
        cardinalityChoice = maxParticipation == -1
                ? ANY_NUMBER
                : maxParticipation == 1
                ? AT_MOST_ONE
                : NO_MORE_THAN;
        DropDownChoice<String> cardinalityChoice = new DropDownChoice<String>(
                "cardinalityChoice",
                new PropertyModel<String>( this, "cardinalityChoice" ),
                Arrays.asList( CARD_OPTIONS ) );
        cardinalityChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( cardinalityField, getCardinalityChoice().equals( NO_MORE_THAN ) );
                target.add( cardinalityField );
                addParticipantLabel( participationContainer );
                target.add( participantLabel );
            }
        } );
        cardinalityChoice.setEnabled( isLockedByUser( getActor() ) );
        participationContainer.add( cardinalityChoice );
    }

    private void addCardinalityField( final WebMarkupContainer participationContainer ) {
        cardinalityField = new NumberTextField<Integer>(
                "maxParticipation",
                new PropertyModel<Integer>( this, "maxParticipation" )
        );
        cardinalityField.setMinimum( 2 );
        cardinalityField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipantLabel( participationContainer );
                target.add( participantLabel );
                update( target, new Change( Change.Type.Updated, getActor(), "maxParticipation" ) );
            }
        } );
        makeVisible( cardinalityField, getCardinalityChoice().equals( NO_MORE_THAN ) );
        participationContainer.add( cardinalityField );
    }

    private void addParticipantLabel( WebMarkupContainer participationContainer ) {
        participantLabel = new Label(
                "participantLabel",
                isSingularParticipation() ? "participant" : "participants" );
        participantLabel.setOutputMarkupId( true );
        participationContainer.addOrReplace( participantLabel );
    }

    private void addSameEmployerParticipation( WebMarkupContainer participationContainer ) {
        CheckBox sameEmployerCheckBox = new CheckBox(
                "hasSameEmployer",
                new PropertyModel<Boolean>( this, "participationRestrictedToEmployed" ) );
        sameEmployerCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "participationRestrictedToEmployed" ) );
            }
        } );
        participationContainer.add( sameEmployerCheckBox );
        sameEmployerCheckBox.setEnabled( isLockedByUser( getActor() ) );
    }

    private void addSupervisedParticipation( WebMarkupContainer participationContainer ) {
        CheckBox supervisedCheckBox = new CheckBox(
                "isSupervised",
                new PropertyModel<Boolean>( this, "supervisedParticipation" ) );
        supervisedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "supervisedParticipation" ) );
            }
        } );
        participationContainer.add( supervisedCheckBox );
        supervisedCheckBox.setEnabled( isLockedByUser( getActor() ) );
    }


    private void addAnonymousParticipation( WebMarkupContainer participationContainer ) {
        CheckBox anonymousCheckBox = new CheckBox(
                "isAnonymous",
                new PropertyModel<Boolean>( this, "anonymousParticipation" ) );
        anonymousCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "anonymousParticipation" ) );
            }
        } );
        participationContainer.add( anonymousCheckBox );
        anonymousCheckBox.setEnabled( isLockedByUser( getActor() ) );
    }


    private void addIsSystem() {
        WebMarkupContainer systemContainer = new WebMarkupContainer( "system" );
        moDetailsDiv.add( systemContainer );
        /*
      Is system checkbox.
     */
        CheckBox systemCheckBox = new CheckBox( "system", new PropertyModel<Boolean>( this, "system" ) );
        systemCheckBox.setOutputMarkupId( true );
        systemCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "system" ) );
            }
        } );
        systemCheckBox.setEnabled( isLockedByUser( getActor() ) );
        systemContainer.setVisible( getActor().isActual() );
        systemContainer.add( systemCheckBox );
    }


    /**
     * Run command to change actor system property.
     *
     * @param isSystem a boolean
     */
    public void setSystem( boolean isSystem ) {
        Actor actor = getActor();
        if ( actor.isSystem() != isSystem )
            doCommand( new UpdatePlanObject( getUser().getUsername(), actor, "system", isSystem ) );
    }

    /**
     * Whether the actor is a system.
     *
     * @return a boolean
     */
    public boolean isSystem() {
        return getActor().isSystem();
    }

    /**
     * Whether participation as the actor is open.
     *
     * @return a boolean
     */
    public boolean isOpenParticipation() {
        return getActor().isOpenParticipation();
    }

    public void setOpenParticipation( boolean val ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "openParticipation", val ) );
    }

    /**
     * Whether the actor can only have one participant.
     *
     * @return a boolean
     */
    public boolean isSingularParticipation() {
        return getActor().isSingularParticipation();
    }

    public String getCardinalityChoice() {
        return cardinalityChoice;
    }

    public void setCardinalityChoice( String val ) {
        cardinalityChoice = val;
        int maxParticipation = val.equals( ANY_NUMBER )
                ? -1
                : val.equals( AT_MOST_ONE )
                ? 1
                : 0;
        if ( maxParticipation != 0 ) {
            doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "maxParticipation", maxParticipation ) );
        }
    }

    public int getMaxParticipation() {
        int val = getActor().getMaxParticipation();
        return val < 1 ? 0 : val;
    }

    public void setMaxParticipation( int val ) {
        if ( val > 0 )
            doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "maxParticipation", val ) );
    }


    /**
     * Whether participation as the actor is open.
     *
     * @return a boolean
     */
    public boolean isParticipationRestrictedToEmployed() {
        return getActor().isParticipationRestrictedToEmployed();
    }

    public void setParticipationRestrictedToEmployed( boolean val ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "participationRestrictedToEmployed", val ) );
    }

    /**
     * Whether participation must be confirmed by supervisor.
     *
     * @return a boolean
     */
    public boolean isSupervisedParticipation() {
        return getActor().isSupervisedParticipation();
    }

    public void setSupervisedParticipation( boolean val ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "supervisedParticipation", val ) );
    }


    /**
     * Whether participation as the actor is open.
     *
     * @return a boolean
     */
    public boolean isAnonymousParticipation() {
        return getActor().isAnonymousParticipation();
    }

    public void setAnonymousParticipation( boolean val ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getActor(), "anonymousParticipation", val ) );
    }


    private Actor getActor() {
        return (Actor) getEntity();
    }

}
