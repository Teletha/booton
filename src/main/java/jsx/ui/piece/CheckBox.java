/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.ui.piece;

import static jsx.style.StyleDescriptor.*;
import static jsx.ui.StructureDescriptor.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import jsx.style.value.Numeric;
import jsx.ui.Style;

/**
 * @version 2015/10/09 15:09:05
 */
public class CheckBox extends MarkedButton<CheckBox> {

    /** The radius. */
    private static final Numeric Radius = new Numeric(0.25, em);

    private static final Style CheckMark = () -> {
        fill.none();
        stroke.color("#FFF").width(2, px).linecap.square().miterLimit(10);
    };

    /**
     * <p>
     * Create Checkbox.
     * </p>
     * 
     * @param value
     * @param label
     */
    public CheckBox(BooleanProperty value, StringProperty label) {
        super("checkbox", null, value, label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void virtualizeMark() {
        svg("polyline", CheckMark, attr("points", "4,7 6,9 10,5"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Numeric radius() {
        return Radius;
    }
}
