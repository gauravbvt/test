package com.mindalliance.channels.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class Scenario extends NamedObject {

    private Set<Part> parts;
    private Map<Long,Part> partIndex;

    public Scenario() {
        setParts( new TreeSet<Part>() );
    }

    private Set<Part> getParts() {
        return parts;
    }

    protected final void setParts( Set<Part> parts ) {
        this.parts = new TreeSet<Part>( parts );
        partIndex = new HashMap<Long,Part>();
        for ( Part p: parts ) {
            partIndex.put( p.getId(), p );
        }
    }

    public Iterator<Part> parts() {
        return getParts().iterator();
    }

    public void addPart( Part part ) {
        getParts().add( part );
        partIndex.put( part.getId(), part );
    }

    public void removePart( Part part ) {
        getParts().remove( part );
        partIndex.remove( part.getId() );
    }

    public Part getPart( long id ) {
        return partIndex.get( id );
    }
}
