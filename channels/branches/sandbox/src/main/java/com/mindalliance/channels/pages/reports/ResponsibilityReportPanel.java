package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Role report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 8:10:03 PM
 */
public class ResponsibilityReportPanel extends Panel {

    /**
     * The query service.
     */
    @SpringBean
    private QueryService queryService;

    private Specable spec;

    private Actor actor;

    public ResponsibilityReportPanel(
            String id,
            final Specable spec,
            Actor actor,
            final Segment segment,
            final boolean showingIssues ) {

        super( id );
        this.spec = spec;
        this.actor = actor;
        setRenderBodyOnly( true );
        final Role role = spec.getRole();
        final Organization organization = spec.getOrganization();
        final Place jurisdiction = spec.getJurisdiction();
        add( new Label( "name", getName() ) );
        add( new Label( "description", spec.getRole().getDescription() )
                .setVisible( !role.getDescription().isEmpty() ) );
        add( new Label( "seg-name", WordUtils.uncapitalize( segment.getPhaseEventTitle() ) ) );
        add( new WebMarkupContainer( "tags-container" )
                .add( new ListView<ModelEntity>( "tags", role.getTags() ) {
                    protected void populateItem( ListItem<ModelEntity> item ) {
                        ModelEntity tag = item.getModel().getObject();
                        item.add( new Label( "tag", tag.getName() ) );
                    }
                } )
                .setVisible( !role.getTags().isEmpty() ) );
        add( new DocumentsReportPanel( "documents", new Model<ModelObject>( role ) ) );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( role ) )
                .setVisible( showingIssues )
        );
        add( new ListView<Part>(
                "parts",
                segment.findParts( organization, role, jurisdiction, User.current().getPlan() ) ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel(
                        "part",
                        item.getModel(),
                        organization,
                        showingIssues ) );
            }
        }
        );
        List<Actor> actors = getActors( organization, role, jurisdiction );
        WebMarkupContainer contactsContainers = new WebMarkupContainer( "contacts-container" );
        contactsContainers.setVisible( organization.isActual() && !actors.isEmpty() );
        add( contactsContainers );
        contactsContainers.add( new WebMarkupContainer( "others" ).setVisible( actor != null ) );
        contactsContainers.add( new ListView<Actor>(
                "actors", actors
        ) {
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                Actor actor = item.getModelObject();
                item.add(
                    new ActorBannerPanel(
                        "actor",
                        segment,
                        new ResourceSpec(
                            actor.equals( Actor.UNKNOWN ) ? null : actor,
                            role.equals( Role.UNKNOWN ) ? null : role,
                            organization.equals( Organization.UNKNOWN ) ? null : organization,
                            null
                        ),
                        false,
                        "../../" ) );
            }
        } );
    }

    private String getName() {
        Place jurisdiction = spec.getJurisdiction();
        Organization organization = spec.getOrganization();
        StringBuilder sb = new StringBuilder();
        sb.append( spec.getRole().getName() );
        if ( jurisdiction != null ) {
            sb.append( " for " );
            sb.append( jurisdiction.getName() );
        }
        if ( !organization.isUnknown() ) {
            sb.append( " in " );
            sb.append( organization.getName() );
        }
        return sb.toString();
    }

    private List<Actor> getActors(
            Organization organization,
            Role role,
            Place jurisdiction ) {
        List<Actor> actors = findActualActors( organization, role, jurisdiction );
        if ( actor == null ) {
            if ( actors.isEmpty() )
                actors.add( Actor.UNKNOWN );
        } else {
            actors.remove( actor );
        }
        return actors;
    }

    private List<Actor> findActualActors( Organization organization, Role role, Place jurisdiction ) {
        Set<Actor> actors = new HashSet<Actor>();
        if ( organization.isActual() ) {
            ResourceSpec s = new ResourceSpec( null, role, organization, jurisdiction );
            for ( Employment employment : queryService.findAllEmploymentsIn( organization ) ) {
                ResourceSpec employmentSpec = new ResourceSpec( employment );
                if ( employmentSpec.narrowsOrEquals( s, User.current().getPlan() ) ) {
                    actors.add( employment.getActor() );
                }
            }
        }
        return new ArrayList<Actor>( actors );
    }
}
