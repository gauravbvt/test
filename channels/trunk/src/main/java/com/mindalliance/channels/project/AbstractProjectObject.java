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
 */
public abstract class AbstractProjectObject extends AbstractJavaBean {

    private Project project;

    /**
     * Default constructor.
     */
    public AbstractProjectObject() {
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
