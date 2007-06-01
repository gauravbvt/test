// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark data model collections with the type of their contents.
 * BeanView isn't able to figure this out on it's own.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.METHOD} )
public @interface CollectionType {
    /**
     * The type to expect the annotated collection to contain.
     * @return the type
     */
    Class type();
}
