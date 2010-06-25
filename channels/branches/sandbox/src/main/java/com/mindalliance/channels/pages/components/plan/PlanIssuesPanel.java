package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Plan issues panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:43:52 AM
 */
public class PlanIssuesPanel extends AbstractIssueTablePanel {
    /**
     * Maximum number of rows of issues to show at a time.
     */
    private static final int MAX_ROWS = 10;
    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived = false;

     public PlanIssuesPanel( String id ) {
        super( id, null, MAX_ROWS );
    }

    protected void addIncluded() {
        CheckBox includeWaivedCheckBox = new CheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( includeWaivedCheckBox );
    }

    public boolean isIncludeWaived() {
        return includeWaived;
    }

    public void setIncludeWaived( boolean includeWaived ) {
        this.includeWaived = includeWaived;
    }

    /**
     * Get all issues, possibly filtered on the model object they are about.
     *
     * @return a list of issues
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> getIssues() {
        List<Issue> issues;
        ModelObject about = getAbout();
        final String issueType = getIssueType();
        if ( about != null ) {
            issues = getAnalyst().listIssues( about, true, includeWaived );
        } else {
            if ( includeWaived ) {
                issues = getQueryService().findAllIssues( getAnalyst() );
            } else {
                issues = getQueryService().findAllUnwaivedIssues( getAnalyst() );
            }
        }
        return (List<Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( issueType.equals( ALL )
                                || ( (Issue) obj ).getType().equals( issueType ) );
                    }
                }
        );
    }

}
