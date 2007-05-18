/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data;

/**
 * A contactable resource that controls access to itself.
 * 
 * @author jf
 */
public interface Accessible {

    boolean hasAccess( Contactable contactable );

}
