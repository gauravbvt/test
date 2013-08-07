package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Classification system panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 11, 2010
 * Time: 10:36:45 AM
 */
public class ClassificationSystemPanel extends AbstractCommandablePanel {

    private String newClassificationName;
    private Classification selectedClassification;
    private String classificationSystem;
    private boolean canBeEdited;
    private WebMarkupContainer classificationsContainer;
    private WebMarkupContainer indexContainer;
    private TextField<String> newClassificationField;

    public ClassificationSystemPanel( String id, String classificationSystem, boolean canBeEdited ) {
        super( id );
        this.classificationSystem = classificationSystem;
        this.canBeEdited = canBeEdited;
        init();
    }

    private void init() {
        classificationsContainer = new WebMarkupContainer( "classifications-container" );
        classificationsContainer.setOutputMarkupId( true );
        add( classificationsContainer );
        addClassificationsList();
        addNameLabel();
        addNewClassification();
        addClassificationIndex();
    }

    private void addNameLabel() {
        Label nameLabel = new Label( "system-name", classificationSystem );
        add( nameLabel );
    }

    private void addClassificationsList() {
        ListView<Classification> classificationsListView = new ListView<Classification>(
                "classifications",
                new PropertyModel<List<Classification>>( this, "classifications" )
        ) {
            @Override
            protected void populateItem( ListItem<Classification> item ) {
                final Classification classification = item.getModelObject();
                // name
                Label nameLabel = new Label( "classification-name", classification.getName() );
                item.add( nameLabel );
                // move to top
                AjaxLink moveLink = new AjaxLink( "move-to-top" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        moveToTop( classification );
                        addClassificationsList();
                        target.add( classificationsContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "classifications" ) );
                    }
                };
                moveLink.setVisible( canBeEdited && item.getIndex() != 0 );
                item.add( moveLink );
                // more
                AjaxLink moreLink = new AjaxLink( "more" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        selectedClassification = classification;
                        addClassificationIndex();
                        target.add( indexContainer );
                    }
                };
                moreLink.setVisible( isReferenced( classification ) );
                item.add( moreLink );
                // delete
                AjaxLink deleteLink = new AjaxLink( "delete" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        delete( classification );
                        addClassificationsList();
                        target.add( classificationsContainer );
                        if ( selectedClassification != null && selectedClassification.equals( classification ) ) {
                            selectedClassification = null;
                        }
                        addClassificationIndex();
                        target.add( indexContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "classifications" ) );
                    }
                };
                deleteLink.setVisible(
                        canBeEdited && !isReferenced( classification ) );
                item.add( deleteLink );
                // css
                int count = getClassifications().size();
                item.add( new AttributeModifier(
                        "class",
                        new Model<String>( itemCssClasses( item.getIndex(), count ) ) ) );
            }
        };
        classificationsListView.setOutputMarkupId( true );
        classificationsContainer.addOrReplace( classificationsListView );
    }

    private String itemCssClasses( int index, int count ) {
        String classes = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) classes += " last";
        return classes;
    }

    public boolean isReferenced( Classification classification ) {
        return getQueryService().isReferenced( classification );
    }

    public void moveToTop( Classification classification ) {
        int index = getPlan().getClassifications().indexOf( classification );
        int level = getPlan().topLevelFor( classification.getSystem() );
        if ( index >= 0 ) {
            doCommand( UpdateObject.makeCommand( getUser().getUsername(), getPlan(),
                    "classifications[" + index + "].level",
                    level - 1,
                    UpdateObject.Action.Set ) );
        }
    }

    public void delete( Classification classification ) {
        if ( canBeEdited )
            doCommand( new UpdatePlanObject( getUser().getUsername(), getPlan(),
                    "classifications",
                    classification,
                    UpdateObject.Action.Remove ) );
    }


    private void addNewClassification() {
        WebMarkupContainer newClassificationContainer = new WebMarkupContainer( "new-classification-container" );
        String cssClasses = "last "
                + ( ( getPlan().classificationsFor( classificationSystem ).size() ) % 2 == 0 ? "even" : "odd" );
        newClassificationContainer.add( new AttributeModifier(
                "class",
                new Model<String>( cssClasses ) ) );
        classificationsContainer.add( newClassificationContainer );
        // input field
        newClassificationField = new TextField<String>(
                "new-classification",
                new PropertyModel<String>( this, "newClassificationName" ) );
        newClassificationField.setOutputMarkupId( true );
        newClassificationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Classification classification = addClassification();
                if ( classification != null ) {
                    newClassificationName = null;
                    target.add( newClassificationField );
                    selectedClassification = classification;
                    addClassificationsList();
                    target.add( classificationsContainer );
                    addClassificationIndex();
                    target.add( indexContainer );
                }
            }
        } );
        makeVisible( newClassificationField, canBeEdited );
        addInputHint( newClassificationField,"Name of a new classification level (press enter)" );
        newClassificationContainer.add( newClassificationField );
    }

    private boolean isNewClassificationNamed() {
        return newClassificationName != null
                && !newClassificationName.isEmpty()
                && !CollectionUtils.exists( getPlan().classificationsFor( classificationSystem ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (Classification) object ).getName(),
                                newClassificationName );
                    }
                } );
    }

    private Classification addClassification() {
        Classification classification = null;
        if ( canBeEdited && isNewClassificationNamed() ) {
            classification = new Classification();
            classification.setName( newClassificationName );
            classification.setSystem( classificationSystem );
            classification.setLevel( getPlan().classificationsFor( classificationSystem ).size() );
            doCommand( new UpdatePlanObject( getUser().getUsername(), getPlan(),
                    "classifications",
                    classification,
                    UpdateObject.Action.Add ) );
        }
        return classification;
    }

    public String getNewClassificationName() {
        return newClassificationName;
    }

    public void setNewClassificationName( String newClassificationName ) {
        this.newClassificationName = StringUtils.capitalize( newClassificationName );
    }

    private void addClassificationIndex() {
        indexContainer = new WebMarkupContainer( "index-container" );
        indexContainer.setOutputMarkupId( true );
        makeVisible( indexContainer, selectedClassification != null );
        addOrReplace( indexContainer );
        if ( selectedClassification == null ) {
            indexContainer.add( new Label(
                    "classification", "" ) );
            Label label = new Label( "index", "" );
            label.setVisible( false );
            indexContainer.add( label );
        } else {
            indexContainer.add( new Label(
                    "classification", selectedClassification.getLabel() ) );
            ClassificationIndexPanel classificationIndexPanel = new ClassificationIndexPanel(
                    "index",
                    new Model<Classification>( selectedClassification )
            );
            indexContainer.add( classificationIndexPanel );
        }
    }

    public List<Classification> getClassifications() {
        List<Classification> classifications = new ArrayList<Classification>();
        classifications.addAll( getPlan().classificationsFor( classificationSystem ) );
        return classifications;
    }

    /**
     * Classification references index panel.
     */
    private class ClassificationIndexPanel extends AbstractIndexPanel {

        private ClassificationIndexPanel(
                String id,
                IModel<Classification> classificationModel ) {
            super( id, classificationModel, null );
        }

        public Classification getClassification() {
            return (Classification) getModel().getObject();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected List<Actor> findIndexedActors() {
            return (List<Actor>) CollectionUtils.select(
                    getQueryService().listActualEntities( Actor.class, isMustBeReferenced() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object obj ) {
                            return ( (Actor) obj ).getClearances().contains( getClassification() );
                        }
                    }
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        protected List<Flow> findIndexedFlows() {
            return (List<Flow>) CollectionUtils.select(
                    getQueryService().findAllFlows(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object obj ) {
                            return ( (Flow) obj ).getClassifications().contains( getClassification() );
                        }
                    }
            );
        }

    }


}
