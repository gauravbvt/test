package com.mindalliance.channels.core.model;

import java.util.List;

/**
 * An identifiable holding elements of information.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/31/12
 * Time: 1:08 PM
 */
public interface EOIsHolder extends Identifiable {

    List<ElementOfInformation> getEois(); // synonymous with getlocalEois()
    List<ElementOfInformation> getLocalEois();
    List<ElementOfInformation> getEffectiveEois();
    void addLocalEoi( ElementOfInformation eoi );
    boolean isClassificationsAccessible();
    List<Classification> getAllEOIClassifications();
    boolean isSpecialHandlingChangeable();
    boolean isDescriptionChangeable();
    boolean isTimeSensitive();
    boolean isTimeSensitive( String eoiContent );
    void setTimeSensitive( String eoiContent, boolean val );
    boolean canSetTimeSensitivity();
    String getEOIHolderLabel();
    boolean canSetElements();
    boolean hasEffectiveEoiNamedExactly( String content );
    boolean isFlow();
    Node getSource();
    Node getTarget();
    boolean isSharing();
    boolean isClassificationsLinked();
    boolean areAllEOIClassificationsSame();
    List<ElementOfInformation> getEOISWithSameClassifications();

    boolean isLocalAndEffective( ElementOfInformation eoi );

    boolean isLocalEoi( ElementOfInformation eoi );
}
