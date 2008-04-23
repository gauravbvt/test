package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import javax.swing.tree.TreeNode;
import java.io.Serializable;

/**
 * ...
 */
public interface Filter extends Serializable, TreeNode {

    Filter getRoot();

    Container getContainer();

    String toString();

    String getText();

    boolean isExpanded();

    void setExpanded( boolean expanded );

    boolean isSelected();

    void setSelected( boolean selected );

    boolean isForceSelected();

    void setForceSelected( boolean selected );

    boolean filter( Ref object );

    /**
     * Test an object for inclusion, assuming either selected, collapsed or no children.
     * @param object the object
     * @return true if the object satisfies this filter.
     */
    boolean match( Ref object );
}
