package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.model.ModelEntity;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The report fine-tuning gizmo.
 */
public class SelectorPanel extends Panel implements IHeaderContributor {

    public static final String ACTOR_PARM = "agent";
    public static final String ORGANIZATION_PARM = "org";
    public static final String PLAN_PARM = "plan";
    public static final String VERSION_PARM = "v";
    private static final String ISSUES_PARM = "issues";

    /** Display string for "All" choices in drop-down. */
    private static final String ALL = "all";

    /**
     * Default value for "All segments" selection.
     */
    private static final Actor   ALL_ACTORS = new Actor( "Anyone" );
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
            @Override
            public void detach() {
                super.detach();
                assignments = null;
                queryService = null;
            }
        } );

        setParameters( parameters );

        Form<?> form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                redirect();
                super.onSubmit();
            }
        };

        form.add(
            new WebMarkupContainer( "switch-plan" )
                .add( new DropDownChoice<Plan>( "plan-sel",
                        new PropertyModel<Plan>( this, "plan" ),
                        new PropertyModel<List<? extends Plan>>( this, "plans" ) )
                    .add( newOnChange( "onchange" ) ) )
                .setVisible( getPlans().size() > 1 ),

            new WebMarkupContainer( "org-select" )
                .add( new DropDownChoice<Organization>( "organization",
                    getOrganizationChoices(),
                    new IChoiceRenderer<Organization>() {
                        public Object getDisplayValue( Organization object ) {
                            return object.equals( ALL_ORGS ) ? "All" : object.getName();
                        }

                        public String getIdValue( Organization object, int index ) {
                            return object.equals( ALL_ORGS ) ?
                                        ALL : Long.toString( object.getId() );
                        }
                    } ).add( newOnChange( "onchange" ) ) )
                ,//.setVisible( getOrganizationChoices().size() > 1 ),

            new WebMarkupContainer( "actor-select" )
                .add( new DropDownChoice<Specable>( "actor",
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
                     } ).add( newOnChange( "onchange" ) ) )
                ,//.setVisible( getActorChoices().size() > 1 ),

            new WebMarkupContainer( "optionals" )
                .add( new CheckBox( "showingIssues" ).add( newOnChange( "onclick" ) ) )
                .setVisible( isPlanner() )
        );

        add( form );
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public final QueryService getQueryService() {
        if ( queryService == null )
            queryService = new PlanService( planManager, attachmentManager, plan );

        return queryService;
    }

    /**
     * Get currently selected assignments.
     * @return an assignment wrapper
     */
    public Assignments getAssignments() {
        Assignments as = getAllAssignments();
        return isActorSelected() && isOrgSelected() ? as.notFrom( actor ).withAll( actor, organization )
             : isActorSelected()                    ? as.notFrom( actor ).withSome( actor )
             : isOrgSelected()                      ? as.withSome( organization )
                                                    : as;

    }

    /**
     * Get a description of the current selection.
     * @return a spec
     */
    public ResourceSpec getSelection() {
        return new ResourceSpec(
                actor instanceof Actor ? (Actor) actor : null,
                actor instanceof Role ? (Role) actor  : null,
                actor instanceof Actor && ( (ModelEntity) actor ).isActual() ? null : organization,
                null );

    }

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

        if ( getPlans().size() > 1 )
            result.put( PLAN_PARM, plan.getUri() );
        if ( isPlanner() && plan.isProduction() )
            result.put( VERSION_PARM, Integer.toString( plan.getVersion() ) );

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
     * @param parameters   the parameters
     */
    private void setParameters( PageParameters parameters ) {
        setValid( true );

        if ( setPlan( parameters ) ) {
            setActor( parameters );
            setOrganization( parameters );

            if ( isPlanner() )
                setShowingIssues( parameters.getAsBoolean( ISSUES_PARM, false ) );
        }
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
                     || !isActorSelected() && as.withSome( org ).isEmpty() )
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
            }
            else
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

        if ( a == null || getAllAssignments().withSome( a ).isEmpty() ) {
            // Specified actor is not assigned anything
            if ( isPlanner() )
                setValid( false );
            else
                throw new AbortWithHttpStatusException( HttpServletResponse.SC_FORBIDDEN, false );

        } else
            setActor( a );

    }

    /**
     * Set plan from uri parameters.
     * @param parameters the parameters
     * @return false if plan was not set
     */
    private boolean setPlan( PageParameters parameters ) {
        String planUri = parameters.getString( PLAN_PARM, user.getPlanUri() );

        int planVersion = parameters.getInt( VERSION_PARM, 0 );

        List<Plan> plans = getPlans();
        if ( plans.isEmpty() ) {
            setValid( false );
            return false;
        }
        for ( Iterator<Plan> it = plans.iterator(); it.hasNext() && plan == null; ) {
            Plan p = it.next();
            if ( planUri.equals( p.getUri() ) ) {
                if ( user.isPlanner( p.getUri() ) ) {
                    if ( planVersion == p.getVersion() || p.isDevelopment() )
                        plan = p;
                } else if ( p.isProduction() )
                    plan = p;
            }
        }

        if ( plan == null ) {
            plan = plans.get( 0 );
            if ( getPlans().size() > 1 )
                setValid( false );
        }

        if ( !isPlanner() && planVersion != 0 )
            setValid( false );

        getQueryService();
        return true;
    }

    private boolean isPlanner() {
        return user.isPlanner( plan.getUri() );
    }

    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        return planManager.getReadablePlans( user );
    }

    //---------------------------------
    private List<Specable> getActorChoices() {
        List<Specable> result = getAllActors( getAllAssignments() );
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
        return isActorSelected() || !isPlanner() ?
                                Collections.singletonList( actor )
                              : getAllActors( getAllAssignments() );
    }

    private List<Specable> getAllActors( Assignments as ) {
        return isOrgSelected() ? as.withSome( organization ).getActors()
                               : as.getActors();
    }

    //---------------------------------
    private List<Organization> getOrganizationChoices() {
        List<Organization> result = getAllAssignments().getOrganizations();
        if ( result.size() > 1 )
            result.add( 0, ALL_ORGS );
        return result;
    }

    public final List<Organization> getOrganizations() {
        return isOrgSelected() ? Collections.singletonList( organization )
                               : getAllOrganizations( getAllAssignments() );
    }

    private List<Organization> getAllOrganizations( Assignments as ) {
        return isActorSelected() ? as.withSome( actor ).getOrganizations()
                                 : as.getOrganizations();
    }

    //---------------------------------
    /**
     * Return current plan.
     *
     * @return a plan
     */
    public final Plan getPlan() {
        return plan;
    }

    /**
     * Switch the current plan.
     *
     * @param plan a plan
     */
    public final void setPlan( Plan plan ) {
        this.plan = plan;
        queryService = null;
        assignments = null;
    }

    public Specable getActor() {
        return actor;
    }

    public void setActor( Specable actor ) {
        this.actor = actor;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;

        if ( isActorSelected() && isOrgSelected()
             && getAllAssignments().withAll( organization, actor ).isEmpty() )
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

    //---------------------------------
    public boolean isActorSelected() {
        return !ALL_ACTORS.equals( actor );
    }

    public boolean isOrgSelected() {
        return !ALL_ORGS.equals( organization );
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
