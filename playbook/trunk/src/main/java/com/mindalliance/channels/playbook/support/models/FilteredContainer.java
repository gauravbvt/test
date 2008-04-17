package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public class FilteredContainer implements Container {

    private Container data;
    private Filter filter;
    private transient List<Ref> buffer;
    private transient ColumnProvider columnProvider;
    private boolean inverted;

    public FilteredContainer( Container data, Filter filter, boolean inverted ) {
         this.data = data;
         this.filter = filter;
         this.inverted = inverted;
     }

    public FilteredContainer( Container data, Filter filter ) {
         this( data, filter, false );
     }

    public final Container getData() {
        return data;
    }

    public final Filter getFilter() {
        return filter;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted( boolean inverted ) {
        this.inverted = inverted;
    }

    public synchronized void detach() {
        getData().detach();
        if ( columnProvider != null )
            columnProvider.detach();
        buffer = null;
    }

    private synchronized List<Ref> getBuffer() {
        if ( buffer == null ) {
            buffer = new ArrayList<Ref>();
            for ( Iterator i = getData().iterator(0,getData().size() ); i.hasNext(); ) {
                final Ref ref = (Ref) i.next();
                if ( isInverted() ) {
                    if ( getFilter().match( ref ) )
                        buffer.add( ref );
                } else {
                    if ( !getFilter().filter( ref ) )
                        buffer.add( ref );
                }
            }
        }
        return buffer;
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getBuffer().subList( first, first+count ).iterator();
    }

    public IModel model( Object object ) {
        return getData().model( object );
    }

    public int size() {
        return getBuffer().size();
    }

    public boolean contains( Ref ref ) {
        return getBuffer().contains( ref );
    }

    public Ref get( int index ) {
        return getBuffer().get( index );
    }

    public List<Class<?>> getAllowedClasses() {
        return null;
    }

    public synchronized ColumnProvider getColumnProvider() {
        if ( columnProvider == null )
            columnProvider = new ColumnProvider( this );

        return columnProvider;
    }

    public void remove( Ref ref ) {
        getData().remove( ref );
        detach();
    }

    public void remove( Referenceable ref ) {
        remove( ref.getReference() );
    }

    public Object getObject() {
        return null;
    }

    public void setObject( Object object ) {
    }

    public void add( Referenceable ref ) {
        getData().add( ref );
        detach();
    }
}
