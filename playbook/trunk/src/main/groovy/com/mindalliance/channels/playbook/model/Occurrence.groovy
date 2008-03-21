package com.mindalliance.channels.playbook.model
/**
 * ...
 */
abstract class Occurrence {
    List<Occurrence> causes = new ArrayList<Occurrence>()

    void addCause( Occurrence o ) {
        causes.add( o )
    }

    void removeCause( Occurrence o ) {
        causes.remove( o )
    }
}