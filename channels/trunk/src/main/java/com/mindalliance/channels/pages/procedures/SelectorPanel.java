package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.attachments.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.engine.query.Assignments;
import com.mindalliance.channels.engine.query.PlanService;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The report fine-tuning gizmo.
 */
public class SelectorPanel extends AbstractUpdatablePanel implements AssignmentsSelector {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SelectorPanel.class );

    public static final String ACTOR_PARM = "agent";

    public static final String ORGANIZATION_PARM = "org";

    public static final String PLAN_PARM = "plan";

    public static final String VERSION_PARM = "v";

    private static final String ISSUES_PARM = "issues";

    /**
     * Display string for "All" choices in drop-down.
     */
    private static final String ALL = "all";

    /**
     * Default value for "All segments" selection.
     */
    private static final Actor ALL_ACTORS = new Actor( "Anyone" );

    private static final Organization ALL_ORGS = new Organization( "any organization" );

    /**
     * The selected actor (or ALL_ACTORS).
     */
    private Specable actor = ALL_ACTORS;

    /**
     * The selected organization (or ALL_ORGS).
     */
    private Organization organization = ALL_ORGS;

    /**
     * True when parameter combination would produce a not-empty report.
     */
    private boolean valid = true;

    /**
     * True when issues are shown.
     */
    private boolean showingIssues;

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private ImagingService imagingService;

    @SpringBean
    private UserService userService;

    @SpringBean
    private SemanticMatcher semanticMatcher;

    private transient QueryService queryService;

    private transient Assignments assignments;

    private final AbstractChannelsWebPage page;

    public SelectorPanel( String id, AbstractChannelsWebPage page ) {
        super( id );
        this.page = page;
        setReportedOnActor();
        setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            @Override
            public void detach() {
                super.detach();
                assignments = null;
                queryService = null;
            }
        } );

        Form<?> form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                redirect();
                super.onSubmit();
            }
        };

        form.add( newPlanSelector().setVisible( getPlans().size() > 1 ),

                  new WebMarkupContainer( "org-select" ).add( new DropDownChoice<Organization>( "organization",
                                                                                                getOrganizationChoices(),
                                                                                                new IChoiceRenderer<Organization>() {
                                                                                                    @Override
                                                                                                    public Object getDisplayValue(
                                                                                                            Organization object ) {
                                                                                                        return object.equals(
                                                                                                                ALL_ORGS )
                                                                                                               ? "All"
                                                                                                               : object.getName();
                                                                                                    }

                                                                                                    @Override
                                                                                                    public String getIdValue(
                                                                                                            Organization object,
                                                                                                            int index ) {
                                                                                                        return object.equals(
                                                                                                                ALL_ORGS )
                                                                                                               ? ALL
                                                                                                               : Long.toString(
                                                                                                                       object.getId() );
                                                                                                    }
                                                                                                } )
                                                                      .add( newOnChange( "onchange" ) ) ),
                  //.setVisible( getOrganizationChoices().size() > 1 ),

                  new WebMarkupContainer( "actor-select" ).add( new DropDownChoice<Specable>( "actor",
                                                                                              getActorChoices(),
                                                                                              new IChoiceRenderer<Specable>() {
                                                                                                  @Override
                                                                                                  public Object getDisplayValue(
                                                                                                          Specable object ) {
                                                                                                      return object.equals(
                                                                                                              ALL_ACTORS )
                                                                                                             ? "All"
                                                                                                             : Assignments
                                                                                                                     .stringify(
                                                                                                                             object );
                                                                                                  }

                                                                                                  @Override
                                                                                                  public String getIdValue(
                                                                                                          Specable object,
                                                                                                          int index ) {
                                                                                                      return object.equals(
                                                                                                              ALL_ACTORS )
                                                                                                             ? ALL
                                                                                                             : Long.toString(
                                                                                                                     ( (ModelObject) object )
                                                                                                                             .getId() );
                                                                                                  }
                                                                                              } )
                                                                        .add( newOnChange( "onchange" ) ) )
                  //.setVisible( getActorChoices().size() > 1 ),

                  /*    , new WebMarkupContainer( "optionals" )
                .add( new CheckBox( "showingIssues" ).add( newOnChange( "onclick" ) ) )
                .setVisible( isPlanner() )*/ );

        add( form );
    }

    private void setReportedOnActor() {
        Actor paramActor = null;
        Actor participationActor = null;
        User user = this.user;
        Participation participation = getQueryService().findParticipation( user.getUsername() );
        if ( participation != null ) {
            participationActor = participation.getActor();
        }
        PageParameters pageParameters = page.getPageParameters();
        long actorId = pageParameters.containsKey( SelectorPanel.ACTOR_PARM ) ? pageParameters
                .getLong( SelectorPanel.ACTOR_PARM ) : -1;
        if ( actorId > 0 ) {
            try {
                paramActor = getQueryService().find( Actor.class, actorId );
            } catch ( Exception e ) {
                LOG.warn( "Actor not found from parameter " + actorId );
            }
        }
        if ( paramActor != null ) {
            if ( user.isPlanner( getPlan().getUri() ) || participationActor != null && participationActor
                    .equals( paramActor ) )
            {
                actor = paramActor;
            }
        }

        if ( actor.equals( ALL_ACTORS ) ) {
            if ( !user.isPlanner( getPlan().getUri() ) ) {
                if ( participationActor != null ) {
                    actor = participationActor;
                } else {
                    throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );
                }
            }
        }
    }

    MarkupContainer newPlanSelector() {
        return new WebMarkupContainer( "switch-plan" ).add( new DropDownChoice<Plan>( "plan-sel",
                                                                                      new PropertyModel<Plan>( this,
                                                                                                               "plan" ),
                                                                                      new PropertyModel<List<? extends Plan>>(
                                                                                              this,
                                                                                              "plans" ) )
                                                                    .add( newOnChange( "onchange" ) ) );
    }

    @Override
    public PlanManager getPlanManager() {
        return planManager;
    }

    /**
     * Get currently selected assignments.
     *
     * @return an assignment wrapper
     */
    @Override
    public Assignments getAssignments() {
        Assignments as = getAllAssignments();
        return isActorSelected() && isOrgSelected() ? as.notFrom( actor ).withAll( actor, organization )
                                                    : isActorSelected() ? as.notFrom( actor ).with( actor )
                                                                        : isOrgSelected() ? as.with( organization )
                                                                                          : as;
    }

    /**
     * Get a description of the current selection.
     *
     * @return a spec
     */
    @Override
    public ResourceSpec getSelection() {
        return new ResourceSpec( actor instanceof Actor ? (Actor) actor : null,
                                 actor instanceof Role ? (Role) actor : null,
                                 actor instanceof Actor && ( (ModelEntity) actor ).isActual() ? null : organization,
                                 null );
    }

    @Override
    public Assignments getAllAssignments() {
        if ( assignments == null )
            assignments = getQueryService().getAssignments();
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

    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();
        page.getParameters();
        if ( isActorSelected() && getActorChoices().size() > 1 )
            result.put( ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
        if ( isOrgSelected() && getOrganizationChoices().size() > 1 )
            result.put( ORGANIZATION_PARM, Long.toString( organization.getId() ) );
        if ( showingIssues )
            result.put( ISSUES_PARM, Boolean.toString( showingIssues ) );

        return result;
    }

    //---------------------------------

    /**
     * Set segment and actor fields given parameters.
     *
     * @return page parameters
     */
    private PageParameters setParameters() {       // todo
        setValid( true );
        PageParameters parameters = page.getParameters();
        setActor( parameters );
        setOrganization( parameters );
        if ( isPlanner() )
            setShowingIssues( parameters.getAsBoolean( ISSUES_PARM, false ) );
        return parameters;
    }

    private void setOrganization( PageParameters parameters ) {
        String orgId = parameters.getString( ORGANIZATION_PARM, ALL );
        Assignments as = getAllAssignments();

        if ( ALL.equals( orgId ) ) {
            List<Organization> orgs = getOrganizations();
            if ( orgs.size() == 1 )
                setOrganization( orgs.get( 0 ) );
        } else
            try {
                Organization org = queryService.find( Organization.class, Long.parseLong( orgId ) );
                if ( isActorSelected() && as.withAll( actor, org ).isEmpty()
                        || !isActorSelected() && as.with( org ).isEmpty() )
                    setValid( false );
                else
                    setOrganization( org );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }

        List<Specable> actors = getActorChoices();
        if ( isOrgSelected() && !isActorSelected() && actors.size() == 1 ) {
            setActor( actors.get( 0 ) );
            setOrganization( ALL_ORGS );
            setValid( false );
        }
    }

    private void setActor( PageParameters parameters ) {

        String actorId = parameters.getString( ACTOR_PARM, ALL );
        Specable a = null;

        if ( isPlanner() ) {
            if ( ALL.equals( actorId ) ) {
                //                List<? extends Specable> actors = getActorChoices();
                //                if ( actors.size()== 2 )
                //                    a = actors.get( 0 );
                //                else
                return;
            } else
                try {
                    a = queryService.find( Actor.class, Long.parseLong( actorId ) );
                } catch ( NumberFormatException ignored ) {
                } catch ( NotFoundException ignored ) {
                }
        } else {
            Participation participation = queryService.findParticipation( user.getUsername() );
            if ( participation != null )
                a = participation.getActor();
        }

        if ( a == null || getAllAssignments().with( a ).isEmpty() ) {
            // Specified actor is not assigned anything
            if ( isPlanner() )
                setValid( false );
            else
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );
        } else
            setActor( a );
    }

    @Override
    public boolean isPlanner() {
        return user.isPlanner( getPlan().getUri() );
    }

    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        return page.getPlans();
    }

    //---------------------------------
    private List<Specable> getActorChoices() {
        List<Specable> result = new ArrayList<Specable>();
        for ( Specable specable : getAllActors( getAllAssignments() ) ) {
            if ( specable instanceof Actor )
                result.add( specable );
        }
        if ( result.size() > 1 )
            result.add( 0, ALL_ACTORS );
        return result;
    }

    /**
     * Return all actors selected by this component.
     *
     * @return a sorted list of actor.
     */
    public List<? extends Specable> getActors() {
        return isActorSelected() || !isPlanner() ? Collections.singletonList( actor )
                                                 : getAllActors( getAllAssignments() );
    }

    private List<Specable> getAllActors( Assignments as ) {
        return isOrgSelected() ? as.with( organization ).getActors() : as.getActors();
    }

    //---------------------------------
    private List<Organization> getOrganizationChoices() {
        List<Organization> result = getAllAssignments().getOrganizations();
        if ( result.size() > 1 )
            result.add( 0, ALL_ORGS );
        return result;
    }

    public final List<Organization> getOrganizations() {
        return isOrgSelected() ? Collections.singletonList( organization ) : getAllOrganizations( getAllAssignments() );
    }

    private List<Organization> getAllOrganizations( Assignments as ) {
        return isActorSelected() ? as.with( actor ).getOrganizations() : as.getOrganizations();
    }

    //---------------------------------

    /**
     * Return current plan.
     *
     * @return a plan
     */
    @Override
    public final Plan getPlan() {
        return page.getPlan();
    }

    /**
     * Switch the current plan.
     *
     * @param plan a plan
     */
    public final void setPlan( Plan plan ) {
        page.setPlan( plan );
        assignments = null;
    }

    @Override
    public Specable getActor() {
        return actor;
    }

    @Override
    public Specable getFocusEntity() {
        return null;
    }

    @Override
    public Flow getFlow() {
        return null;
    }

    @Override
    public Segment getSegment() {
        return null;
    }

    @Override
    public Assignments getSources( Part part ) {
        return getAllAssignments().getSources( part );
    }

    @Override
    public List<Commitment> getCommitments() {
        return null; // Todo
    }

    @Override
    public List<Commitment> getCommitmentsTriggering( Part part ) {
        return null;  //Todo
    }

    public void setActor( Specable actor ) {
        this.actor = actor;
    }

    @Override
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;

        if ( isActorSelected() && isOrgSelected() && getAllAssignments().withAll( organization, actor ).isEmpty() )
            setActor( ALL_ACTORS );
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid( boolean valid ) {
        this.valid = valid;
    }

    public boolean isShowingIssues() {
        return isPlanner() && showingIssues;
    }

    public void setShowingIssues( boolean showingIssues ) {
        this.showingIssues = showingIssues;
    }

    public User getUser() {
        return user;
    }

    //---------------------------------
    @Override
    public boolean isActorSelected() {
        return !ALL_ACTORS.equals( actor );
    }

    @Override
    public boolean isOrgSelected() {
        return !ALL_ORGS.equals( organization );
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    @Override
    public ImagingService getImagingService() {
        return imagingService;
    }

    @Override
    public PlanService getPlanService() {
        return new PlanService( planManager, semanticMatcher, userService, getPlan(), attachmentManager );
    }
}
