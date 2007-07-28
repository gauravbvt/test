// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.data.support.GUID;

/**
 * An action item targeting selected users and that may depend on
 * other action items being completed.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class Todo extends Announcement {

    private List<Todo> prerequisites;

    /**
     * Default constructor.
     */
    public Todo() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Todo( GUID guid ) {
        super( guid );
    }

    /**
     * Return the prerequisites.
     */
    public List<Todo> getPrerequisites() {
        return prerequisites;
    }

    /**
     * Set the prerequisites.
     * @param prerequisites the prerequisites to set
     */
    public void setPrerequisites( List<Todo> prerequisites ) {
        this.prerequisites = prerequisites;
    }

    /**
     * Add a prerequisite.
     * @param todo the prerequisite
     */
    public void addPrerequisite( Todo todo ) {
        prerequisites.add( todo );
    }

    /**
     * Remove a prerequisite.
     * @param todo the prerequisite
     */
    public void removePrerequisite( Todo todo ) {
        prerequisites.remove( todo );
    }
}
