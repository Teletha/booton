/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.dom;

/**
 * @version 2013/07/01 21:30:14
 */
public class EmulateText extends Text {

    /** The text. */
    private String text;

    /**
     * @param contents
     */
    public EmulateText(Object contents) {
        this.text = String.valueOf(contents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> T appedChild(T newNode) {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> T insertBefore(T newNode, Object referenceNode) {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node firstChild() {
        // API definition
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node lastChild() {
        // API definition
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String textContent() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void textContent(String textContent) {
        text = String.valueOf(textContent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}
