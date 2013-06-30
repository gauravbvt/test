// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SystemIssueType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SystemIssueType_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SystemIssueType.entityManager;
    
    public static final EntityManager SystemIssueType.entityManager() {
        EntityManager em = new SystemIssueType().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SystemIssueType.countSystemIssueTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SystemIssueType o", Long.class).getSingleResult();
    }
    
    public static List<SystemIssueType> SystemIssueType.findAllSystemIssueTypes() {
        return entityManager().createQuery("SELECT o FROM SystemIssueType o", SystemIssueType.class).getResultList();
    }
    
    public static SystemIssueType SystemIssueType.findSystemIssueType(Integer id) {
        if (id == null) return null;
        return entityManager().find(SystemIssueType.class, id);
    }
    
    public static List<SystemIssueType> SystemIssueType.findSystemIssueTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SystemIssueType o", SystemIssueType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void SystemIssueType.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SystemIssueType.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SystemIssueType attached = SystemIssueType.findSystemIssueType(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SystemIssueType.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SystemIssueType.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SystemIssueType SystemIssueType.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SystemIssueType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
