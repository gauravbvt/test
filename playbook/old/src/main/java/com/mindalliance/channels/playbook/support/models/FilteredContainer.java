package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.UserScope;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.Mapper;
import org.apache.wicket.model.IDetachable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
public class FilteredContainer extends RefContainer {

    private Container data;
    private Filter filter;
    private boolean strict;

    private static final long serialVersionUID = 2029951316750602859L;

    public FilteredContainer() {
    }

    public FilteredContainer( Container data, Filter filter, boolean strict ) {
         this();
         this.data = data;
         this.filter = filter;
         filter.setContainer( data );
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

    /**
     * If true, the filter will be applied without consulting its
     * subfilters.
     * @param strict the strictness of the filter...
     */
    public void setStrict( boolean strict ) {
        this.strict = strict;
    }

    @Override
    public synchronized void detach() {
        super.detach();
        setContents( null );
        IDetachable d = getData();
        if ( !( d instanceof UserScope ) )
            d.detach();
    }

    @Override
    protected synchronized List<Ref> getContents() {
        List<Ref> buffer = super.getContents();
        if ( buffer == null ) {
            buffer = new ArrayList<Ref>();
            for ( Ref ref : getData() ) {
                  if ( isStrict() ) {
                    if ( getFilter().isMatching( ref ) && getFilter().isIncluding( ref ) ) {
                        buffer.add( ref );
                    }
                } else if ( getFilter().isApplicableTo( ref ) )
                    buffer.add( ref );
            }
            setContents( buffer );
        }
        return buffer;
    }

    @Override
    public List<Class<? extends Referenceable>> getAllowedClasses() {
        return getData().getAllowedClasses();
    }

    @Override
    public void remove( Ref ref ) {
        getData().remove( ref );
        setContents( null );
    }

    @Override
    public void add( Referenceable ref ) {
        getData().add( ref );
        setContents( null );
    }

    @Override
    public Map<String,Object> toMap() {
        Map<String,Object> result = super.toMap();

        result.put( "strict", isStrict() );
        result.put( "data", (Object) Mapper.toPersistedValue( getData() ) );
        result.put( "filter", (Object) Mapper.toPersistedValue( filter ) );

        return result;
    }

    @Override
    public void initFromMap( Map<String,Object> map ) {
        super.initFromMap( map );

        strict = (Boolean) map.get( "strict" );
        data = (Container) Mapper.valueFromPersisted( map.get( "data" ) );
        filter = (Filter) Mapper.valueFromPersisted( map.get( "filter" ) );
        filter.setContainer( data );
    }

    @Override
    public String toString() {
        return "filtered data";
    }
}
