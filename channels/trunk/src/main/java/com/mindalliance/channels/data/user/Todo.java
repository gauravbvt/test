/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * An action item targeting selected users and that may depend on
 * other action items being completed.
 * 
 * @author jf
 */
public class Todo extends Announcement {

    private List<Todo> prerequisites;

    public Todo() {
        super();
    }

    public Todo( GUID guid ) {
        super( guid );
    }

    /**
     * @return the prerequisites
     */
    public List<Todo> getPrerequisites() {
        return prerequisites;
    }

    /**
     * @param prerequisites the prerequisites to set
     */
    public void setPrerequisites( List<Todo> prerequisites ) {
        this.prerequisites = prerequisites;
    }

    /**
     * @param todo
     */
    public void addPrerequisite( Todo todo ) {
        prerequisites.add( todo );
    }

    /**
     * @param todo
     */
    public void removePrerequisite( Todo todo ) {
        prerequisites.remove( todo );
    }

}
