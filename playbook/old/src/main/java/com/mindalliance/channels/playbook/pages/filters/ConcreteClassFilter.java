package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
public class ConcreteClassFilter extends Filter {

    private static final long serialVersionUID = 6961268344121784333L;

    private Class<?> clazz;

    public ConcreteClassFilter() {
    }

    public ConcreteClassFilter( Class<?> clazz ) {
        super( "Actual "
            + RefUtils.pluralize(
                ContainerSummary.toDisplay(
                    clazz.getSimpleName() ).toLowerCase() ) );
        this.clazz = clazz;
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMatching( Ref object ) {
        Referenceable referenceable = object.deref();
        return referenceable != null && clazz.equals( referenceable.getClass() );
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return clazz.equals( c );
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz( Class<?> clazz ) {
        this.clazz = clazz;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = super.toMap();
        result.put( "class", getClazz().getName() );
        return result;
    }

    @Override
    public void initFromMap( Map<String, Object> map ) {
        super.initFromMap( map );
        String name = (String) map.get( "class" );
        try {
            setClazz( Class.forName( name ) );
        } catch ( ClassNotFoundException e ) {
            LoggerFactory.getLogger( getClass() )
                    .error( "Unable to reload class " + name, e );
        }
    }
}
