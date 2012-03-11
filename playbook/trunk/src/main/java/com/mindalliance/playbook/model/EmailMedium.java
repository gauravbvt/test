package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

/**
 * Email medium.
 */
@Entity
@Indexed
public class EmailMedium extends GenericMedium {

    private static final long serialVersionUID = 4702889067635290869L;

    public EmailMedium() {
    }

    public EmailMedium( String type, String address ) {
        super( null, type, address );
    }

    public EmailMedium( Contact contact, EmailMedium medium ) {
        super( contact, medium.getType(), medium.getAddress() );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.EMAIL;
    }

    @Override
    public String getCssClass() {
        return "m-email";
    }

    @Override
    public String getActionUrl() {
        return "mailto:" + getAddress();
    }
}
