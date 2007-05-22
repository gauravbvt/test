/**
 * 
 */
package com.mindalliance.zk.beanview;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.AbstractComponent;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.zk.component.ZkArrayFactory;
import com.mindalliance.zk.component.ZkClassOptionsFactory;
import com.mindalliance.zk.component.ZkCollectionFactory;
import com.mindalliance.zk.component.ZkEnumFactory;
import com.mindalliance.zk.component.ZkPrimitiveFactory;

/**
 * A factory that generates a new ZK component based on a particular bean property type.
 */
public class ZkComponentFactory {

    private PropertyComponent settable;

    private BeanView bean;

    protected List<PropertyComponentFactory> factories = new ArrayList<PropertyComponentFactory>();

    protected void installDefaultFactories()
    {
        factories.add(new ZkPrimitiveFactory());
        factories.add(new ZkArrayFactory());
        factories.add(new ZkEnumFactory());
        factories.add(new ZkCollectionFactory());
        factories.add(new ZkClassOptionsFactory());
    }

    /**
     * Creates a new component factory for a particular bean property.
     * @param key The bean property to create the factory for
     * @param type the type of the bean property
     * @param bean the BeanView to associate the new component with
     */
    public ZkComponentFactory(String key, Class type, BeanView bean)
    {
        if (factories.isEmpty())
            installDefaultFactories();

        this.bean = bean;

        for (PropertyComponentFactory factory : factories)
        {
            settable = factory.getComponent(key, type, bean);
            
            if (settable != null) {
            	//((AbstractComponent)settable).setId(key);
                return;
            }
        }
        return;
    }

    /**
     * Returns the component generated by this factory
     * @return a ZK component that implements the PropertyComponent interface
     */
    public PropertyComponent getSettable()
    {
        return settable;
    }
    
    public BeanView getBean()  {
    	return bean;
    }
}
