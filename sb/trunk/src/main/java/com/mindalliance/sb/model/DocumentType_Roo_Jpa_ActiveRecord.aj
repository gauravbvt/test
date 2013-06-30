// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.DocumentType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect DocumentType_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager DocumentType.entityManager;
    
    public static final EntityManager DocumentType.entityManager() {
        EntityManager em = new DocumentType().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long DocumentType.countDocumentTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM DocumentType o", Long.class).getSingleResult();
    }
    
    public static List<DocumentType> DocumentType.findAllDocumentTypes() {
        return entityManager().createQuery("SELECT o FROM DocumentType o", DocumentType.class).getResultList();
    }
    
    public static DocumentType DocumentType.findDocumentType(Integer id) {
        if (id == null) return null;
        return entityManager().find(DocumentType.class, id);
    }
    
    public static List<DocumentType> DocumentType.findDocumentTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM DocumentType o", DocumentType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void DocumentType.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void DocumentType.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DocumentType attached = DocumentType.findDocumentType(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void DocumentType.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void DocumentType.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public DocumentType DocumentType.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DocumentType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
