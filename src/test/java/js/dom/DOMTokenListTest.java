/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.dom;

import jsx.style.Style;

import org.junit.Test;

/**
 * @version 2014/11/13 9:36:37
 */
public class DOMTokenListTest {

    @Test
    public void add() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        assert !list.contains(CSS1);

        list.add(CSS1);
        assert list.contains(CSS1);
    }

    @Test
    public void addMultiple() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        assert !list.contains(CSS1);
        assert !list.contains(CSS2);

        list.add(CSS1);
        assert list.contains(CSS1);
        assert !list.contains(CSS2);
        assert list.length() == 1;

        list.add(CSS2);
        assert list.contains(CSS1);
        assert list.contains(CSS2);
        assert list.length() == 2;
    }

    @Test
    public void addDuplicated() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        assert !list.contains(CSS1);

        list.add(CSS1);
        assert list.contains(CSS1);
        assert list.length() == 1;

        list.add(CSS1);
        assert list.contains(CSS1);
        assert list.length() == 1;
    }

    @Test
    public void addNull() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        list.add((String) null);
        assert list.contains("null");

        list.add((Class) null);
        assert list.contains("null");
    }

    @Test(expected = EmulateDOMError.class)
    public void addEmpty() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.add("");
    }

    @Test(expected = EmulateDOMError.class)
    public void addWhitespace() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.add("space ");
    }

    @Test
    public void remove() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        list.add(CSS1);
        assert list.contains(CSS1);

        list.remove(CSS1);
        assert !list.contains(CSS1);
        assert list.length() == 0;
    }

    @Test
    public void removeNull() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();
        list.remove((String) null);
        list.remove((Class) null);

        // no error
    }

    @Test(expected = EmulateDOMError.class)
    public void removeEmpty() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.remove("");
    }

    @Test(expected = EmulateDOMError.class)
    public void removeWhitespace() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.remove("space ");
    }

    @Test
    public void toggle() throws Exception {
        DOMTokenList list = new EmulateDOMTokenList();

        list.toggle(CSS1);
        assert list.contains(CSS1);

        list.toggle(CSS1);
        assert !list.contains(CSS1);
        assert list.length() == 0;
    }

    @Test(expected = EmulateDOMError.class)
    public void toggleEmpty() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.toggle("");
    }

    @Test(expected = EmulateDOMError.class)
    public void toggleWhitespace() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.toggle("space ");
    }

    @Test(expected = EmulateDOMError.class)
    public void containsEmpty() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.contains("");
    }

    @Test(expected = EmulateDOMError.class)
    public void containsWhitespace() {
        DOMTokenList list = new EmulateDOMTokenList();
        list.contains("space ");
    }

    private static Style CSS1 = () -> {
    };

    private static Style CSS2 = () -> {
    };
}
