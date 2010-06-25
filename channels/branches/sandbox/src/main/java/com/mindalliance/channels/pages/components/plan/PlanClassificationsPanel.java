package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan classifications panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 3, 2009
 * Time: 12:36:48 PM
 */
public class PlanClassificationsPanel extends AbstractCommandablePanel {

    private WebMarkupContainer indexContainer;
    private Classification selectedClassification;

    public PlanClassificationsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    private void init() {
        addClassificationsEditor();
        indexContainer = new WebMarkupContainer( "indexContainer" );
        indexContainer.setOutputMarkupId( true );
        add( indexContainer );
        addClassificationIndex();
        adjustFields();
    }

    private void adjustFields() {
        makeVisible( indexContainer, selectedClassification != null );
    }

    private void addClassificationsEditor() {
        ClassificationsEditor editor = new ClassificationsEditor( "editor", getModel() );
        add( editor );

    }

    private void addClassificationIndex() {
        Label classificationLabel = new Label(
                "classification",
                new Model<String>( selectedClassification == null
                        ? ""
                        : selectedClassification.getLabel() ) );
        classificationLabel.setOutputMarkupId( true );
        indexContainer.addOrReplace( classificationLabel );
        if ( selectedClassification == null ) {
            Label indexLabel = new Label( "index", "" );
            indexContainer.setOutputMarkupId( true );
            indexContainer.addOrReplace( indexLabel );
        } else {
            ClassificationIndexPanel classificationIndexPanel = new ClassificationIndexPanel(
                    "index",
                    getModel()
            );
            indexContainer.setOutputMarkupId( true );
            indexContainer.addOrReplace( classificationIndexPanel );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() && change.isForInstanceOf( Classification.class ) ) {
            selectedClassification = (Classification) change.getSubject( getQueryService() );
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.isForInstanceOf( Classification.class ) ) {
            addClassificationIndex();
            adjustFields();
            target.addComponent( indexContainer );
        } else if ( change.isUpdated() && change.getProperty().equals( "classifications") ) {
            if ( !getPlan().getClassifications().contains( selectedClassification)) {
                selectedClassification = null;
                addClassificationIndex();
                target.addComponent( indexContainer );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    private Classification getClassification() {
        return selectedClassification;
    }

    /**
     * Classification references index panel.
     */
    private class ClassificationIndexPanel extends AbstractIndexPanel {

        private ClassificationIndexPanel(
                String id,
                IModel<? extends Identifiable> model
        ) {
            super( id, model, null );
        }

        @SuppressWarnings( "unchecked" )
        protected List<Actor> findIndexedActors() {
            return (List<Actor>) CollectionUtils.select(
                    getQueryService().listActualEntities( Actor.class ),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ( (Actor) obj ).getClearances().contains( getClassification() );
                        }
                    }
            );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Event> findIndexedEvents() {
            return new ArrayList<Event>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Organization> findIndexedOrganizations() {
            return new ArrayList<Organization>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Segment> findIndexedSegments() {
            return new ArrayList<Segment>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Phase> findIndexedPhases() {
            return new ArrayList<Phase>();
        }

        /**
          * {@inheritDoc}
          */
         protected List<TransmissionMedium> findIndexedMedia() {
             return new ArrayList<TransmissionMedium>();
         }

        /**
         * {@inheritDoc}
         */
        protected List<Place> findIndexedPlaces() {
            return new ArrayList<Place>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Role> findIndexedRoles() {
            return new ArrayList<Role>();
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings( "unchecked" )
        protected List<Flow> findIndexedFlows() {
            return (List<Flow>) CollectionUtils.select(
                    getQueryService().findAllFlows(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ( (Flow) obj ).getClassifications().contains( getClassification() );
                        }
                    }
            );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            return new ArrayList<Part>();
        }
    }

}
