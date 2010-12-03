// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

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
 * Task details for an assignment.
 */
public class AssignmentReportPage extends AbstractReportPage {

    private FlowTable sends;

    private FlowTable receives;

    //--------------------------------
    public AssignmentReportPage( PageParameters parameters ) {
        super( parameters );

        Part part = getPart();
        add(
            SOPsReportPage.newFeedbackWidget( getPlanManager(), getService().getPlan() ),
            new Label( "pageTitle" ),
            new Label( "reportTitle" ),

            new BookmarkablePageLink<SOPsReportPage>( "top-link",
                                                      SOPsReportPage.class, getTopParameters() ),
            new Label( "assignment.part.segment.event.name" ),
            new Label( "assignment.part.segment.phase.name" ),
            new Label( "type" ),
            new Label( "assignment.part.task" ),

            new WebMarkupContainer( "instructions" )
                .add( new Label( "assignment.part.description" ) )
                .setVisible( !part.getDescription().isEmpty() ),

            new Label( "assignment.employment.job.role" ),
            new Label( "jurisdiction" ),
            new Label( "timing" ),
            new Label( "repeat" ),

            new ListView<Attachment>( "documentation" ) {
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

            new ListView<Attachment>( "policies" ) {
                @Override
                protected void populateItem( ListItem<Attachment> item ) {
                    Attachment attachment = item.getModelObject();
                    item.add(
                        new ExternalLink( "policy-link", attachment.getUrl() )
                            .add( new Label( "policy-title", attachment.getLabel() ) )
                    );

                    item.add( newCssClass( attachment.getType().toString().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-policies" ).setVisible( getPolicies().isEmpty() ),

            new WebMarkupContainer( "sends" ).add(
                new ListView<String>( "sends.headerStrings" ) {
                    @Override
                    protected void populateItem( ListItem<String> item ) {
                        item.add( new Label( "header", item.getModelObject() ) );
                    } },
                new ListView<FlowTable.Row>( "sends.rows" ) {
                    @Override
                    protected void populateItem( ListItem<FlowTable.Row> item ) {
                        FlowTable.Row row = item.getModelObject();
                        item.add(
                            new Label( "flowName", row.getFlowName() ),
                            new ListView<Flow>( "cells", row.getCells() ) {
                                @Override
                                protected void populateItem( ListItem<Flow> item ) {
                                    item.add( newFlowLink( item.getModelObject() )
                                    );
                                }
                            } );
                        item.add( newCssClass( item.getIndex() % 2 == 0 ? "even" : "odd" ) );

                    }
                }
            ).setVisible( !getSends().isEmpty() ),


            new WebMarkupContainer( "receives" ).add(
                new ListView<String>( "receives.headerStrings" ) {
                    @Override
                    protected void populateItem( ListItem<String> item ) {
                        item.add( new Label( "header", item.getModelObject() ) );
                    } },
                new ListView<FlowTable.Row>( "receives.rows" ) {
                    @Override
                    protected void populateItem( ListItem<FlowTable.Row> item ) {
                        FlowTable.Row row = item.getModelObject();
                        item.add(
                            new Label( "flowName", row.getFlowName() ),
                            new ListView<Flow>( "cells", row.getCells() ) {
                                @Override
                                protected void populateItem( ListItem<Flow> item ) {
                                    item.add( newFlowLink( item.getModelObject() )
                                    );
                                }
                            } );
                        item.add( newCssClass( item.getIndex() % 2 == 0 ? "even" : "odd" ) );
                    }
                }
            ).setVisible( !getReceives().isEmpty() ),

            new ListView<Goal>( "risks" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item.add( new Label( "risk-description", goal.getFullTitle() ) );
                    item.add( newCssClass( goal.getLevel().getName().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-risk" ).setVisible( getRisks().isEmpty() ),

            new ListView<Goal>( "gains" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item
                        .add( new Label( "gain-description", goal.getFullTitle() ) )
                        .add( newCssClass( goal.getLevel().getName().toLowerCase() ) );
                }
            },
            new WebMarkupContainer( "no-gain" ).setVisible( getGains().isEmpty() )

        );
    }

    private Component newFlowLink( Flow flow ) {
        PageParameters parms = getTopParameters();
        parms.put( SelectorPanel.ACTOR_PARM,
                   Long.toString( ( (Identifiable) getActor() ) .getId() ) );
        parms.put( TASK_PARM, Long.toString( getPart().getId() ) );

        String delay;
        if ( flow == null )
            delay = "";
        else {
            parms.put( FLOW_PARM, Long.toString( flow.getId() ) );
            delay = flow.getMaxDelay().toString();
        }

        Component result =
                new BookmarkablePageLink<FlowReportPage>( "flow", FlowReportPage.class, parms )
                        .add( new Label( "delay", delay ) );

        if ( flow != null )
            result.add( newCssClass( getService().computeSharingPriority( flow )
                                            .toString().toLowerCase() ));

        return result.setVisible( flow != null );
    }

    public String getTiming() {
        return getPart().getCompletionTime().toString();
    }

    public String getRepeat() {
        Part part = getPart();
        return part.isRepeating() ? "Yes, " + part.getRepeatsEvery()
                                  : "No";
    }

    public List<Attachment> getDocumentation() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : getAttachmentManager().getMediaReferences( getPart() ) )
            if ( Attachment.Type.Reference.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Attachment> getPolicies() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : getAttachmentManager().getMediaReferences( getPart() ) )
            if ( !Attachment.Type.Reference.equals( a.getType() )
                 && !Attachment.Type.Image.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Goal> getRisks() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getPart() ) )
            if ( !goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Goal> getGains() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getPart() ) )
            if ( goal.isGain() )
                result.add( goal );

        return result;
    }

    public String getJurisdiction() {
        Place place = getAssignment().getJurisdiction();
        return place == null ? "Any" : place.toString();
    }

    public FlowTable getSends() {
        if ( sends == null ) {
            Part part = getPart();
            sends = new FlowTable( part, part.sends() );
        }
        return sends;
    }

    public FlowTable getReceives() {
        if ( receives == null ) {
            Part part = getPart();
            receives = new FlowTable( part, part.receives() );
        }
        return receives;
    }

    //=======================================
    public class FlowTable implements Serializable {

        /** Rows indexed by flow name. */
        private final Map<String,Row> rows = new HashMap<String, Row>();

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

        public List<String> getHeaderStrings() {
            List<String> result = new ArrayList<String>( headers.size() );
            for ( QuantifiedSpec header : headers )
                result.add( header.toString() );

            return result;
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
            private final Map<QuantifiedSpec,Flow> cells = new HashMap<QuantifiedSpec, Flow>();

            public Row( String flowName ) {
                assert flowName != null;
                this.flowName = flowName;
            }

            private void put( Part part, Flow flow ) {
                Assignments all =
                        getService().getAssignments().without( getActor() ).assignedTo( part );

                if ( !all.isEmpty() ) {
                    QuantifiedSpec header = new QuantifiedSpec( all.getCommonSpec( part ),
                                                                all.size() != 1 && flow.isAll() );

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

        public QuantifiedSpec(
                Actor actor, Role role, Organization organization, Place jurisdiction,
                boolean all ) {
            super( actor, role, organization, jurisdiction );
            this.all = all;
        }

        public QuantifiedSpec( Specable specable, boolean all ) {
            super( specable );
            this.all = all;
        }

        public boolean isAll() {
            return all;
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

        @Override
        public String toString() {
            String prefix = all                                    ? "all "
                          : !isActor() || getActor().isArchetype() ? "a "
                                                                   : "";

            return prefix + super.getReportSource();
        }
    }
}
