package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A phases list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 21, 2009
 * Time: 9:51:20 AM
 */
public class PhaseListPanel extends AbstractCommandablePanel {
    /**
     * Phases list container.
     */
    private WebMarkupContainer phasesDiv;

    public PhaseListPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        phasesDiv = new WebMarkupContainer( "phasesDiv" );
        phasesDiv.setOutputMarkupId( true );
        add( phasesDiv );
        makePhasesTable();
    }

    private void makePhasesTable() {
        List<PhaseWrapper> phaseWrappers = getWrappedPhases();
        phasesDiv.addOrReplace( new ListView<PhaseWrapper>( "phase", phaseWrappers ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<PhaseWrapper> item ) {
                addPhaseCell( item );
                addDeleteCell( item );
            }
        } );
    }

    private void addPhaseCell( final ListItem<PhaseWrapper> item ) {
        item.setOutputMarkupId( true );
        final PhaseWrapper wrapper = item.getModelObject();
        WebMarkupContainer nameContainer = new WebMarkupContainer( "name-container" );
        item.add( nameContainer );
        final List<String> choices;
        if ( wrapper.isMarkedForCreation() ) {
            choices = getQueryService().findAllEntityNames( Phase.class );
        } else {
            choices = new ArrayList<String>();
        }
        // text field
        TextField<String> nameField = new AutoCompleteTextField<String>(
                "name-input",
                new PropertyModel<String>( wrapper, "name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.getInstance().matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        nameField.setVisible( getPlan().isDevelopment() && wrapper.isMarkedForCreation() );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                makePhasesTable();
                target.addComponent( phasesDiv );
                update( target, new Change( Change.Type.Added, getPlan(), "phases" ) );
            }
        } );
        nameContainer.add( nameField );
        EntityLink phaseLink = new EntityLink( "phase-link", new PropertyModel<Phase>( wrapper, "phase" ) );
        phaseLink.setVisible( !wrapper.isMarkedForCreation() );
        nameContainer.add( phaseLink );
    }

    private void addDeleteCell( ListItem<PhaseWrapper> item ) {
        final PhaseWrapper wrapper = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete phase?" ) {
            public void onClick( AjaxRequestTarget target ) {
                Phase deletedPhase = wrapper.getPhase();
                wrapper.deletePhase();
                makePhasesTable();
                target.addComponent( phasesDiv );
                update( target,
                        new Change(
                                Change.Type.Collapsed,
                                deletedPhase
                        ) );
                getCommander().cleanup( Phase.class, deletedPhase.getName() );
            }
        };
        makeVisible(
                deleteLink,
                getPlan().isDevelopment()
                        && !wrapper.isMarkedForCreation()
                        && getPlan().getPhases().size() > 1
                        && !getQueryService().isReferenced( wrapper.getPhase() ) );
        item.addOrReplace( deleteLink );
    }


    /**
     * Get phase wrappers.
     *
     * @return a list of plan phase wrappers
     */
    public List<PhaseWrapper> getWrappedPhases() {
        // Existing phases
        List<PhaseWrapper> wrappers = new ArrayList<PhaseWrapper>();
        for ( Phase phase : getPlan().getPhases() ) {
            wrappers.add( new PhaseWrapper( phase ) );
        }
        // Sort
        Collections.sort( wrappers, new Comparator<PhaseWrapper>() {
            public int compare( PhaseWrapper wrapper, PhaseWrapper otherWrapper ) {
                return wrapper.getPhase().compareTo( otherWrapper.getPhase() );
            }
        } );
        // New phase
        PhaseWrapper creationPhaseWrapper = new PhaseWrapper( new Phase() );
        creationPhaseWrapper.setMarkedForCreation( true );
        wrappers.add( creationPhaseWrapper );
        return wrappers;
    }

    public class PhaseWrapper implements Serializable {
        /**
         * Wrapped phase.
         */
        private Phase phase;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;

        public PhaseWrapper( Phase phase ) {
            this.phase = phase;
        }

        public Phase getPhase() {
            return phase;
        }

        public void setPhase( Phase phase ) {
            this.phase = phase;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public String getName() {
            return phase.getName();
        }

        public void setName( String name ) {
            Plan plan = getPlan();
            if ( name != null && !name.trim().isEmpty() ) {
                Phase phase = getQueryService().findOrCreate( Phase.class, name.trim() );
                if ( !plan.getPhases().contains( phase ) ) {
                    doCommand( new UpdatePlanObject(
                            plan,
                            "phases",
                            phase,
                            UpdateObject.Action.Add
                    ) );
                }
            }
        }

        public void deletePhase() {
            if ( getPlan().getPhases().contains( phase ) && !getQueryService().isReferenced( phase ) ) {
                // Possible but unlikely race condition if another command executed here
                // that alters the results of the above test.
                doCommand( new UpdatePlanObject(
                        getPlan(),
                        "phases",
                        phase,
                        UpdateObject.Action.RemoveExceptLast
                ) );
            }
        }
    }
}
