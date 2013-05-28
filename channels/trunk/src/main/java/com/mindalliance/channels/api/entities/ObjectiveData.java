package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Objective;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/28/13
 * Time: 9:01 AM
 */
@XmlType( propOrder = {"kind", "goalCategory"} )
public class ObjectiveData implements Serializable {

    public final static String RISK_MITIGATED = "risk mitigated";
    public final static String GAIN_REALIZED = "gain realized";

    private Objective objective;

    public ObjectiveData() {
    }

    public ObjectiveData( String serverUrl, Objective objective, CommunityService communityService ) {
        this.objective = objective;
    }

    @XmlElement
    public String getGoalCategory() {
        return objective.getGoalCategory().getLabel( objective.isPositive() );
    }

    @XmlElement
    public String getKind() {
        return objective.isPositive()
                ? GAIN_REALIZED
                : RISK_MITIGATED;
    }
}
