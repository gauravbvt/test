/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.reference;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * ReferenceData data
 * 
 * @author jf
 */
public class ReferenceData extends AbstractJavaBean implements Named, Described {
    
    private String name;
    private String description;

    public int compareTo( Named o ) {
        return name.compareTo( o.getName() );
    }

    
    /**
     * Return the value of description.
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * Set the value of description.
     * @param description The new value of description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    
    /**
     * Return the value of name.
     */
    public String getName() {
        return name;
    }

    
    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }



}
