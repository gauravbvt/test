package com.mindalliance.channels;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;

import java.util.List;

/**
 * Issue detection service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 25, 2009
 * Time: 10:22:27 AM
 */
public interface Detective extends Service {

    /**
     * Detect all waived issues on a model object .
     * @param modelObject a model object
     * @param propertySpecific - whether issues are property specific
     * @return a list of issues
     */
    List<Issue> detectWaivedIssues(
            ModelObject modelObject,
            Boolean propertySpecific );

    /**
      * Detect all waived issues on a model object .
      * @param modelObject a model object
     * @param propertySpecific - whether issues are property specific
      * @return a list of issues
      */
    List<Issue> detectUnwaivedIssues(
            ModelObject modelObject,
            Boolean propertySpecific );

    /**
     * Detect all waived property issues on a model object .
     * @param modelObject a model object
     * @param property a string - issues for this property if given
     * @return a list of issues
     */
    List<Issue> detectWaivedPropertyIssues(
            ModelObject modelObject,
            String property );

    /**
      * Detect all waived property issues on a model object .
      * @param modelObject a model object
      * @param property a string - issues for this property if given
       * @return a list of issues
      */
    List<Issue> detectUnwaivedPropertyIssues(
            ModelObject modelObject,
            String property);

}
