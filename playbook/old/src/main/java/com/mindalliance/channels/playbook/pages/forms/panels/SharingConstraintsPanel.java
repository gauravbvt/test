package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 1:13:15 PM
 */
public class SharingConstraintsPanel extends AbstractComponentPanel {

    protected MultipleStringChooser allowedPurposesChooser;
    protected MultipleStringChooser privateTopicsChooser;

    public SharingConstraintsPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        allowedPurposesChooser = new MultipleStringChooser("allowedPurposes", this, propPath+".allowedPurposes",
                                                            new RefQueryModel(getProject(), new Query("findAllPurposes")));
        addReplaceable(allowedPurposesChooser);
        privateTopicsChooser = new MultipleStringChooser("privateTopics", this, propPath+".privateTopics",
                                                            new RefQueryModel(getElement(), new Query("findAllTopics")));
        addReplaceable(privateTopicsChooser);

    }

}
