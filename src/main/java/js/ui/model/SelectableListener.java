/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.ui.model;

/**
 * @version 2013/04/05 9:32:19
 */
public interface SelectableListener<T> {

    /**
     * <p>
     * Notify item selection event.
     * </p>
     * 
     * @param item
     */
    public void select(int index, T item);

    /**
     * <p>
     * Notiify item deselection event.
     * </p>
     * 
     * @param item
     */
    public void deselect(int index, T item);

    /**
     * <p>
     * Notify item selection event.
     * </p>
     * 
     * @param item
     */
    public void add(int index, T item);

    /**
     * <p>
     * Notiify item deselection event.
     * </p>
     * 
     * @param item
     * @param index TODO
     */
    public void remove(T item, int index);
}
