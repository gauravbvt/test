package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.Channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:50 PM
 */
@Entity
public class RFI extends AbstractPersistentPlanObject {

    public static final RFI UNKNOWN = new RFI( Channels.UNKNOWN_RFI_ID );


    @ManyToOne
    private RFISurvey rfiSurvey;

    /**
     * username of user being questioned.
     */
    private String surveyedUsername;

    // Employment = organization and role ids, and title
    private String title;
    private Long organizationId;
    private Long roleId;

    private Date deadline;
    private Date nagged;
    private boolean declined = false;

    private String reasonDeclined = "";

    private boolean naggingRequested = false;

    @OneToMany( mappedBy = "rfi", cascade = CascadeType.ALL )
    private List<RFIForward> forwards = new ArrayList<RFIForward>();

    @OneToMany( mappedBy = "rfi", cascade = CascadeType.ALL )
    private List<AnswerSet> answerSets = new ArrayList<AnswerSet>();

    public RFI() {
    }

    public RFI( long id ) {
        this.id = id;
    }

    public RFI( String username, String planUri, int planVersion ) {
        super( planUri, planVersion, username );
    }

    public RFI( String username, String planUri, int planVersion, String surveyedUsername, Employment employment ) {
        this( username, planUri, planVersion );
        this.surveyedUsername = surveyedUsername;
        title = employment.getTitle();
        organizationId = employment.getOrganization().getId();
        roleId = employment.getRole().getId();
    }

    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public String getSurveyedUsername() {
        return surveyedUsername;
    }

    public void setSurveyedUsername( String surveyedUsername ) {
        this.surveyedUsername = surveyedUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId( Long organizationId ) {
        this.organizationId = organizationId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId( Long roleId ) {
        this.roleId = roleId;
    }

    public RFISurvey getRfiSurvey() {
        return rfiSurvey;
    }

    public void setRfiSurvey( RFISurvey rfiSurvey ) {
        this.rfiSurvey = rfiSurvey;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined( boolean declined ) {
        this.declined = declined;
    }

    public String getReasonDeclined() {
        return reasonDeclined;
    }

    public void setReasonDeclined( String reasonDeclined ) {
        this.reasonDeclined = reasonDeclined;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline( Date deadline ) {
        this.deadline = deadline;
    }

    public boolean isNaggingRequested() {
        return naggingRequested;
    }

    public void setNaggingRequested( boolean naggingRequested ) {
        this.naggingRequested = naggingRequested;
    }

    public Date getNagged() {
        return nagged;
    }

    public void setNagged( Date nagged ) {
        this.nagged = nagged;
    }

    public List<RFIForward> getForwards() {
        return forwards;
    }

    public void setForwards( List<RFIForward> forwards ) {
        this.forwards = forwards == null ? new ArrayList<RFIForward>() : forwards;
    }

    public void addForwarding( RFIForward forwarding ) {
        getForwards().add( forwarding );
    }

    public List<AnswerSet> getAnswerSets() {
        return answerSets;
    }

    public void setAnswerSets( List<AnswerSet> answerSets ) {
        this.answerSets = answerSets;
    }

    public boolean isLate( QueryService queryService ) {
        return !isDeclined()
                && getDeadline() != null
                && new Date().after( getDeadline() )
                && getRfiSurvey().isOngoing( queryService );
    }
}
