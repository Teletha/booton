/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package booton.css;

import static booton.css.Vendor.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import kiss.I;

/**
 * @version 2014/10/20 16:18:28
 */
public abstract class CSSWriter {

    /**
     * <p>
     * Write property.
     * </p>
     * 
     * @param name
     * @param values
     * @param vendors
     */
    public void property(CSSProperty property) {
        property.write(this);
    }

    /**
     * <p>
     * Write property.
     * </p>
     * 
     * @param name
     * @param values
     * @param vendors
     */
    public void property(String name, List values, Vendor... vendors) {
        property(EnumSet.of(Standard, vendors), " ", name, values.toArray());
    }

    /**
     * <p>
     * Write property with separator.
     * </p>
     * 
     * @param name
     * @param values
     */
    public void propertyWithSeparator(String name, List values) {
        propertyWithSeparator(name, values.toArray());
    }

    /**
     * <p>
     * Write property.
     * </p>
     * 
     * @param name
     * @param calcurated
     */
    public void property(String name, Object... values) {
        property(EnumSet.of(Standard), " ", name, values);
    }

    /**
     * <p>
     * Write property.
     * </p>
     * 
     * @param name
     * @param calcurated
     */
    public void propertyWithSeparator(String name, Object... values) {
        property(EnumSet.of(Standard), ",", name, values);
    }

    /**
     * <p>
     * Helper method to write property.
     * </p>
     * 
     * @param prefixes A list of vendors for property name.
     * @param separator A value separator.
     * @param name A property name.
     * @param values A list of property values.
     */
    private void property(EnumSet<Vendor> prefixes, String separator, String name, Object... values) {
        if (name != null && name.length() != 0 && values != null) {
            EnumMap<Vendor, List<String>> properties = new EnumMap(Vendor.class);

            // calculate dependent vendors
            EnumSet<Vendor> vendors = EnumSet.copyOf(prefixes);

            for (Object value : values) {
                if (value instanceof CSSValue) {
                    vendors.addAll(((CSSValue) value).vendors());
                }
            }

            for (Vendor vendor : vendors) {
                List<String> text = new ArrayList();

                for (Object value : values) {
                    if (value != null) {
                        if (value instanceof CSSValue) {
                            String vendered = ((CSSValue) value).valueFor(vendor);

                            if (vendered != null && vendered.length() != 0) {
                                text.add(vendered);
                            }
                        } else if (value instanceof Number) {
                            Number number = (Number) value;

                            if (number.intValue() == number.doubleValue()) {
                                text.add(String.valueOf(number.intValue()));
                            } else {
                                text.add(number.toString());
                            }
                        } else {
                            String decoded = value.toString();

                            if (decoded != null && decoded.length() != 0) {
                                text.add(decoded);
                            }
                        }
                    }
                }
                properties.put(vendor, text);
            }

            for (Entry<Vendor, List<String>> property : properties.entrySet()) {
                String value = I.join(separator, property.getValue());

                if (value.length() != 0) {
                    Vendor vendor = property.getKey();

                    if (!prefixes.contains(vendor)) {
                        vendor = Standard;
                    }
                    write(vendor, name, value);
                }
            }
        }
    }

    protected abstract void write(Vendor vendor, String name, String value);
}
