package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.forms.panels.AgentSpecPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.environment.Policy;
import com.mindalliance.channels.playbook.ifm.Channels;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:26:54 PM
 */
public class PolicyPartiesTab extends AbstractFormTab {

    protected AgentSpecPanel sourceAgentSpecPanel;
    MultipleStringChooser relationshipNamesChooser;
    protected AgentSpecPanel recipientAgentSpecPanel;

    public PolicyPartiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        sourceAgentSpecPanel = new AgentSpecPanel("sourceAgentSpec", this, "sourceAgentSpec", EDITABLE, feedback);
        addReplaceable(sourceAgentSpecPanel);
        relationshipNamesChooser = new MultipleStringChooser("relationshipNames", this, "relationshipNames", EDITABLE, feedback,
                                                              new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        addReplaceable(relationshipNamesChooser);
        recipientAgentSpecPanel = new AgentSpecPanel("recipientAgentSpec", this, "recipientAgentSpec", EDITABLE, feedback);
        addReplaceable(recipientAgentSpecPanel);
    }

}