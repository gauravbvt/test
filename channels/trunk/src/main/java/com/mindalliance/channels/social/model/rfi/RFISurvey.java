package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.social.model.AbstractModelObjectReferencingPPO;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * A survey about a model object and based on a questionnaire.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 10:50 AM
 */
@Entity
public class RFISurvey extends AbstractModelObjectReferencingPPO {

    public static final RFISurvey UNKNOWN = new RFISurvey( Channels.UNKNOWN_RFI_SURVEY_ID );
    private static final String DELETED = "(DELETED)";

    private Questionnaire questionnaire;
    private boolean closed = false;
    private Date deadline;
    @OneToMany( mappedBy="rfiSurvey", cascade = CascadeType.ALL )
    private List<RFI> rfis;

    public RFISurvey() {
    }

    public RFISurvey( String uri, int version, String username ) {
        super( uri, version, username );
    }

    public RFISurvey( Long id ) {
        this.id = id;
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        this.questionnaire = questionnaire;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        this.closed = closed;
    }

    public List<RFI> getRfis() {
        return rfis;
    }

    public void setRfis( List<RFI> rfis ) {
        this.rfis = rfis;
    }

    public boolean isObsolete( QueryService queryService) {
        return getModelObject( queryService ) == null;
    }
    
    public boolean isActive( QueryService queryService ) {
        return !isClosed() && !isObsolete( queryService );
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline( Date deadline ) {
        this.deadline = deadline;
    }

    public String getStatusLabel( QueryService queryService ) {
        return isClosed()
                ? "Closed"
                : isObsolete( queryService )
                ? "Obsolete"
                : "Open";
                
    }

    public String getLabel( QueryService queryService ) {
        StringBuilder sb = new StringBuilder(  );
        sb
                .append( isClosed() ? "Closed survey about "  : "Survey about ")
                .append( getTypeName()  )
                .append( " \"" )
                .append( getModelObjectName( queryService ) )
                .append( "\": " )
                .append(  getQuestionnaire().getName() );
        return sb.toString();
    }

    private String getModelObjectName( QueryService queryService ) {
        ModelObject mo = getModelObject( queryService );
        return mo == null 
                ? DELETED
                : mo.getName();
    }

}
