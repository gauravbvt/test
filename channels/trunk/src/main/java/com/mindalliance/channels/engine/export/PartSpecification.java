package com.mindalliance.channels.engine.export;


import java.io.Serializable;

/**
 * Specification of a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2009
 * Time: 7:29:43 PM
 */
public class PartSpecification implements Serializable {

    private String id;
    private String roleName;
    private String task;
    private String taskDescription;
    private String organizationName;

    public PartSpecification(
            String id,
            String task,
            String taskDescription,
            String roleName,
            String organizationName ) {
        this.id = id;
        this.task = task;
        this.taskDescription = taskDescription;
        this.roleName = roleName;
        this.organizationName = organizationName;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName ) {
        this.roleName = roleName;
    }

    public String getTask() {
        return task;
    }

    public void setTask( String task ) {
        this.task = task;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription( String taskDescription ) {
        this.taskDescription = taskDescription;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName( String organizationName ) {
        this.organizationName = organizationName;
    }
}
