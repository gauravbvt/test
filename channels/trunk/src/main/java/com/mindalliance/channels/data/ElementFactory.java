// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.util.GUID;
import com.mindalliance.channels.util.GUIDFactory;

/**
 * A creator of model objects.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 106 $
 */
public class ElementFactory {

    private GUIDFactory guidFactory;

    /**
     * Default constructor.
     */
    public ElementFactory() {
    }

    /**
     * Create a new model object.
     * @param <T> the return type
     * @param elementClass a subclass of AbstractElement
     * @return the new instance
     */
    public <T extends AbstractElement> T newInstance(
            Class<T> elementClass ) {

        try {

            Constructor<T> constructor =
            	elementClass.getDeclaredConstructor(
                        new Class[]{ GUID.class } );

            return constructor.newInstance(
                    new Object[] { getGuidFactory().newGuid() } );

        } catch ( NoSuchMethodException e ) {
            // Will not happen:  constructor implemented in AbstractElement
            // but just in case...
            throw new RuntimeException( e );

        } catch ( IllegalAccessException e ) {
            // Will not happen:  constructor is visible by package
            // but just in case...
            throw new RuntimeException( e );

        } catch ( InstantiationException e ) {
            // When subclass is abstract, or something...
            throw new RuntimeException( e );

        } catch ( InvocationTargetException e ) {
            // When constructor failed
            throw new RuntimeException( e );
        }
    }

    /**
     * Get the GUID factory used for beans created by this factory.
     */
    public final GUIDFactory getGuidFactory() {
        return this.guidFactory;
    }

    /**
     * Set the GUID factory used when creating new model objects.
     * @param guidFactory the GUID factory.
     */
    public void setGuidFactory( GUIDFactory guidFactory ) {
        this.guidFactory = guidFactory;
    }
}
