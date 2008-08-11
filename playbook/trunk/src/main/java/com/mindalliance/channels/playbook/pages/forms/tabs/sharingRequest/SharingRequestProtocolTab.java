package com.mindalliance.channels.playbook.pages.forms.tabs.sharingRequest;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.OutgoingSharingProtocolPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:14:17 PM
 */
public class SharingRequestProtocolTab extends AbstractFormTab {

    private static final long serialVersionUID = 106231697111617108L;

    public SharingRequestProtocolTab(
            String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        addReplaceable(
                new OutgoingSharingProtocolPanel(
                        "protocol", this, "protocol" ) );
    }
}
