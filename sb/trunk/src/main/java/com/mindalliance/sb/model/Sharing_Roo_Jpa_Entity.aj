// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Sharing;
import com.mindalliance.sb.model.SharingPK;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

privileged aspect Sharing_Roo_Jpa_Entity {
    
    declare @type: Sharing: @Entity;
    
    declare @type: Sharing: @Table(name = "sharing");
    
    @EmbeddedId
    private SharingPK Sharing.id;
    
    public SharingPK Sharing.getId() {
        return this.id;
    }
    
    public void Sharing.setId(SharingPK id) {
        this.id = id;
    }
    
}