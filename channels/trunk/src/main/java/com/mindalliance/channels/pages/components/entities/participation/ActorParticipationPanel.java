package com.mindalliance.channels.pages.components.entities.participation;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/8/13
 * Time: 12:06 PM
 */
public class ActorParticipationPanel extends AbstractCommandablePanel implements Guidable {

    private final static String AT_MOST_ONE = "At most one";
    private final static String ANY_NUMBER = "Any number of";
    private final static String NO_MORE_THAN = "No more than";
    private final static String[] CARD_OPTIONS = {AT_MOST_ONE, ANY_NUMBER, NO_MORE_THAN};

    private WebMarkupContainer participationContainer;
    private NumberTextField<Integer> cardinalityField;
    private Label participantLabel;
    private String cardinalityChoice;

    public ActorParticipationPanel( String id, PropertyModel<Actor> actorModel, Set<Long> expansions ) {
        super( id, actorModel, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "agent-participation";
    }


    private void init() {
        addParticipationFields();
    }

    private void addParticipationFields() {
        participationContainer = new WebMarkupContainer( "participationConstraints" );
        participationContainer.setOutputMarkupId( true );
        participationContainer.setVisible( getActor().isActual() );
        add( participationContainer );
        addOpenParticipationCheckBox(  );
        addParticipationCardinality(  );
        addSameEmployerParticipation(  );
        addSupervisedParticipation(  );
        addAnonymousParticipation(  );
    }

    private void addOpenParticipationCheckBox(  ) {
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

    private void addParticipationCardinality(  ) {
        addCardinalityChoice(  );
        addCardinalityField(  );
        addParticipantLabel(  );
    }

    private void addCardinalityChoice(  ) {
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
                addParticipantLabel(  );
                target.add( participantLabel );
            }
        } );
        cardinalityChoice.setEnabled( isLockedByUser( getActor() ) );
        participationContainer.add( cardinalityChoice );
    }

    private void addCardinalityField(  ) {
        cardinalityField = new NumberTextField<Integer>(
                "maxParticipation",
                new PropertyModel<Integer>( this, "maxParticipation" )
        );
        cardinalityField.setMinimum( 2 );
        cardinalityField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipantLabel(  );
                target.add( participantLabel );
                update( target, new Change( Change.Type.Updated, getActor(), "maxParticipation" ) );
            }
        } );
        makeVisible( cardinalityField, getCardinalityChoice().equals( NO_MORE_THAN ) );
        participationContainer.add( cardinalityField );
    }

    private void addParticipantLabel(  ) {
        participantLabel = new Label(
                "participantLabel",
                isSingularParticipation() ? "user" : "users" );
        participantLabel.setOutputMarkupId( true );
        participationContainer.addOrReplace( participantLabel );
    }

    private void addSameEmployerParticipation(  ) {
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

    private void addSupervisedParticipation(  ) {
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


    private void addAnonymousParticipation(  ) {
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
        return (Actor) getModel().getObject();
    }



}
