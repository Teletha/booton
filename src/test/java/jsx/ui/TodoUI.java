/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.ui;

import static booton.reactive.FunctionHelper.*;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import jsx.ui.TodoTasks.Filter;
import jsx.ui.TodoTasks.Task;
import jsx.ui.TodoUIStyle.BUTTONS;
import jsx.ui.TodoUIStyle.FOOTER;
import booton.reactive.css.DynamicStyle;

/**
 * @version 2014/09/01 15:14:06
 */
public class TodoUI extends Widget1<TodoTasks> {

    /** Reassign to meaningful name. */
    private final TodoTasks todos = model1;

    /** The completed tasks. */
    final IntegerBinding completedSize = Bindings.size(todos.list.filtered(Task::isCompleted));

    /** The incompleted tasks. */
    final IntegerBinding incompletedSize = Bindings.size(todos.list.filtered(not(Task::isCompleted)));

    /** The todo size state. */
    private final BooleanBinding exceedSize = todos.list.sizeProperty().greaterThan(10);

    /** The selected filter style. */
    private final DynamicStyle<Filter> selectedFileter = new DynamicStyle(todos.filter) {

        {
            // font.bold();
        }
    };

    /** The input field. */
    final Input input = new Input()
            .disableIf(exceedSize)
            .shortcut(Key.ENTER, this::add)
            .placeholder(Bindings.when(exceedSize).then("要件は10件まで").otherwise("新しい要件"));

    /** The filter button. */
    final Button all = new Button().label("all").click(todos::showAll).style(selectedFileter.is(Filter.All));

    /** The filter button. */
    final Button active = new Button()
            .label("active")
            .click(todos::showActive)
            .style(selectedFileter.is(Filter.Active));

    /** The filter button. */
    final Button completed = new Button()
            .label("completed")
            .click(todos::showCompleted)
            .style(selectedFileter.is(Filter.Completed));

    /** The clear button. */
    final Button clear = new Button()
            .label("clear completed (", completedSize, ")")
            .showIf(completedSize.greaterThan(0))
            .click(todos::removeCompleted);

    /**
     * Add todo task.
     */
    private void add() {
        String value = input.value.get();

        if (value != null && value.length() != 0) {
            todos.list.add(new Task(value));

            input.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void virtualize(VirtualStructure $〡) {
        int imcompleted = incompletedSize.get();

        $〡.asis.〡(input);
        $〡.vbox.〡(Item.class, todos);
        $〡.hbox.〡﹟(FOOTER.class).〡(() -> {
            $〡.hbox.〡(imcompleted, imcompleted < 2 ? " item" : "items", " left");
            $〡.hbox.〡﹟(BUTTONS.class).〡(all, active, completed);
            $〡.asis.〡(clear);
        });
    }

    /**
     * @version 2014/09/01 11:31:37
     */
    class Item extends Widget1<Task> {

        /** The edit mode. */
        private BooleanProperty editing = new SimpleBooleanProperty();

        /** The todo text. */
        Output text = new Output(model1.contents).hideIf(editing).dbclick(this::startEdit);

        /** The remove button. */
        Button delete = new Button().label("×").showIf(text.hover).click($(todos.list::remove, model1));

        /** The editable todo text. */
        Input edit = new Input(model1.contents).showIf(editing).shortcut(Key.ENTER, this::finishEdit);

        /**
         * {@inheritDoc}
         */
        @Override
        protected void virtualize(VirtualStructure $〡) {
            $〡.sbox.〡(text, delete);
            $〡.asis.〡(edit);
        }

        /**
         * 
         */
        @ModelModifier
        protected void startEdit() {
            editing.set(true);
        }

        /**
         * 
         */
        @ModelModifier
        protected void finishEdit() {
            editing.set(false);
            model1.contents.set(edit.value.get());
        }
    }
}
