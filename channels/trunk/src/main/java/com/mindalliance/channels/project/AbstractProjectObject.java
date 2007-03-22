// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.project;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.Project;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * An object local to a project.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 */
public abstract class AbstractProjectObject
    extends AbstractJavaBean {

    private Project project;

    /**
     * Default constructor.
     * Note: this should only be used by the infrastructure.
     */
    public AbstractProjectObject() {
        super();
    }

    /**
     * Default constructor.
     * @param project the project of this object
     */
    public AbstractProjectObject( Project project ) {
        this();
        setProject( project );
    }

    /**
     * Return the value of project.
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Set the value of project.
     * @param project The new value of project
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setProject( Project project ) {
        this.project = project;
    }
}
