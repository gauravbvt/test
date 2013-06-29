package com.mindalliance.playbook.model;

import java.util.Date;

/**
 * Object that has a last-modified timestamp property and accessors for it.
 * This is used in conjunction with Hibernate's entity listener for automatically adjusting it.
 */
public interface Timestamped {
    
    Date getLastModified();
    
    void setLastModified( Date date );

}
