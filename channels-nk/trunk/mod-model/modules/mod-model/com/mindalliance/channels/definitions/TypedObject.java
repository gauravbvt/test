// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.GUID;

/**
 * A reference data with a type.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - 1 CategorySet
 */
public abstract class TypedObject extends NamedObject
    implements Typed {

    private CategorySet categorySet;

    /**
     * Default constructor.
     */
    public TypedObject() {
        this( null, Taxonomy.Any );
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public TypedObject( GUID guid ) {
        this( guid, Taxonomy.Any );
    }

    /**
     * Create a typed object of the given taxonomy.
     * @param guid the guid
     * @param taxonomy the taxonomy
     */
    public TypedObject( GUID guid, Taxonomy taxonomy ) {
        super( guid );
        setCategorySet( new CategorySet( taxonomy ) );
    }

    /**
     * Return the value of categorySet.
     */
    @DisplayAs( direct = "categories:",
            reverse = "category set for {1}",
            reverseMany = "category set for:"
            )
    public CategorySet getCategorySet() {
        return categorySet;
    }

    /**
     * Set the value of categorySet.
     * @param categorySet The new value of categorySet
     */
    public void setCategorySet( CategorySet categorySet ) {
        this.categorySet = categorySet;
    }
}
