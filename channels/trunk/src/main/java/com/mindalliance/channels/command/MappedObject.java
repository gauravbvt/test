package com.mindalliance.channels.command;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.ModelObject;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * An object with model object references converted to a safely serializable map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2009
 * Time: 8:06:57 PM
 */
public class MappedObject implements Serializable {
    /**
     * The class of the mapped object.
     */
    private String className;
    /**
     * Map representation.
     */
    private Map<String,Object> map = new HashMap<String,Object>();

    public MappedObject( Class clazz ) {
        className = clazz.getName();
    }

    /**
     * Convert property to a safely serializable map entry.
     * @param name a string
     * @param value an object
     */
    public void set( String name, Object value ) {
        if ( value instanceof ModelObject ) {
            map.put( name, new ModelObjectRef( (ModelObject) value));
        } else {
            map.put( name, value );
        }
    }

    /**
     * Reconstitute mapped object.
     * @param commander a commander
     * @return an object
     * @throws CommandException if conversion from map fails
     */
    public Object fromMap( Commander commander ) throws CommandException {
        try {
            Object object = Class.forName( className ).newInstance();
            for (String name : map.keySet() ) {
                Object value = map.get(name);
                if ( value instanceof ModelObjectRef ) {
                    PropertyUtils.setSimpleProperty( object, name,  ((ModelObjectRef)value).resolve( commander ));
                } else {
                    PropertyUtils.setSimpleProperty( object, name, value );
                }
            }
            return object;
        } catch ( ClassNotFoundException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( IllegalAccessException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( InvocationTargetException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( NoSuchMethodException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( NotFoundException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( CommandException e ) {
            throw new CommandException("Failed to convert from map.", e);
        } catch ( InstantiationException e ) {
            throw new CommandException("Failed to convert from map.", e);
        }
    }
}
