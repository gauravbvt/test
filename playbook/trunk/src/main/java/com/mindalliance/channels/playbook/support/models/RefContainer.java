package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Simple container on a list of refs.
 */
public class RefContainer implements Container, Serializable {

    private List<Ref> contents;
    private transient ContainerSummary summary;

    public RefContainer() {}

    public RefContainer( List<Ref> contents ) {
        this();
        setContents( contents );
    }

    protected synchronized List<Ref> getContents() {
        return contents;
    }

    protected synchronized void setContents( List<Ref> contents ) {
        this.contents = contents;
    }

    public int size() {
        return getContents().size();
    }

    public Ref get( int index ) {
        return getContents().get( index );
    }

    public boolean add( Ref ref ) {
        return getContents().add( ref );
    }

    public void add( Referenceable ref ) {
        add( ref.getReference() );
    }

    public boolean contains( Ref ref ) {
        return getContents().contains( ref );
    }

    public synchronized void detach() {
        summary = null;
        if ( contents != null )
            for ( Ref ref: contents )
                ref.detach();
    }

    public List<Class<? extends Referenceable>> getAllowedClasses() {
        return new ArrayList<Class<? extends Referenceable>>();
    }

    public synchronized ContainerSummary getSummary() {
        if ( summary == null )
            summary = new ContainerSummary( this );
        return summary;
    }

    public int indexOf( Ref ref ) {
        return getContents().indexOf( ref );
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getContents().subList( first, first+count ).iterator();
    }

    public Iterator<Ref> iterator() {
        return getContents().iterator();
    }

    public IModel<Ref> model( Ref object ) {
        return new RefModel( object );
    }

    public void remove( Ref ref ) {
        getContents().remove( ref );
    }

    public void remove( Referenceable ref ) {
        remove( ref.getReference() );
    }

    public void initFromMap( Map map ) {
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put( Mappable.CLASS_NAME_KEY, getClass().getName() );
        return map;
    }
}
