package com.mindalliance.sb.mvc;

/**
 * @TODO comment this
 */
public interface FormattedValue {

    Object getFieldValue();

    String getFieldName();

    String getName();

    boolean isVisible();

    String getValue();

    boolean isNull();

    boolean isQuotable();

    String getJavascriptValue();

    String getHtmlValue();
}
