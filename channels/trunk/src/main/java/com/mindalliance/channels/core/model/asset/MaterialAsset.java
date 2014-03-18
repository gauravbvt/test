package com.mindalliance.channels.core.model.asset;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Material Asset is something physical and costly to replicate (unlike information which is ethereal and infinitely
 * replicable) that is needed for the execution of a task.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 11:00 AM
 */
public class MaterialAsset extends ModelEntity {

    public static MaterialAsset UNKNOWN;

    /**
     * Name of unknown asset.
     */
    public static String UnknownName = "(unknown)";

    private List<MaterialAsset> dependencies = new ArrayList<MaterialAsset>();

    private boolean placeholder;

    private List<AssetField> fields = new ArrayList<AssetField>();

    public MaterialAsset() {
    }

    public MaterialAsset( String name ) {
        super( name );
    }

    public static String classLabel() {
        return "assets";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public String getTypeName() {
        return "asset";
    }

    public List<MaterialAsset> getDependencies() {
        return dependencies == null ? new ArrayList<MaterialAsset>() : dependencies;
    }

    public void setDependencies( List<MaterialAsset> dependencies ) {
        this.dependencies = dependencies;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder( boolean placeholder ) {
        this.placeholder = placeholder;
    }

    public List<AssetField> getFields() {
        return fields == null ? new ArrayList<AssetField>() : fields;
    }

    public void setFields( List<AssetField> fields ) {
        this.fields = fields;
    }

    public List<AssetField> getValuableFields() {
        Set<AssetField> effectiveFields = new HashSet<AssetField>( getFields() );
        if ( isActual() ) {
            for ( ModelEntity modelEntity : getAllTypes() ) {
                effectiveFields.addAll( ((MaterialAsset)modelEntity).getFields() );
            }
        }
        return new ArrayList<AssetField>( effectiveFields );
    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined()
                && getDependencies().isEmpty()
                && getFields().isEmpty();
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || CollectionUtils.exists(
                getDependencies(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ModelObject.areIdentical( (MaterialAsset) object, mo );
                    }
                } );
    }

    public void addDependency( MaterialAsset asset ) {
        if ( dependencies == null ) dependencies = new ArrayList<MaterialAsset>();
        if ( !dependencies.contains( asset ) ) {
            dependencies.add( asset );
        }
    }

    public void addField( AssetField field ) {
        if ( fields == null ) fields = new ArrayList<AssetField>();
        if ( !fields.contains( field ) ) {
            fields.add( field );
        }
    }

    public List<String> getGroups() {
        Set<String> groups = new HashSet<String>();
        for ( AssetField field : getFields() ) {
            groups.add( field.getGroup() );
        }
        return new ArrayList<String>( groups );
    }

    public List<String> getValuableGroups() {
        Set<String> groups = new HashSet<String>();
        for ( AssetField field : getValuableFields() ) {
            groups.add( field.getGroup() );
        }
        return new ArrayList<String>( groups );
    }


    @SuppressWarnings("unchecked")
    public List<AssetField> getFieldsInGroup( final String group ) {
        return (List<AssetField>) CollectionUtils.select(
                getFields(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (AssetField) object ).getGroup().equals( group );
                    }
                } );
    }

    @SuppressWarnings("unchecked")
    public List<AssetField> getValuableFieldsInGroup( final String group ) {
        return (List<AssetField>) CollectionUtils.select(
                getValuableFields(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (AssetField) object ).getGroup().equals( group );
                    }
                } );
    }
}
