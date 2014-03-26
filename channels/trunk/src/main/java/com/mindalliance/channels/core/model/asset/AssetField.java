package com.mindalliance.channels.core.model.asset;

import java.io.Serializable;

/**
 * An asset type-specific property.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 11:06 AM
 */
public class AssetField implements Serializable {

    private String name;
    private String description;
    private String group;
    private String value;
    private boolean required;

    public AssetField() {
    }

    public AssetField( AssetField assetField ) {
        name = assetField.getName();
        description = assetField.getDescription();
        group = assetField.getGroup();
        required = assetField.isRequired();
        value = assetField.getValue();
    }

    public AssetField( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }


    public String getGroup() {
        return group == null ? "" : group;
    }

    public void setGroup( String group ) {
        this.group = group;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired( boolean required ) {
        this.required = required;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( isRequired() ? "Required field " : "Field " )
                .append( getName() );
        if ( getValue() != null ) {
            sb.append( "=" );
            sb.append( getValue() );
        }
        return sb.toString();

    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof  AssetField ) {
           AssetField other = (AssetField)object;
            return getName().equals( other.getName() ); // equal if same name
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + getName().hashCode();
        return hash;
    }

}
