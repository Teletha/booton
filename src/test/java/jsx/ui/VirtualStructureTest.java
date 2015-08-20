/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.ui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import jsx.style.Style;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @version 2014/09/11 14:57:41
 */
// @RunWith(ScriptRunner.class)
public class VirtualStructureTest {

    /** Empty style. */
    private static final Style style = () -> {
    };

    @Test
    public void text() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.ⅼ("text");

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsText(root.items.get(0), "text");
    }

    @Test
    public void texts() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.〡("first", "second");

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 2;
        assertAsText(root.items.get(0), "first");
        assertAsText(root.items.get(1), "second");
    }

    @Test
    public void textSequencialCall() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.ⅼ("first");
        root〡.ⅼ("second");

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 2;
        assertAsText(root.items.get(0), "first");
        assertAsText(root.items.get(1), "second");
    }

    @Test
    @Ignore
    public void widgetText() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.ⅼ(widget(sub〡 -> {
            sub〡.ⅼ("widget text");
        }));

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "widget", c -> {
            assertAsText(c.items.get(0), "widget text");
        });
    }

    @Test
    public void boxText() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, "text");

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "hbox", e -> {
            assertAsText(e.items.get(0), "text");
        });
    }

    @Test
    public void boxTextSequentialCall() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, "first");
        root〡.hbox.ⅼ(style, "second");

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 2;
        assertAsElement(root, 0, "hbox", e -> {
            assertAsText(e.items.get(0), "first");
        });
        assertAsElement(root, 1, "hbox", e -> {
            assertAsText(e.items.get(0), "second");
        });
    }

    @Test
    public void boxTextNestedCall() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, () -> {
            root〡.hbox.ⅼ(style, "nested text");
        });

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "hbox", e -> {
            assertAsElement(e, 0, "hbox", text -> {
                assertAsText(text.items.get(0), "nested text");
            });
        });
    }

    @Test
    public void boxWidgetText() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, widget(sub〡 -> {
            sub〡.hbox.ⅼ(style, "nested text");
        }));

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "hbox", e -> {
            assertAsElement(e, 0, "widget", w -> {
                assertAsElement(w, 0, "hbox", nest -> {
                    assertAsText(nest.items.get(0), "nested text");
                });
            });
        });
    }

    @Test
    public void group() throws Exception {
        List<String> items = Arrays.asList("first", "second", "third");

        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, StringWidget.class, items);

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "hbox", e -> {
            assert e.items.length() == 3;
            assertAsElement(e, 0, "widget", text -> {
                assertAsText(text.items.get(0), "first");
            });
            assertAsElement(e, 1, "widget", text -> {
                assertAsText(text.items.get(0), "second");
            });
            assertAsElement(e, 2, "widget", text -> {
                assertAsText(text.items.get(0), "third");
            });
        });
    }

    @Test
    public void range() throws Exception {
        VirtualStructure root〡 = new VirtualStructure();
        root〡.hbox.ⅼ(style, 3, i -> {
            root〡.vbox.ⅼ(style, "text" + i);
        });

        VirtualElement root = root〡.getRoot();
        assert root.items.length() == 1;
        assertAsElement(root, 0, "hbox", e -> {
            assert e.items.length() == 3;

            assertAsElement(e, 0, "vbox", child -> {
                assertAsText(child.items.get(0), "text0");
            });
            assertAsElement(e, 1, "vbox", child -> {
                assertAsText(child.items.get(0), "text1");
            });
            assertAsElement(e, 2, "vbox", child -> {
                assertAsText(child.items.get(0), "text2");
            });
        });
    }

    /**
     * @version 2014/09/13 12:18:58
     */
    private static class StringWidget extends Widget1<String> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void virtualize(VirtualStructure $〡) {
            $〡.ⅼ(model1);
        }
    }

    /**
     * <p>
     * Assertion helper for {@link VirtualElement}.
     * </p>
     * 
     * @param e
     * @param index
     * @param name
     * @param child
     */
    private void assertAsElement(VirtualElement e, int index, String name, Consumer<VirtualElement> child) {
        VirtualNode node = e.items.get(index);
        assert node instanceof VirtualElement;

        VirtualElement virtual = (VirtualElement) node;
        assert virtual.name.equals(name);
        child.accept(virtual);
    }

    /**
     * <p>
     * Assertion helper for {@link VirtualText}.
     * </p>
     * 
     * @param node
     * @param expectedText
     */
    private void assertAsText(VirtualNode node, String expectedText) {
        assert node instanceof VirtualText;

        VirtualText virtual = (VirtualText) node;
        assert virtual.text.equals(expectedText);
    }

    /**
     * <p>
     * Assertion helper for {@link VirtualElement}.
     * </p>
     * 
     * @param node
     * @param name
     * @param value
     */
    private void assertAttribute(VirtualNode node, String name, String value) {
        assert node instanceof VirtualElement;

        VirtualElement virtual = (VirtualElement) node;
        int index = virtual.attributes.key(name);
        assert index != -1;
        assert virtual.attributes.value(index).equals(value);
    }

    /**
     * <p>
     * Helper method to create widget.
     * </p>
     * 
     * @param dsl
     * @return
     */
    private static Widget widget(Consumer<VirtualStructure> dsl) {
        return new WidgetDelegator(dsl);
    }

    /**
     * @version 2014/09/13 2:07:14
     */
    private static class WidgetDelegator extends Widget {

        private final Consumer<VirtualStructure> delegator;

        /**
         * @param delegator
         */
        private WidgetDelegator(Consumer<VirtualStructure> delegator) {
            this.delegator = delegator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void virtualize(VirtualStructure $〡) {
            delegator.accept($〡);
        }
    }
}
