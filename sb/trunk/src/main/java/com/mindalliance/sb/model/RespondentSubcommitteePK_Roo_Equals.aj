// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.RespondentSubcommitteePK;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect RespondentSubcommitteePK_Roo_Equals {
    
    public boolean RespondentSubcommitteePK.equals(Object obj) {
        if (!(obj instanceof RespondentSubcommitteePK)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        RespondentSubcommitteePK rhs = (RespondentSubcommitteePK) obj;
        return new EqualsBuilder().append(respondent, rhs.respondent).append(subcommittee, rhs.subcommittee).isEquals();
    }
    
    public int RespondentSubcommitteePK.hashCode() {
        return new HashCodeBuilder().append(respondent).append(subcommittee).toHashCode();
    }
    
}