package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.pages.filters.Filter
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.pages.filters.UserScope
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.ColumnProvider
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.support.models.RefModel

/**
* ...
*/
class Tab extends IfmElement implements Container {

    Boolean shared
    Filter filter
    String name = 'Everything'
    Container base
    ColumnProvider columnProvider = new ColumnProvider( this )

    Tab() {
        this.base = new UserScope()
    }

    String toString() {
        name
    }

    public void add(Referenceable ref) {
        base.add( ref );
    }

    public boolean contains(Ref ref) {
        // TODO complete this
        return base.contains( ref );
    }

    public Ref get(int index) {
        // TODO complete this
        return base.get( index );
    }

    public List<Class<?>> getAllowedClasses() {
        // TODO complete this
        return base.getAllowedClasses();
    }

    public Object getObject() {
        return base instanceof Tab ? base.getObject() : base ;
    }

    public void setObject(Object object) {
        if ( base instanceof Tab )
            base.setObject( object )
        else
            setBase( (Container) object );
    }

    public Iterator<Ref> iterator(int first, int count) {
        // TODO complete this
        return base.iterator( first, count );
    }

    public IModel model(Object object) {
        return new RefModel( object );
    }

    public void remove(Ref ref) {
        base.remove( ref )
    }

    public void remove(Referenceable ref) {
        base.remove( ref )
    }

    public int size() {
        // TODO complete this
        return base.size();
    }


}