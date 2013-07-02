// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentMissionAreaPK;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect IncidentMissionAreaPK_Roo_Equals {
    
    public boolean IncidentMissionAreaPK.equals(Object obj) {
        if (!(obj instanceof IncidentMissionAreaPK)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        IncidentMissionAreaPK rhs = (IncidentMissionAreaPK) obj;
        return new EqualsBuilder().append(incident, rhs.incident).append(missionArea, rhs.missionArea).append(organization, rhs.organization).append(respondent, rhs.respondent).isEquals();
    }
    
    public int IncidentMissionAreaPK.hashCode() {
        return new HashCodeBuilder().append(incident).append(missionArea).append(organization).append(respondent).toHashCode();
    }
    
}