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

import static js.dom.UIAction.*;
import static jsx.ui.StructureDescriptor.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import jsx.style.BinaryStyle;
import jsx.style.StyleDescriptor;
import jsx.style.value.Color;
import jsx.ui.LowLevelWidget;
import jsx.ui.Style;

/**
 * @version 2015/10/05 12:29:31
 */
public class CheckBox extends LowLevelWidget<CheckBox> {

    /** The check status. */
    public final BooleanProperty check;

    /** The associated label. */
    public final StringProperty label;

    /**
     * <p>
     * Create Checkbox.
     * </p>
     * 
     * @param value
     * @param label
     */
    public CheckBox(BooleanProperty value, StringProperty label) {
        if (value == null) value = new SimpleBooleanProperty();
        if (label == null) label = new SimpleStringProperty();

        this.check = value;
        this.label = label;

        when(Click).at($.Root).to(update(e -> check.set(!check.get())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void virtualize() {
        box(Root, $.Root, rootStyle.getValue(), () -> {
            element(SVG, "svg", $.SVG, viewBox(0, 0, 100, 100), () -> {
                element(SVG, "rect", $.CheckBox, size(90, 90), position(5, 5), attr("rx", 5), attr("ry", 5));
                element(SVG, "path", $.CheckMark.of(check), d().moveTo(16.667, 62.167)
                        .relatively()
                        .curveTo(3.109, 5.55, 7.217, 10.591, 10.926, 15.75)
                        .curveTo(2.614, 3.636, 5.149, 7.519, 8.161, 10.853)
                        .curveTo(-0.046, -0.051, 1.959, 2.414, 2.692, 2.343)
                        .curveTo(0.895, -0.088, 6.958, -8.511, 6.014, -7.3)
                        .curveTo(5.997, -7.695, 11.68, -15.463, 16.931, -23.696)
                        .curveTo(6.393, -10.025, 12.235, -20.373, 18.104, -30.707)
                        .absolutely()
                        .curveTo(82.004, 24.988, 84.802, 20.601, 87, 16));
            });
            text($.Label, label.get());
        });
    }

    /**
     * @version 2015/10/05 12:15:25
     */
    private static class $ extends StyleDescriptor {

        static Style Root = () -> {
            position.relative();
            cursor.pointer();
        };

        static Style SVG = () -> {
            box.size(16, px);
            flexItem.alignSelf.center();
        };

        static Style CheckBox = () -> {
            stroke.linecap.round().linejoin.round().color(Color.Black).miterLimit(1).width(3, px);
            fill.none();
        };

        static BinaryStyle CheckMark = state -> {
            fill.none();
            stroke.color(80, 80, 80).width(6, px).linecap.round().linejoin.round().dashArray(120, 130).dashOffset(state ? 0 : 121);
            transit().duration(0.2, s).easeInOut().whenever();
        };

        static Style Label = () -> {
            margin.left(0.4, em);
        };
    }
}
