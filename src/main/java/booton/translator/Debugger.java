/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package booton.translator;

import static booton.translator.CompilerRecorder.*;
import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.objectweb.asm.AnnotationVisitor;

import booton.translator.Node.TryCatchFinally;

/**
 * @version 2014/06/07 15:00:34
 */
public class Debugger extends AnnotationVisitor {

    /** The processing environment. */
    private static final boolean whileTest;

    /** The list for debug patterns. */
    private static final List<Pattern[]> patterns = new ArrayList();

    /** The current debugger. */
    private static Debugger debugger;

    // initialization
    static {
        // enable(".+\\$OffsetIdPrinterParser", "parse");

        boolean flag = false;

        for (StackTraceElement element : new Error().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                flag = true;
                break;
            }
        }
        whileTest = flag;
    }

    /** The use flag. */
    private boolean enable = false;

    /** The use flag. */
    private boolean firstTime = true;

    /** Can i use getDominator safely? */
    private boolean dominatorSafe = false;

    /**
     * 
     */
    private Debugger() {
        super(ASM9);

        // update
        debugger = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(String methodName, Object type) {
        if (!patterns.isEmpty()) {
            Class clazz = (Class) type;

            Iterator<Pattern[]> iterator = patterns.iterator();

            while (iterator.hasNext()) {
                Pattern[] patterns = iterator.next();

                if (patterns[0].matcher(clazz.getName()).matches() && patterns[1].matcher(methodName).matches()) {
                    enable = true;
                    return;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        enable = true;
    }

    /**
     * <p>
     * Register current processing target.
     * </p>
     * 
     * @param style A class name regex.
     * @param methodName A method name regex.
     */
    public static void enable() {
        debugger.enable = true;
    }

    /**
     * <p>
     * Register debug target.
     * </p>
     * 
     * @param className A class name regex.
     * @param methodName A method name regex.
     */
    public static void enable(String className, String methodName) {
        patterns.add(new Pattern[] {Pattern.compile(className), Pattern.compile(methodName)});
    }

    /**
     * @return
     */
    public static boolean isEnable() {
        if (debugger.enable && debugger.firstTime) {
            debugger.firstTime = false;

            printHeader(false);
        }
        return debugger.enable;
    }

    /**
     * <p>
     * Print debug message.
     * </p>
     * 
     * @param values
     */
    public static void info(Object... values) {
        StringBuilder text = new StringBuilder();
        CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList();

        for (Object value : values) {
            if (value instanceof Node) {
                Node node = (Node) value;
                nodes.addIfAbsent(node);
                text.append("n").append(node.id);
            } else if (value instanceof List) {
                List<Node> list = (List<Node>) value;

                for (Node node : list) {
                    nodes.addIfAbsent(node);
                }
            } else if (value instanceof Set) {
                Set<Node> list = (Set<Node>) value;

                for (Node node : list) {
                    nodes.addIfAbsent(node);
                }
            } else {
                text.append(value);
            }
        }
        text.append("   ").append(link(false));

        System.out.println(text);
        System.out.println(format(nodes));
    }

    /**
     * <p>
     * Print debug message.
     * </p>
     * 
     * @param values
     */
    public static void print(Object... values) {
        if (isEnable()) {
            info(values);
        }
    }

    /**
     * <p>
     * Print method info as header like.
     * </p>
     */
    public static void printHeader(boolean safe) {
        debugger.dominatorSafe = safe;

        if (isEnable()) {
            System.out.println("==== " + link(true) + " ====");
        }
    }

    /**
     * <p>
     * Create link expression.
     * </p>
     * 
     * @return
     */
    private static String link(boolean head) {
        String methodName;

        if (whileTest) {
            String testClassName = computeTestClassName(getScript().source);
            String testMethodName = computeTestMethodName(testClassName);

            methodName = testMethodName == null ? getMethodName() : testMethodName;
        } else {
            methodName = getMethodName();
        }

        if (!methodName.startsWith("<")) {
            methodName = "#".concat(methodName);
        }
        return "(" + getScript().source.getName() + ".java:" + (head ? getMethodLine() : getLine()) + ") " + methodName;
    }

    /**
     * @param message
     */
    public static void print(Object message) {
        if (isEnable()) {
            System.out.println(message);
        }
    }

    /**
     * <p>
     * Dump node tree.
     * </p>
     * 
     * @param node
     */
    public static void print(Node node) {
        if (node != null) {
            print(Collections.singletonList(node));
        }
    }

    /**
     * <p>
     * Dump node tree.
     * </p>
     * 
     * @param nodes
     */
    public static void print(List<Node> nodes) {
        if (isEnable()) {
            System.out.println(format(nodes));
        }
    }

    /**
     * <p>
     * Dump node tree.
     * </p>
     * 
     * @param nodes
     */
    public static void print(String text, List<Node> nodes) {
        print(text);
        print(nodes);
    }

    /**
     * <p>
     * Compute actual test class name.
     * </p>
     * 
     * @param clazz
     * @return
     */
    private static String computeTestClassName(Class clazz) {
        String name = clazz.getName();

        int index = name.indexOf('$');

        if (index != -1) {
            name = name.substring(0, index);
        }
        return name;
    }

    /**
     * <p>
     * Compute actual test method name.
     * </p>
     * 
     * @param testClassName
     * @return
     */
    private static String computeTestMethodName(String testClassName) {
        for (StackTraceElement element : new Error().getStackTrace()) {
            if (element.getClassName().equals(testClassName)) {
                return element.getMethodName();
            }
        }
        return null;
    }

    /**
     * <p>
     * Helper method to format node tree.
     * </p>
     * 
     * @param nodes
     * @return
     */
    private static String format(List<Node> nodes) {
        Set<TryCatchFinally> tries = new LinkedHashSet();

        for (Node node : nodes) {
            if (node.tries != null) {
                tries.addAll(node.tries);
            }
        }

        // compute max id length
        int max = 1;

        for (Node node : nodes) {
            max = Math.max(max, String.valueOf(node.id).length());
        }

        int incoming = 0;
        int outgoing = 0;
        int backedge = 0;

        for (Node node : nodes) {
            incoming = Math.max(incoming, node.incoming.size() * max + (node.incoming.size() - 1) * 2);
            outgoing = Math.max(outgoing, node.outgoing.size() * max + (node.outgoing.size() - 1) * 2);
            backedge = Math.max(backedge, node.backedges.size() * max + (node.backedges.size() - 1) * 2);
        }

        Formatter format = new Formatter();

        for (Node node : nodes) {
            StringBuilder tryFlow = new StringBuilder();

            for (TryCatchFinally block : tries) {
                if (block.start == node) {
                    tryFlow.append("s");
                } else if (block.end == node) {
                    tryFlow.append("e");
                } else if (block.catcher == node) {
                    tryFlow.append("c");
                } else if (block.exit == node) {
                    tryFlow.append("x");
                } else {
                    tryFlow.append("  ");
                }
            }

            format.write(String.valueOf(node.id), max);
            format.write("  in : ");
            format.formatNode(node.incoming, incoming);
            format.write("out : ");
            format.formatNode(node.outgoing, outgoing);
            format.write("dom : ");
            format.formatNode(list(getDominator(node)), 1);
            format.write("doms : ");
            format.formatNode(node.dominators, node.dominators.size());
            format.write("prev : ");
            format.formatNode(list(node.previous), 1);
            format.write("next : ");
            format.formatNode(list(node.next), 1);
            format.write("dest : ");
            format.formatNode(list(node.destination), 1);

            if (backedge != 0) {
                format.write("back : ");
                format.formatNode(node.backedges, backedge);
            }
            if (!tries.isEmpty()) {
                format.write("try : ");
                format.write(tryFlow.toString(), tries.size() * 2 + 2);
            }
            format.write("code : ");
            format.formatCodeFragment(node.stack);
            format.write("\r\n");
        }
        return format.toString();
    }

    /**
     * <p>
     * Create single item list.
     * </p>
     * 
     * @param node
     * @return
     */
    private static List<Node> list(Node node) {
        if (node == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(node);
    }

    /**
     * <p>
     * Helper method to compute dominator node.
     * </p>
     * 
     * @param target
     * @return
     */
    private static Node getDominator(Node target) {
        if (debugger.dominatorSafe) {
            return target.getDominator();
        } else {
            return getDominator(target, new HashSet());
        }
    }

    /**
     * Compute the immediate dominator of this node.
     * 
     * @return A dominator node. If this node is root, <code>null</code>.
     */
    private static Node getDominator(Node target, Set<Node> nodes) {
        if (!nodes.add(target)) {
            return null;
        }

        // check cache
        // We must search a immediate dominator.
        //
        // At first, we can ignore the older incoming nodes.
        List<Node> candidates = new CopyOnWriteArrayList(target.incoming);

        // compute backedges
        for (Node node : candidates) {
            if (target.backedges.contains(node)) {
                candidates.remove(node);
            }
        }

        int size = candidates.size();

        switch (size) {
        case 0: // this is root node
            return null;

        case 1: // only one incoming node
            return candidates.get(0);

        default: // multiple incoming nodes
            Node candidate = candidates.get(0);

            search: while (candidate != null) {
                for (int i = 1; i < size; i++) {
                    if (!hasDominator(candidates.get(i), candidate, nodes)) {
                        candidate = getDominator(candidate, nodes);
                        continue search;
                    }
                }
                return candidate;
            }
            return null;
        }
    }

    /**
     * Helper method to check whether the specified node dominate this node or not.
     * 
     * @param dominator A dominator node.
     * @return A result.
     */
    private static boolean hasDominator(Node target, Node dominator, Set<Node> nodes) {
        Node current = target;

        while (current != null) {
            if (current == dominator) {
                return true;
            }
            current = getDominator(current, nodes);
        }

        // Not Found
        return false;
    }

    /**
     * @version 2012/11/30 16:09:26
     */
    private static final class Formatter {

        /** The actual buffer. */
        private final StringBuilder builder = new StringBuilder();

        /** The tab length. */
        private final int tab = 8;

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void write(String message) {
            builder.append(message);
        }

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void write(String message, int max) {
            builder.append(message);

            // calcurate required tab
            int requireTab = 1;

            while (requireTab * tab <= max) {
                requireTab++;
            }

            // calcurate remaining spaces
            int remaining = requireTab * tab - message.length();
            int actualTab = 0;

            while (0 < remaining - tab * actualTab) {
                actualTab++;
            }

            for (int i = 0; i < actualTab; i++) {
                builder.append('\t');
            }
        }

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void formatNode(List<Node> nodes, int max) {
            StringBuilder builder = new StringBuilder("[");

            for (int i = 0; i < nodes.size(); i++) {
                builder.append(String.valueOf(nodes.get(i).id));

                if (i != nodes.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");

            write(builder.toString(), max + 2);
        }

        /**
         * Helper method to format code fragment.
         * 
         * @param operands
         */
        private void formatCodeFragment(List<Operand> operands) {
            for (int i = 0; i < operands.size(); i++) {
                Operand operand = operands.get(i);
                builder.append(operand).append(" [").append(type(operand)).append("]");

                if (i != operands.size() - 1) {
                    builder.append(" ");
                }
            }
        }

        /**
         * <p>
         * Helper method to detect type of {@link Operand}.
         * </p>
         * 
         * @param operand
         * @return
         */
        private String type(Operand operand) {
            if (operand instanceof OperandString) {
                return "String";
            } else if (operand instanceof OperandExpression) {
                return "Expression";
            } else if (operand instanceof OperandCondition) {
                OperandCondition condition = (OperandCondition) operand;

                StringBuilder builder = new StringBuilder("Condition then ").append(condition.then.id);

                if (condition.elze != null) {
                    builder.append(" else ").append(condition.elze.id);
                }
                return builder.toString();
            } else if (operand instanceof OperandArray) {
                return "Array";
            } else if (operand instanceof OperandNumber) {
                return "Number";
            } else if (operand instanceof OperandEnclose) {
                return type(((OperandEnclose) operand).value) + " in Enclose";
            } else if (operand instanceof OperandAmbiguousZeroOneTernary) {
                return "TernaryCondition";
            }
            return "";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
