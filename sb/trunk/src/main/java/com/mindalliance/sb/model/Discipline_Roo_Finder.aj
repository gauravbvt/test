// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.Discipline;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Discipline_Roo_Finder {
    
    public static TypedQuery<Discipline> Discipline.findDisciplinesByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Discipline.entityManager();
        TypedQuery<Discipline> q = em.createQuery("SELECT o FROM Discipline AS o WHERE o.name = :name", Discipline.class);
        q.setParameter("name", name);
        return q;
    }
    
}
