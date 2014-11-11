/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.bwt;

import static jsx.bwt.SelectStyle2.*;
import js.dom.Element;
import js.dom.UIAction;
import js.dom.UIEvent;
import jsx.bwt.view.ScrollableListView;
import jsx.bwt.view.ScrollableListView.ItemRenderer;
import jsx.bwt.view.SlidableView;
import jsx.event.Subscribe;
import jsx.event.SubscribeUI;
import jsx.model.SelectableModel;
import jsx.model.SelectableModel.Add;
import jsx.model.SelectableModel.Deselect;
import jsx.model.SelectableModel.Remove;

/**
 * @version 2013/07/29 2:00:34
 */
public class Select<M> extends FormUI<Select> {

    /** The associated model. */
    public final SelectableModel<M> model;

    /** The option list. */
    public final SlidableView options;

    /** The associated view. */
    protected final ScrollableListView view;

    /** The view-model binder. */
    private final Binder binder = new Binder();

    /** The select button. */
    private final Button arrow;

    public <E extends Enum> Select(Class<E> type) {
        this(new SelectableModel(type));
    }

    /**
     * <p>
     * Create select form.
     * </p>
     */
    public Select(M... model) {
        this(new SelectableModel(model));
    }

    /**
     * <p>
     * Create select form.
     * </p>
     */
    public Select(SelectableModel<M> selectable) {
        model = selectable;
        model.subscribe(binder);

        form.add(SelectForm).attr("type", "input").attr("placeholder", "Mastery Set Name");

        view = new ScrollableListView(10, 28).provide(binder);
        view.root.add(SelectItemList).subscribe(binder);

        options = root.child(new SlidableView(view));

        arrow = root.child(new Button(Icon.BottomArrow, event -> {
            options.toggle();
        }));
        arrow.root.add(SelectArrow);

        // options.bind(binder);q

        if (model.size() == 0) {
            disable();
        }
    }

    @SubscribeUI(type = UIAction.Key_Down)
    private void selectPrevious() {
        model.selectPrevious();
    }

    @SubscribeUI(type = UIAction.Key_Up)
    private void selectNext() {
        model.selectNext();
    }

    @Override
    @SubscribeUI(type = UIAction.Focus)
    protected void startInput() {
        root.add(FormUIStyle2.Focus);
    }

    @Override
    @SubscribeUI(type = UIAction.Blur)
    protected void endInput() {
        root.remove(FormUIStyle2.Focus);
    }

    /**
     * @version 2013/07/31 1:26:46
     */
    private class Binder implements ItemRenderer {

        @SubscribeUI(type = UIAction.Click)
        private void selectItem(UIEvent event) {
            model.setSelectionIndex(Integer.parseInt(event.target.attr("index")));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int countItem() {
            return model.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void renderItem(int itemIndex, Element element) {
            element.add(SelectItem).attr("index", itemIndex).text(model.get(itemIndex));

            if (itemIndex == model.getSelectionIndex()) {
                element.add(SelectedItem);
            } else {
                element.remove(SelectedItem);
            }
        }

        @Subscribe
        public void select(jsx.model.SelectableModel.Select select) {
            form.val(select.item.toString());

            view.render(select.index);
        }

        @Subscribe
        public void deselect(Deselect event) {
            view.render(event.index);
        }

        @Subscribe
        public void add(Add event) {
            view.provide(this);
            view.render(event.index);

            enable();
        }

        @Subscribe
        public void remove(Remove event) {
            view.provide(this);

            if (model.size() == 0) {
                disable();
            }
        }
    }
}
