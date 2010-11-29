// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Task details for an assignment.
 */
public class AssignmentReportPage extends WebPage {

    public static final String TASK_PARM = "task";

    private transient PlanService service;

    private transient Assignment assignment;

    private Plan plan;

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    private FlowTable sends;

    private FlowTable receives;

    //--------------------------------
    public AssignmentReportPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            @Override
            public void detach() {
                super.detach();
                service = null;
                assignment = null;
            }
        } );
        Part part = getAssignment().getPart();
        add(
            SOPsReportPage.newFeedbackWidget( planManager, getService().getPlan() ),
            new Label( "pageTitle" ),
            new Label( "reportTitle" ),
            new WebMarkupContainer( "switch-plan" )
                .add( new DropDownChoice<Plan>( "plan-sel",
                        new PropertyModel<Plan>( this, "plan" ),
                        new PropertyModel<List<? extends Plan>>( this, "plans" ) )
                    .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                            @Override
                            protected void onUpdate( AjaxRequestTarget target ) {
                                setRedirect( true );
                                setResponsePage( SOPsReportPage.class, getTopParameters() );
                            } } ) )
                .setVisible( getPlans().size() > 1 ),

            new BookmarkablePageLink<SOPsReportPage>( "top-link",
                                                      SOPsReportPage.class, getTopParameters() ),
            new Label( "event" ),
            new Label( "phase" ),
            new Label( "type" ),
            new Label( "assignment.part.task" ),

            new WebMarkupContainer( "instructions" )
                .add( new Label( "assignment.part.description" ) )
                .setVisible( !part.getDescription().isEmpty() ),

            new Label( "assignment.employment.job.role" ),
            new Label( "jurisdiction" ),
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

                    item.add( new AttributeModifier( "class", true,
                                    new Model<String>( attachment.getType().toString() ) ) );
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
                                    Flow flow = item.getModelObject();
                                    String delay = flow == null ? ""
                                                                : flow.getMaxDelay().toString();
                                    item.add(
                                        new BookmarkablePageLink<FlowReportPage>(
                                                "flow", FlowReportPage.class,
                                                getFlowParameters( flow ) )
                                            .add( new Label( "delay", delay ) )
                                            .setVisible( flow != null )
                                    );
                                }
                            }
                            );
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
                                    Flow flow = item.getModelObject();
                                    String delay = flow == null ? ""
                                                                : flow.getMaxDelay().toString();
                                    item.add(
                                        new BookmarkablePageLink<FlowReportPage>(
                                                "flow", FlowReportPage.class,
                                                getFlowParameters( flow ) )
                                            .add( new Label( "delay", delay ) )
                                            .setVisible( flow != null )
                                    );
                                }
                            }
                            );
                    }
                }
            ).setVisible( !getReceives().isEmpty() ),
            new ListView<Goal>( "risks" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item.add( new Label( "risk-description", goal.getFullTitle() ) );
                    item.add( new AttributeModifier( "class", true,
                                            new Model<String>( goal.getLevel().getName() ) ) );
                }
            },
            new WebMarkupContainer( "no-risk" ).setVisible( getRisks().isEmpty() ),

            new ListView<Goal>( "gains" ) {
                @Override
                protected void populateItem( ListItem<Goal> item ) {
                    Goal goal = item.getModelObject();
                    item.add( new Label( "gain-description", goal.getFullTitle() ) );
                    item.add( new AttributeModifier( "class", true,
                                            new Model<String>( goal.getLevel().getName() ) ) );
                }
            },
            new WebMarkupContainer( "no-gain" ).setVisible( getGains().isEmpty() )

        );
    }

    //--------------------------------
    private Assignment getAssignment() {
        if ( assignment == null )
            try {
                PageParameters parameters = getPageParameters();
                assignment = getAssignment( parameters.getLong( SelectorPanel.ACTOR_PARM, 0 ),
                                            parameters.getLong( TASK_PARM, 0 ) );
            } catch ( NotFoundException ignored ) {
                throw new AbortWithHttpStatusException( HttpServletResponse.SC_NOT_FOUND, false );
            }

        return assignment;
    }

    private Assignment getAssignment( long actorId, long taskId ) throws NotFoundException {
        Part task = getService().find( Part.class, taskId );
        Specable actor = getActor( actorId );

        Assignments assignments = getService().getAssignments()
                                    .assignedTo( task ).withSome( actor );
        if ( assignments.isEmpty() )
            throw new NotFoundException();

        else if ( assignments.size() != 1 )
            LoggerFactory.getLogger( AssignmentReportPage.class ).warn(
                        "More than one assignment for task {} and actor {}", taskId, actorId );

        return assignments.getAssignments().iterator().next();
    }

    private PlanService getService() {
        if ( service == null )
            try {
                PageParameters parameters = getPageParameters();
                service = getService( parameters.getString( SelectorPanel.PLAN_PARM, null ),
                                      parameters.getInt( SelectorPanel.VERSION_PARM, 0 ) );
                setPlan( service.getPlan() );
            } catch ( NotFoundException ignored ) {
                throw new AbortWithHttpStatusException( HttpServletResponse.SC_NOT_FOUND, false );
            }
        return service;
    }

    private PlanService getService( String uri, int version ) throws NotFoundException {
        boolean development;
        if ( uri == null )
            throw new NotFoundException();

        else if ( user.isPlanner( uri ) ) {
            int number = planManager.getDefinitionManager().get( uri )
                            .getDevelopmentVersion().getNumber();

            development = version == 0 || version == number;

        } else if ( user.isParticipant( uri ) )
            development = false;

        else
            throw new AbortWithHttpStatusException( HttpServletResponse.SC_FORBIDDEN, false );

        PlanDao planDao = planManager.getDao( uri, development );
        if ( planDao == null )
            throw new NotFoundException();

        return new PlanService( planManager, attachmentManager, planDao.getPlan() );
    }

    private Specable getActor( long actorId ) throws NotFoundException {
        try {
            return getService().find( Actor.class, actorId );
        } catch ( NotFoundException ignored ) {
            return getService().find( Role.class, actorId );
        }
    }

    public Specable getActor() {
        Actor actor = assignment.getActor();
        return actor.isUnknown() ? assignment.getRole() : actor;
    }

    //--------------------------------
    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        return planManager.getReadablePlans( user );
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    public String getReportTitle() {
        return "Channels - SOPs - " + getActor();
    }

    public String getPageTitle() {
        return getReportTitle();
    }

    public String getEvent() {
        return getAssignment().getPart().getSegment().getEvent().getName();
    }

    public String getPhase() {
        return getAssignment().getPart().getSegment().getPhase().getName();
    }

    public String getType() {
        Part part = getAssignment().getPart();
        return Assignments.isImmediate( part )    ? "Immediate Tasks"
             : Assignments.isOptional( part )     ? "Optional Task"
             : Assignments.isNotification( part ) ? "Information Notified"
             : Assignments.isRequest( part )      ? "Information Requested"
                                                  : "Other";
    }

    public PageParameters getTopParameters() {
        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, plan.getVersion() );
        return parms;
    }

    public PageParameters getFlowParameters( Flow flow ) {
        PageParameters parms = getTopParameters();
        parms.put( SelectorPanel.ACTOR_PARM,
                   Long.toString( ( (Identifiable) getActor() ) .getId() ) );
        if ( flow != null )
            parms.put( "flow", Long.toString( flow.getId() ) );
        return parms;
    }

    public String getRepeat() {
        Part part = getAssignment().getPart();
        return part.isRepeating() ? "Yes, " + part.getRepeatsEvery()
                                  : "No";
    }

    public List<Attachment> getDocumentation() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : attachmentManager.getMediaReferences( getAssignment().getPart() ) )
            if ( Attachment.Type.Reference.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Attachment> getPolicies() {
        List<Attachment> answer = new ArrayList<Attachment>();
        for ( Attachment a : attachmentManager.getMediaReferences( getAssignment().getPart() ) )
            if ( !Attachment.Type.Reference.equals( a.getType() )
                 && !Attachment.Type.Image.equals( a.getType() ) )
                answer.add( a );

        return answer;
    }

    public List<Goal> getRisks() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getAssignment().getPart() ) )
            if ( !goal.isGain() )
                result.add( goal );

        return result;
    }

    public List<Goal> getGains() {
        List<Goal> result = new ArrayList<Goal>();

        for ( Goal goal : getService().findAllGoalsImpactedByFailure( getAssignment().getPart() ) )
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
            Part part = getAssignment().getPart();
            sends = new FlowTable( part, part.sends() );
        }
        return sends;
    }

    public FlowTable getReceives() {
        if ( receives == null ) {
            Part part = getAssignment().getPart();
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
