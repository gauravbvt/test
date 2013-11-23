package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;

import java.util.List;

/**
 * A strategy for detecting issues on a model object
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:44:36 AM
 */
public interface IssueDetector {

    /**
     * The detector's unique kind.
     * @return a string
     */
    String getKind();

    /**
     * Detect an issue on a model object
     *
     *
     * @param communityService a community service service
     * @param identifiable -- the identifiable being analyzed
     * @return a list of Issues
     */
    List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable );

    /**
     * Tests whether the detector applies to the model object
     *
     * @param identifiable -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    boolean appliesTo( Identifiable identifiable );

    /**
     * Tests whether the detector applies to the naed property of the model object
     *
     * @param identifiable -- the ModelObject being analyzed
     * @param property    -- the name of a property of the model object
     * @return whether the detector applies
     */
    boolean appliesTo( Identifiable identifiable, String property );

    /**
     * Gets the name of the specific property tested, if applicable
     *
     * @return the name of a property or null if test applies to some combination of properties
     */
    String getTestedProperty();

    /**
     * Whether issues detected by this detectro can be waived.
     * @return a boolean
     */
    boolean canBeWaived();

    /**
     * Whether the detector is for a specific property.
     *
     * @return a boolean
     */
    boolean isPropertySpecific();

    /**
     * Test if this detector applies to an object.
     * @param identifiable the object
     * @param waived true if waiving was applied
     * @param propertySpecific  true if specific to a property
     * @return true if detector is applicable
     */
    boolean isApplicable( Identifiable identifiable, boolean waived, boolean propertySpecific );
}
