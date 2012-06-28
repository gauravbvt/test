package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Goal;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web Service data element for a goal.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/7/11
 * Time: 7:20 PM
 */
@XmlType( propOrder = {"kind", "name", "description", "category", "level", "organizationId"} )
public class GoalData  implements Serializable {

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
                : StringEscapeUtils.escapeXml( goal.getName() );
    }

    @XmlElement
    public String getKind() {
        return goal.isRiskMitigation()
                ? "mitigate risk"
                : "achieve goal";
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( goal.getDescription() );
    }

    @XmlElement
    public String getCategory() {
        return StringEscapeUtils.escapeXml( goal.getCategoryLabel() );
    }

    @XmlElement
    public String getLevel() {
        return StringEscapeUtils.escapeXml( goal.getLevelLabel() );
    }

    @XmlElement
    public Long getOrganizationId() {
        return goal.getOrganization().getId();
    }

    public String getLabel() {
        return goal.getLabel();
    }
}
