// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Discipline;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

privileged aspect Discipline_Roo_Jpa_Entity {
    
    declare @type: Discipline: @Entity;
    
    declare @type: Discipline: @Table(name = "discipline");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT")
    private Integer Discipline.id;
    
    public Integer Discipline.getId() {
        return this.id;
    }
    
    public void Discipline.setId(Integer id) {
        this.id = id;
    }
    
}