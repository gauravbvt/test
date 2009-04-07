package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.util.Play;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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
     * Whether to include the flows of all members.
     */
    private boolean restricted = false;
    /**
     * Plays table.
     */
    private PlaysTablePanel playsTable;

    public EntityFlowsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init( );
    }

    private void init(  ) {
        CheckBox restrictCheckBox = new CheckBox(
                "restricted",
                new PropertyModel<Boolean>( this, "restricted" ) );
        restrictCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addPlaysTable(  );
                target.addComponent( playsTable );
            }
        } );
        add( restrictCheckBox );
        Label restrictedLabel = new Label(
                "restricted-string",
                new PropertyModel<String>( this, "restrictedString" ) );
        add( restrictedLabel );
        addPlaysTable(  );
    }

    private void addPlaysTable(  ) {
        /*       playsTable = new PlaysTablePanel(
                        "flows",
                        new PropertyModel<ResourceSpec>( this, "resourceSpec" ),
                        new PropertyModel<Boolean>( this, "excluded" ),
                        20,
                        expansions );
        */
        playsTable = new PlaysTablePanel(
                "flows",
                new PropertyModel<ModelObject>( this, "entity" ),
                new PropertyModel<List<Play>>( this, "plays" ),
                getExpansions(),
                20 );
        playsTable.setOutputMarkupId( true );
        addOrReplace( playsTable );
    }

    public List<Play> getPlays() {
        ModelObject entity = getEntity();
        if ( entity instanceof Actor ) return getActorPlays();
        else if ( entity instanceof Role ) return getRolePlays();
        else if ( entity instanceof Organization ) return getOrganizationPlays();
        else return getPlacePlays();
    }

    private List<Play> getActorPlays() {
        List<Play> plays = new ArrayList<Play>();
        plays.addAll( getDqo().findAllPlays( ResourceSpec.with( getEntity() ), false ) );
        if ( !isRestricted() ) {
            // Include all plays for responsibilities of actor
            List<ResourceSpec> responsibilities = getDqo()
                    .findAllResponsibilitiesOf( (Actor) getEntity() );
            for ( ResourceSpec resourceSpec : responsibilities ) {
                plays.addAll( getDqo().findAllPlays( resourceSpec, false ) );
            }
        }
        return plays;
    }

    private List<Play> getRolePlays() {
        List<Play> plays = new ArrayList<Play>();
        for ( Play play : getDqo().findAllPlays( ResourceSpec.with( getEntity() ), false ) ) {
            // Don't include play if list is restricted and play involve an actor
            if ( !( isRestricted() && play.getPart().getActor() != null ) ) {
                plays.add( play );
            }
        }
        return plays;
    }

    private List<Play> getOrganizationPlays() {
        List<Play> plays = new ArrayList<Play>();
        for ( Play play : getDqo().findAllPlays( ResourceSpec.with( getEntity() ), false ) ) {
            // Don't include play if list is restricted and play involve an actor or a role
            if ( !( isRestricted()
                    && ( play.getPart().getActor() != null || play.getPart().getRole() != null ) ) ) {
                plays.add( play );
            }
        }
        return plays;
    }

    private List<Play> getPlacePlays() {
        // TODO - restricted means necessarily within place, and not including what contains the place
        return getDqo().findAllPlays( ResourceSpec.with( getEntity() ), isRestricted() );
    }

/*
    public boolean isInclusive() {
        return !restricted;
    }
*/

    public boolean isRestricted() {
        return restricted;
    }


    public void setRestricted( boolean val ) {
        restricted = val;
    }

    public String getRestrictedString() {
        ModelObject entity = getEntity();
        if ( entity instanceof Actor )
            return "exclusive to " + entity.getName();
        else if ( entity instanceof Role )
            return "with " + entity.getName() + " as a group";
        else if ( entity instanceof Organization )
            return "to " + entity.getName() + " as an entity";
        else return "occurring specifically in " + entity.getName();
    }

    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    public ResourceSpec getResourceSpec() {
        return ResourceSpec.with( getEntity() );
    }
}
