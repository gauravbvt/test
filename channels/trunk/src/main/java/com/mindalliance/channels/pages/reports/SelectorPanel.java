package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
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

    private static final String SEGMENT_PARM = "0";
    private static final String ACTOR_PARM = "1";
    private static final String ISSUES_PARM = "issues";
    private static final String ALL = "all";

    /** Default value for "All segments" selection. */
    private static final Segment ALL_SEGMENTS = new Segment();

    /** Localized sorter-upper. */
    private static final Collator SORTER = Collator.getInstance();

    /** The selected segment (or AllSegments) */
    private Segment segment = ALL_SEGMENTS;

    /** The selected actor (or Actor.UNKNOWN) */
    private Actor actor = Actor.UNKNOWN;

    /** True when parameter combination would produce a not-empty report. */
    private boolean valid = true;

    /** True when issues are shown. */
    private boolean showingIssues = true;

    @SpringBean
    private QueryService queryService;

    public SelectorPanel( String id, PageParameters parameters ) {
        super( id );

        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        setParameters( queryService, parameters );

        add( new Form( "form" ) {
            @Override
            protected void onSubmit() {
                redirect();
                super.onSubmit();
            }
        }
            .add( new DropDownChoice<Segment>( "segment", getSegmentChoices( queryService ),
                    new IChoiceRenderer<Segment>() {
                        public Object getDisplayValue( Segment object ) {
                            return ALL_SEGMENTS.equals( object ) ? "All"
                                                                  : object.getName();
                        }

                        public String getIdValue( Segment object, int index ) {
                            return ALL_SEGMENTS.equals( object ) ? ALL
                                                                 : Long.toString( object.getId() );
                        }
                    } ).add( newOnChange( "onchange" ) ),

                 new DropDownChoice<Actor>( "actor", getActorsChoices( queryService ),
                                            new IChoiceRenderer<Actor>() {
                        public Object getDisplayValue( Actor object ) {
                            return object.equals( Actor.UNKNOWN ) ? "All"
                                                                  : object.getNormalizedName();
                        }

                        public String getIdValue( Actor object, int index ) {
                            return object.equals( Actor.UNKNOWN ) ? ALL
                                                                  : Long.toString( object.getId() );
                        }
                    } ).add( newOnChange( "onchange" ) ),

                 new CheckBox( "showingIssues" ).add( newOnChange( "onclick" ) ) ) );
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
                    isAllSegments() ? queryService.list( Actor.class )
                            : queryService.findActors( segment ) );
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
     * @param parameters the parameters
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

        if ( parameters.containsKey( ISSUES_PARM ) )
            setShowingIssues( parameters.getAsBoolean( ISSUES_PARM, true ) );
    }

    /**
     * Build a new parameter container for the current selections.
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();

        if ( isAllActors() ) {
            if ( !isAllSegments() )
                result.put( SEGMENT_PARM, Long.toString( segment.getId() ) );

        } else {
            result.put( SEGMENT_PARM, isAllSegments() ? ALL : Long.toString( segment.getId() ) );
            result.put( ACTOR_PARM, Long.toString( actor.getId() ) );
        }

        if ( !showingIssues )
            result.put( ISSUES_PARM, Boolean.toString( showingIssues ) );

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

    private List<Actor> getActorsChoices( QueryService service ) {
        List<Actor> result = new ArrayList<Actor>(
                isAllSegments() ? service.list( Actor.class )
                                 : service.findActors( segment ) );
        Collections.sort(
                result,
                new Comparator<Actor>() {
                    public int compare( Actor o1, Actor o2 ) {
                        return SORTER.compare( o1.getNormalizedName(), o2.getNormalizedName() );
                    }
                } );
        result.add( 0, Actor.UNKNOWN );
        return result;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
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

    public boolean isAllSegments() {
        return ALL_SEGMENTS.equals( segment );
    }

    public boolean isShowingIssues() {
        return showingIssues;
    }

    public void setShowingIssues( boolean showingIssues ) {
        this.showingIssues = showingIssues;
    }

    /**
     * Add something to the page header.
     * @param response the header
     */
    public void renderHead( IHeaderResponse response ) {
//        response.renderOnDomReadyJavascript(
//                "document.getElementById('apply').style.display = \"none\";" );
    }
}
