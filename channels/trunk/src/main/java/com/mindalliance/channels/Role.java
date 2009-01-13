package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.Resource;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * A generic role.
 */
public class Role extends ModelObject implements Player, Resourceable {

    public Role() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Role( String name ) {
        super( name );
    }

    /**
     * Find or create a role by name
     * @param name String a given name
     * @return a new or existing role
     */
    public static Role named( String name ) {
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeRole( name );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays() {
        List<Play> list = new ArrayList<Play>();
        Iterator<Scenario> scenarios = Project.getProject().getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() && ( (Part) flow.getSource() ).getRole() == this ) {
                        // role sends
                        Play play = new Play( (Part) flow.getSource(), flow, true );
                        list.add( play );
                    }
                    if ( flow.getTarget().isPart() && ( (Part) flow.getTarget() ).getRole() == this ) {
                        // role receives
                        Play play = new Play( (Part) flow.getTarget(), flow, false );
                        list.add( play );
                    }
                }
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Resource> findAllResources() {
        Set<Resource> set = new HashSet<Resource>();
        Iterator<Scenario> scenarios = Project.getProject().getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getRole() == this && Resource.hasResource( part ) ) {
                    Resource resource = new Resource( part );
                    // Find all channels used to communicate with this part
                    Iterator<Flow> flows = scenario.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getChannel() != null && !flow.getChannel().isEmpty() ) {
                            if ( flow.getTarget() == part && !flow.isAskedFor() ) {
                                resource.addChannel( flow.getChannel() );
                            }
                            if ( flow.getSource() == part && flow.isAskedFor() ) {
                                resource.addChannel( flow.getChannel() );
                            }
                        }
                    }
                    set.add( resource );
                }
            }
        }
        List<Resource> list = new ArrayList<Resource>();
        list.addAll( set );
        return list;
    }
}
