// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Incident;
import com.mindalliance.sb.model.OrganizationIncident;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

privileged aspect Incident_Roo_DbManaged {
    
    @OneToMany(mappedBy = "incident", cascade = CascadeType.REMOVE)
    private Set<OrganizationIncident> Incident.organizationIncidents;
    
    @Column(name = "name", columnDefinition = "VARCHAR", length = 127, unique = true)
    @NotNull
    private String Incident.name;
    
    public Set<OrganizationIncident> Incident.getOrganizationIncidents() {
        return organizationIncidents;
    }
    
    public void Incident.setOrganizationIncidents(Set<OrganizationIncident> organizationIncidents) {
        this.organizationIncidents = organizationIncidents;
    }
    
    public String Incident.getName() {
        return name;
    }
    
    public void Incident.setName(String name) {
        this.name = name;
    }
    
}
