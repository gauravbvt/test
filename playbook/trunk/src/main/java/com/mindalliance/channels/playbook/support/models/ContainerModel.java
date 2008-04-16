package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public class ContainerModel extends RefPropertyModel implements Container {

    private List<Class<?>> allowedClasses;
    private ColumnProvider columnProvider;
    private List<Ref> contents;

    public ContainerModel( Object modelObject, String expression, List<Class<?>> allowedClasses ) {
        super( modelObject, expression );
        this.allowedClasses = Collections.unmodifiableList( allowedClasses );
    }

    public Object getTarget() {
        return super.getTarget();
    }

    //==================================
    public Ref get( int index ) {
        return getContents().get( index );
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
    public void detach() {
        super.detach();
        if ( columnProvider != null )
            columnProvider.detach();
        contents = null;
    }

    public List<Class<?>> getAllowedClasses() {
        return allowedClasses;
    }

    public ColumnProvider getColumnProvider() {
        if ( columnProvider == null )
            columnProvider = new ColumnProvider( this );

        return columnProvider;
    }

    private List<Ref> getContents() {
        if ( contents == null ) {
            contents = (List<Ref>) getObject();
            if ( contents == null ) {
                // Happens on empty scenarios, etc
                contents = new ArrayList<Ref>();
            }
        }

        return contents;
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getContents().subList( first, first+count ).iterator();
    }

    public int size() {
        return getContents().size();
    }

    public IModel model( Object object ) {
        return new RefModel( object );
    }
}
