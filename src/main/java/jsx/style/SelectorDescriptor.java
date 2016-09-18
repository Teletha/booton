/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.style;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import jsx.ui.Style;
import jsx.ui.flux.Location;

/**
 * @version 2016/09/17 16:18:29
 */
public class SelectorDescriptor extends SelectorDSL {

    /** The root element. */
    private Element root = new Element();

    /** The current element. */
    private Element element = root;

    private BiConsumer<String, Style> process;

    /**
     * <p>
     * Create blank selector descriptor.
     * </p>
     */
    SelectorDescriptor() {
        this(PropertyDefinition::createSubRule);
    }

    /**
     * @param object
     */
    public SelectorDescriptor(BiConsumer<String, Style> process) {
        this.process = process;

        root.selectors.add("$");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectorDSL at(Location location) {
        element.selectors.add("." + location.name());

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeSelector attribute(String name) {
        return new AttributeSelector(Objects.requireNonNull(name));
    }

    /**
     * <p>
     * Write combinator.
     * </p>
     * 
     * @param type A combinator type.
     * @param forward A direction.
     * @return
     */
    @Override
    final SelectorDescriptor combine(String type, boolean forward) {
        Element e = element;

        if (forward) {
            e.sub = element = new Element();
            e.combinator = type;
        } else {
            root = element = new Element();
            root.sub = e;
            root.combinator = type;
        }
        return this;
    }

    /**
     * <p>
     * Write pseudo class.
     * </p>
     * 
     * @param name A pseudo class name.
     * @return Chainable API.
     */
    @Override
    final SelectorDescriptor pseudo(boolean isElement, String name) {
        if (isElement) {
            element.pseudoElement = name;
        } else {
            element.pseudoClasses.add(name);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void style(Style sub) {
        process.accept(root.toString(), sub);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * @version 2016/09/17 16:20:33
     */
    public class AttributeSelector {

        private final StringBuilder selector = new StringBuilder();

        /**
         * @param name
         */
        private AttributeSelector(String name) {
            selector.append("[").append(name);

            element.selectors.add(selector);
        }

        public SelectorDescriptor has() {
            selector.append("]");

            return SelectorDescriptor.this;
        }

        public SelectorDescriptor match(String value) {
            selector.append("=").append(value).append("]");

            return SelectorDescriptor.this;
        }

        public void match(String value, Style sub) {
            match(value).style(sub);
        }

        public SelectorDescriptor matchWithSpace(String value) {
            selector.append("~=").append(value).append("]");

            return SelectorDescriptor.this;
        }

        public SelectorDescriptor matchWithHyphen(String value) {
            selector.append("|=").append(value).append("]");

            return SelectorDescriptor.this;
        }

        public SelectorDescriptor startsWith(String value) {
            selector.append("^=").append(value).append("]");

            return SelectorDescriptor.this;
        }

        public SelectorDescriptor endsWith(String value) {
            selector.append("$=").append(value).append("]");

            return SelectorDescriptor.this;
        }

        public SelectorDescriptor contains(String value) {
            selector.append("*=").append(value).append("]");

            return SelectorDescriptor.this;
        }
    }

    /**
     * @version 2016/09/18 10:57:14
     */
    public static class Element {

        List<CharSequence> selectors = new ArrayList();

        String combinator;

        String pseudoElement;

        List<CharSequence> pseudoClasses = new ArrayList();

        Element sub;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for (CharSequence selector : selectors) {
                builder.append(selector);
            }

            for (CharSequence pseudo : pseudoClasses) {
                builder.append(":").append(pseudo);
            }

            if (pseudoElement != null) {
                builder.append("::").append(pseudoElement);
            }

            if (combinator != null) {
                builder.append(combinator).append(sub);
            }
            return builder.toString();
        }
    }
}
