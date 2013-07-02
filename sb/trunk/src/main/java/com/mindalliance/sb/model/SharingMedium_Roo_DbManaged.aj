// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Medium;
import com.mindalliance.sb.model.Sharing;
import com.mindalliance.sb.model.SharingMedium;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

privileged aspect SharingMedium_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumn(name = "medium", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Medium SharingMedium.medium;
    
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "respondent", referencedColumnName = "respondent", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "respondent_organization", referencedColumnName = "respondent_organization", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "incident", referencedColumnName = "incident", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "organization", referencedColumnName = "organization", nullable = false, insertable = false, updatable = false) })
    private Sharing SharingMedium.sharing;
    
    public Medium SharingMedium.getMedium() {
        return medium;
    }
    
    public void SharingMedium.setMedium(Medium medium) {
        this.medium = medium;
    }
    
    public Sharing SharingMedium.getSharing() {
        return sharing;
    }
    
    public void SharingMedium.setSharing(Sharing sharing) {
        this.sharing = sharing;
    }
    
}