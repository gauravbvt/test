package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.imaging.ImagingService;

import java.util.List;

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

    AttachmentManager getAttachmentManager();

    QueryService getQueryService();

    ImagingService getImagingService();

    Specable getActor();

    Specable getFocusEntity();

    Flow getFlow();

    Segment getSegment();

    Assignments getSources( Part part );

    List<Commitment> getCommitments();

    List<Commitment> getCommitmentsTriggering( Part part );
}
