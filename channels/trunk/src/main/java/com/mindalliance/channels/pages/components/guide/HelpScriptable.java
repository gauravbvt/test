package com.mindalliance.channels.pages.components.guide;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.surveys.RFISurvey;

/**
 * Provides support for help scripts.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/26/13
 * Time: 10:38 AM
 */
public interface HelpScriptable {

    static final String GUIDE = "guide";

    Flow getAnyFlow();
    Event getAnyEvent();
    Phase getAnyPhase();
    Organization getAnyActualOrganization();
    Role getAnyRole();
    Actor getAnyActualAgent();
    Place getAnyActualPlace();
    TransmissionMedium getAnyMedium();
    InfoProduct getAnyInfoProduct();
    InfoFormat getAnyInfoFormat();
    Function getAnyFunction();
    MaterialAsset getAnyMaterialAsset();
    Flow getAnySharingFlow();
    RFISurvey getUnknownRFISurvey();
    Feedback  getUnknownFeedback();

}
