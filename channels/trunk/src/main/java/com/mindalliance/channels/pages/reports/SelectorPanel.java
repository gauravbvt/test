package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The report fine-tuning gizmo.
 */
public class SelectorPanel extends Panel {

    private static final String SCENARIO_PARM = "0";
    private static final String ACTOR_PARM = "1";
    private static final String ALL = "all";

    private static final Scenario AllScenarios = new Scenario();

    private Scenario scenario;

    private Actor actor;

    private boolean valid = true;

    @SpringBean
    private QueryService queryService;

    public SelectorPanel( String id, PageParameters parameters ) {
        super( id );

        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        setParameters( parameters );

        DropDownChoice<Scenario> scenarioChoices = new DropDownChoice<Scenario>(
                "scenario", getScenarioChoices(), new IChoiceRenderer<Scenario>() {
                    public Object getDisplayValue( Scenario object ) {
                        return AllScenarios == object ? "All scenarios" : object.getName();
                    }

                    public String getIdValue( Scenario object, int index ) {
                        return AllScenarios == object ? ALL : Long.toString( object.getId() );
                    }
                } );

        scenarioChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    setRedirect( true );
                    setResponsePage( getPage().getClass(), getParameters() );
                }
            } );
        add( scenarioChoices );

        DropDownChoice<Actor> actorChoices = new DropDownChoice<Actor>(
                "actor", getActorsChoices(), new IChoiceRenderer<Actor>() {
                    public Object getDisplayValue( Actor object ) {
                        return Actor.UNKNOWN.equals( object ) ? "All actors" : object.getName();
                    }

                    public String getIdValue( Actor object, int index ) {
                        return Actor.UNKNOWN.equals( object ) ?
                               ALL : Long.toString( object.getId() );
                    }
                } );
        actorChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    setRedirect( true );
                    setResponsePage( getPage().getClass(), getParameters() );
                }
            } );
        add( actorChoices );
    }

    /**
     * Return all the scenarios selected by this component.
     * @return a sorted list of scenarios
     */
    public List<Scenario> getScenarios() {
        List<Scenario> result;

        if ( isAllScenarios() ) {
            result = queryService.list( Scenario.class );
            Collections.sort( result );
        } else {
            result = new ArrayList<Scenario>();
            result.add( scenario );
        }

        return result;
    }

    /**
     * Return all actors selectable by this component.
     * @return a sorted list of actor.
     */
    public List<Actor> getActors() {
        List<Actor> result;

        if ( isAllActors() ) {
            result = queryService.list( Actor.class );
            Collections.sort( result );
        } else {
            result = new ArrayList<Actor>();
            result.add( actor );
        }

        return result;
    }

    /**
     * Set scenario and actor fields given parameters.
     * @param parameters the parameters
     */
    public final void setParameters( PageParameters parameters ) {
        setValid( true );
        setScenario( AllScenarios );
        String scenarioId = parameters.getString( SCENARIO_PARM, ALL );
        if ( !ALL.equals( scenarioId ) ) {
            try {
                long id = Long.parseLong( scenarioId );
                setScenario( queryService.find( Scenario.class, id ) );
            } catch ( NumberFormatException ignored ) {
                setValid( false );
            } catch ( NotFoundException ignored ) {
                setValid( false );
            }
        }

        setActor( Actor.UNKNOWN );
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
    }

    public PageParameters getParameters() {
        PageParameters result = new PageParameters();

        if ( isAllActors() ) {
            if ( !isAllScenarios() )
                result.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );

        } else {
            result.put( SCENARIO_PARM,
                        isAllScenarios() ? ALL : Long.toString( scenario.getId() ) );
            result.put( ACTOR_PARM, Long.toString( actor.getId() ) );
        }

        return result;
    }

    private List<Scenario> getScenarioChoices() {
        List<Scenario> result = new ArrayList<Scenario>(
                isAllActors() ? queryService.list( Scenario.class )
                              : queryService.findScenarios( actor ) );
        Collections.sort( result );
        result.add( 0, AllScenarios );
        return result;
    }

    private List<Actor> getActorsChoices() {
        List<Actor> result = new ArrayList<Actor>(
                isAllScenarios() ? queryService.list( Actor.class )
                                 : queryService.findActors( scenario ) );
        Collections.sort( result );
        result.add( 0, Actor.UNKNOWN );
        return result;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid( boolean valid ) {
        this.valid = valid;
    }

    public boolean isAllActors() {
        return Actor.UNKNOWN.equals( actor );
    }

    public boolean isAllScenarios() {
        return AllScenarios == scenario;
    }
}
