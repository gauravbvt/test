package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationTemplatePanel;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.Channels;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:27:17 PM
 */
public class PolicySharingTab extends AbstractFormTab {

    protected MultipleStringChooser purposesChooser;
    protected InformationTemplatePanel infoTemplatePanel;
    protected DynamicFilterTree mediumTypesTree;

    public PolicySharingTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        purposesChooser = new MultipleStringChooser("purposes", this, "purposes", EDITABLE, feedback,
                                                    new RefQueryModel(Channels.instance(), new Query("findAllPurposes")));
        addReplaceable(purposesChooser);
        infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this, "informationTemplate", EDITABLE, feedback);
        addReplaceable(infoTemplatePanel);
        mediumTypesTree = new DynamicFilterTree("mediumTypes", new RefPropertyModel(getElement(), "mediumTypes"),
                                                 new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = mediumTypesTree.getNewSelections();
                RefUtils.set(getElement(), "mediumTypes", selectedTypes);
            }
        };
    }


}