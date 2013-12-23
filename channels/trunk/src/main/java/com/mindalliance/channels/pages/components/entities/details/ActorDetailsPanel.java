package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.WorkTime;
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
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
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


    /**
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    //Container to add components to.
    private WebMarkupContainer moDetailsDiv;

    private WebMarkupContainer languagesContainer;

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
        addIsSystem();
        addContactInfo();
        addAvailability();
        addLanguages();
        addClearances();
    }


    private void addContactInfo() {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contact" );
        moDetailsDiv.add( contactContainer );
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( getActor() ),
                false,
                true ) );
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
                    UpdateObject.Action.AddUnique ) );
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

    private void addAvailability() {
        DropDownChoice<WorkTime.WorkPeriod> availabilityChoice = new DropDownChoice<WorkTime.WorkPeriod>(
                "availability",
                new PropertyModel<WorkTime.WorkPeriod>(this, "availabilityWorkPeriod"),
                WorkTime.allWorkPeriods(),
                new IChoiceRenderer<WorkTime.WorkPeriod>(  ) {
                    @Override
                    public Object getDisplayValue( WorkTime.WorkPeriod workPeriod ) {
                        return workPeriod.getLabel();
                    }
                    @Override
                    public String getIdValue( WorkTime.WorkPeriod object, int index ) {
                        return Integer.toString( index );
                    }
                });
        availabilityChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getActor(),
                                "availability"
                        ) );
            }
        });
        availabilityChoice.setEnabled( isLockedByUser( getActor() ) );
        availabilityChoice.setVisible( getActor().isActual() );
        moDetailsDiv.add( availabilityChoice );
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

    public WorkTime.WorkPeriod getAvailabilityWorkPeriod() {
        return getActor().getAvailability().getWorkPeriod();
    }

    public void setAvailabilityWorkPeriod( WorkTime.WorkPeriod workPeriod ) {
        doCommand(
                new UpdatePlanObject(
                        getUser().getUsername(),
                        getActor(),
                        "availability",
                        new WorkTime( workPeriod ) ) );
    }

    /**
     * Whether the actor is a system.
     *
     * @return a boolean
     */
    public boolean isSystem() {
        return getActor().isSystem();
    }

    private Actor getActor() {
        return (Actor) getEntity();
    }

}
