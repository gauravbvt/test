package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classifications editor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 3, 2009
 * Time: 1:25:37 PM
 */
public class ClassificationsEditor extends AbstractCommandablePanel {

    private String selectedSystem;
    private TextField<String> newSystemField;
    private DropDownChoice<String> systemChoice;
    private WebMarkupContainer classificationsContainer;

    public ClassificationsEditor( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        this.setOutputMarkupId( true );
        addNewSystem();
        addSystemChoice();
        addClassificationList();
    }

    private void addNewSystem() {
        newSystemField = new TextField<String>(
                "newSystem",
                new PropertyModel<String>( this, "systemName" ) );
        newSystemField.setOutputMarkupId( true );
        newSystemField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addSystemChoice();
                addClassificationList();
                target.addComponent( systemChoice );
                target.addComponent( newSystemField );
                target.addComponent( classificationsContainer );
            }
        } );
        add( newSystemField );
    }

    private void addSystemChoice() {
        systemChoice = new DropDownChoice<String>(
                "systems",
                new PropertyModel<String>( this, "selectedSystem" ),
                new PropertyModel<List<String>>( this, "systems" )
        );
        systemChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addClassificationList();
                target.addComponent( classificationsContainer );
            }
        } );
        systemChoice.setOutputMarkupId( true );
        addOrReplace( systemChoice );
    }

    private void addClassificationList() {
        classificationsContainer = new WebMarkupContainer( "classificationsContainer" );
        classificationsContainer.setOutputMarkupId( true );
        addOrReplace( classificationsContainer );
        ListView<ClassificationWrapper> classificationList = new ListView<ClassificationWrapper>(
                "classifications",
                new PropertyModel<List<ClassificationWrapper>>( this, "wrappers" ) ) {
            protected void populateItem( ListItem<ClassificationWrapper> item ) {
                addNameCell( item );
                addMoveToTopCell( item );
                addDeleteCell( item );
                addMoreCell( item );
            }
        };
        classificationsContainer.add( classificationList );
    }

    private void addNameCell( ListItem<ClassificationWrapper> item ) {
        ClassificationWrapper wrapper = item.getModelObject();
        WebMarkupContainer newClassificationLabel = new WebMarkupContainer( "new" );
        newClassificationLabel.setVisible( wrapper.isMarkedForCreation() );
        item.add( newClassificationLabel );
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( wrapper, "name" )
        );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addClassificationList();
                target.addComponent( classificationsContainer );
            }
        } );
        item.add( nameField );
    }

    private void addMoveToTopCell( ListItem<ClassificationWrapper> item ) {
        final ClassificationWrapper wrapper = item.getModelObject();
        AjaxFallbackLink moveLink = new AjaxFallbackLink( "move-to-top" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.moveToTop();
                addClassificationList();
                target.addComponent( classificationsContainer );
            }
        };
        moveLink.setVisible( !wrapper.isMarkedForCreation() && item.getIndex() != 0 );
        item.add( moveLink );
    }

    private void addDeleteCell( ListItem<ClassificationWrapper> item ) {
        final ClassificationWrapper wrapper = item.getModelObject();
        AjaxFallbackLink deleteLink = new AjaxFallbackLink( "delete" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.delete();
                addClassificationList();
                target.addComponent( classificationsContainer );
            }
        };
        deleteLink.setVisible( !wrapper.isMarkedForCreation() && !wrapper.isReferenced() );
        item.add( deleteLink );
    }

    private void addMoreCell( ListItem<ClassificationWrapper> item ) {
        final ClassificationWrapper wrapper = item.getModelObject();
        AjaxFallbackLink moreLink = new AjaxFallbackLink( "more" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Selected, wrapper.getClassification() ) );
            }
        };
        moreLink.setVisible( !wrapper.isMarkedForCreation() );
        item.add( moreLink );
    }


    public String getSystemName() {
        return "";
    }

    public void setSystemName( String system ) {
        selectedSystem = system;
    }

    public String getSelectedSystem() {
        return selectedSystem;
    }

    public List<ClassificationWrapper> getWrappers() {
        List<ClassificationWrapper> wrappers = new ArrayList<ClassificationWrapper>();
        if ( selectedSystem != null ) {
            for ( Classification classification : getPlan().classificationsFor( selectedSystem ) ) {
                wrappers.add( new ClassificationWrapper( classification ) );
            }
            wrappers.add( new ClassificationWrapper( selectedSystem ) );
        }
        return wrappers;
    }

    public List<String> getSystems() {
        List<String> systems = getPlan().classificationSystems();
        if ( selectedSystem != null && !systems.contains( selectedSystem ) ) {
            systems.add( selectedSystem );
        }
        Collections.sort( systems );
        return systems;
    }

    public class ClassificationWrapper implements Serializable {

        private Classification classification;
        private boolean markedForCreation;

        public ClassificationWrapper( String system ) {
            classification = new Classification();
            classification.setSystem( system );
            markedForCreation = true;
        }

        public ClassificationWrapper( Classification classification ) {
            this.classification = classification;
            markedForCreation = false;
        }

        public Classification getClassification() {
            return classification;
        }

        public void setClassification( Classification classification ) {
            this.classification = classification;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public String getName() {
            return classification.getName();
        }

        public void setName( String name ) {
            if ( name != null
                    && !name.trim().isEmpty()
                    && isUnique( name ) ) {
                classification.setName( name );
                if ( markedForCreation ) {
                    doCommand( new UpdatePlanObject(
                            getPlan(),
                            "classifications",
                            classification,
                            UpdateObject.Action.Add
                    ) );
                } else {
                    int index = getPlan().getClassifications().indexOf( classification );
                    if ( index >= 0 ) {
                        doCommand( UpdateObject.makeCommand(
                                getPlan(),
                                "classifications[" + index + "].name",
                                name,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        private boolean isUnique( final String name ) {
            return !CollectionUtils.exists(
                    getPlan().classificationsFor( getSystem() ),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ( (Classification) obj ).getName().equals( name );
                        }
                    }
            );
        }

        public String getSystem() {
            return classification.getSystem();
        }

        public void moveToTop() {
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

        public void delete() {
            doCommand( new UpdatePlanObject(
                    getPlan(),
                    "classifications",
                    classification,
                    UpdateObject.Action.Remove
            ) );
        }

        public boolean isReferenced() {
            return getQueryService().isReferenced( getClassification() );
        }
    }

}
