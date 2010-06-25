package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 11:46:56 AM
 */
public class OrganizationReportPanel extends Panel {

    /**
     * An organization.
     */
    private Organization organization;

    /**
     * The segment in context.
     */
    private Segment segment;

    @SpringBean
    private QueryService queryService;

    /**
     * The actor to single out or null to show all actors.
     */
    private Actor actor;

    public OrganizationReportPanel(
            String id,
            Organization organization,
            boolean manyOrganizations,
            Segment segment,
            final Actor actor,
            final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        this.actor = actor;
        this.organization = organization;
        this.segment = segment;
        OrganizationHeaderPanel orgDetails = new OrganizationHeaderPanel(
                "org-details",
                organization,
                showingIssues);
        add( orgDetails );
        orgDetails.setVisible( manyOrganizations );
        List<ResourceSpec> responsibilities = getResponsibilities();
        add( new Label(
                "no-procedure",
                "No information sharing procedures" ).setVisible( responsibilities.isEmpty() ) );
        add( new ListView<ResourceSpec>(
                "sections",
                getResponsibilities() ) {
            @Override
            protected void populateItem( ListItem<ResourceSpec> item ) {
                item.add( new ResponsibilityReportPanel(
                        "section",
                        item.getModelObject(),
                        actor,
                        OrganizationReportPanel.this.segment,
                        showingIssues ).setRenderBodyOnly( true ) );
            }
        } );
    }

    private List<ResourceSpec> getResponsibilities() {
        if ( actor != null ) {
            return findActorResponsibilities();
        } else {
            return findAllResponsibilities();
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<ResourceSpec> findActorResponsibilities() {
        List<Assignment> assignments =
                (List<Assignment>) CollectionUtils.select(
                        queryService.findAllAssignments( actor, segment ),
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                Assignment assignment = (Assignment) object;
                                return organization.equals( assignment.getOrganization() );
                            }
                        } );
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();
        for ( Assignment assignment : assignments ) {
            specs.add( new ResourceSpec( assignment ) );
        }
        return new ArrayList<ResourceSpec>(specs);
    }

    private List<ResourceSpec> findAllResponsibilities() {
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();
        for ( Part p : queryService.findAllParts(
                segment,
                ResourceSpec.with( organization ),
                false ) ) {
            ResourceSpec spec = p.resourceSpec();
            Organization specOrg = spec.getOrganization();
            // Tweak the matching
            boolean matches = specOrg == null && organization.isUnknown()
                    || specOrg != null && !organization.isWithin( specOrg );
            if ( matches ) {
                spec.setOrganization( organization );
                if ( spec.getRole() == null ) {
                    spec.setRole( Role.UNKNOWN );
                }
                specs.add( spec );
            }
        }
        return new ArrayList<ResourceSpec>( specs );
    }

}
