
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.DocumentCategory
 *  03/26/2013 11:09:00
 * 
 */
public class DocumentCategory {

    private Integer id;
    private String name;
    private Short sequence;
    private Integer tenantId;
    private Set<com.analystdb.data.Documents> documentses = new HashSet<com.analystdb.data.Documents>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getSequence() {
        return sequence;
    }

    public void setSequence(Short sequence) {
        this.sequence = sequence;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Set<com.analystdb.data.Documents> getDocumentses() {
        return documentses;
    }

    public void setDocumentses(Set<com.analystdb.data.Documents> documentses) {
        this.documentses = documentses;
    }

}
