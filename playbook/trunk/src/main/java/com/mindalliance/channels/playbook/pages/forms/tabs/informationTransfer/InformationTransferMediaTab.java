package com.mindalliance.channels.playbook.pages.forms.tabs.informationTransfer;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.ifm.model.MediumType;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Button;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:54:42 PM
 */
public class InformationTransferMediaTab extends InformationActBasicTab {

    ListChoice mediumTypesChoice;
    ListChoice preferredMediaList;
    Button addPreferredButton;
    Button removePreferred;
    Button upPreferredButton;
    Button downPreferred;

    public InformationTransferMediaTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {  // TODO
  /*      preferredMediaList = new SortableListView("preferredMedia", "mediumType",
                                                  new RefQueryModel(getProject(), new Query("findAllTypes", "MediumType"))) {
            protected void populateItemInternal(ListItem listItem) {
                MediumType mediumType
            }
        };*/
    }
    
}
