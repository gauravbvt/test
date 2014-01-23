package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A standardized information product.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/29/12
 * Time: 1:57 PM
 */
public class InfoProduct extends ModelEntity implements EOIsHolder {

    /**
     * Unknown medium.
     */
    public static InfoProduct UNKNOWN;

    /**
     * Name of unknown info product.
     */
    public static String UnknownName = "(unknown)";

    private List<ElementOfInformation> eois = new ArrayList<ElementOfInformation>();
    private boolean classificationsLinked;

    public InfoProduct() {
    }

    public InfoProduct( String name ) {
        super( name );
    }

    @Override
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return CollectionUtils.exists(
                allCommitments.toList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        InfoProduct infoProduct = ( (Commitment) object ).getSharing().getInfoProduct();
                        return infoProduct != null && infoProduct.narrowsOrEquals( InfoProduct.this );
                    }
                }
        );

    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined()
                && getLocalEois().isEmpty();
    }

    public List<ElementOfInformation> getEois() {
        return eois;
    }

    @Override
    public List<ElementOfInformation> getLocalEois() {
        return eois;
    }

    /**
     * Return all EOIS inherited from categories (no redundancies) and all local EOIs not overridden by inherited EOIS.
     * Local EOIS are at the top of the list.
     *
     * @return a list of EOIS
     */
    public List<ElementOfInformation> getEffectiveEois() {
        List<ElementOfInformation> allEois = new ArrayList<ElementOfInformation>();
        List<ElementOfInformation> inheritedEois = getInheritedEois();
        for ( final ElementOfInformation eoi : getLocalEois() ) {
            if ( !isOverridden( eoi, inheritedEois ) ) {
                allEois.add( eoi );
            }
        }
        allEois.addAll( inheritedEois );
        return Collections.unmodifiableList( allEois );
    }

    private List<ElementOfInformation> getInheritedEois() {
        List<ElementOfInformation> inheritedEois = new ArrayList<ElementOfInformation>();
        for ( ModelEntity category : this.getAllTypes() ) {
            for ( final ElementOfInformation eoi : ( (InfoProduct) category ).getLocalEois() ) {
                if ( !CollectionUtils.exists(
                        inheritedEois,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return Matcher.same( ( (ElementOfInformation) object ).getContent(), eoi.getContent() );
                            }
                        }
                ) ) {
                    inheritedEois.add( eoi );
                }
            }
        }
        return inheritedEois;
    }

    private boolean isOverridden( final ElementOfInformation eoi, List<ElementOfInformation> inheritedEois ) {
        return CollectionUtils.exists(
                inheritedEois,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (ElementOfInformation) object ).getContent(), eoi.getContent() );
                    }
                }
        );
    }

    public void setEois( List<ElementOfInformation> eois ) {
        setLocalEois( eois );
    }

    public void setLocalEois( List<ElementOfInformation> eois ) {
        this.eois = eois;
    }


    @Override
    public String getTypeName() {
        return "infoproduct";
    }

    @Override
    public String getKindLabel() {
        return "information product";
    }


    @Override
    public void addLocalEoi( ElementOfInformation eoi ) {
        eois.add( eoi );
    }

    public void setClassificationsLinked( boolean classificationsLinked ) {
        this.classificationsLinked = classificationsLinked;
    }

    // EOIHolder

    @Override
    public boolean isTimeSensitive( String eoiContent ) {
        return false;
    }

    @Override
    public void setTimeSensitive( String eoiContent, boolean val ) {
        // Do nothing
    }



    @Override
    public boolean areAllEOIClassificationsSame() {
        // No eoi has classifications different from those of another eoi.
        return !CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        final ElementOfInformation eoi = (ElementOfInformation) obj;
                        return CollectionUtils.exists(
                                getEffectiveEois(),
                                new Predicate() {
                                    public boolean evaluate( Object obj ) {
                                        return !CollectionUtils.isEqualCollection(
                                                eoi.getClassifications(),
                                                ( (ElementOfInformation) obj ).getClassifications()
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    public boolean isClassificationsAccessible() {
        return true;
    }

    @Override
    public List<Classification> getAllEOIClassifications() {
        Set<Classification> allClassifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            allClassifications.addAll( eoi.getClassifications() );
        }
        return new ArrayList<Classification>( allClassifications );
    }

    @Override
    public boolean isSpecialHandlingChangeable() {
        return true;
    }

    @Override
    public boolean isDescriptionChangeable() {
        return true;
    }

    @Override
    public boolean isTimeSensitive() {
        return false;
    }

    @Override
    public boolean canSetTimeSensitivity() {
        return false;
    }

    @Override
    public String getEOIHolderLabel() {
        return "Information product \"" + getName() + "\"";
    }

    @Override
    public boolean canSetElements() {
        return true;
    }

    @Override
    public boolean hasEffectiveEoiNamedExactly( final String content ) {
        return CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ElementOfInformation) object ).getContent().equals( content );
                    }
                }
        );
    }

    @Override
    public boolean isFlow() {
        return false;
    }

    @Override
    public Node getSource() {
        return null;
    }

    @Override
    public Node getTarget() {
        return null;
    }

    @Override
    public boolean isSharing() {
        return false;
    }

    @Override
    public boolean isClassificationsLinked() {
        return classificationsLinked;
    }

    @Override
    public List<ElementOfInformation> getEOISWithSameClassifications() {
        List<Classification> allClassifications = getAllEOIClassifications();
        List<ElementOfInformation> eoisCopy = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            ElementOfInformation copy = new ElementOfInformation();
            copy.setContent( eoi.getContent() );
            copy.setDescription( eoi.getDescription() );
            copy.setSpecialHandling( eoi.getSpecialHandling() );
            copy.setClassifications( new ArrayList<Classification>( allClassifications ) );
            eoisCopy.add( copy );
        }
        return eoisCopy;
    }

    @Override
    public boolean isLocalAndEffective( ElementOfInformation eoi ) {
        return isLocalEoi( eoi ) && !isOverridden( eoi, getInheritedEois() );
    }

    @Override
    public boolean isLocalEoi( ElementOfInformation eoi ) {
        return eois.contains( eoi );
    }

    public static String classLabel() {
        return "Information products";
    }
}
