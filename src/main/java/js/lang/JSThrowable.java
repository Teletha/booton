/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.lang;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import js.util.ArrayList;
import booton.translator.JavaAPIProvider;

/**
 * @version 2013/01/19 9:27:51
 */
@JavaAPIProvider(Throwable.class)
class JSThrowable {

    /** The error message. */
    private final String message;

    /** The cause error. */
    private Throwable cause;

    /** The stacktrace info. */
    private StackTraceElement[] stacktrace;

    /**
     * 
     */
    public JSThrowable() {
        this("", null);
    }

    /**
     * @param message
     */
    public JSThrowable(String message) {
        this(message, null);
    }

    /**
     * @param cause
     */
    public JSThrowable(Throwable cause) {
        this("", cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JSThrowable(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
        this.stacktrace = createStackTrace(Debugger.error());
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public JSThrowable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public String getLocalizedMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Provides programmatic access to the stack trace information printed by
     * {@link #printStackTrace()}. Returns an array of stack trace elements, each representing one
     * stack frame. The zeroth element of the array (assuming the array's length is non-zero)
     * represents the top of the stack, which is the last method invocation in the sequence.
     * Typically, this is the point at which this throwable was created and thrown. The last element
     * of the array (assuming the array's length is non-zero) represents the bottom of the stack,
     * which is the first method invocation in the sequence.
     * <p>
     * Some virtual machines may, under some circumstances, omit one or more stack frames from the
     * stack trace. In the extreme case, a virtual machine that has no stack trace information
     * concerning this throwable is permitted to return a zero-length array from this method.
     * Generally speaking, the array returned by this method will contain one element for every
     * frame that would be printed by {@code printStackTrace}. Writes to the returned array do not
     * affect future calls to this method.
     * 
     * @return an array of stack trace elements representing the stack trace pertaining to this
     *         throwable.
     * @since 1.4
     */
    public StackTraceElement[] getStackTrace() {
        return stacktrace;
    }

    /**
     * Sets the stack trace elements that will be returned by {@link #getStackTrace()} and printed
     * by {@link #printStackTrace()} and related methods. This method, which is designed for use by
     * RPC frameworks and other advanced systems, allows the client to override the default stack
     * trace that is either generated by {@link #fillInStackTrace()} when a throwable is constructed
     * or deserialized when a throwable is read from a serialization stream.
     * <p>
     * If the stack trace of this {@code Throwable}
     * {@linkplain Throwable#Throwable(String, Throwable, boolean, boolean) is not writable},
     * calling this method has no effect other than validating its argument.
     * 
     * @param stackTrace the stack trace elements to be associated with this {@code Throwable}. The
     *            specified array is copied by this call; changes in the specified array after the
     *            method invocation returns will have no affect on this {@code Throwable}'s stack
     *            trace.
     * @throws NullPointerException if {@code stackTrace} is {@code null} or if any of the elements
     *             of {@code stackTrace} are {@code null}
     * @since 1.4
     */
    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stacktrace = stackTrace;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public void printStackTrace() {

    }

    /**
     * <p>
     * Create stack trace ement from the specified {@link NativeError}.
     * </p>
     * 
     * @param error
     * @return
     */
    static StackTraceElement[] createStackTrace(NativeError error) {
        Pattern pattern = null;

        List<String> lines = new ArrayList(Arrays.asList(error.getStackTrace().split("\n")));

        if (error.hasProperty("columnNumber")) {
            // firefox
            pattern = Pattern.compile("\\.?(.+)?@(.+):(.+)");
            lines = lines.subList(0, lines.size() - 2);
        } else if (error.hasProperty("sourceURL")) {
            // webkit
        } else if (error.hasProperty("number")) {
            // ie
            pattern = Pattern.compile("\\s*at\\s*(.+)\\s\\((.+):(.+):(.+)\\)");
            lines.remove(0);
        } else {
            // blink
            pattern = Pattern.compile("\\s*at\\s*([^\\s]+).+\\((.+):(.+):(.+)\\)");
            lines.remove(0);
        }

        StackTraceElement[] elements = new StackTraceElement[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            Matcher matcher = pattern.matcher(lines.get(i));

            if (matcher.matches()) {
                String method = matcher.group(1);
                int index = method.lastIndexOf(".");
                method = index == -1 ? method : method.substring(index + 1);

                elements[i] = new StackTraceElement("", method, matcher.group(2), Integer.parseInt(matcher.group(3)));
            }
        }
        return elements;
    }
}