package com.mindalliance.channels.playbook.pages.forms.tabs.person;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 3:26:52 PM
 */
public class PersonResponsibilitiesTab  extends ResourceResponsibilitiesTab {

    DynamicFilterTree positionTree;

    public PersonResponsibilitiesTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        List<Ref> allPositions = project.findAllResourcesOfType("Position");
        positionTree = new DynamicFilterTree("positions", new RefPropertyModel(element, "positions"), new Model((Serializable) allPositions)) {
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = positionTree.getNewSelections();
                RefUtils.set(element, "positions", newSelections);
             }
        };
        positionTree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );
        addReplaceable(positionTree);
     }
}
