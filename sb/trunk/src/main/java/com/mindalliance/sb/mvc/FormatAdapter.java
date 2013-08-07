package com.mindalliance.sb.mvc;

import java.util.Iterator;

/**
 * @TODO comment this
 */
public interface FormatAdapter extends Iterable<FormattedValue> {

    FormattedValue get( String fieldName );

    @Override
    Iterator<FormattedValue> iterator();
}
