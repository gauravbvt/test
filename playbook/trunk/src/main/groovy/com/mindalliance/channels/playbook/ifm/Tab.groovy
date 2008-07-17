package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.pages.filters.UserScope
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.ContainerSummary
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.pages.filters.RootFilter
import com.mindalliance.channels.playbook.support.models.FilteredContainer
import com.mindalliance.channels.playbook.pages.filters.Filter
import com.mindalliance.channels.playbook.support.models.ContainerSummary

/**
* ...
*/
class Tab extends IfmElement implements Container {

    Boolean shared
    Filter filter
    String name = 'Everything'
    Container base
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

    Map toMap() {
        Map map = super.toMap()
        buffer = null
        return map
    }

    public void initFromMap(Map map) {
        super.initFromMap(map)
        filter.container = base
        filter.invalidate();

    }

    List transientProperties() {
        super.transientProperties() + [ "summary", "buffer", "allowedClasses", "object" ]
    }

    //---------------------------------
    public synchronized Filter getFilter() {
        if ( filter == null )
            setFilter( new RootFilter( base ) );

        return filter
    }

    public synchronized void setFilter( Filter filter ) {
        this.filter = filter;
        filter.setContainer( base );
        buffer = null;
    }

    public synchronized Container getBuffer() {
        if ( buffer == null )
            buffer = new FilteredContainer( base, getFilter() )

        return buffer
    }

    public synchronized void detach() {
        if ( buffer != null ) {
            super.detach();
            getFilter().invalidate();
            base.detach();
            buffer = null
        }
    }

    //---------------------------------
    public ContainerSummary getSummary() {
        return getBuffer().getSummary();
    }

    public boolean contains(Ref ref) {
        return getBuffer().contains( ref );
    }

    public Ref get(int index) {
        return getBuffer().get( index );
    }

    public int indexOf(Ref ref) {
        return getBuffer().indexOf( ref );
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

    public Iterator<Ref> iterator() {
        return getBuffer().iterator();
    }

    public IModel model( Object object ) {
        return base.model( object );
    }

    public void add( Referenceable ref ) {
        base.add( ref );
        detach();
    }

    public void remove(Ref ref) {
        base.remove( ref )
        detach();
    }

    public void remove(Referenceable ref) {
        base.remove( ref )
        detach();
    }

    public int size() {
        return getBuffer().size();
    }
}