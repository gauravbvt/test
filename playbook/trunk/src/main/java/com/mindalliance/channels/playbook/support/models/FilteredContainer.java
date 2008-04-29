package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class FilteredContainer extends RefContainer {

    private Container data;
    private Filter filter;
    private boolean strict;

    private transient List<Class<?>> allowedClasses;

    public FilteredContainer( Container data, Filter filter, boolean strict ) {
         this.data = data;
         this.filter = filter;
         this.strict = strict;
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

    public boolean isStrict() {
        return strict;
    }

    public void setStrict( boolean strict ) {
        this.strict = strict;
    }

    public synchronized void detach() {
        super.detach();
        getData().detach();
        setContents( null );
        allowedClasses = null;
    }

    protected synchronized List<Ref> getContents() {
        List<Ref> buffer = super.getContents();
        if ( buffer == null ) {
            buffer = new ArrayList<Ref>();
            for ( Ref ref : getData() ) {
                if ( isStrict() ) {
                    if ( getFilter().match( ref ) )
                        buffer.add( ref );
                } else {
                    if ( !getFilter().filter( ref ) )
                        buffer.add( ref );
                }
            }
            setContents( buffer );
        }
        return buffer;
    }

    public synchronized List<Class<?>> getAllowedClasses() {
        if ( allowedClasses == null ) {
            List<Class<?>> ac = new ArrayList<Class<?>>();
            for ( Class<?> c : getData().getAllowedClasses() )
                if ( getFilter().allowsClass( c ) )
                    ac.add( c );
            allowedClasses = ac;
        }
        return allowedClasses;
    }

    public void remove( Ref ref ) {
        getData().remove( ref );
        detach();
    }

    public void add( Referenceable ref ) {
        getData().add( ref );
        detach();
    }
}
