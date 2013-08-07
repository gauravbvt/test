package com.mindalliance.sb.model;

/**                   
 * Interface for object uniquely identified by their name.
 * Used in HTML and CSV formatting.
 */
public interface NamedObject {

    /**
     * @return a unique name identifying this object.
     */
    String getName();

}
