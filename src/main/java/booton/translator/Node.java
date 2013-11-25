/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package booton.translator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version 2013/10/09 15:55:18
 */
class Node {

    /** The re-usable operand. */
    static final Operand END = new OperandExpression(";");

    /** The identified label for this node. */
    final int id;

    /** The debug flag. */
    final Debugger debugger;

    /** The actual operand stack. */
    final LinkedList<Operand> stack = new LinkedList();

    /** The node list. */
    final CopyOnWriteArrayList<Node> incoming = new CopyOnWriteArrayList();

    /** The node list. */
    final CopyOnWriteArrayList<Node> outgoing = new CopyOnWriteArrayList();

    /** The node list. */
    final CopyOnWriteArrayList<Node> dominators = new CopyOnWriteArrayList();

    /** The node list. */
    final CopyOnWriteArrayList<Node> backedges = new CopyOnWriteArrayList();

    /** The try-catch-finally starting node list. */
    final List<TryCatchFinally> tries = new CopyOnWriteArrayList();

    /** The line number. */
    int number = -1;

    /** The previous node. */
    Node previous;

    /** This node is switch starting node. */
    private Switch switchy;

    /** The dominator node. */
    private Node dominator;

    /** The state. */
    private boolean whileFindingDominator;

    /** The flag whether this node has already written or not. */
    private boolean written = false;

    /** The flag whether this node indicates loop condition or not. */
    private boolean loopCondition;

    /** The flag whether this node indicates loop entrance or not. */
    private Node loopEntrance;

    /** The flag whether this node indicates loop exit or not. */
    private boolean loopExit;

    /** The number of additional write calls. */
    private int additionalCalls = 0;

    /** The number of current write calls. */
    private int currentCalls = 0;

    /**
     * @param label
     */
    Node(int id, Debugger debugger) {
        this.id = id;
        this.debugger = debugger;
    }

    /**
     * <p>
     * Helper method to add new operand to the top of operands stack.
     * </p>
     * 
     * @param operand A new operand to add.
     */
    final void addOperand(Object operand) {
        if (operand instanceof Operand) {
            stack.add((Operand) operand);
        } else if (operand instanceof Integer) {
            stack.add(new OperandNumber((Integer) operand));
        } else {
            stack.add(new OperandExpression(operand));
        }
    }

    /**
     * <p>
     * Helper method to add new operand to the top of operands stack.
     * </p>
     * 
     * @param operand A new operand to add.
     */
    final void addOperand(Object operand, Class type) {
        stack.add(new OperandExpression(operand, type));
    }

    /**
     * @param operands
     */
    final void addExpression(Object... operands) {
        for (Object operand : operands) {
            addOperand(operand);
        }
        stack.add(END);
    }

    /**
     * <p>
     * Helper method to chech whether this node has the specified operand or not.
     * </p>
     * 
     * @param test
     * @return
     */
    final boolean has(Operand test) {
        for (Operand operand : stack) {
            if (operand == test) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Helper method to remove the operand which is stored in the specified index from the operands
     * stack.
     * </p>
     * 
     * @param index An index that you want to remove from the operands stack.
     * @return A removed operand.
     */
    final Operand remove(int index) {
        return remove(index, true);
    }

    /**
     * <p>
     * Helper method to remove the operand which is stored in the specified index from the operands
     * stack.
     * </p>
     * 
     * @param index An index that you want to remove from the operands stack.
     * @return A removed operand.
     */
    final Operand remove(int index, boolean processDuplication) {
        // Calculate index
        index = stack.size() - 1 - index;

        if (index < 0) {
            // Remove operand from the previous node if we can.
            //
            // calculated = stack.size() - 1 - index
            // index - stack.size() = -calculated - 1;
            return previous == null || incoming.isEmpty() ? null : previous.remove(-index - 1, processDuplication);
        }

        // Retrieve and remove it
        Operand operand = stack.remove(index);

        if (processDuplication && operand.duplicated) {
            operand.duplicated = false;

            // Duplicate pointer
            stack.add(index, operand);
        }

        // API definition
        return operand;
    }

    /**
     * <p>
     * Helper method to peek the operand which is stored in the specified index from the operands
     * stack.
     * </p>
     * 
     * @param index An index that you want to peek from the operands stack.
     * @return A target operand.
     */
    final Operand peek(int index) {
        // Calculate index
        index = stack.size() - 1 - index;

        if (index < 0) {
            // Peek operand from the previous node if we can.
            //
            // calculated = stack.size() - 1 - index
            // index - stack.size() = -calculated - 1;
            return previous == null || incoming.isEmpty() ? null : previous.peek(-index - 1);
        }

        // Retrieve it
        Operand operand = stack.get(index);

        // API definition
        return operand;
    }

    /**
     * <p>
     * Helper method to set the operand at the specified index from the operands stack.
     * </p>
     * 
     * @param index An index that you want to peek from the operands stack.
     * @return A target operand.
     */
    final Operand set(int index, Operand operand) {
        // Calculate index
        index = stack.size() - 1 - index;

        if (index < 0) {
            // Set operand to the previous node if we can.
            //
            // calculated = stack.size() - 1 - index
            // index - stack.size() = -calculated - 1;
            return previous == null || incoming.isEmpty() ? null : previous.set(-index - 1, operand);
        }

        // Retrieve and remove it
        operand = stack.set(index, operand);

        if (operand.duplicated) {
            operand.duplicated = false;

            // Duplicate pointer
            stack.add(index, operand);
        }

        // API definition
        return operand;
    }

    /**
     * <p>
     * Helper method to add new conditional operand on the top of this stack.
     * </p>
     * 
     * @param left A left operand.
     * @param operator A condition operator.
     * @param right A right operand.
     * @param transition A transition node.
     */
    final void condition(Operand left, int operator, Operand right, Node transition) {
        stack.add(new OperandCondition(left, operator, right, transition));
    }

    /**
     * Helper method to join latest two operands.
     * 
     * @param separator
     * @return Chainable API.
     */
    final Node join(String separator) {
        Operand left = remove(0);
        Operand right = remove(0);

        if (left.infer().type() == char.class) {
            left = new OperandExpression(Javascript.writeMethodCode(String.class, "codePointAt", left, int.class, 0), int.class);
        }

        if (right.infer().type() == char.class) {
            right = new OperandExpression(Javascript.writeMethodCode(String.class, "codePointAt", right, int.class, 0), int.class);
        }

        stack.add(new OperandExpression(right + separator + left, right.infer()));

        // API definition
        return this;
    }

    /**
     * <p>
     * Helper method to enclose current operand.
     * </p>
     * 
     * @return Chainable API.
     */
    final Node enclose() {
        stack.add(new OperandEnclose(remove(0)));

        // API definition
        return this;
    }

    /**
     * Helper method to check whether the specified node dominate this node or not.
     * 
     * @param dominator A dominator node.
     * @return A result.
     */
    final boolean hasDominator(Node dominator) {
        Node current = this;

        while (current != null) {
            if (current == dominator) {
                return true;
            }
            current = current.getDominator();
        }

        // Not Found
        return false;
    }

    /**
     * Compute the immediate dominator of this node.
     * 
     * @return A dominator node. If this node is root, <code>null</code>.
     */
    final Node getDominator() {
        // check cache
        if (dominator == null && !whileFindingDominator) {
            whileFindingDominator = true;

            // We must search a immediate dominator.
            //
            // At first, we can ignore the older incoming nodes.
            List<Node> candidates = new CopyOnWriteArrayList(incoming);

            // compute backedges
            for (Node node : candidates) {
                if (backedges.contains(node)) {
                    candidates.remove(node);
                }
            }

            int size = candidates.size();

            switch (size) {
            case 0: // this is root node
                dominator = null;
                break;

            case 1: // only one incoming node
                dominator = candidates.get(0);
                break;

            default: // multiple incoming nodes
                Node candidate = candidates.get(0);

                search: while (candidate != null) {
                    for (int i = 1; i < size; i++) {
                        if (!candidates.get(i).hasDominator(candidate)) {
                            candidate = candidate.getDominator();
                            continue search;
                        }
                    }

                    dominator = candidate;
                    break;
                }
                break;
            }
            whileFindingDominator = false;
        }

        // API definition
        return dominator;
    }

    /**
     * <p>
     * Detect whether the specified node is traversable from this node.
     * </p>
     * 
     * @param node A target node.
     * @return A result.
     */
    final boolean canReachTo(Node node, Node... exclusionNodes) {
        List<Node> exclusions = Arrays.asList(exclusionNodes);
        Set<Node> recorder = new HashSet();
        recorder.add(this);

        Deque<Node> queue = new ArrayDeque();
        queue.add(this);

        while (!queue.isEmpty()) {
            for (Node out : queue.pollFirst().outgoing) {
                if (out == node) {
                    return true;
                }

                if (!exclusions.contains(out) && recorder.add(out)) {
                    queue.addLast(out);
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Helper method to check whether the specified node is incoming.
     * </p>
     * 
     * @param node
     * @return
     */
    final boolean equalsAsIncoming(Node node) {
        return node == this || incoming.contains(node);
    }

    /**
     * <p>
     * Helper method to check whether the specified node is outgoing.
     * </p>
     * 
     * @param node
     * @return
     */
    final boolean equalsAsOutgoing(Node node) {
        return node == this || outgoing.contains(node);
    }

    /**
     * <p>
     * Helper method to connect nodes each other.
     * </p>
     * 
     * @param node A target node.
     */
    final void connect(Node node) {
        outgoing.addIfAbsent(node);
        node.incoming.addIfAbsent(this);
    }

    /**
     * <p>
     * Helper method to disconnect nodes each other.
     * </p>
     * 
     * @param node A target node.
     */
    final void disconnect(Node node) {
        outgoing.remove(node);
        node.incoming.remove(this);
    }

    /**
     * <p>
     * Create switch statement.
     * </p>
     * 
     * @param defaults A default node.
     * @param keys A case key values.
     * @param cases A list of case nodes.
     */
    final void createSwitch(Node defaults, int[] keys, List<Node> cases) {
        switchy = new Switch(this, defaults, keys, cases);

        // connect enter node with each case node
        for (Node node : cases) {
            if (node != defaults) {
                connect(node);
            }
        }

        // connect enter node with default node
        connect(defaults);
    }

    /**
     * <p>
     * Write script fragment.
     * </p>
     * 
     * @param buffer
     */
    final void write(ScriptWriter buffer) {
        if (!written) {
            written = true;

            if (number != -1) {
                buffer.comment(number);
            }

            // =============================================================
            // Switch Block
            // =============================================================
            // Switch block is independent from other blocks, so we must return at the end.
            if (switchy != null) {
                // execute first to detect default node
                Node exit = switchy.searchExit();

                // enter switch
                buffer.write("switch", "(" + switchy.value + ")", "{");

                // each cases
                for (Node node : switchy.cases()) {
                    for (int value : switchy.values(node)) {
                        buffer.append("case ", value, ":").line();
                    }
                    process(node, buffer);
                }

                // default case
                if (!switchy.noDefault) {
                    buffer.append("default:").line();
                    process(switchy.defaults, buffer);
                }

                // exit switch
                buffer.append("}").line();

                // write following node
                process(exit, buffer);

                return; // must
            }

            // =============================================================
            // Try-Catch-Finally Block
            // =============================================================
            for (int i = 0; i < tries.size(); i++) {
                buffer.write("try", "{");
            }

            // =============================================================
            // Other Block
            // =============================================================
            int outs = outgoing.size();
            int backs = backedges.size();

            if (outs == 0) {
                // end node
                buffer.append(this);
            } else if (outs == 1) {
                // do while or normal
                if (backs == 0) {
                    // normal node with follower
                    buffer.append(this);
                    process(outgoing.get(0), buffer);
                } else {
                    // do while or infinite loop
                    writeDoWhile(buffer);
                }
            } else if (outs == 2) {
                // while, for or if
                if (backs == 0) {
                    writeIf(buffer);
                } else if (backs == 1 && backedges.get(0).outgoing.size() == 1) {
                    writeFor(buffer);
                } else {
                    writeWhile(buffer);
                }
            }

            // =============================================================
            // Try-Catch-Finally Block
            // =============================================================
            for (TryCatchFinally block : tries) {
                buffer.write("}", "catch", "($)", "{");
                buffer.write("$", "=", Javascript.writeMethodCode(Throwable.class, "wrap", Object.class, "$"), ";")
                        .line();

                for (int i = 0; i < block.catches.size(); i++) {
                    Catch current = block.catches.get(i);
                    String variable = current.variable;

                    if (current.exception == null) {
                        // finally block
                        buffer.write(variable, "=", "$;").line();
                        process(current.node, buffer);
                    } else {
                        buffer.write("if", "($ instanceof " + Javascript.computeClassName(current.exception) + ")", "{");
                        buffer.write(variable, "=", "$;").line();
                        process(current.node, buffer);
                        buffer.write("}", "else");

                        if (i + 1 == block.catches.size()) {
                            buffer.write("", "{");
                            buffer.write("throw $;");
                            buffer.write("}");
                        } else {
                            buffer.write(" ");
                        }
                    }
                }
                buffer.write("}"); // close try statement

                Node exit = block.exit;

                if (exit != null) {
                    process(exit, buffer);
                }
            }
        }
    }

    /**
     * <p>
     * Write infinite loop structure.
     * </p>
     * 
     * @param buffer
     */
    private void writeInfiniteLoop(ScriptWriter buffer) {
        // make rewritable this node
        written = false;

        // disconnect from the backedge node of infinite loop
        disconnect(backedges.get(backedges.size() - 1));
        backedges.clear();

        // write script fragment
        buffer.write("for", "(;;)", "{");
        write(buffer);
        buffer.write("}");
    }

    /**
     * <p>
     * Write while structure.
     * </p>
     * 
     * @param buffer
     */
    private void writeWhile(ScriptWriter buffer) {
        Node[] nodes = detectProcessAndFollower();

        if (nodes == null) {
            writeInfiniteLoop(buffer);
        } else {
            setLoopStructureInfo(this, this, nodes[1]);

            // write script fragment
            buffer.write("l" + id + ":", "while", "(" + this + ")", "{");
            process(nodes[0], buffer);
            buffer.write("}").line();
            process(nodes[1], buffer);
        }
    }

    /**
     * <p>
     * Write do-while structure.
     * </p>
     * 
     * @param buffer
     */
    private void writeDoWhile(ScriptWriter buffer) {
        // setup condition expression node
        Node condition = backedges.get(0);

        if (condition.outgoing.size() == 1) {
            writeInfiniteLoop(buffer);
        } else {
            condition.written = true;

            Node follow;

            if (condition.outgoing.get(0) == this) {
                follow = condition.outgoing.get(1);
            } else {
                follow = condition.outgoing.get(0);
            }
            setLoopStructureInfo(this, condition, follow);

            // write script fragment
            buffer.write("l" + id, ":", "do", "{");
            buffer.append(this);
            process(outgoing.get(0), buffer);
            buffer.write("}", "while", "(" + condition + ")");
            condition.process(follow, buffer);
        }
    }

    /**
     * <p>
     * Write for structure.
     * </p>
     * 
     * @param buffer
     */
    private void writeFor(ScriptWriter buffer) {
        Node[] nodes = detectProcessAndFollower();

        if (nodes == null) {
            writeInfiniteLoop(buffer);
        } else {
            // setup update expression node
            Node update = backedges.get(0);
            update.written = true;

            // remove tail semicolon if present
            if (update.stack.peekLast() == END) {
                update.remove(0);
            }

            setLoopStructureInfo(this, update, nodes[1]);
            setLoopStructureInfo(this, this, nodes[1]);

            // write script fragment
            buffer.write("l" + id + ":", "for", "(;", this + ";", update + ")", "{");
            process(nodes[0], buffer);
            buffer.write("}").line();
            process(nodes[1], buffer);
        }
    }

    /**
     * <p>
     * Write if structure.
     * </p>
     * 
     * @param buffer
     */
    private void writeIf(ScriptWriter buffer) {
        OperandCondition condition = (OperandCondition) stack.peekLast();

        if (condition.next == outgoing.get(0)) {
            condition.invert();
        }

        Node then = null;
        Node elze = null;
        Node follow = null;

        if (outgoing.get(0).incoming.size() != 1) {
            // no else
            condition.invert();

            then = outgoing.get(1);
            follow = outgoing.get(0);
        } else if (outgoing.get(1).incoming.size() != 1) {
            // no else
            then = outgoing.get(0);
            follow = outgoing.get(1);
        } else {
            // with else
            then = outgoing.get(0);
            elze = outgoing.get(1);
            follow = dominators.stream().filter(node -> !outgoing.contains(node)).findFirst().orElse(null);

            if (follow != null) {
                follow.currentCalls--;
            }
        }

        // write script fragment
        buffer.write("if", "(" + this + ")", "{");
        process(then, buffer);
        if (elze != null) {
            buffer.write("}", "else", "{");
            process(elze, buffer);
        }
        buffer.write("}").line();
        process(follow, buffer);
    }

    /**
     * <p>
     * Detect a node relationship between this node and the next node.
     * </p>
     * 
     * @param next A next node to write.
     * @param buffer A script code buffer.
     */
    private void process(Node next, ScriptWriter buffer) {
        if (next != null) {
            next.currentCalls++;

            // continue
            if (next.loopCondition && hasDominator(next.loopEntrance)) {
                debugger.print(() -> buffer.comment(id + " -> " + next.id + " Entrance " + next.loopEntrance.id));
                buffer.append("continue l", next.loopEntrance.id, ";").line();
                return;
            }

            // break
            if (!loopCondition && next.loopExit && hasDominator(next.loopEntrance)) {
                debugger.print(() -> buffer.comment(id + " -> " + next.id + " Entrance " + next.loopEntrance.id));
                buffer.append("break l", next.loopEntrance.id, ";").line();
                return;
            }

            // count a number of required write call
            int requiredCalls = next.incoming.size() - next.backedges.size() + next.additionalCalls;

            debugger.print(() -> buffer.comment(id + " -> " + next.id + " (" + next.currentCalls + " of " + requiredCalls + ")"));

            // normal process
            if (requiredCalls <= next.currentCalls) {
                Node dominator = next.getDominator();

                if (dominator == null || dominator == this || next.loopExit) {
                    next.write(buffer);
                }
            }
        }
    }

    /**
     * <p>
     * Detect the follower node.
     * </p>
     * 
     * @return A non-follower node.
     */
    private Node[] detectProcessAndFollower() {
        Node first = outgoing.get(0);
        Node last = outgoing.get(1);
        Node back = backedges.get(backedges.size() - 1);

        if (first.canReachTo(back, getDominator()) && last.canReachTo(back, getDominator())) {
            return null;
        }

        if (backedges.get(0).hasDominator(first)) {
            return new Node[] {first, last};
        } else {
            return new Node[] {last, first};
        }
    }

    /**
     * <p>
     * Analyze structure.
     * </p>
     * 
     * @param entrance
     * @param condition
     * @param exit
     */
    private void setLoopStructureInfo(Node entrance, Node condition, Node exit) {
        condition.loopCondition = true;
        exit.loopExit = true;

        if (exit.loopEntrance == null) {
            exit.loopEntrance = entrance;
        }

        if (condition.loopEntrance == null) {
            condition.loopEntrance = entrance;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();

        for (Operand operand : stack) {
            builder.append(operand.disclose());
        }
        return builder.toString();
    }

    /**
     * @version 2013/01/23 9:25:08
     */
    static class Switch {

        /** The entering node. */
        private final Node enter;

        /** The evaluated value. */
        private final Operand value;

        /** The default node of this switch statement. */
        private final Node defaults;

        /** The case nodes of this switch statement. */
        private final List<Node> cases;

        /** The case value of this switch statement. */
        private final List<Integer> keys = new ArrayList();

        /** Whether this switch has default node or not. */
        private boolean noDefault = false;

        /**
         * <p>
         * Creat switch block infomation holder.
         * </p>
         * 
         * @param enter
         * @param defaults
         * @param keys
         * @param cases
         */
        private Switch(Node enter, Node defaults, int[] keys, List<Node> cases) {
            this.enter = enter;
            this.value = enter.remove(0);
            this.defaults = defaults;
            this.cases = cases;

            for (int key : keys) {
                this.keys.add(key);
            }
        }

        /**
         * <p>
         * Find all case values for the specified node.
         * </p>
         * 
         * @param node A target node.
         * @return A collected case values.
         */
        private List<Integer> values(Node node) {
            CopyOnWriteArrayList<Integer> values = new CopyOnWriteArrayList();

            for (int i = 0; i < cases.size(); i++) {
                if (cases.get(i) == node) {
                    values.addIfAbsent(keys.get(i));
                }
            }
            return values;
        }

        /**
         * <p>
         * Find all unique cases for the specified node.
         * </p>
         * 
         * @param node A target node.
         * @return A collected case values.
         */
        private List<Node> cases() {
            CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList();

            for (int i = 0; i < cases.size(); i++) {
                if (cases.get(i) != defaults) {
                    nodes.addIfAbsent(cases.get(i));
                }
            }
            return nodes;
        }

        /**
         * <p>
         * Search exit node of this switch block.
         * </p>
         * 
         * @return Null or exit node.
         */
        private Node searchExit() {
            // The end node is not default node.
            if (defaults.incoming.size() != 1 && defaults.incoming.contains(enter)) {
                noDefault = true; // default node does not exist
            }

            for (Node node : defaults.incoming) {
                node.addExpression("break");
            }

            if (!noDefault) {
                List<Node> nodes = new LinkedList();
                nodes.addAll(defaults.outgoing);

                while (!nodes.isEmpty()) {
                    Node node = nodes.remove(0);

                    if (node.getDominator() == enter) {
                        // add break statement to each incoming node
                        for (Node incoming : node.incoming) {
                            incoming.addExpression("break");
                        }
                        return node;
                    }
                    nodes.addAll(node.outgoing);
                }
            }
            return defaults;
        }

        /**
         * <p>
         * Helper method to detect special enum method.
         * </p>
         * 
         * @param name
         * @param description
         * @return
         */
        static boolean isEnumSwitchTable(String name, String description) {
            // For Eclipse JDT compiler.
            if (name.startsWith("$SWITCH_TABLE$")) {
                return true;
            }

            // For JDK compiler.
            if (name.startsWith("$SwitchMap$")) {
                return true;
            }
            return false;
        }
    }

    /**
     * @version 2013/11/24 23:28:29
     */
    static class TryCatchFinallyBlocks {

        /** The managed try-catch-finally blocks. */
        private final List<TryCatchFinally> blocks = new ArrayList();

        /**
         * <p>
         * Manage block.
         * </p>
         * 
         * @param start
         * @param end
         * @param catcher
         * @param exception
         */
        void addTryCatchFinallyBlock(Node start, Node end, Node catcher, Class exception) {
            for (TryCatchFinally block : blocks) {
                // The try-catch-finally block which indicates the same start node
                // without error class means finally block.
                // But this translator ignores finally block to use compiler duplicated codes.
                if (exception == null && block.start == start) {
                    // block.finalizer = end;
                    return;
                }

                // The try-catch-finally block which indicates the same start and end nodes
                // means multiple catches.
                if (block.start == start && block.end == end) {
                    block.addCatchBlock(exception, catcher);
                    return;
                }

                // In Java 6 and later, the old jsr and ret instructions are effectively deprecated.
                // These instructions were used to build mini-subroutines inside methods.
                //
                // The try-catch block which indicates the same catch node is copied by compiler,
                // so we must ignore it.
                if (block.catcher == catcher) {
                    return;
                }
            }
            blocks.add(new TryCatchFinally(start, end, catcher, exception));
        }

        /**
         * <p>
         * Preprocess.
         * </p>
         */
        void process() {
            // To analyze try-catch-finally statement tree, we must connect each nodes.
            // But these connections disturb the analysis of other statements (e.g. if, for).
            // So we must disconnect them immediately after analysis of try-catch-finally statement.

            // At first, do connecting only.
            for (TryCatchFinally block : blocks) {
                block.start.connect(block.catcher);

                for (Catch catchBlock : block.catches) {
                    block.start.connect(catchBlock.node);
                }
            }

            // Then, we can analyze.
            for (TryCatchFinally block : blocks) {
                // Associate node with block.
                block.start.tries.add(block);
                block.searchExit();
            }

            // At last, disconnect immediately after analysis.
            for (TryCatchFinally block : blocks) {
                block.start.disconnect(block.catcher);

                for (Catch catchBlock : block.catches) {
                    block.start.disconnect(catchBlock.node);
                }
            }

            // Purge the catch block which is inside loop structure directly.
            for (TryCatchFinally block : blocks) {
                for (Catch catchBlock : block.catches) {
                    Set<Node> recorder = new HashSet();
                    recorder.add(catchBlock.node);

                    Deque<Node> queue = new ArrayDeque();
                    queue.add(catchBlock.node);

                    while (!queue.isEmpty()) {
                        Node node = queue.pollFirst();

                        for (Node out : node.outgoing) {
                            if (out.hasDominator(catchBlock.node)) {
                                if (recorder.add(out)) {
                                    // test next node
                                    queue.add(out);
                                }
                            } else {
                                if (!out.backedges.isEmpty()) {
                                    // purge the catch block from the loop structure
                                    node.disconnect(out);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * <p>
         * Set exception variable name.
         * </p>
         * 
         * @param current A target node.
         * @param variable A variable name.
         */
        void assignVariableName(Node current, String variable) {
            for (TryCatchFinally block : blocks) {
                for (Catch catchBlock : block.catches) {
                    if (catchBlock.node == current) {
                        catchBlock.variable = variable;
                    }
                }
            }
        }
    }

    /**
     * @version 2013/04/11 19:45:29
     */
    static class TryCatchFinally {

        /** The start node. */
        final Node start;

        /** The end node. */
        final Node end;

        /** The catcher node. */
        final Node catcher;

        /** The catch blocks. */
        final List<Catch> catches = new ArrayList();

        /** The exit node. */
        Node exit;

        /**
         * @param start
         * @param end
         * @param catcher
         * @param exception
         */
        private TryCatchFinally(Node start, Node end, Node catcher, Class exception) {
            this.start = start;
            this.end = end;
            this.catcher = catcher;

            addCatchBlock(exception, catcher);
        }

        /**
         * <p>
         * Add catch block.
         * </p>
         * 
         * @param exception
         * @param catcher
         */
        private void addCatchBlock(Class exception, Node catcher) {
            for (Catch block : catches) {
                if (block.exception == exception) {
                    return;
                }
            }
            catches.add(new Catch(exception, catcher));
        }

        /**
         * <p>
         * Search exit node of this try-catch-finally block.
         * </p>
         */
        private void searchExit() {
            Deque<Node> nodes = new ArrayDeque();
            nodes.addAll(end.outgoing);
            nodes.addAll(catcher.outgoing);

            while (!nodes.isEmpty()) {
                Node node = nodes.pollFirst();

                if (node.getDominator() == start) {
                    exit = node;
                    return;
                }
                nodes.addAll(node.outgoing);
            }
        }
    }

    /**
     * @version 2013/04/11 11:32:44
     */
    private static class Catch {

        /** The Throwable class, may be null for finally statmenet. */
        private final Class exception;

        /** The associated node. */
        private final Node node;

        /** The exception variable name. */
        private String variable;

        /**
         * @param exception
         * @param node
         */
        private Catch(Class exception, Node node) {
            this.exception = exception;
            this.node = node;
            this.node.additionalCalls++;
        }
    }
}
