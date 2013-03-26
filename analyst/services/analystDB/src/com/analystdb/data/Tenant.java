
package com.analystdb.data;

import java.util.Date;


/**
 *  analystDB.Tenant
 *  03/26/2013 11:09:00
 * 
 */
public class Tenant {

    private Integer tenantId;
    private String name;
    private String logo;
    private Boolean disabled;
    private Date expires;

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

}
