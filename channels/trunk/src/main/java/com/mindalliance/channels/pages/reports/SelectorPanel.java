package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.Collections;
import java.util.List;

/**
 * The report fine-tuning gizmo.
 */
public class SelectorPanel extends Panel implements IHeaderContributor {

    private static final String SEGMENT_PARM = "segment";
    private static final String ACTOR_PARM = "agent";
    private static final String ORGANIZATION_PARM = "org";
    private static final String ISSUES_PARM = "issues";
    private static final String DIAGRAMS_PARM = "diagrams";
    private static final String ALL = "all";


    /**
     * Default value for "All segments" selection.
     */
    private static final Segment ALL_SEGMENTS = new Segment();
    private static final Actor   ALL_ACTORS = new Actor();
    private static final Organization ALL_ORGS = new Organization();

    /**
     * Localized sorter-upper.
     */
    private static final Collator SORTER = Collator.getInstance();

    /**
     * The selected segment (or All_Segments)
     */
    private Segment segment = ALL_SEGMENTS;

    /**
     * The selected actor (or ALL_ACTORS)
     */
    private Actor actor = ALL_ACTORS;

    /**
     * The selected organization (or ALL_ORGS)
     */
    private Organization organization = ALL_ORGS;

    /**
     * The selected plan.
     */
    private Plan plan;

    /**
     * True when parameter combination would produce a not-empty report.
     */
    private boolean valid = true;

    /**
     * True when issues are shown.
     */
    private boolean showingIssues;

    /**
     * True when issues are shown.
     */
    private boolean showingDiagrams;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private User user;

    private transient QueryService queryService;

    private transient Assignments assignments;

    public SelectorPanel( String id, PageParameters parameters ) {
        super( id );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            /**
             * @see org.apache.wicket.model.IDetachable#detach()
             */
            @Override
            public void detach() {
                super.detach();
                assignments = null;
                queryService = null;
            }
        } );

        setPlan( user.getPlan() );
        setParameters( getQueryService(), parameters );
        Form<?> form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                redirect();
                super.onSubmit();
            }
        };

        // addPlanSwitcher( form );
        form.add(

            new DropDownChoice<Segment>( "segment",
                getSegmentChoices(),
                new IChoiceRenderer<Segment>() {
                    public Object getDisplayValue( Segment object ) {
                        return ALL_SEGMENTS.equals( object ) ? "All" : object.getName();
                    }

                    public String getIdValue(
                            Segment object, int index ) {
                        return ALL_SEGMENTS.equals( object ) ? ALL : Long.toString( object.getId() );
                    }
                } ).add( newOnChange( "onchange" ) ),

            new DropDownChoice<Organization>( "organization",
                getOrganizationChoices(),
                new IChoiceRenderer<Organization>() {
                    public Object getDisplayValue( Organization object ) {
                        return object.equals( ALL_ORGS ) ? "All" : object.getName();
                    }

                    public String getIdValue( Organization object, int index ) {
                        return object.equals( ALL_ORGS ) ?
                                    ALL : Long.toString( object.getId() );
                    }
                } ).add( newOnChange( "onchange" ) ),

             new DropDownChoice<Specable>( "actor",
                 getActorChoices(),
                 new IChoiceRenderer<Specable>() {
                     public Object getDisplayValue( Specable object ) {
                         return object.equals( ALL_ACTORS ) ? "All"
                                                            : Assignments.stringify( object );
                     }

                     public String getIdValue( Specable object, int index ) {
                         return object.equals( ALL_ACTORS ) ?
                                    ALL : Long.toString( ( (ModelObject) object ).getId() );
                     }
                 } ).add( newOnChange( "onchange" ) ),

            new WebMarkupContainer( "optionals" )
                .add( new CheckBox( "showingIssues" ).add( newOnChange( "onclick" ) ) )
                .add( new CheckBox( "showingDiagrams" ).add( newOnChange( "onclick" ) ) )
                .setVisible( isUserPlanner() )
        );

        add( form );
    }

/*
    private void addPlanSwitcher( Form form ) {
        WebMarkupContainer switchPlanContainer = new WebMarkupContainer( "switch-plan" );
        switchPlanContainer.setVisible( getPlannablePlans().size() > 1 );
        form.add( switchPlanContainer );
        DropDownChoice<Plan> planDropDownChoice = new DropDownChoice<Plan>( "plan-sel",
                new PropertyModel<Plan>(
                        this,
                        "plan" ),
                new PropertyModel<List<? extends Plan>>(
                        this,
                        "plannablePlans" ) );
        planDropDownChoice.add( newOnChange( "onchange" ) );
        switchPlanContainer.add( planDropDownChoice );
    }
*/

    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<Plan> getPlannablePlans() {
        return planManager.getPlannablePlans( User.current() );
    }

    /**
     * Return current plan.
     *
     * @return a plan
     */
    public final synchronized Plan getPlan() {
        return plan;
    }

    /**
     * Switch the current plan.
     *
     * @param plan a plan
     */
    public final synchronized void setPlan( Plan plan ) {
        this.plan = plan;
        setQueryService( null );
    }

    public final synchronized void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public final synchronized QueryService getQueryService() {
        if ( queryService == null )
            queryService = new PlanService( planManager, attachmentManager, plan );

        return queryService;
    }

    private Assignments getAssignments() {
        if ( assignments == null )
            assignments = Assignments.getAssignments( getQueryService() );
        return assignments;
    }

    private IBehavior newOnChange( String event ) {
        return new AjaxFormComponentUpdatingBehavior( event ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                redirect();
            }
        };
    }

    private void redirect() {
        setRedirect( true );
        setResponsePage( getPage().getClass(), getParameters() );
    }


    private List<Segment> getSegmentChoices() {
        List<Segment> result =
                isOrgSelected() && isActorSelected() ? getAllSegments( organization, actor )
              : isOrgSelected()                      ? getAllSegments( organization )
              : isActorSelected()                    ? getAllSegments( actor )
                                                     : getAllSegments();

        result.add( 0, ALL_SEGMENTS );
        return result;
    }

    /**
     * Return all the segment selected by this component.
     *
     * @return a sorted list of segment
     */
    public List<Segment> getSegments() {
        List<Segment> result =
                isSegmentSelected()                  ? Collections.singletonList( segment )
              : isOrgSelected() && isActorSelected() ? getAllSegments( organization, actor )
              : isOrgSelected()                      ? getAllSegments( organization )
              : isActorSelected()                    ? getAllSegments( actor )
                                                     : getAllSegments();

        if ( !isSegmentSelected() )
            Collections.sort( result );
        return result;
    }

    private List<Segment> getAllSegments() {
        return getAssignments().getSegments();
    }

    private List<Segment> getAllSegments( Specable specable ) {
        return getAssignments().withSome( specable ).getSegments();
    }

    private List<Segment> getAllSegments( Organization org, Specable specable ) {
        return getAssignments().withAll( org, specable ).getSegments();
    }

    /**
     * Set segment and actor fields given parameters.
     *
     * @param queryService for parameter resolution
     * @param parameters   the parameters
     */
    private void setParameters( QueryService queryService, PageParameters parameters ) {
        setValid( true );
        String segmentId = parameters.getString( SEGMENT_PARM, ALL );
        if ( !ALL.equals( segmentId ) )
            try {
                long id = Long.parseLong( segmentId );
                setSegment( queryService.find( Segment.class, id ) );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }
        String actorId = parameters.getString( ACTOR_PARM, ALL );
        if ( !ALL.equals( actorId ) )
            try {
                long id = Long.parseLong( actorId );
                setActor( queryService.find( Actor.class, id ) );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }
        String orgId = parameters.getString( ORGANIZATION_PARM, ALL );
        if ( !ALL.equals( orgId ) )
            try {
                long id = Long.parseLong( orgId );
                Organization org = queryService.find( Organization.class, id );
                if ( !isActorSelected() || queryService.findEmployers( actor ).contains( org ) )
                    setOrganization( org );
                else // actor not in specified org
                    setValid( false );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }

        if ( isUserPlanner() ) {
            if ( parameters.containsKey( ISSUES_PARM ) )
                setShowingIssues( parameters.getAsBoolean( ISSUES_PARM, true ) );
            if ( parameters.containsKey( DIAGRAMS_PARM ) )
                setShowingDiagrams( parameters.getAsBoolean( DIAGRAMS_PARM, true ) );
        }
    }

    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();
        if ( isSegmentSelected() )
            result.put( SEGMENT_PARM, Long.toString( segment.getId() ) );
        if ( isActorSelected() )
            result.put( ACTOR_PARM, Long.toString( actor.getId() ) );
        if ( isOrgSelected() )
            result.put( ORGANIZATION_PARM, Long.toString( organization.getId() ) );
        if ( showingIssues )
            result.put( ISSUES_PARM, Boolean.toString( showingIssues ) );
        if ( showingDiagrams )
            result.put( DIAGRAMS_PARM, Boolean.toString( showingDiagrams ) );
        return result;
    }

    private boolean isUserPlanner() {
        return user.isPlanner( getPlan().getUri() );
    }

    private List<Specable> getActorChoices() {
        List<Specable> result =
                isOrgSelected() && isSegmentSelected() ? getAllActors( organization, segment )
              : isOrgSelected()                        ? getAllActors( organization )
              : isSegmentSelected()                    ? getAllActors( segment )
                                                       : getAllActors();

        result.add( 0, ALL_ACTORS );
        return result;
    }

    /**
     * Return all actors selected by this component.
     *
     * @return a sorted list of actor.
     */
    public List<? extends Specable> getActors() {
        List<? extends Specable> result =
            isActorSelected()                      ? Collections.singletonList( actor )
          : isOrgSelected() && isSegmentSelected() ? getAllActors( organization, segment )
          : isOrgSelected()                        ? getAllActors( organization )
          : isSegmentSelected()                    ? getAllActors( segment )
                                                   : getAllActors();

        return result;
    }

    private List<Specable> getAllActors( Organization organization, Segment segment ) {
        return getAssignments().withSome( segment ).withSome( organization ).getActors();
    }

    private List<Specable> getAllActors( Organization organization ) {
        return getAssignments().withSome( organization ).getActors();
    }

    private List<Specable> getAllActors( Segment segment ) {
        return getAssignments().withSome( segment ).getActors();
    }

    private List<Specable> getAllActors() {
        return getAssignments().getActors();
    }

    private List<Organization> getOrganizationChoices() {
        List<Organization> result =
            isActorSelected() && isSegmentSelected() ? getAllOrganizations( segment, actor )
          : isActorSelected()                        ? getAllOrganizations( actor )
          : isSegmentSelected()                      ? getAllOrganizations( segment )
                                                     : getAllOrganizations();
        result.add( 0, ALL_ORGS );
        return result;
    }

    public List<Organization> getOrganizations() {
        List<Organization> result =
            isOrgSelected()                          ? Collections.singletonList( organization )
          : isActorSelected() && isSegmentSelected() ? getAllOrganizations( segment, actor )
          : isActorSelected()                        ? getAllOrganizations( actor )
          : isSegmentSelected()                      ? getAllOrganizations( segment )
                                                     : getAllOrganizations();

        return result;
    }

    private List<Organization> getAllOrganizations() {
        return getAssignments().getOrganizations();
    }

    private List<Organization> getAllOrganizations( Segment segment ) {
        return getAssignments().withSome( segment ).getOrganizations();
    }

    private List<Organization> getAllOrganizations( Segment segment, Specable actor ) {
        return getAssignments().withSome( segment ).withSome( actor ).getOrganizations();
    }

    private List<Organization> getAllOrganizations( Specable actor ) {
        return getAssignments().withSome( actor ).getOrganizations();
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment( Segment segment ) {
        this.segment = segment;
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid( boolean valid ) {
        this.valid = valid;
    }

    public boolean isActorSelected() {
        return !ALL_ACTORS.equals( actor );
    }

    public boolean isOrgSelected() {
        return !ALL_ORGS.equals( organization );
    }

    public boolean isSegmentSelected() {
        return !ALL_SEGMENTS.equals( segment );
    }

    public boolean isShowingIssues() {
        return isUserPlanner() && showingIssues;
    }

    public void setShowingIssues( boolean showingIssues ) {
        this.showingIssues = showingIssues;
    }

    public boolean isShowingDiagrams() {
        return showingDiagrams;
    }

    public void setShowingDiagrams( boolean showingDiagrams ) {
        this.showingDiagrams = showingDiagrams;
    }

    /**
     * Add something to the page header.
     *
     * @param response the header
     */
    public void renderHead( IHeaderResponse response ) {
//        response.renderOnDomReadyJavascript(
//                "document.getElementById('apply').style.display = \"none\";" );
    }
}
