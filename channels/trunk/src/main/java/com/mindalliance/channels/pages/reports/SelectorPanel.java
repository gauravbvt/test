package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Localized sorter-upper.
     */
    private static final Collator SORTER = Collator.getInstance();

    /**
     * The selected segment (or AllSegments)
     */
    private Segment segment = ALL_SEGMENTS;

    /**
     * The selected actor (or Actor.UNKNOWN)
     */
    private Actor actor = Actor.UNKNOWN;
    /**
     * The selected organization (or Organization.UNKNOWN)
     */
    private Organization organization = Organization.UNKNOWN;

    /**
     * True when parameter combination would produce a not-empty report.
     */
    private boolean valid = true;

    /**
     * True when issues are shown.
     */
    private boolean showingIssues = false;

    /**
     * True when issues are shown.
     */
    private boolean showingDiagrams = false;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private QueryService queryService;

    public SelectorPanel( String id, PageParameters parameters ) {
        super( id );

        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        setParameters( queryService, parameters );
        Form form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                redirect();
                super.onSubmit();
            }
        };
        addPlanSwitcher( form );
        form.add( new DropDownChoice<Segment>( "segment", getSegmentChoices( queryService ),
                new IChoiceRenderer<Segment>() {
                    public Object getDisplayValue( Segment object ) {
                        return ALL_SEGMENTS.equals( object ) ? "All"
                                : object.getName();
                    }

                    public String getIdValue( Segment object, int index ) {
                        return ALL_SEGMENTS.equals( object ) ? ALL
                                : Long.toString( object.getId() );
                    }
                } ).add( newOnChange( "onchange" ) ) );
        WebMarkupContainer showOptionalsContainer = new WebMarkupContainer( "optionals" );
        showOptionalsContainer.setVisible( isUserPlanner() );
        form.add( showOptionalsContainer );
        showOptionalsContainer.add( new CheckBox( "showingIssues" ).add( newOnChange( "onclick" ) ) );
        showOptionalsContainer.add( new CheckBox( "showingDiagrams" ).add( newOnChange( "onclick" ) ) );
        if ( isUserPlanner() ) {
            form.add( new DropDownChoice<Organization>(
                    "organization",
                    getOrganizationChoices( queryService ),
                    new IChoiceRenderer<Organization>() {
                        public Object getDisplayValue( Organization object ) {
                            return object.equals( Organization.UNKNOWN ) ? "All"
                                    : object.getName();
                        }

                        public String getIdValue( Organization object, int index ) {
                            return object.equals( Organization.UNKNOWN ) ? ALL
                                    : Long.toString( object.getId() );
                        }
                    } ).add( newOnChange( "onchange" ) ) );
            form.add( new DropDownChoice<Actor>(
                    "actor",
                    getActorChoices( queryService ),
                    new IChoiceRenderer<Actor>() {
                        public Object getDisplayValue( Actor object ) {
                            return object.equals( Actor.UNKNOWN ) ? "All"
                                    : object.getNormalizedName();
                        }

                        public String getIdValue( Actor object, int index ) {
                            return object.equals( Actor.UNKNOWN ) ? ALL
                                    : Long.toString( object.getId() );
                        }
                    } ).add( newOnChange( "onchange" ) ) );
        } else {
            form.add( new Label( "actor", "" ) );
            form.add( new Label( "organization", "" ) );
        }
        add( form );
    }

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
    public Plan getPlan() {
        return User.current().getPlan();
    }

    /**
     * Switch the user's current plan.
     *
     * @param plan a plan
     */
    public void setPlan( Plan plan ) {
        User.current().setPlan( plan );
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
     * Return all the segment selected by this component.
     *
     * @return a sorted list of segment
     */
    public List<Segment> getSegments() {
        List<Segment> result;

        if ( isAllSegments() ) {
            result = new ArrayList<Segment>(
                    isAllActors() ? queryService.list( Segment.class )
                            : queryService.findSegments( actor ) );
            Collections.sort( result );
        } else {
            result = new ArrayList<Segment>();
            result.add( segment );
        }
        return result;
    }

    /**
     * Return all actors selectable by this component.
     *
     * @return a sorted list of actor.
     */
    public List<Actor> getActors() {
        List<Actor> result;

        if ( isAllActors() ) {
            result = new ArrayList<Actor>(
                    isAllSegments()
                            ? queryService.list( Actor.class )
                            : queryService.findActualActors( segment ) );
            Collections.sort( result );
        } else {
            result = new ArrayList<Actor>();
            result.add( actor );
        }
        return result;
    }

    /**
     * Set segment and actor fields given parameters.
     *
     * @param queryService for parameter resolution
     * @param parameters   the parameters
     */
    public final void setParameters( QueryService queryService, PageParameters parameters ) {
        setValid( true );
        String segmentId = parameters.getString( SEGMENT_PARM, ALL );
        if ( !ALL.equals( segmentId ) ) {
            try {
                long id = Long.parseLong( segmentId );
                setSegment( queryService.find( Segment.class, id ) );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }
        }
        if ( isUserPlanner() ) {
            String actorId = parameters.getString( ACTOR_PARM, ALL );
            if ( !ALL.equals( actorId ) ) {
                try {
                    long id = Long.parseLong( actorId );
                    setActor( queryService.find( Actor.class, id ) );
                } catch ( NumberFormatException ignored ) {
                    setValid( false );
                } catch ( NotFoundException ignored ) {
                    setValid( false );
                }
            }
            String orgId = parameters.getString( ORGANIZATION_PARM, ALL );
            if ( !ALL.equals( orgId ) ) {
                try {
                    long id = Long.parseLong( orgId );
                    setOrganization( queryService.find( Organization.class, id ) );
                } catch ( NumberFormatException ignored ) {
                    setValid( false );
                } catch ( NotFoundException ignored ) {
                    setValid( false );
                }
            }
            if ( !isAllActors() && !isAllOrganizations() ) {
                if ( !isEmployee( getActor(), getOrganization() ) ) {
                    setActor( Actor.UNKNOWN );
                }
            }
        } else {
            Participation participation = queryService.findParticipation( User.current().getUsername() );
            if ( participation == null ) {
                setActor( Actor.UNKNOWN );
            } else {
                setActor( participation.getActor() );
            }
            setOrganization( Organization.UNKNOWN );
        }

        if ( parameters.containsKey( ISSUES_PARM ) )
            setShowingIssues( parameters.getAsBoolean( ISSUES_PARM, true ) );

        if ( parameters.containsKey( DIAGRAMS_PARM ) )
            setShowingDiagrams( parameters.getAsBoolean( DIAGRAMS_PARM, true ) );
    }

    private boolean isUserPlanner() {
        User user = User.current();
        return user.isPlanner( user.getPlanUri() );
    }

    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();

        if ( !isAllSegments() )
            result.put( SEGMENT_PARM, Long.toString( segment.getId() ) );
        if ( !isAllActors() ) {
            result.put( ACTOR_PARM, Long.toString( actor.getId() ) );
        }
        if ( !isAllOrganizations() ) {
            result.put( ORGANIZATION_PARM, Long.toString( organization.getId() ) );
        }
        if ( showingIssues )
            result.put( ISSUES_PARM, Boolean.toString( showingIssues ) );
        if ( showingDiagrams )
            result.put( DIAGRAMS_PARM, Boolean.toString( showingDiagrams ) );
        return result;
    }

    private List<Segment> getSegmentChoices( QueryService service ) {
        List<Segment> result = new ArrayList<Segment>(
                isAllActors() ? service.list( Segment.class )
                        : service.findSegments( actor ) );
        Collections.sort( result );
        result.add( 0, ALL_SEGMENTS );
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private List<Actor> getActorChoices( QueryService service ) {
        List<Actor> result = new ArrayList<Actor>(
                isAllSegments() ? service.listActualEntities( Actor.class )
                        : service.findActualActors( segment ) );
        Collections.sort(
                result,
                new Comparator<Actor>() {
                    public int compare( Actor o1, Actor o2 ) {
                        return SORTER.compare( o1.getNormalizedName(), o2.getNormalizedName() );
                    }
                } );
        if ( !isAllOrganizations() ) {
            result = (List<Actor>) CollectionUtils.select(
                    result,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return isEmployee( (Actor) object, getOrganization() );
                        }
                    }
            );
        }
        result.add( 0, Actor.UNKNOWN );
        return result;
    }

    private boolean isEmployee( Actor act, Organization org ) {
        return queryService.findEmployers( act ).contains( org );
    }

    private List<Organization> getOrganizationChoices( QueryService service ) {
        List<Organization> result = new ArrayList<Organization>(
                isAllSegments() ? service.listActualEntities( Organization.class )
                        : service.findActualOrganizations( segment ) );
        result.remove( Organization.UNKNOWN );
        Collections.sort(
                result,
                new Comparator<Organization>() {
                    public int compare( Organization o1, Organization o2 ) {
                        return SORTER.compare( o1.getName(), o2.getName() );
                    }
                } );
        result.add( 0, Organization.UNKNOWN );
        return result;
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

    public boolean isAllActors() {
        return actor.equals( Actor.UNKNOWN );
    }

    public boolean isAllOrganizations() {
        return organization.equals( Organization.UNKNOWN );
    }

    public boolean isAllSegments() {
        return ALL_SEGMENTS.equals( segment );
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
