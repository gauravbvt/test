package com.mindalliance.channels.playbook.pages.forms.tabs.position;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.ifm.project.resources.Position;
import com.mindalliance.channels.playbook.ifm.info.Location;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 11:01:42 AM
 */
public class PositionJurisdictionTab extends AbstractFormTab {

    protected LocationPanel jurisdictionPanel;

    public PositionJurisdictionTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        Position position = (Position)getElement().deref();
        jurisdictionPanel = new LocationPanel("jurisdiction", this, "jurisdiction");
        addReplaceable(jurisdictionPanel);
    }
}
