// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.CategorySet;
import com.mindalliance.channels.data.definitions.TypedObject;
import com.mindalliance.channels.data.definitions.Typology;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.GUIDFactory;

/**
 * A creator of elements.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 106 $
 */
public class ElementFactory {

    private GUIDFactory guidFactory;
    private Typology typology;

    /**
     * Default constructor.
     */
    public ElementFactory() {
    }

    /**
     * Create a new model object.
     *
     * @param <T> the return type
     * @param elementClass a subclass of TypedObject
     * @return the new instance
     */
    public <T extends TypedObject> T newInstance( Class<T> elementClass ) {

        try {

            Constructor<T> constructor =
                elementClass.getDeclaredConstructor(
                    new Class[] { GUID.class } );

            return constructor.newInstance(
                    new Object[] { getGuidFactory().newGuid() } );

        } catch ( NoSuchMethodException e ) {
            // Will not happen: constructor implemented in
            // InferableObject but just in case...
            throw new RuntimeException( e );

        } catch ( IllegalAccessException e ) {
            // Will not happen: constructor is visible by package
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
     * Create a new instance with a typeset of given category.
     * @param <T> the return type
     * @param elementClass a subclass of TypedObject
     * @param categoryGroup the category of the typeset
     */
    public <T extends TypedObject> T newInstance(
            Class<T> elementClass, Taxonomy categoryGroup ) {

        T result = newInstance( elementClass );
        CategorySet ts = new CategorySet( categoryGroup );
        result.setCategorySet( ts );

        return result;
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

    /**
     * Return the value of typology.
     */
    public Typology getTypology() {
        return this.typology;
    }

    /**
     * Set the value of typology.
     * @param typology The new value of typology
     */
    public void setTypology( Typology typology ) {
        this.typology = typology;
    }
}
