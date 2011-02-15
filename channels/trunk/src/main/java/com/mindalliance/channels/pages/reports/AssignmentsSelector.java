package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.query.Assignments;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 12:26 PM
 */
public interface AssignmentsSelector {

    /**
     * Get currently selected assignments.
     * @return an assignment wrapper
     */
    Assignments getAssignments();

    /**
      * Get a description of the current selection.
      * @return a spec
      */
    ResourceSpec getSelection();

    Assignments getAllAssignments();

    boolean isPlanner();

    Plan getPlan();

    Organization getOrganization();

    boolean isOrgSelected();

    boolean isActorSelected();
}
