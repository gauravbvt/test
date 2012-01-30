package com.mindalliance.channels.playbook.pages.forms.tabs.eventType;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 3:58:40 PM
 */
public class EventTypeTopicsTab extends AbstractFormTab {

    protected AutoCompleteTextField<String> newTopicField;
    protected AjaxButton addTopicButton;
    protected WebMarkupContainer topicsDiv;
    protected RefreshingView topicsView;
    protected WebMarkupContainer inheritedTopicsDiv;
    protected RefreshingView inheritedTopicsView;
    private String newTopic;
    private static final long serialVersionUID = 4649704922703780740L;

    public EventTypeTopicsTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        newTopicField = new AutoCompleteTextField<String>(
                "newTopic", new Model<String>() ) {
            private static final long serialVersionUID = -8038646791932197166L;

            @Override
            protected Iterator<String> getChoices( String input ) {
                return matchingTopics( input );
            }
        };
        newTopicField.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            -5907861597666554533L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        String topic =
                                newTopicField.getDefaultModelObjectAsString();
                        topicToAddInputed( topic, target );
                    }
                } );
        addReplaceable( newTopicField );
        inheritedTopicsDiv = new WebMarkupContainer( "inheritedTopicsDiv" );
        inheritedTopicsView = new RefreshingView<String>(
                "inheritedTopics",
                new Model<Serializable>( (Serializable) getInheritedTopics() ) ) {
            private static final long serialVersionUID = 2301659439892366301L;

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return new ModelIteratorAdapter<String>( getInheritedTopics().iterator() ) {
                    @Override
                    protected IModel<String> model( String topic ) {
                        return new Model<String>( topic );
                    }
                };
            }

            @Override
            protected void populateItem( Item item ) {
                String inheritedTopic = item.getDefaultModelObjectAsString();
                Label inheritedTopicLabel =
                        new Label( "inheritedTopic", inheritedTopic );
                item.add( inheritedTopicLabel );
                final Ref narrowedEventType = (Ref) Query.execute(
                        getElement(),
                        "findNarrowedEventTypeWithTopic",
                        inheritedTopic );
                AjaxLink<?> narrowedEventTypeLink =
                        new AjaxLink( "narrowedEventTypeLink" ) {
                            private static final long serialVersionUID =
                                    -1798626668065659805L;

                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                edit( narrowedEventType, target );
                            }
                        };
                narrowedEventTypeLink.add(
                    new Label(
                        "narrowedEventTypeName",
                        new RefPropertyModel( narrowedEventType, "name" ) ) );
                item.add( narrowedEventTypeLink );
            }
        };
        inheritedTopicsDiv.add( inheritedTopicsView );
        addReplaceable( inheritedTopicsDiv );
        addTopicButton = new AjaxButton( "addTopic" ) {
            private static final long serialVersionUID = 511708341485750125L;

            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form ) {
                RefUtils.add( getElement(), "topics", newTopic );
                newTopicField.setModelObject( "" );
                newTopic = null;
                addTopicButton.setEnabled( false );
                target.addComponent( addTopicButton );
                target.addComponent( newTopicField );
                target.addComponent( topicsDiv );
            }
        };
        addTopicButton.setEnabled( false );
        addReplaceable( addTopicButton );
        topicsDiv = new WebMarkupContainer( "topicsDiv" );
        topicsView = new RefreshingView<String>(
                "topics", new RefPropertyModel( getElement(), "topics" ) ) {
            private static final long serialVersionUID = 27694702056467897L;

            @Override
            protected Iterator<IModel<String>> getItemModels() {
                List<String> topics = getTopics();
                return new ModelIteratorAdapter<String>( topics.iterator() ) {
                    @Override
                    protected IModel<String> model( String object ) {
                        return new Model<String>( object );
                    }
                };
            }

            @Override
            protected void populateItem( final Item<String> item ) {
                Label topicLabel = new Label(
                        "topic", item.getDefaultModelObjectAsString() );
                item.add( topicLabel );
                AjaxLink<?> deleteTopicLink = new AjaxLink( "deleteTopic" ) {
                    private static final long serialVersionUID =
                            -6827985218640503558L;

                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        String topic = item.getDefaultModelObjectAsString();
                        RefUtils.remove( getElement(), "topics", topic );
                        target.addComponent( topicsDiv );
                    }
                };
                item.add( deleteTopicLink );
            }
        };
        topicsDiv.add( topicsView );
        addReplaceable( topicsDiv );
    }

    @SuppressWarnings( { "unchecked" } )
    private List<String> getTopics() {
        return (List<String>) getProperty( "topics" );
    }

    private void topicToAddInputed( String topic, AjaxRequestTarget target ) {
        newTopic = topic.trim();
        addTopicButton.setEnabled( isNewTopic( topic ) );
        target.addComponent( addTopicButton );
    }

    private boolean isNewTopic( String topic ) {
        return !( newTopic == null || newTopic.isEmpty()
                  || getInheritedTopics().contains( topic )
                  || getTopics().contains( topic ) );
    }

    @SuppressWarnings( { "unchecked" } )
    private List<String> getInheritedTopics() {
        return (List<String>) Query.execute(
                getTaxonomy(), "findInheritedTopics", getElement() );
    }

    private Iterator<String> matchingTopics( String input ) {
        List<String> matches = new ArrayList<String>();
        Collection<String> allTopics = new ArrayList<String>();
        allTopics.addAll( getTopics() );
        allTopics.addAll( getInheritedTopics() );
        for ( String topic : allTopics ) {
            if ( topic.toLowerCase().startsWith( input.toLowerCase() ) ) {
                matches.add( topic );
            }
        }
        return matches.iterator();
    }
}
