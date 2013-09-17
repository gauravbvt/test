package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Delegated-to media panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 4, 2010
 * Time: 3:20:59 PM
 */
public class DelegatedToMediaPanel extends AbstractCommandablePanel {
    /**
     * Table of media.
     */
    private WebMarkupContainer mediaContainer;

    public DelegatedToMediaPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addMediaList();
    }

    private void addMediaList() {
        List<MediumWrapper> mediaWrappers = getWrappedMedia();
        mediaContainer = new WebMarkupContainer( "mediaContainer" );
        mediaContainer.setOutputMarkupId( true );
        addOrReplace( mediaContainer );
        ListView<MediumWrapper> mediaList = new ListView<MediumWrapper>(
                "media",
                mediaWrappers
        ) {
            protected void populateItem( ListItem<MediumWrapper> item ) {
                item.setOutputMarkupId( true );
                addItemMediumLink( item );
                addItemMediumChoice( item );
                addItemDeleteMedium( item );
            }
        };
        mediaContainer.add( mediaList );
    }

    private void addItemMediumLink( ListItem<MediumWrapper> item ) {
        MediumWrapper wrapper = item.getModelObject();
        if ( wrapper.isMarkedForCreation() ) {
            Label noLink = new Label( "mediumLink", "" );
            makeVisible( noLink, false );
            item.add( noLink );
        } else {
            EntityLink mediumLink = new EntityLink(
                    "mediumLink",
                    new PropertyModel<TransmissionMedium>( wrapper, "medium" ) );
            item.add( mediumLink );
        }
    }

    private void addItemMediumChoice( ListItem<MediumWrapper> item ) {
        MediumWrapper wrapper = item.getModelObject();
        List<TransmissionMedium> candidates = wrapper.isMarkedForCreation()
                ? getCandidates()
                : new ArrayList<TransmissionMedium>();
        DropDownChoice<TransmissionMedium> mediaChoice = new DropDownChoice<TransmissionMedium>(
                "delegatedTo",
                new PropertyModel<TransmissionMedium>( wrapper, "medium" ),
                candidates,
                new IChoiceRenderer<TransmissionMedium>() {
                    public Object getDisplayValue( TransmissionMedium choice ) {
                        return choice == null ? "Select a medium" : choice.getName();
                    }

                    public String getIdValue( TransmissionMedium object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        mediaChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getModeledMedium(), "delegatedToMedia" ) );
                addMediaList();
                target.add( mediaContainer );
            }
        } );
        makeVisible( mediaChoice, wrapper.isMarkedForCreation()
                && !candidates.isEmpty()
                && isLockedByUser( getModeledMedium() ) );
        item.add( mediaChoice );
    }


    /**
     * Find all known media that are not already listed as delegated to or are not subsumed by a listed medium.
     *
     * @return a list of transmission media
     */
    private List<TransmissionMedium> getCandidates() {
        Set<TransmissionMedium> candidates = new HashSet<TransmissionMedium>();
        List<TransmissionMedium> delegatedToMedia = getModeledMedium().getDelegatedToMedia();
        List<TransmissionMedium> allMedia = getQueryService().findAllModelObjects( TransmissionMedium.class );
        for ( final TransmissionMedium candidate : allMedia ) {
            boolean covered = CollectionUtils.exists(
                    delegatedToMedia,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            TransmissionMedium delegatedTo = (TransmissionMedium) object;
                            return candidate.equals( delegatedTo )
                                    || candidate.getAllTypes().contains( delegatedTo );
                        }
                    }
            );
            if ( !covered
                    && !candidate.isDirect()
                    && !candidate.equals( getModeledMedium() )
                    && !getModeledMedium().getAllTypes().contains( candidate )
                    && !candidate.isUniversal()
                    && !candidate.isUnknown()
                    && !getModeledMedium().getInheritedDelegates().contains( candidate )
                    && getModeledMedium().canDelegateTo( candidate ) ) {
                candidates.add( candidate );
            }
        }
        return new ArrayList<TransmissionMedium>( candidates );
    }

    private void addItemDeleteMedium( ListItem<MediumWrapper> item ) {
        final MediumWrapper wrapper = item.getModelObject();
        AjaxLink deleteLink = new AjaxLink(
                "delete" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.deleteMedium();
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getModeledMedium(),
                                "delegatedToMedia"
                        ) );
                addMediaList();
                target.add( mediaContainer );
            }
        };
        makeVisible( deleteLink, wrapper.canBeDeleted() );
        item.add( deleteLink );
    }

    private List<MediumWrapper> getWrappedMedia() {
        List<MediumWrapper> wrappers = new ArrayList<MediumWrapper>();
        for ( TransmissionMedium delegatedTo : getModeledMedium().getEffectiveDelegatedToMedia() ) {
            wrappers.add( new MediumWrapper( delegatedTo ) );
        }
        wrappers.add( new MediumWrapper() );
        return wrappers;
    }

    private TransmissionMedium getModeledMedium() {
        return (TransmissionMedium) getModel().getObject();
    }

    /**
     * Transmission medium wrapper.
     */
    public class MediumWrapper implements Serializable {
        /**
         * Wrapped medium.
         */
        private TransmissionMedium medium;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;

        public MediumWrapper() {
            markedForCreation = true;
        }

        public MediumWrapper( TransmissionMedium medium ) {
            this.medium = medium;
            markedForCreation = false;
        }

        public TransmissionMedium getMedium() {
            return medium;
        }

        public void setMedium( TransmissionMedium val ) {
            assert markedForCreation;
            if ( !getModeledMedium().getDelegatedToMedia().contains( val ) ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), getModeledMedium(),
                        "delegatedToMedia",
                        val,
                        UpdateObject.Action.AddUnique ) );
            }
            medium = val;
        }

        public void deleteMedium() {
            assert !markedForCreation;
            String name = medium.getName();
            doCommand( new UpdatePlanObject( getUser().getUsername(), getModeledMedium(),
                    "delegatedToMedia",
                    medium,
                    UpdateObject.Action.Remove ) );
            getCommander().cleanup( TransmissionMedium.class, name );
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public String getName() {
            return medium == null ? "" : medium.getName();
        }

        public boolean canBeDeleted() {
            return !isMarkedForCreation()
                    && isLockedByUser( getModeledMedium() )
                    && !getModeledMedium().getInheritedDelegates().contains( medium );
        }
    }
}
