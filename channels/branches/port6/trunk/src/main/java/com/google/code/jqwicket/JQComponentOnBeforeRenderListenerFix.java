package com.google.code.jqwicket;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * TODO: Remove when JQWicket issue #29 is solved.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/24/13
 * Time: 12:25 PM
 */
public class JQComponentOnBeforeRenderListenerFix implements
        IComponentOnBeforeRenderListener {

    public JQComponentOnBeforeRenderListenerFix() {
        this(new JQContributionConfig());
    }

    public JQComponentOnBeforeRenderListenerFix(JQContributionConfig config) {
        JQContributionConfig.set(config);
    }

    /**
     * {@inheritDoc}
     */
    public void onBeforeRender(Component component) {
        if (component == null)
            return;

        if (IJQHeaderContributor.class.isAssignableFrom(component.getClass())) {
            addJQueryHeaderContributor(component, (IJQHeaderContributor) component);
        }

        addJQueryHeaderContributor(component, findJQueryBehaviors(component));
    }

    private Collection<JQBehavior> findJQueryBehaviors(Component component) {
        return new LinkedHashSet<JQBehavior>(component.getBehaviors(JQBehavior.class));
    }

    private void addJQueryHeaderContributor(Component component, IJQHeaderContributor... contributor) {

        if (Utils.isEmpty(contributor))
            return;

        addJQueryHeaderContributor(component, Arrays.asList( contributor ));
    }

    private void addJQueryHeaderContributor(Component component,
                                            Collection<? extends IJQHeaderContributor> contributors) {

        if (Utils.isEmpty(contributors))
            return;

        JQContributionRenderer renderer = JQContributionRendererFix.get();    // todo - use FIX
        renderer.addContributors(contributors);
        component.add(renderer);

    }
}
