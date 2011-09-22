package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.core.query.Assignments;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Assignment report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/15/11
 * Time: 9:30 PM
 */
public class AssignmentReportPanel extends AbstractUpdatablePanel {

    private FlowTable sends;

    private FlowTable receives;
    private final ReportHelper reportHelper;


    public AssignmentReportPanel( String id, ReportHelper reportHelper ) {
        super( id );
        this.reportHelper = reportHelper;
        init();
    }

    private void init() {
        reportHelper.setAsDefaultModel( this );
        Part part = reportHelper.getPart();
        add( new Label( "assignment.part.task" ),
                new WebMarkupContainer( "instructions" )
                        .add( new Label( "assignment.part.description" ) )
                        .setVisible( !part.getDescription().isEmpty() ),

                new Label( "assignment.employment.label" ),
                new Label( "jurisdiction", new PropertyModel<Place>( this, "jurisdiction" ) ),
                new Label( "timing", new PropertyModel<String>( this, "timing" ) ),
                new Label( "repeat", new PropertyModel<String>( this, "repeat" ) ),

                new ListView<Attachment>(
                        "documentation",
                        new PropertyModel<List<Attachment>>( this, "documentation" ) ) {
                    @Override
                    protected void populateItem( ListItem<Attachment> item ) {
                        Attachment attachment = item.getModelObject();
                        item.add(
                                new ExternalLink( "doc-link", attachment.getUrl() )
                                        .add( new Label( "doc-title", attachment.getLabel() ) )
                        );
                    }
                },
                new WebMarkupContainer( "no-docs" ).setVisible( getDocumentation().isEmpty() ),

                new ListView<Attachment>(
                        "policies",
                        new PropertyModel<List<Attachment>>( this, "policies" ) ) {
                    @Override
                    protected void populateItem( ListItem<Attachment> item ) {
                        Attachment attachment = item.getModelObject();
                        item.add(
                                new ExternalLink( "policy-link", attachment.getUrl() )
                                        .add( new Label( "policy-title", attachment.getLabel() ) )
                        );

                        item.add( reportHelper.newCssClass( attachment.getType().toString().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-policies" ).setVisible( getPolicies().isEmpty() ),

                new WebMarkupContainer( "sends" ).add(
                        new ListView<QuantifiedSpec>(
                                "sends.headers",
                                new PropertyModel<List<QuantifiedSpec>>( this, "sends.headers" ) ) {
                            @Override
                            protected void populateItem( ListItem<QuantifiedSpec> item ) {
                                item.add(
                                        new Label( "head", item.getModelObject().getHead() ),
                                        new Label( "tail", item.getModelObject().getTail() )
                                                .setRenderBodyOnly( true )
                                );
                            }
                        },
                        new ListView<FlowTable.Row>(
                                "sends.rows",
                                new PropertyModel<List<FlowTable.Row>>( this, "sends.rows" ) ) {
                            @Override
                            protected void populateItem( ListItem<FlowTable.Row> item ) {
                                FlowTable.Row row = item.getModelObject();
                                item.add(
                                        new Label( "flowName", row.getFlowName() ),
                                        new ListView<Flow>( "cells", row.getCells() ) {
                                            @Override
                                            protected void populateItem( ListItem<Flow> item ) {
                                                item.add( reportHelper.newFlowLink( item.getModelObject() )
                                                );
                                            }
                                        } );
                                item.add( reportHelper.newCssClass( item.getIndex() % 2 == 0 ? "even" : "odd" ) );

                            }
                        }
                ).setVisible( !getSends().isEmpty() ),
                new WebMarkupContainer( "receives" ).add(
                        new ListView<QuantifiedSpec>(
                                "receives.headers",
                                new PropertyModel<List<QuantifiedSpec>>( this, "receives.headers" ) ) {
                            @Override
                            protected void populateItem( ListItem<QuantifiedSpec> item ) {
                                item.add(
                                        new Label( "head", item.getModelObject().getHead() ),
                                        new Label( "tail", item.getModelObject().getTail() )
                                                .setRenderBodyOnly( true )
                                );
                            }
                        },
                        new ListView<FlowTable.Row>(
                                "receives.rows",
                                new PropertyModel<List<FlowTable.Row>>( this, "receives.rows" ) ) {
                            @Override
                            protected void populateItem( ListItem<FlowTable.Row> item ) {
                                FlowTable.Row row = item.getModelObject();
                                item.add(
                                        new Label( "flowName", row.getFlowName() ),
                                        new ListView<Flow>( "cells", row.getCells() ) {
                                            @Override
                                            protected void populateItem( ListItem<Flow> item ) {
                                                item.add( reportHelper.newFlowLink( item.getModelObject() )
                                                );
                                            }
                                        } );
                                item.add( reportHelper.newCssClass( item.getIndex() % 2 == 0 ? "even" : "odd" ) );
                            }
                        }
                ).setVisible( !getReceives().isEmpty() ),

                new ListView<Goal>(
                        "risks",
                        new PropertyModel<List<Goal>>( this, "risks" ) ) {
                    @Override
                    protected void populateItem( ListItem<Goal> item ) {
                        Goal goal = item.getModelObject();
                        item.add( new Label( "risk-description", goal.getFullTitle() ) );
                        item.add( reportHelper.newCssClass( goal.getLevel().getName().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-risk" ).setVisible( getRisks().isEmpty() ),

                new ListView<Goal>( "gains",
                        new PropertyModel<List<Goal>>( this, "gains" ) ) {
                    @Override
                    protected void populateItem( ListItem<Goal> item ) {
                        Goal goal = item.getModelObject();
                        item
                                .add( new Label( "gain-description", goal.getFullTitle() ) )
                                .add( reportHelper.newCssClass( goal.getLevel().getName().toLowerCase() ) );
                    }
                },
                new WebMarkupContainer( "no-gain" ).setVisible( getGains().isEmpty() )
        );

    }

    public String getTiming() {
        return reportHelper.getPart().getCompletionTime().toString();
    }

    public String getRepeat() {
        Part part = reportHelper.getPart();
        return part.isRepeating() ? "Yes, " + part.getRepeatsEvery()
                : "No";
    }

    public List<Attachment> getDocumentation() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : reportHelper.getAttachmentManager()
                .getMediaReferences( reportHelper.getPart() ) )
            if ( AttachmentImpl.Type.Reference.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Attachment> getPolicies() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : reportHelper.getAttachmentManager()
                .getMediaReferences( reportHelper.getPart() ) )
            if ( !AttachmentImpl.Type.Reference.equals( a.getType() )
                    && !AttachmentImpl.Type.Image.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Goal> getRisks() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : reportHelper.getPlanService()
                .findAllGoalsImpactedByFailure( reportHelper.getPart() ) )
            if ( !goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Goal> getGains() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : reportHelper.getPlanService()
                .findAllGoalsImpactedByFailure( reportHelper.getPart() ) )
            if ( goal.isGain() )
                result.add( goal );

        return result;
    }

    public String getJurisdiction() {
        Place place = reportHelper.getAssignment().getJurisdiction();
        return place == null ? "Any" : place.toString();
    }

    public FlowTable getSends() {
        if ( sends == null ) {
            Part part = reportHelper.getPart();
            sends = new FlowTable( part, removeProhibited( removeUnfocused( part.sends() ) ));
        }
        return sends;
    }

    public FlowTable getReceives() {
        if ( receives == null ) {
            Part part = reportHelper.getPart();
            receives = new FlowTable( part, removeProhibited( removeUnfocused( part.receives() ) ) );
        }
        return receives;
    }

    @SuppressWarnings( "unchecked" )
    private Iterator<Flow> removeUnfocused( Iterator<Flow> flows ) {
        return (Iterator<Flow>) IteratorUtils.filteredIterator(
                flows,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !reportHelper.getCommitments((Flow)object).isEmpty();
                    }
                });
    }

    private Iterator<Flow> removeProhibited( Iterator<Flow> flows ) {
        List<Flow> result = new ArrayList<Flow>(  );
        while( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( !flow.isProhibited() ) {
                result.add( flow );
            }
        }
        return result.iterator();
    }


    //=======================================
    public class FlowTable implements Serializable {

        /**
         * Rows indexed by flow name.
         */
        private final Map<String, Row> rows = new HashMap<String, Row>();

        private final List<QuantifiedSpec> headers;

        public FlowTable( Part part, Iterator<Flow> flows ) {
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                String name = flow.getName();

                Row row = rows.get( name );
                if ( row == null ) {
                    row = new Row( name );
                    rows.put( name, row );
                }

                Node other = part.equals( flow.getSource() ) ? flow.getTarget()
                        : flow.getSource();

                if ( other.isPart() )
                    row.put( (Part) other, flow );
                else
                    for ( ExternalFlow xFlow : ( (Connector) other ).getExternalFlows() )
                        row.put( xFlow.getPart(), xFlow );
            }

            Set<QuantifiedSpec> specs = new HashSet<QuantifiedSpec>();
            for ( Row row : rows.values() )
                specs.addAll( row.getSpecs() );

            headers = new ArrayList<QuantifiedSpec>( specs );
            Collections.sort( headers );
        }

        public List<QuantifiedSpec> getHeaders() {
            return Collections.unmodifiableList( headers );
        }

        public List<Row> getRows() {
            List<Row> result = new ArrayList<Row>( rows.size() );
            for ( Row row : rows.values() )
                if ( !row.isEmpty() )
                    result.add( row );

            Collections.sort( result );
            return result;
        }

        public boolean isEmpty() {
            return getRows().isEmpty();
        }

        //================
        public class Row implements Comparable<Row>, Serializable {

            private final String flowName;
            private final Map<QuantifiedSpec, Flow> cells = new HashMap<QuantifiedSpec, Flow>();

            public Row( String flowName ) {
                assert flowName != null;
                this.flowName = flowName;
            }

            private void put( Part part, Flow flow ) {
                Assignments all =
                        reportHelper.getPlanService().getAssignments()
                                .without( reportHelper.getActor() ).assignedTo( part );

                if ( !all.isEmpty() ) {
                    QuantifiedSpec header = new QuantifiedSpec( all.getCommonSpec( part ),
                            all.size() != 1 && flow.isAll(),
                            part.equals( flow.getSource() ) );

                    if ( cells.containsKey( header ) )
                        throw new IllegalArgumentException(
                                "Similar flow already indexed: "
                                        + flow.getName() + " for " + header );

                    cells.put( header, flow );
                }
            }

            public Flow get( QuantifiedSpec header ) {
                return cells.get( header );
            }

            public String getFlowName() {
                return flowName;
            }

            public Set<QuantifiedSpec> getSpecs() {
                return cells.keySet();
            }

            public List<Flow> getCells() {
                List<Flow> list = new ArrayList<Flow>();
                for ( QuantifiedSpec head : getHeaders() )
                    list.add( cells.get( head ) );

                return list;
            }

            public int compareTo( Row o ) {
                return flowName.compareTo( o.getFlowName() );
            }

            @Override
            public boolean equals( Object obj ) {
                return this == obj
                        || obj != null
                        && getClass() == obj.getClass()
                        && flowName.equals( ( (Row) obj ).getFlowName() );
            }

            @Override
            public int hashCode() {
                return flowName.hashCode();
            }

            public boolean isEmpty() {
                return cells.isEmpty();
            }
        }
    }

    //=======================================
    public static class QuantifiedSpec extends ResourceSpec {

        private boolean all;
        private boolean receiving;

        public QuantifiedSpec(
                Actor actor, Role role, Organization organization, Place jurisdiction, boolean all,
                boolean receiving ) {
            super( actor, role, organization, jurisdiction );
            this.all = all;
            this.receiving = receiving;
        }

        public QuantifiedSpec( Specable specable, boolean all, boolean receiving ) {
            super( specable );
            this.all = all;
            this.receiving = receiving;
        }

        public boolean isAll() {
            return all;
        }

        public boolean isReceiving() {
            return receiving;
        }

        @Override
        public boolean equals( Object obj ) {
            return super.equals( obj )
                    && all == ( (QuantifiedSpec) obj ).isAll();
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + ( all ? 1 : 0 );
        }

        public String getHead() {
            if ( all || !isActor() || getActor().isArchetype() || getActor().isUnknown() )
                return receiving
                        ? "Any"
                        : all
                        ? "All"
                        : "Any";
            else
                return getActor().getName();
        }

        public String getTail() {
            StringBuilder sb = new StringBuilder();

            if ( !isAnyRole() ) {
                sb.append( getRole().getName() );
            }

            if ( !isAnyOrganization() ) {
                sb.append( " at " );
                sb.append( getOrganization().getName() );
            }
            if ( !isAnyJurisdiction() ) {
                sb.append( " for " );
                sb.append( getJurisdiction().getName() );
            }
            return sb.toString();
        }

        @Override
        public String toString() {

            return getReportSource( all ? "all "
                                        : !isActor() || getActor().isArchetype() ? "a "
                                        : "" );
        }
    }

}
