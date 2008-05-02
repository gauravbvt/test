package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocalityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceNetworkTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 5:07:54 PM
 */
abstract public class AbstractResourceForm extends AbstractProjectElementForm {

    ResourceIdentityTab identityTab;
    ResourceLocalityTab localityTab;
    ResourceResponsibilitiesTab responsibilitiesTab;
    ResourceNetworkTab networkTab;

    public AbstractResourceForm(String id, Ref element) {
        super(id, element);
    }

}
