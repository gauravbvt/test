package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
public class ContainerModel extends RefPropertyModel<Ref> implements Container {

    private final List<Class<? extends Referenceable>> allowedClasses;
    private transient ContainerSummary summary;
    private transient List<Ref> contents;

    public ContainerModel(
            Serializable target, String expression, List<Class<? extends Referenceable>> allowedClasses ) {

        super( target, expression );
        this.allowedClasses = allowedClasses;
    }

    //==================================
    public Ref get( int index ) {
        return getContents().get( index );
    }

    public int indexOf( Ref ref ) {
        return getContents().indexOf( ref );
    }

    public boolean contains( Ref ref ) {
        return getContents().contains( ref );
    }

    public void add( Referenceable ref ) {
        Ref target = (Ref) super.getChainedModel().getObject();
        target.add( ref, super.getExpression() );
        detach();
    }

    public void remove( Ref ref ) {
        Ref target = (Ref) super.getChainedModel().getObject();
        target.remove( ref, super.getExpression() );
        detach();
    }

    public void remove( Referenceable ref ) {
        remove( ref.getReference() );
    }

    //==================================
    @Override
    public void detach() {
        super.detach();
        if ( summary != null )
            summary.detach();
        contents = null;
    }

    public List<Class<? extends Referenceable>> getAllowedClasses() {
        return Collections.unmodifiableList( allowedClasses );
    }

    public ContainerSummary getSummary() {
        if ( summary == null )
            summary = new ContainerSummary( this );

        return summary;
    }

    @SuppressWarnings( { "unchecked" } )
    private List<Ref> getContents() {
        if ( contents == null ) {
            contents = (List<Ref>) getObject();
            if ( contents == null ) {
                // Happens on empty scenarios, etc
                contents = new ArrayList<Ref>(0);
            }
        }

        return contents;
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getContents().subList( first, first + count ).iterator();
    }

    public Iterator<Ref> iterator() {
        return getContents().iterator();
    }

    public int size() {
        return getContents().size();
    }

    public IModel<Ref> model( Ref object ) {
        return new RefModel( object );
    }

    // Mappable
    public Map<String, Object> toMap() {
        return null;// TODO
    }

    public void initFromMap( Map<String, Object> map ) {
        // TODO
    }

    // end Mappable
}
