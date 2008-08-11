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

/**
* ...
*/
class Tab extends IfmElement implements Container, Named, Described {
    private static final long serialVersionUID = -83012746444045817L;

    Boolean shared
    Filter filter
    String name = 'Everything'
    String description = ''
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

    Map<String,Object> toMap() {
        Map map = super.toMap()
        buffer = null
        return map
    }

    public void initFromMap(Map<String,Object> map) {
        super.initFromMap(map)
        filter.container = base
        filter.invalidate();

    }

    List transientProperties() {
        super.transientProperties() + [ "summary", "buffer", "allowedClasses", "object" ] as List
    }

    public Set hiddenProperties() {
        Set result = super.hiddenProperties()
        result.add( "summary" )
        result.add( "object" )
        return result
    }

    //---------------------------------
    public synchronized Filter getFilter() {
        if ( filter == null )
            setFilter( new RootFilter( base ) );

        return filter
    }

    public synchronized void setFilter( Filter filter ) {
        this.filter = filter;
        filter.container = base;
        buffer = null;
    }

    public synchronized Container getBuffer() {
        if ( buffer == null ) {
            buffer = new FilteredContainer( base, getFilter() )
        }

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
        return getBuffer().summary;
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

    public List<Class<? extends Referenceable>> getAllowedClasses() {
        return getBuffer().allowedClasses;
    }

    public Object getObject() {
        return base instanceof Tab ? base.object : base ;
    }

    public void setObject(Object object) {
        if ( base instanceof Tab ) {
            base.object = object
        } else
            setBase( (Container) object );
        detach();
    }

    public Iterator<? extends Ref> iterator( int first, int count ) {
        return getBuffer().iterator( first, count );
    }

    public Iterator<Ref> iterator() {
        return getBuffer().iterator();
    }

    public IModel<Ref> model( Ref object ) {
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