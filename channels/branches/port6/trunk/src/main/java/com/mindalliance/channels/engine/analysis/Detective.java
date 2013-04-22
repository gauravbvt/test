package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Issue detection service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 25, 2009
 * Time: 10:22:27 AM
 */
public interface Detective {

    /**
     * Detect all waived issues on a model object .
     *
     * @param queryService
     * @param modelObject a model object
     * @param propertySpecific - whether issues are property specific
     * @return a list of issues
     */
    List<? extends Issue> detectWaivedIssues( QueryService queryService, ModelObject modelObject, Boolean propertySpecific );

    /**
      * Detect all waived issues on a model object .
      *
     * @param queryService
    * @param modelObject a model object
   * @param propertySpecific - whether issues are property specific
    * @return a list of issues
      */
    List<? extends Issue> detectUnwaivedIssues( QueryService queryService, ModelObject modelObject, Boolean propertySpecific );

    /**
     * Detect all waived property issues on a model object .
     *
     * @param queryService
     * @param modelObject a model object
     * @param property a string - issues for this property if given
     * @return a list of issues
     */
    List<? extends Issue> detectWaivedPropertyIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
      * Detect all waived property issues on a model object .
      *
     * @param queryService
   * @param modelObject a model object
   * @param property a string - issues for this property if given
    * @return a list of issues
      */
    List<? extends Issue> detectUnwaivedPropertyIssues( QueryService queryService, ModelObject modelObject, String property );

}
