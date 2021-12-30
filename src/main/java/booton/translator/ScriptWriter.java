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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import booton.BootonConfiguration;
import kiss.I;

/**
 * @version 2013/11/26 11:29:00
 */
class ScriptWriter {

    /** The optimization flag. */
    private final BootonConfiguration config = I.make(BootonConfiguration.class);

    /** The actual buffer. */
    private final StringBuilder buffer = new StringBuilder();

    private int mark = 0;

    /** The current depth of indentation for debug. */
    private int depth = 0;

    /**
     * <p>
     * Append debug infomation.
     * </p>
     * 
     * @param owner
     * @param methodName
     * @param description
     */
    public void debug(Class owner, String methodName, String description) {
        if (!config.compression) {
            buffer.append("// ");
            buffer.append(owner.getName()).append("#").append(methodName).append("(");

            Type type = Type.getMethodType(description);
            Type[] args = type.getArgumentTypes();

            for (int i = 0; i < args.length; i++) {
                buffer.append(args[i].getClassName());

                if (i < args.length - 1) {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
            line();
        }
    }

    /**
     * <p>
     * Write comment.
     * </p>
     * 
     * @param comment
     */
    public ScriptWriter comment(Object comment) {
        if (!config.compression) {
            buffer.append("// ");
            write(comment);
            line();
        }

        return this;
    }

    /**
     * <p>
     * Formt line for debug.
     * </p>
     * 
     * @return A chainable API.
     */
    public ScriptWriter line() {
        if (!config.compression) {
            // write line separator
            buffer.append("\r\n");

            // write indent
            for (int i = 0; i < depth; i++) {
                buffer.append('\t');
            }
        }
        return this;
    }

    /**
     * <p>
     * Format code for degug.
     * </p>
     * 
     * @return A chainable API.
     */
    private ScriptWriter startIndent() {
        depth++;
        return line();
    }

    /**
     * <p>
     * Format code for degug.
     * </p>
     * 
     * @return A chainable API.
     */
    private ScriptWriter endIndent() {
        depth--;
        return line();
    }

    /**
     * Helper method to write script source.
     * 
     * @param fragments
     * @return
     */
    public ScriptWriter append(Object... fragments) {
        for (Object fragment : fragments) {
            write(fragment);
        }

        // API definition
        return this;
    }

    /**
     * Helper method to write script source.
     * 
     * @param position
     * @param fragment
     * @return
     */
    public ScriptWriter insertAt(int position, Object fragment) {
        buffer.insert(position, fragment);

        // API definition
        return this;
    }

    /**
     * <p>
     * Helper method to write script code with white space.
     * </p>
     */
    public ScriptWriter write(Object... fragments) {
        for (int i = 0; i < fragments.length; i++) {
            write(fragments[i], false);

            if (!config.compression && i + 1 != fragments.length) {
                write(" ");
            }
        }
        // API definition
        return this;
    }

    /**
     * Helper method to write String literal.
     * 
     * @param fragments
     * @return
     */
    public ScriptWriter string(Object literal) {
        return append('"', literal, '"');
    }

    /**
     * <p>
     * Helper method to write separator ",".
     * </p>
     */
    public ScriptWriter separator() {
        return append(",").line();
    }

    /**
     * <p>
     * Optimize source code.
     * </p>
     * <ol>
     * <li>Remove tail whitespaces.</li>
     * <li>Remove tail separator comma.</li>
     * <li>Remove tail "return;" expression.</li>
     * </ol>
     */
    public void optimize() {
        remove(",");
        remove(";");
        remove(" ");
        remove("return");
    }

    /**
     * <p>
     * Remove tailing characters if it is matched.
     * </p>
     * 
     * @param c A character to remove.
     * @return A last position.
     */
    private int remove(String chracters) {
        int last = removeWhitespaces();
        int length = chracters.length() - 1;

        if (last < length) {
            return last;
        }

        if (buffer.substring(last - length).equals(chracters)) {
            buffer.delete(last - length, last + 1);
            last -= length;
        }
        return last;
    }

    /**
     * <p>
     * Remove tailing whitespaces.
     * </p>
     * 
     * @return A last position.
     */
    private int removeWhitespaces() {
        int last = buffer.length() - 1;

        if (last < 0) {
            return 0;
        }

        while (Character.isWhitespace(buffer.charAt(last))) {
            buffer.deleteCharAt(last--);
        }
        return last;
    }

    /**
     * <p>
     * Remove tailing whitespaces and separator.
     * </p>
     * 
     * @return A last position.
     */
    private int removeSeparator() {
        int last = removeWhitespaces();

        if (buffer.charAt(last) == ',') {
            buffer.deleteCharAt(last);
            last = removeWhitespaces();
        }
        return last;
    }

    /**
     * Helper method to write script source.
     * 
     * @param fragment
     * @return
     */
    private ScriptWriter write(Object fragment) {
        return write(fragment, true);
    }

    /**
     * Helper method to write script source.
     * 
     * @param fragment
     * @return
     */
    private ScriptWriter write(Object fragment, boolean line) {
        if (fragment instanceof Map) {
            write("{");
            Iterator<Entry> iterator = ((Map) fragment).entrySet().iterator();

            if (iterator.hasNext()) {
                Entry entry = iterator.next();
                write(entry.getKey(), ":", entry.getValue());

                while (iterator.hasNext()) {
                    separator();

                    entry = iterator.next();
                    write(entry.getKey(), ":", entry.getValue());
                }
            }
            write("}");
        } else if (fragment instanceof List) {
            write("[");

            Iterator<Entry> iterator = ((List) fragment).iterator();

            if (iterator.hasNext()) {
                write(iterator.next());

                while (iterator.hasNext()) {
                    write(",", iterator.next());
                }
            }
            write("]");
        } else {

            String value = fragment.toString();
            int length = value.length();

            if (length != 0) {
                switch (value.charAt(0)) {
                case '}':
                case ']':
                    do {
                        remove(",");
                    } while (buffer.charAt(buffer.length() - 1) == ',');
                    break;
                }
            }

            // brace makes indentation
            if (length != 0 && value.charAt(0) == '}') {
                int last = buffer.length() - 1;
                int lastNonSpace = last;

                while (Character.isWhitespace(buffer.charAt(lastNonSpace))) {
                    lastNonSpace--;
                }

                if (last == lastNonSpace) {
                    endIndent();
                } else if (buffer.charAt(lastNonSpace) == '{') {
                    buffer.delete(lastNonSpace + 1, last + 1);
                    depth--;
                } else {
                    buffer.deleteCharAt(last);
                    depth--;
                }
            }

            // write actual code
            buffer.append(value);

            // brace makes indentation
            if (length != 0) {
                char last = value.charAt(length - 1);

                switch (last) {
                case '{':
                    startIndent();
                    break;

                case ';':
                    if (line) line();
                    break;
                }
            }
        }

        // API definition
        return this;
    }

    /**
     * Remove semicolon at the end.
     */
    void literalize() {
        int index = buffer.length() - 1;

        if (index != -1 && buffer.charAt(index) == ';') {
            buffer.deleteCharAt(index);
        }
    }

    /**
     * Clear data.
     */
    ScriptWriter clear() {
        buffer.delete(0, buffer.length());

        // API definition
        return this;
    }

    /**
     * <p>
     * Mark the current position.
     * </p>
     * 
     * @return
     */
    ScriptWriter mark() {
        mark = buffer.length();

        // API definition
        return this;
    }

    String toFragment() {
        return buffer.substring(mark);
    }

    /**
     * <p>
     * Calcurate size of this code.
     * </p>
     * 
     * @return
     */
    public int length() {
        return buffer.length();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return buffer.toString();
    }
}
