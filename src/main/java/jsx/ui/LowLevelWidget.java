/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

import js.dom.UIEvent;
import js.dom.User;
import jsx.style.Style;
import jsx.style.StyleDSL;
import jsx.style.StyleProperty;
import kiss.Disposable;
import kiss.Events;

/**
 * @version 2016/04/07 17:41:32
 */
public abstract class LowLevelWidget<Styles extends StyleDSL, T extends LowLevelWidget<Styles, T>> extends Widget<Styles> {

    /** The disposable list. */
    private List<Disposable> disposables;

    private BooleanProperty hover;

    protected final StyleProperty userStyle = new StyleProperty();

    /**
     * 
     */
    protected LowLevelWidget() {
        super();
    }

    /**
     * @param id
     */
    protected LowLevelWidget(int id) {
        super(id);
    }

    /**
     * @param id
     */
    protected LowLevelWidget(Object id) {
        super(Objects.hash(id));
    }

    public BooleanProperty hover() {
        if (hover == null) {
            hover = new SimpleBooleanProperty(false);

            when(User.MouseEnter).at(WidgetRoot).to(v -> hover.set(true));
            when(User.MouseLeave).at(WidgetRoot).to(v -> hover.set(false));
        }
        return hover;
    }

    /**
     * @return
     */
    public T click(Runnable action) {
        when(User.Click).at(WidgetRoot).take(this::isValid).to(update(e -> action.run()));

        return (T) this;
    }

    /**
     * @return
     */
    public T dbclick(Runnable action) {
        when(User.DoubleClick).at(WidgetRoot).take(this::isValid).to(update(e -> action.run()));

        return (T) this;
    }

    /**
     * @return
     */
    public T hover(Runnable action) {
        return (T) this;
    }

    /**
     * <p>
     * Create {@link Events} for key down.
     * </p>
     * 
     * @return
     */
    public Events<Key> keyDown() {
        return null;
    }

    /**
     * <p>
     * Set key binding aginst the specified action.
     * </p>
     * 
     * @param key A shortcut key stroke.
     * @param action A binding function.
     * @return Chainable API.
     */
    public T shortcut(Key key, Runnable action) {
        if (key != null && action != null) {
            Predicate<UIEvent> byKey = e -> e.which == key.code;

            Events<UIEvent> keyPress = when(User.KeyPress).at(WidgetRoot).take(byKey);
            Events<UIEvent> keyUp = when(User.KeyUp).at(WidgetRoot).take(byKey);
            // All js environment never fire keypress event in IME mode.
            // So the following code can ignore key event while IME is on.
            Events<UIEvent> keyInput = keyUp.skipUntil(keyPress).take(1).repeat();

            // activate shortcut command
            disposeLater(keyInput.take(this::isValid).to(update(e -> action.run())));
        }
        return (T) this;
    }

    public T enableIf(ObservableValue<Boolean> condition) {

        return (T) this;
    }

    public T disableIf(Supplier<Boolean> condition) {
        return (T) this;
    }

    protected BooleanProperty disabled = new SimpleBooleanProperty();

    public T disableIf(Events<Boolean> condition) {
        condition.toValue(disabled);

        return (T) this;
    }

    public T showIf(ObservableValue<Boolean> condition) {

        return (T) this;
    }

    public T showIf(BooleanSupplier condition) {
        return (T) this;
    }

    public T hideIf(ObservableValue<Boolean> condition) {
        return (T) this;
    }

    public T style(Style style) {
        userStyle.setValue(style);

        return (T) this;
    }

    public T styleIf(BooleanBinding condition, Style style) {

        return (T) this;
    }

    public T popupIf(Events<Boolean> condition, Widget widget) {
        return (T) this;
    }

    /**
     * <p>
     * Helper method to create cleanup holder.
     * </p>
     */
    protected final void disposeLater(Disposable disposable) {
        if (disposables == null) {
            disposables = new ArrayList();
        }
        disposables.add(disposable);
    }

    /**
     * <p>
     * Check whether this {@link LowLevelWidget} is valid or not.
     * </p>
     * 
     * @param e Ignore this value.
     * @return A result.
     */
    protected boolean isValid(UIEvent e) {
        return true;
    }
}
