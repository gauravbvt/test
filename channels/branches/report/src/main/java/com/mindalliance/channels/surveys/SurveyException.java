package com.mindalliance.channels.surveys;

/**
 * A survey service exception.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 11:09:36 AM
 */
public class SurveyException extends Exception {

    public SurveyException() {
    }

    public SurveyException( String message ) {
        super( message );
    }

    public SurveyException( String message, Throwable cause ) {
        super( message, cause );
    }

    public SurveyException( Throwable cause ) {
        super( cause );
    }
}
