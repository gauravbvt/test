package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
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
    private WebMarkupContainer classificationsContainer;
    private WebMarkupContainer indexContainer;
    private TextField<String> newClassificationField;

    public ClassificationSystemPanel( String id, String classificationSystem ) {
        super( id );
        this.classificationSystem = classificationSystem;
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
            protected void populateItem( ListItem<Classification> item ) {
                final Classification classification = item.getModelObject();
                // name
                Label nameLabel = new Label( "classification-name", classification.getName() );
                item.add( nameLabel );
                // move to top
                AjaxFallbackLink moveLink = new AjaxFallbackLink( "move-to-top" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        moveToTop( classification );
                        addClassificationsList();
                        target.addComponent( classificationsContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "classifications" ) );
                    }
                };
                moveLink.setVisible( isLockedByUser( getPlan() ) && item.getIndex() != 0 );
                item.add( moveLink );
                // more
                AjaxFallbackLink moreLink = new AjaxFallbackLink( "more" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        selectedClassification = classification;
                        addClassificationIndex();
                        target.addComponent( indexContainer );
                    }
                };
                moreLink.setVisible( isReferenced( classification ) );
                item.add( moreLink );
                // delete
                AjaxFallbackLink deleteLink = new AjaxFallbackLink( "delete" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        delete( classification );
                        addClassificationsList();
                        target.addComponent( classificationsContainer );
                        if ( selectedClassification != null && selectedClassification.equals( classification ) ) {
                            selectedClassification = null;
                        }
                        addClassificationIndex();
                        target.addComponent( indexContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "classifications" ) );
                    }
                };
                deleteLink.setVisible(
                        isLockedByUser( getPlan() )
                                && !isReferenced( classification ) );
                item.add( deleteLink );
                // css
                int count = getClassifications().size();
                item.add( new AttributeModifier(
                        "class",
                        true,
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
            doCommand( UpdateObject.makeCommand(
                    getPlan(),
                    "classifications[" + index + "].level",
                    level - 1,
                    UpdateObject.Action.Set
            ) );
        }
    }

    public void delete( Classification classification ) {
        doCommand( new UpdatePlanObject(
                getPlan(),
                "classifications",
                classification,
                UpdateObject.Action.Remove
        ) );
    }


    private void addNewClassification() {
        WebMarkupContainer newClassificationContainer = new WebMarkupContainer( "new-classification-container" );
        String cssClasses = "last "
                + ( ( getPlan().classificationsFor( classificationSystem).size() ) % 2 == 0 ? "even" : "odd" );
        newClassificationContainer.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( cssClasses ) ) );
        classificationsContainer.add( newClassificationContainer );
        // input field
        newClassificationField = new TextField<String>(
                "new-classification",
                new PropertyModel<String>( this, "newClassificationName" ) );
        newClassificationField.setOutputMarkupId( true );
        newClassificationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                Classification classification = addClassification();
                if ( classification != null ) {
                    newClassificationName = null;
                    target.addComponent( newClassificationField );
                    selectedClassification = classification;
                    addClassificationsList();
                    target.addComponent( classificationsContainer );
                    addClassificationIndex();
                    target.addComponent( indexContainer );
                }
            }
        } );
        newClassificationContainer.add( newClassificationField );
    }

    private boolean isNewClassificationNamed() {
        return !( newClassificationName == null || newClassificationName.isEmpty() )
                && !CollectionUtils.exists(
                getPlan().classificationsFor( classificationSystem ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return Matcher.getInstance().same(
                                ( (Classification) object ).getName(),
                                newClassificationName );
                    }
                }
        );
    }

    private Classification addClassification() {
        Classification classification = null;
        if ( isNewClassificationNamed() ) {
            classification = new Classification();
            classification.setName( newClassificationName );
            classification.setSystem( classificationSystem );
            classification.setLevel( getPlan().classificationsFor( classificationSystem ).size() );
            doCommand( new UpdatePlanObject(
                    getPlan(),
                    "classifications",
                    classification,
                    UpdateObject.Action.Add
            ) );
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

    }


}
