// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.profiles.Actor;
import com.mindalliance.channels.profiles.Team;
import com.mindalliance.channels.support.GUID;

/**
 * A predefined team. Later: ad hoc teams as well?.
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class TeamAgent extends Agent {

    private Team team;

    /**
     * Default constructor.
     */
    public TeamAgent() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public TeamAgent( GUID guid ) {
        super( guid );
    }

    /**
     * Return the team as sole actor.
     */
    public List<Actor> getActors() {
        List<Actor> actors = new ArrayList<Actor>();
        actors.add( team );
        return actors;
    }

    /**
     * Return the team.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Set the team.
     * @param team the team
     */
    public void setTeam( Team team ) {
        this.team = team;
    }

}
