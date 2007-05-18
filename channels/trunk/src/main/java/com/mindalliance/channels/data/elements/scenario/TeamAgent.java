/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.elements.resources.Team;
import com.mindalliance.channels.util.GUID;

/**
 * A predefined team. Later: ad hoc teams as well?.
 * 
 * @author jf
 */
public class TeamAgent extends Agent {

    private Team team;

    public TeamAgent() {
        super();
    }

    public TeamAgent( GUID guid ) {
        super( guid );
    }

    /**
     * Return the team as sole actor.
     */
    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<Actor>();
        actors.add(team);
        return actors;
    }

    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeam( Team team ) {
        this.team = team;
    }

}
