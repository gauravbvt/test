package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy;
import com.mindalliance.channels.playbook.ifm.taxonomy.Category;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 2:52:19 PM
 */
abstract public class AbstractCategoryForm extends AbstractElementForm {

    public AbstractCategoryForm(String id, Ref element) {
        super(id, element);
    }

    // ElementPanel

    public Taxonomy getTaxonomy() {
        Ref ref = ((Category)element.deref()).getTaxonomy();
        if (ref != null) {
            return (Taxonomy)ref.deref();
        }
        else {
            return null;
        }
    }

    public boolean isTaxonomyPanel() {
        return true;
    }


    // End ElementPanel

}
