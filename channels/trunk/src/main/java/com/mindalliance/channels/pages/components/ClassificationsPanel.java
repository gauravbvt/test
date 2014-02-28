package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classifications panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2009
 * Time: 2:22:44 PM
 */
public class ClassificationsPanel extends AbstractCommandablePanel {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ClassificationsPanel.class );

    /**
     * Classifiable model.
     */
    private IModel<Identifiable> classifiedModel;
    /**
     * Classifications container.
     */
    private WebMarkupContainer classificationsContainer;
    /**
     * Property of the classified model object to get the classifiable bean.
     */
    private String classifiableProperty;
    /**
     * WHether editing is enabled.
     */
    private boolean editEnabled;

    public ClassificationsPanel(
            String id,
            IModel<Identifiable> classifiedModel,
            String classifiableProperty,
            boolean enabled ) {
        super( id );
        this.classifiableProperty = classifiableProperty;
        this.classifiedModel = classifiedModel;
        editEnabled = enabled && isLockedByUser( getCollaborationModel() );
        init();
    }

    private void init() {
        classificationsContainer = new WebMarkupContainer( "classificationsContainer" );
        classificationsContainer.setOutputMarkupId( true );
        add( classificationsContainer );
        addClassifications();
    }

    private void addClassifications() {
        ListView<ClassificationWrapper> classificationsListView =
                new ListView<ClassificationWrapper>(
                        "classifications",
                        getWrappers() ) {
                    protected void populateItem( ListItem<ClassificationWrapper> item ) {
                        item.setOutputMarkupId( true );
                        addSystemChoice( item );
                        addNameChoice( item );
                        addDelete( item );
                    }
                };
        classificationsListView.setOutputMarkupId( true );
        classificationsContainer.addOrReplace( classificationsListView );
    }

    private void addSystemChoice( final ListItem<ClassificationWrapper> item ) {
        ClassificationWrapper wrapper = item.getModelObject();
        Label systemLabel = new Label(
                "system",
                new Model<String>( wrapper.isMarkedForCreation() ? "" : wrapper.getSystem() ) );
        makeVisible( systemLabel, !wrapper.isMarkedForCreation() );
        item.add( systemLabel );
        List<String> choices = getSystemChoices();
        DropDownChoice<String> systemsChoice = new DropDownChoice<String>(
                "systemChoice",
                new PropertyModel<String>( wrapper, "system" ),
                choices
        );
        systemsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameChoice( item );
                target.add( item );
            }
        } );
        makeVisible( systemsChoice, wrapper.isMarkedForCreation() && !choices.isEmpty() );
        systemsChoice.setEnabled( editEnabled );
        item.add( systemsChoice );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getSystemChoices() {
        final List<Classification> classifications = (List<Classification>) ChannelsUtils.getProperty(
                getClassified(),
                getClassifiableProperty(),
                new ArrayList<String>()
        );
        if ( classifications.isEmpty() ) {
            return getCollaborationModel().classificationSystems();
        } else {
            return (List<String>) CollectionUtils.select(
                    getCollaborationModel().classificationSystems(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            final String system = (String) obj;
                            return !CollectionUtils.exists(
                                    classifications,
                                    new Predicate() {
                                        public boolean evaluate( Object obj ) {
                                            return ( (Classification) obj ).getSystem().equals( system );
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getNameChoices( final String system ) {
        if ( system == null ) {
            return new ArrayList<String>();
        } else {
            return (List<String>) CollectionUtils.collect(
                    getCollaborationModel().classificationsFor( system ),
                    new Transformer() {
                        public Object transform( Object input ) {
                            return ( (Classification) input ).getName();
                        }
                    }
            );
        }
    }


    private void addNameChoice( ListItem<ClassificationWrapper> item ) {
        ClassificationWrapper wrapper = item.getModelObject();
        Label nameLabel = new Label(
                "name",
                new Model<String>( wrapper.isMarkedForCreation() ? "" : wrapper.getName() ) );
        makeVisible( nameLabel, !wrapper.isMarkedForCreation() );
        nameLabel.setOutputMarkupId( true );
        item.addOrReplace( nameLabel );
        List<String> choices = getNameChoices( wrapper.getSystem() );
        DropDownChoice<String> namesChoice = new DropDownChoice<String>(
                "nameChoice",
                new PropertyModel<String>( wrapper, "name" ),
                choices
        );
        namesChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addClassifications();
                target.add( classificationsContainer );
                update(
                        target,
                        new Change( Change.Type.Updated, getClassified(), classifiableProperty ) );
            }
        } );
        namesChoice.setOutputMarkupId( true );
        namesChoice.setEnabled( wrapper.getSystem() != null && editEnabled );
        makeVisible( namesChoice, wrapper.isMarkedForCreation() && !choices.isEmpty() );
        item.addOrReplace( namesChoice );
    }

    private void addDelete( ListItem<ClassificationWrapper> item ) {
        final ClassificationWrapper wrapper = item.getModelObject();
        AjaxLink deleteLink = new AjaxLink( "delete" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.delete();
                addClassifications();
                target.add( classificationsContainer );
                update(
                        target,
                        new Change( Change.Type.Updated, getClassified(), classifiableProperty ) );
            }
        };
        makeVisible( deleteLink, !wrapper.isMarkedForCreation() && editEnabled );
        item.add( deleteLink );
    }


    public List<ClassificationWrapper> getWrappers() {
        List<ClassificationWrapper> wrappers = new ArrayList<ClassificationWrapper>();
        for ( Classification classification : getClassifications() ) {
            wrappers.add( new ClassificationWrapper( classification ) );
        }
        if ( editEnabled ) wrappers.add( new ClassificationWrapper() );
        return wrappers;
    }

    public String getClassifiableProperty() {
        return classifiableProperty;
    }

    private Identifiable getClassified() {
        return classifiedModel.getObject();
    }

    @SuppressWarnings( "unchecked" )
    private List<Classification> getClassifications() {
        return (List<Classification>) ChannelsUtils.getProperty(
                getClassified(),
                classifiableProperty,
                null
        );
    }

    /**
     * Classification wrapper.
     */
    public class ClassificationWrapper implements Serializable {

        private String system;
        private String name;
        private boolean markedForCreation;

        public ClassificationWrapper( Classification classification ) {
            name = classification.getName();
            system = classification.getSystem();
            markedForCreation = false;
        }

        public ClassificationWrapper() {
            markedForCreation = true;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public String getSystem() {
            return system;
        }

        public String getName() {
            return name;
        }

        public void setName( String val ) {
            assert markedForCreation;
            name = val;
            Classification classification = getClassification();
            if ( classification != null )
                try {
                    doCommand( UpdateObject.makeCommand( getUser().getUsername(), getClassified(),
                            getClassifiableProperty(),
                            classification,
                            UpdateObject.Action.AddUnique ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to set name");
                }
        }

        public void setSystem( String val ) {
            system = val;
        }

        private Classification getClassification() {
            if ( system != null && name != null ) {
                return getCollaborationModel().getClassification( system, name );
            } else {
                return null;
            }
        }

        public void delete() {
            assert !markedForCreation;
            Classification oldClassification = getClassification();
            if ( oldClassification != null ) {
                try {
                    doCommand( UpdateObject.makeCommand( getUser().getUsername(), getClassified(),
                            getClassifiableProperty(),
                            oldClassification,
                            UpdateObject.Action.Remove ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to delete");
                }
            }
        }
    }

}
