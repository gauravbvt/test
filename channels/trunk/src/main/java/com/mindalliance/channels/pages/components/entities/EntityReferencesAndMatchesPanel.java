package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Entity references and matches index panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 15, 2009
 * Time: 4:19:24 PM
 */
public class EntityReferencesAndMatchesPanel extends AbstractUpdatablePanel {

    private boolean showReferences = true;
    private CheckBox referencesCheckBox;
    private CheckBox matchesCheckBox;
    private EntityIndexPanel indexPanel;

    public EntityReferencesAndMatchesPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addReferencesOrMatches();
        addIndexPanel();
    }

    private void addIndexPanel() {
        indexPanel = new EntityIndexPanel( "index", getModel(), null );
        indexPanel.setOutputMarkupId( true );
        addOrReplace( indexPanel );
    }

    private void addReferencesOrMatches() {
        WebMarkupContainer refsVsMatches = new WebMarkupContainer( "referencesVsMatches" );
        refsVsMatches.setVisible( getEntity().isType() );
        add( refsVsMatches );
        referencesCheckBox = new CheckBox(
                "references",
                new PropertyModel<Boolean>( this, "showReferences" ) );
        referencesCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIndexPanel();
                target.add( matchesCheckBox );
                target.add( indexPanel );
            }
        } );
        refsVsMatches.add( referencesCheckBox );
        matchesCheckBox = new CheckBox(
                "matches",
                new PropertyModel<Boolean>( this, "showMatches" ) );
        matchesCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIndexPanel();
                target.add( referencesCheckBox );
                target.add( indexPanel );
            }
        } );
        refsVsMatches.add( matchesCheckBox );
    }

    public boolean isShowReferences() {
        return showReferences;
    }

    public void setShowReferences( boolean val ) {
        showReferences = val;
    }

    public boolean isShowMatches() {
        return !showReferences;
    }

    public void setShowMatches( boolean val ) {
        showReferences = !val;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    /**
     * Entity index panel.
     */
    private class EntityIndexPanel extends AbstractIndexPanel {

        public EntityIndexPanel(
                String id,
                IModel<? extends Identifiable> model,
                Set<Long> expansions ) {
            super( id, model, expansions );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Actor> findIndexedActors() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Actor.class )
                    : listMatches( Actor.class );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Event> findIndexedEvents() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Event.class )
                    : listMatches( Event.class );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Organization> findIndexedOrganizations() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Organization.class )
                    : listMatches( Organization.class );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Phase> findIndexedPhases() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Phase.class )
                    : listMatches( Phase.class );
        }

        /**
          * {@inheritDoc}
          */
         protected List<TransmissionMedium> findIndexedMedia() {
             return isShowReferences()
                     ? getQueryService().findAllReferencing( getEntity(), TransmissionMedium.class )
                     : listMatches( TransmissionMedium.class );
         }

        /**
         * {@inheritDoc}
         */
        protected List<Place> findIndexedPlaces() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Place.class )
                    : listMatches( Place.class );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Role> findIndexedRoles() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Role.class )
                    : listMatches( Role.class );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Flow> findIndexedFlows() {
            return isShowReferences()
                ? getQueryService().findAllReferencing( getEntity(), Flow.class )
                : new ArrayList<Flow>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<InfoProduct> findIndexedInfoProducts() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), InfoProduct.class )
                    : new ArrayList<InfoProduct>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<InfoFormat> findIndexedInfoFormats() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), InfoFormat.class )
                    : new ArrayList<InfoFormat>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Function> findIndexedFunctions() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Function.class )
                    : new ArrayList<Function>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<MaterialAsset> findIndexedMaterialAssets() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), MaterialAsset.class )
                    : new ArrayList<MaterialAsset>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Part.class )
                    : new ArrayList<Part>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Segment> findIndexedSegments() {
            return isShowReferences()
                    ? getQueryService().findAllReferencing( getEntity(), Segment.class )
                    : new ArrayList<Segment>();
        }

        @SuppressWarnings( "unchecked" )
        private <T extends ModelEntity> List<T> listMatches( Class<T> entityClass ) {
            if ( entityClass.isAssignableFrom( getEntity().getClass() ) ) {
                return (List<T>)CollectionUtils.select(
                        getQueryService().listEntitiesNarrowingOrEqualTo( getEntity() ),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return !getEntity().equals( obj );
                            }
                        });
            } else {
                return new ArrayList<T>();
            }
        }

    }

}
