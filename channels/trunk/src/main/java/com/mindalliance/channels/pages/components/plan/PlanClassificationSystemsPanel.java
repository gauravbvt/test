package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Plan classification systems panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 11, 2010
 * Time: 10:30:08 AM
 */
public class PlanClassificationSystemsPanel extends AbstractCommandablePanel {

    private WebMarkupContainer classificationSystemsContainer;
    private String newClassificationSystemName;
    private String selectedClassificationSystem;
    private Component classificationSystemPanel;
    /**
     * New system name field.
     */
    private TextField<String> classificationSystemField;

    public PlanClassificationSystemsPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        classificationSystemsContainer = new WebMarkupContainer( "classification-systems-container" );
        classificationSystemsContainer.setOutputMarkupId( true );
        addOrReplace( classificationSystemsContainer );
        addClassificationSystemsList();
        addNewClassificationSystem();
        addClassificationSystemPanel( null );
    }

    private void addClassificationSystemsList() {
        ListView<String> classificationSystemsListView = new ListView<String>(
                "classification-systems",
                new PropertyModel<List<String>>( this, "classificationSystems" )
        ) {
            protected void populateItem( ListItem<String> item ) {
                final String name = item.getModelObject();
                Label nameLabel = new Label( "name", name );
                item.add( nameLabel );
                AjaxFallbackLink details = new AjaxFallbackLink( "details" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        addClassificationSystemPanel( name );
                        target.add( classificationSystemPanel );
                        addClassificationSystemsList();
                        target.add( classificationSystemsContainer );
                    }
                };
                item.add( details );
                int count = getClassificationSystems().size();
                item.add( new AttributeModifier(
                        "class",
                        new Model<String>( itemCssClasses( item.getIndex(), count ) ) ) );
            }
        };
        classificationSystemsListView.setOutputMarkupId( true );
        classificationSystemsContainer.addOrReplace( classificationSystemsListView );
    }

    private String itemCssClasses( int index, int count ) {
        String classes = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) classes += " last";
        return classes;
    }

    public List<String> getClassificationSystems() {
        Set<String> names = new HashSet<String>();
        if ( selectedClassificationSystem != null ) {
            names.add( selectedClassificationSystem );
        }
        for ( Classification classification : getPlan().getClassifications() ) {
            names.add( classification.getSystem() );
        }
        List<String> systems = new ArrayList<String>( names );
        Collections.sort( systems );
        return systems;
    }

    private void addNewClassificationSystem() {
        WebMarkupContainer newSystemContainer = new WebMarkupContainer( "new-classification-system-container" );
        String cssClasses = "last " + ( ( getClassificationSystems().size() ) % 2 == 0 ? "even" : "odd" );
        newSystemContainer.add( new AttributeModifier(
                "class",
                new Model<String>( cssClasses ) ) );
        classificationSystemsContainer.add( newSystemContainer );
        classificationSystemField = new TextField<String>(
                "new-classification-system",
                new PropertyModel<String>( this, "newClassificationSystemName" ) );
        classificationSystemField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addClassificationSystemsList();
                target.add( classificationSystemsContainer );
                addClassificationSystemPanel( getNewClassificationSystemName() );
                target.add( classificationSystemPanel );
                newClassificationSystemName = null;
                target.add( classificationSystemField );
            }
        } );
        newSystemContainer.add( classificationSystemField );
        newSystemContainer.setVisible( isLockedByUser( Channels.ALL_CLASSIFICATIONS ) );
    }

    private void addClassificationSystemPanel( String classificationSystemName ) {
        selectedClassificationSystem = classificationSystemName;
        if ( classificationSystemName == null ) {
            classificationSystemPanel = new Label( "classification-system", "" );
            classificationSystemPanel.setOutputMarkupId( true );
        } else {
            classificationSystemPanel = new ClassificationSystemPanel(
                    "classification-system",
                    classificationSystemName );
        }
        makeVisible( classificationSystemPanel, classificationSystemName != null );
        addOrReplace( classificationSystemPanel );
    }


    public String getNewClassificationSystemName() {
        return newClassificationSystemName;
    }

    public void setNewClassificationSystemName( String newClassificationSystemName ) {
        this.newClassificationSystemName = StringUtils.capitalize( newClassificationSystemName );
    }

    /**
     * {@inheritDoc}
     */
    protected void update( AjaxRequestTarget target, Change change ) {
        if ( change.isUpdated() ) {
            addClassificationSystemsList();
            target.add( classificationSystemsContainer );
        }
        super.update( target, change );
    }

}
