package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.social.model.AbstractModelObjectReferencingPPO;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.Messageable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
public class RFISurvey extends AbstractModelObjectReferencingPPO implements Messageable {

    public static final RFISurvey UNKNOWN = new RFISurvey( Channels.UNKNOWN_RFI_SURVEY_ID );
    private static final String DELETED = "(DELETED)";
    public static final String STATUS = "status";
    @ManyToOne
    private Questionnaire questionnaire;
    private boolean closed = false;
    private boolean canBeForwarded = true;
    private Date deadline;
    // Questionnaire's about.
    private String about;
    @OneToMany( mappedBy = "rfiSurvey", cascade = CascadeType.ALL )
    private List<RFI> rfis;

    public RFISurvey() {
    }

    public RFISurvey( Plan plan, String username ) {
        super( plan.getUri(), plan.getVersion(), username );
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
        setAbout( questionnaire.getAbout() );
    }

    public String getAbout() {
        return about;
    }

    public void setAbout( String about ) {
        this.about = about;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        this.closed = closed;
    }

    public boolean isCanBeForwarded() {
        return canBeForwarded;
    }

    public void setCanBeForwarded( boolean canBeForwarded ) {
        this.canBeForwarded = canBeForwarded;
    }

    public List<RFI> getRfis() {
        return rfis;
    }

    public void setRfis( List<RFI> rfis ) {
        this.rfis = rfis;
    }


    public boolean isOngoing( QueryService queryService, Analyst analyst ) {
        return !isClosed() && !isObsolete( queryService, analyst );
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline( Date deadline ) {
        this.deadline = deadline;
    }

    public String getStatusLabel( QueryService queryService, Analyst analyst ) {
        return isClosed()
                ? "Closed"
                : isObsolete( queryService, analyst )
                ? "Obsolete"
                : "Ongoing";

    }

    public String getLabel( QueryService queryService ) {
        StringBuilder sb = new StringBuilder();
        sb
                .append( isClosed() ? "Closed survey on " : "Survey on " )
                .append( getQuestionnaire().getName() );
        if ( !getQuestionnaire().isIssueRemediation() ) {
            sb.append( ", about " )
                    .append( getMoLabel() );
        }
        return sb.toString();
    }

    private String getModelObjectName( QueryService queryService ) {
        ModelObject mo = getModelObject( queryService );
        return mo == null
                ? DELETED
                : mo.getName();
    }

    public boolean isObsolete( QueryService queryService, Analyst analyst ) {
        return !getQuestionnaire().isActive()
                || getModelObject( queryService ) == null
                || getQuestionnaire().isObsolete( queryService, analyst );
    }

    @Override
    public String getName() {
        return getQuestionnaire().getName();
    }

    /// Messageable


    @Override
    public String getToUsername( String topic ) {
        return topic.equals( STATUS )
                ? ChannelsUserInfo.PLANNERS
                : null;
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( STATUS ) ) {
            return getStatusContent( format, planService, surveysDAO );
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    private String getStatusContent( Format format, PlanService planService, SurveysDAO surveysDAO ) {
        return null; // todo
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        return null;
    }
}
