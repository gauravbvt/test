package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.pages.filters.Filter
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.pages.filters.UserScope
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.ColumnProvider
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.pages.filters.RootFilter
import com.mindalliance.channels.playbook.support.models.FilteredContainer

/**
* ...
*/
class Tab extends IfmElement implements Container {

    Boolean shared
    Filter filter
    String name = 'Everything'
    Container base
    ColumnProvider columnProvider
    Container buffer

    //---------------------------------
    Tab() {
        this( new UserScope() )
    }

    Tab( Container base ) {
        super()
        this.base = base
    }

    String toString() {
        name
    }

    List transientProperties() {
        super.transientProperties() + [ "columnProvider", "buffer" ]
    }

    //---------------------------------
    public synchronized ColumnProvider getColumnProvider() {
        if ( columnProvider == null )
            columnProvider = new ColumnProvider( this );

        return columnProvider
    }

    public synchronized Filter getFilter() {
        if ( filter == null )
            filter = new RootFilter( base );

        return filter
    }

    public synchronized Container getBuffer() {
        if ( buffer == null )
            buffer = new FilteredContainer( base, getFilter() )

        return buffer
    }

    public synchronized void detach() {
        super.detach()
        if ( filter != null )
            filter.setInvalid( true )

        buffer.detach()
    }

    //---------------------------------
    public boolean contains(Ref ref) {
        return getBuffer().contains( ref );
    }

    public Ref get(int index) {
        return getBuffer().get( index );
    }

    public List<Class<?>> getAllowedClasses() {
        return getBuffer().getAllowedClasses();
    }

    public Object getObject() {
        return base instanceof Tab ? base.getObject() : base ;
    }

    public void setObject(Object object) {
        if ( base instanceof Tab )
            base.setObject( object )
        else
            setBase( (Container) object );
        detach();
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getBuffer().iterator( first, count );
    }

    public IModel model( Object object ) {
        return base.model( object );
    }

    public void add( Referenceable ref ) {
        getBuffer().add( ref );
        detach()
    }

    public void remove(Ref ref) {
        base.remove( ref )
        detach()
    }

    public void remove(Referenceable ref) {
        base.remove( ref )
        detach()
    }

    public int size() {
        return getBuffer().size();
    }
}