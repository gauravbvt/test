package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.ElementOfInformation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for an information sharing agreement.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/11
 * Time: 10:23 AM
 */
@XmlType( propOrder = { "beneficiaryId", "information", "elementsOfInformation", "usage", "documentation"} )
public class AgreementData {

    private Agreement agreement;

    public AgreementData() {
        // required
    }

    public AgreementData( Agreement agreement ) {
        this.agreement = agreement;
    }

    @XmlElement
    public Long getBeneficiaryId() {
        return agreement.getBeneficiary().getId();
    }

    @XmlElement
    public String getInformation() {
        return agreement.getInformation();
    }

    @XmlElement
    public List<ElementOfInformationData> getElementsOfInformation() {
        List<ElementOfInformationData> eois = new ArrayList<ElementOfInformationData>(  );
        for ( ElementOfInformation eoi : agreement.getEois() ) {
            eois.add( new ElementOfInformationData( eoi ) );
        }
        return eois;
    }

    @XmlElement
    public String getUsage() {
        return agreement.getUsage();
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( agreement );
    }
}
