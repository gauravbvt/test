// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Getter method annotation for indicating how to display the
 * name of the property in a tree or graph.
 *
 * The value of the annotation should be a valid argument to
 * MessageFormat.format() with the actual value of the property
 * being used as an argument.
 *
 * @see java.text.MessageFormat#format
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface DisplayAs {

    /**
     * The direct format for a single-valued property
     * (or a collection of one value).
     * For example, this.getOwner() --> "is owned by {0}"
     */
    String direct();

    /**
     * The reverse format for a property.
     * For example, if x.getOwner() == this  --> "owns {0}"
     */
    String reverse();

    /**
     * Format when multiple values for a property are encountered.
     * Defaults to "{0}:" where {0} is the name of the property.
     * Defaults to the name of the property.
     * For example, this.getOwners() --> "owners:".
     */
    String directMany() default "{0}:" ;

    /**
     * Format when multiple values for a reverse property are encountered.
     * Defaults to "reverse {0}:" where {0} is the name of the property.
     * For example, "owned by:".
     */
    String reverseMany() default "{0} for:" ;
}
