package com.mindalliance.channels.surveys;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Unknown survey.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/15/11
 * Time: 10:11 AM
 */
public class UnknownSurvey extends Survey {

    /**
     * Unknown survey id.
     */
    public static final long UNKNOWN_ID = Long.MIN_VALUE;

    private static UnknownSurvey INSTANCE;

    private UnknownSurvey() {
        setId( UNKNOWN_ID );
    }

    public static UnknownSurvey getInstance() {
        if ( INSTANCE == null )
            INSTANCE = new UnknownSurvey();
        return INSTANCE;
    }

    public boolean isUnknown() {
        return true;
    }

    @Override
    public Identifiable findIdentifiable( Analyst analyst ) throws NotFoundException {
        return null;
    }

    @Override
    public boolean matches( Type type, Identifiable identifiable ) {
        return type == Survey.Type.Unknown;
    }

    @Override
    public String getInvitationTemplate() {
        return null;
    }

    @Override
    public String getSurveyTemplate() {
        return null;
    }

    @Override
    protected List<String> getDefaultContacts( Analyst analyst ) {
        return new ArrayList<String>();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected String getIdentifiableSpecs() {
        return "";
    }

    @Override
    protected void setIdentifiableSpecs( String specs ) {
        // do nothing
    }

    @Override
    public Identifiable getAbout( Analyst analyst ) {
        return null;
    }

    @Override
    public String getSurveyType() {
        return "unknown";
    }
}
