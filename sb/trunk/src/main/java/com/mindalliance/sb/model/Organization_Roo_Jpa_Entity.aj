// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Organization;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

privileged aspect Organization_Roo_Jpa_Entity {
    
    declare @type: Organization: @Entity;
    
    declare @type: Organization: @Table(name = "organization");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT")
    private Integer Organization.id;
    
    public Integer Organization.getId() {
        return this.id;
    }
    
    public void Organization.setId(Integer id) {
        this.id = id;
    }
    
}