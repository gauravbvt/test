package com.mindalliance.channels.pages.components.diagrams;

import java.io.Serializable;

/**
 * Diagram look-and-feel configuration.
 */
public class Settings implements Serializable {

    /** The requested size of the diagram (can be null). */
    private double[] size;

    /** The orientation of the diagram. */
    private String orientation;

    /** Whether to add an image map. */
    private boolean usingMap = true;

    /** True if panel is ajax-enabled. */
    private boolean usingAjax = true;

    /** Unique CSS identifier to image container. */
    private String domIdentifier;

    public Settings() {
    }

    public Settings(
            String domIdentifier, String orientation, double[] size, boolean usingAjax,
            boolean usingMap ) {

        this.domIdentifier = domIdentifier;
        this.orientation = orientation;
        this.size = size;
        this.usingAjax = usingAjax;
        this.usingMap = usingMap;
    }

    public String getDomIdentifier() {
        return domIdentifier;
    }

    public String getOrientation() {
        return orientation;
    }

    public double[] getSize() {
        return size;
    }

    public boolean isUsingAjax() {
        return usingAjax;
    }

    public boolean isUsingMap() {
        return usingMap;
    }

    public void setOrientationLeftRight() {
        orientation = "LR";
    }

    public void setOrientationTopBottom() {
        orientation = "TB";
    }
}
