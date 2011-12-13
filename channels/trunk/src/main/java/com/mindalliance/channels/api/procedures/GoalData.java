package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Goal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for a goal.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/7/11
 * Time: 7:20 PM
 */
@XmlRootElement( name = "goal", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"kind", "name", "description", "category", "level", "organizationId"} )
public class GoalData {

    private Goal goal;

    public GoalData() {
        // required
    }

    public GoalData( Goal goal ) {
        this.goal = goal;
    }

    @XmlElement
    public String getName() {
        return goal.getName().isEmpty()
                ? null
                : goal.getName();
    }

    @XmlElement
    public String getKind() {
        return goal.isRiskMitigation()
                ? "mitigate risk"
                : "achieve goal";
    }

    @XmlElement
    public String getDescription() {
        return goal.getDescription();
    }

    @XmlElement
    public String getCategory() {
        return goal.getCategoryLabel();
    }

    @XmlElement
    public String getLevel() {
        return goal.getLevelLabel();
    }

    @XmlElement
    public Long getOrganizationId() {
        return goal.getOrganization().getId();
    }
}
