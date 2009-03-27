package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Actor;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Set;

/**
 * Entity flows panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 2:22:21 PM
 */
public class EntityFlowsPanel extends AbstractCommandablePanel {
    /**
     * Container.
     */
    private WebMarkupContainer includeContainer;
    /**
     * Whether to include the flows of all members.
     */
    private boolean membersIncluded = false;
    /**
     * Plays table.
     */
    private PlaysTablePanel playsTable;

    public EntityFlowsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init( expansions );
    }

    private void init( final Set<Long> expansions ) {
        includeContainer = new WebMarkupContainer("include");
        includeContainer.setVisible( !(getEntity() instanceof Actor) );
        add(includeContainer);
        CheckBox specificCheckBox = new CheckBox(
                "members-included",
                new PropertyModel<Boolean>( this, "membersIncluded" ) );
        specificCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addPlaysTable( expansions );
                target.addComponent( playsTable );
            }
        } );
        includeContainer.add( specificCheckBox );
        Label entityLabel = new Label( "entity", new PropertyModel( this, "entity.label" ) );
        includeContainer.add( entityLabel );
        addPlaysTable( expansions );
    }

    private void addPlaysTable( Set<Long> expansions ) {
        playsTable = new PlaysTablePanel(
                "flows",
                new PropertyModel<ResourceSpec>( this, "resourceSpec" ),
                new PropertyModel<Boolean>( this, "membersExcluded" ),
                20,
                expansions );
        playsTable.setOutputMarkupId( true );
        addOrReplace( playsTable );
    }

    public boolean isMembersExcluded() {
        return !membersIncluded;
    }

    public boolean isMembersIncluded() {
        return membersIncluded;
    }


    public void setMembersIncluded( boolean val ) {
        membersIncluded = val;
    }

    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    public ResourceSpec getResourceSpec() {
        return ResourceSpec.with( (ModelObject) getModel().getObject() );
    }
}
