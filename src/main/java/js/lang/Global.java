/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import booton.translator.Javascript;
import booton.translator.Translator;
import js.dom.Document;
import js.dom.History;
import js.dom.Location;
import js.dom.Window;
import js.lang.builtin.JSON;
import js.lang.builtin.Storage;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;

/**
 * <p>
 * Define global objects and static methods in Booton environment.
 * </p>
 * 
 * @version 2015/09/29 0:52:18
 */
public class Global {

    /** The booton root object. */
    public static NativeObject boot;

    /** The global object in web environment. */
    public static Window window = emulate(Window.class);

    /** The root document in web environment. */
    public static Document document = emulate(Document.class);

    /**
     * <p>
     * Returns a reference to the History object, which provides an interface for manipulating the
     * browser session history (pages visited in the tab or frame that the current page is loaded
     * in).
     * </p>
     */
    public static History history;

    /**
     * <p>
     * Returns a Location object, which contains information about the URL of the document and
     * provides methods for changing that URL. You can also assign to this property to load another
     * URL.
     * </p>
     */
    public static Location location;

    /**
     * <p>
     * localStorage is the same as sessionStorage with same same-origin rules applied but it is
     * persistent. localStorage was introduced in Firefox 3.5.
     * </p>
     */
    public static Storage localStorage = emulate(Storage.class);

    /**
     * <p>
     * This is a global object (sessionStorage) that maintains a storage area that's available for
     * the duration of the page session. A page session lasts for as long as the browser is open and
     * survives over page reloads and restores. Opening a page in a new tab or window will cause a
     * new session to be initiated.
     * </p>
     */
    public static Storage sessionStorage = emulate(Storage.class);

    /**
     * <p>
     * The JSON object contains methods for converting values to JavaScript Object Notation (JSON)
     * and for converting JSON to values.
     * </p>
     */
    public static final JSON JSON = emulate(JSON.class);

    /**
     * <p>
     * The performance API.
     * </p>
     */
    public static NativePerformance performance = new NativePerformance();

    /**
     * <p>
     * Create emulater object of the specified type.
     * </p>
     * 
     * @param string A object type name.
     * @return A created Object.
     */
    private static <T> T emulate(Class type) {
        try {
            Class emulater = Class.forName(type.getPackage().getName() + ".Emulate" + type.getSimpleName());

            return (T) I.make(emulater);
        } catch (ClassNotFoundException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * The requestAnimationFrame function tells the browser that you wish to perform an animation
     * and requests that the browser call a specified function to update an animation before the
     * next repaint. The method takes as an argument a callback to be invoked before the repaint.
     * </p>
     * <p>
     * You should call this method whenever you're ready to update your animation onscreen. This
     * will request that your animation function be called before the browser performs the next
     * repaint. The number of callbacks is usually 60 times per second, but will generally match the
     * display refresh rate in most web browsers as per W3C recommendation. The callback rate may be
     * reduced to a lower rate when running in background tabs.
     * </p>
     * 
     * @param callback A parameter specifying a function to call when it's time to update your
     *            animation for the next repaint. The callback has one single argument, a
     *            DOMHighResTimeStamp, which indicates the current time for when
     *            requestAnimationFrame starts to fire callbacks.
     */
    public static native void requestAnimationFrame(Runnable callback);

    /**
     * <p>
     * Calls a function or executes a code snippet after specified delay.
     * </p>
     * 
     * @param function A callable function.
     * @param delay A delay time.
     * @return A timeout id.
     */
    public static long setTimeout(Runnable runnable, long delay) {
        return I.make(TaskScheduler.class).add(new NativeFunction(runnable).bind(runnable), delay);
    }

    /**
     * <p>
     * Clears the delay set by setTimeout().
     * </p>
     * 
     * @param timeoutId The ID of the timeout you wish to clear, as returned by setTimeout().
     */
    public static void clearTimeout(long timeoutId) {
        I.make(TaskScheduler.class).remove(timeoutId);
    }

    /**
     * <p>
     * Retrieve context (this).
     * </p>
     * 
     * @return
     */
    public static native Object getContext();

    /**
     * <p>
     * Retrieve current function (arguments.callee).
     * </p>
     * 
     * @return
     */
    public static native NativeFunction getContextFuntion();

    /**
     * <p>
     * Retrieve arguments as array.
     * </p>
     * 
     * @return
     */
    public static native Object[] getArgumentArray();

    /**
     * <p>
     * Parses a string argument and returns an integer of 10 radix.
     * </p>
     * 
     * @param value The value to parse. If string is not a string, then it is converted to one.
     *            Leading whitespace in the string is ignored.
     * @return A parsed number value.
     */
    public static int parseInt(String value) {
        double parsed = Double.parseDouble(value);

        if (0 <= parsed) {
            return (int) Math.floor(parsed);
        } else {
            return (int) Math.ceil(parsed);
        }
    }

    /**
     * <p>
     * Parses a string argument and returns an integer of the specified radix or base.
     * </p>
     * 
     * @param value The value to parse. If string is not a string, then it is converted to one.
     *            Leading whitespace in the string is ignored.
     * @param radix An integer that represents the radix of the above mentioned string. Always
     *            specify this parameter to eliminate reader confusion and to guarantee predictable
     *            behavior. Different implementations produce different results when a radix is not
     *            specified.
     * @return A parsed number value.
     */
    public static native int parseInt(String value, int radix);

    /**
     * <p>
     * Parses a string argument and returns a floating point number.
     * </p>
     * 
     * @param value A string that represents the value you want to parse.
     * @return A parsed number value.
     */
    public static native float parseFloat(char value);

    /**
     * <p>
     * Parses a string argument and returns a floating point number.
     * </p>
     * 
     * @param value A string that represents the value you want to parse.
     * @return A parsed number value.
     */
    public static float parseFloat(String value) {
        if (value == null) {
            return Float.NaN;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    /**
     * <p>
     * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
     * interested in ECMAScript 6 Number.isNaN.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static boolean isNaN(int value) {
        return Float.isNaN(value);
    }

    /**
     * <p>
     * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
     * interested in ECMAScript 6 Number.isNaN.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static boolean isNaN(long value) {
        return Double.isNaN(value);
    }

    /**
     * <p>
     * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
     * interested in ECMAScript 6 Number.isNaN.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static boolean isNaN(float value) {
        return Float.isNaN(value);
    }

    /**
     * <p>
     * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
     * interested in ECMAScript 6 Number.isNaN.
     * </p>
     * 
     * @param value The value to be tested. Stringt.
     */
    public static boolean isNaN(double value) {
        return Double.isNaN(value);
    }

    /**
     * <p>
     * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
     * interested in ECMAScript 6 Number.isNaN.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static native boolean isNaN(Object value);

    /**
     * <p>
     * Evaluates an argument to determine whether it is a finite number.
     * </p>
     * <p>
     * You can use this function to determine whether a number is a finite number. The isFinite
     * function examines the number in its argument. If the argument is NaN, positive infinity, or
     * negative infinity, this method returns false; otherwise, it returns true.
     * </p>
     * 
     * @param value The number to evaluate.
     * @return A result.
     */
    public static boolean isFinite(int value) {
        return Float.isFinite(value);
    }

    /**
     * <p>
     * Evaluates an argument to determine whether it is a finite number.
     * </p>
     * <p>
     * You can use this function to determine whether a number is a finite number. The isFinite
     * function examines the number in its argument. If the argument is NaN, positive infinity, or
     * negative infinity, this method returns false; otherwise, it returns true.
     * </p>
     * 
     * @param value The number to evaluate.
     * @return A result.
     */
    public static boolean isFinite(long value) {
        return Double.isFinite(value);
    }

    /**
     * <p>
     * Evaluates an argument to determine whether it is a finite number.
     * </p>
     * <p>
     * You can use this function to determine whether a number is a finite number. The isFinite
     * function examines the number in its argument. If the argument is NaN, positive infinity, or
     * negative infinity, this method returns false; otherwise, it returns true.
     * </p>
     * 
     * @param value The number to evaluate.
     * @return A result.
     */
    public static boolean isFinite(float value) {
        return Float.isFinite(value);
    }

    /**
     * <p>
     * Evaluates an argument to determine whether it is a finite number.
     * </p>
     * <p>
     * You can use this function to determine whether a number is a finite number. The isFinite
     * function examines the number in its argument. If the argument is NaN, positive infinity, or
     * negative infinity, this method returns false; otherwise, it returns true.
     * </p>
     * 
     * @param value The number to evaluate.
     * @return A result.
     */
    public static boolean isFinite(double value) {
        return Double.isFinite(value);
    }

    /**
     * <p>
     * Evaluates an argument to determine whether it is a finite number.
     * </p>
     * <p>
     * You can use this function to determine whether a number is a finite number. The isFinite
     * function examines the number in its argument. If the argument is NaN, positive infinity, or
     * negative infinity, this method returns false; otherwise, it returns true.
     * </p>
     * 
     * @param value The number to evaluate.
     * @return A result.
     */
    public static native boolean isFinite(Object value);

    /**
     * <p>
     * Determines whether its argument is a number.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static native boolean isNumeric(char value);

    /**
     * <p>
     * Determines whether its argument is a string.
     * </p>
     * 
     * @param value The value to be tested.
     * @return A result.
     */
    public static native boolean isString(Object value);

    /**
     * <p>
     * Convert the specified value to signed integer (32bit).
     * </p>
     * 
     * @param value A target value to convert.
     * @return A signed integer.
     */
    public static int toSignedInteger(int value) {
        return value;
    }

    /**
     * <p>
     * Convert the specified value to signed integer (32bit).
     * </p>
     * 
     * @param value A target value to convert.
     * @return A signed integer.
     */
    public static int toSignedInteger(double value) {
        return (int) value;
    }

    /**
     * <p>
     * Throw native error to build stack trace.
     * </p>
     * 
     * @return
     */
    static native NativeError error();

    /**
     * @version 2015/09/29 2:59:31
     */
    @SuppressWarnings("unused")
    private static class Coder extends Translator<Global> {

        /** The booton root object. */
        public String boot = "boot";

        /** The global object in web environment. */
        public String window = "window";

        /** The root document. */
        public String document = "document";

        /** The root document. */
        public String history = "history";

        /**
         * <p>
         * Returns a Location object, which contains information about the URL of the document and
         * provides methods for changing that URL. You can also assign to this property to load
         * another URL.
         * </p>
         */
        public String location = "location";

        /**
         * <p>
         * localStorage is the same as sessionStorage with same same-origin rules applied but it is
         * persistent. localStorage was introduced in Firefox 3.5.
         * </p>
         */
        public String localStorage = "localStorage";

        /**
         * <p>
         * This is a global object (sessionStorage) that maintains a storage area that's available
         * for the duration of the page session. A page session lasts for as long as the browser is
         * open and survives over page reloads and restores. Opening a page in a new tab or window
         * will cause a new session to be initiated.
         * </p>
         */
        public String sessionStorage = "sessionStorage";

        /**
         * <p>
         * The JSON object contains methods for converting values to JavaScript Object Notation
         * (JSON) and for converting JSON to values.
         * </p>
         */
        public String JSON = "JSON";

        /** The performance API. */
        public String performance = "performance";

        /**
         * <p>
         * The requestAnimationFrame function tells the browser that you wish to perform an
         * animation and requests that the browser call a specified function to update an animation
         * before the next repaint. The method takes as an argument a callback to be invoked before
         * the repaint.
         * </p>
         * <p>
         * You should call this method whenever you're ready to update your animation onscreen. This
         * will request that your animation function be called before the browser performs the next
         * repaint. The number of callbacks is usually 60 times per second, but will generally match
         * the display refresh rate in most web browsers as per W3C recommendation. The callback
         * rate may be reduced to a lower rate when running in background tabs.
         * </p>
         * 
         * @param callback A parameter specifying a function to call when it's time to update your
         *            animation for the next repaint. The callback has one single argument, a
         *            DOMHighResTimeStamp, which indicates the current time for when
         *            requestAnimationFrame starts to fire callbacks.
         */
        public String requestAnimationFrame(Runnable callback) {
            return "requestAnimationFrame(" + function(0) + ")";
        }

        /**
         * <p>
         * Calls a function or executes a code snippet after specified delay.
         * </p>
         * 
         * @param function A callable function.
         * @param delay A delay time.
         * @return A timeout id.
         */
        public String setTimeout(Runnable runnable, long delay) {
            return "setTimeout(" + function(0) + "," + param(1) + ")";
        }

        /**
         * <p>
         * Clears the delay set by setTimeout().
         * </p>
         * 
         * @param timeoutId The ID of the timeout you wish to clear, as returned by setTimeout().
         */
        public String clearTimeout(long timeoutId) {
            return "clearTimeout(" + param(0) + ")";
        }

        /**
         * <p>
         * Retrieve context (this).
         * </p>
         * 
         * @return
         */
        public String getContext() {
            return "this";
        }

        /**
         * <p>
         * Retrieve current function (arguments.callee).
         * </p>
         * 
         * @return
         */
        public String getContextFuntion() {
            return "arguments.callee";
        }

        /**
         * <p>
         * Clears the delay set by setTimeout().
         * </p>
         * 
         * @param timeoutId The ID of the timeout you wish to clear, as returned by setTimeout().
         */
        public String getArgumentArray() {
            return "Φ(\"" + Javascript.computeSimpleClassName(Object.class) + "\",Array.prototype.slice.call(arguments))";
        }

        /**
         * <p>
         * Parses a string argument and returns an integer of 10 radix.
         * </p>
         * 
         * @param value The value to parse. If string is not a string, then it is converted to one.
         *            Leading whitespace in the string is ignored.
         * @return A parsed number value.
         */
        public String parseInt(String value) {
            return "parseInt(" + param(0) + ")";
        }

        /**
         * <p>
         * Parses a string argument and returns an integer of the specified radix or base.
         * </p>
         * 
         * @param value The value to parse. If string is not a string, then it is converted to one.
         *            Leading whitespace in the string is ignored.
         * @param radix An integer that represents the radix of the above mentioned string. Always
         *            specify this parameter to eliminate reader confusion and to guarantee
         *            predictable behavior. Different implementations produce different results when
         *            a radix is not specified.
         * @return A parsed number value.
         */
        public String parseInt(String value, int radix) {
            return "parseInt(" + param(0) + "," + param(1) + ")";
        }

        /**
         * <p>
         * Parses a string argument and returns a floating point number.
         * </p>
         * 
         * @param value A string that represents the value you want to parse.
         * @return A parsed number value.
         */
        public String parseFloat(String value) {
            return "parseFloat(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
         * interested in ECMAScript 6 Number.isNaN.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isNaN(int value) {
            return "isNaN(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
         * interested in ECMAScript 6 Number.isNaN.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isNaN(long value) {
            return "isNaN(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
         * interested in ECMAScript 6 Number.isNaN.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isNaN(float value) {
            return "isNaN(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
         * interested in ECMAScript 6 Number.isNaN.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isNaN(double value) {
            return "isNaN(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether a value is NaN or not. Be careful, this function is broken. You may be
         * interested in ECMAScript 6 Number.isNaN.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isNaN(Object value) {
            return "isNaN(" + param(0) + ")";
        }

        /**
         * <p>
         * Evaluates an argument to determine whether it is a finite number.
         * </p>
         * <p>
         * You can use this function to determine whether a number is a finite number. The isFinite
         * function examines the number in its argument. If the argument is NaN, positive infinity,
         * or negative infinity, this method returns false; otherwise, it returns true.
         * </p>
         * 
         * @param value The number to evaluate.
         * @return A result.
         */
        public String isFinite(int value) {
            return "isFinite(" + param(0) + ")";
        }

        /**
         * <p>
         * Evaluates an argument to determine whether it is a finite number.
         * </p>
         * <p>
         * You can use this function to determine whether a number is a finite number. The isFinite
         * function examines the number in its argument. If the argument is NaN, positive infinity,
         * or negative infinity, this method returns false; otherwise, it returns true.
         * </p>
         * 
         * @param value The number to evaluate.
         * @return A result.
         */
        public String isFinite(long value) {
            return "isFinite(" + param(0) + ")";
        }

        /**
         * <p>
         * Evaluates an argument to determine whether it is a finite number.
         * </p>
         * <p>
         * You can use this function to determine whether a number is a finite number. The isFinite
         * function examines the number in its argument. If the argument is NaN, positive infinity,
         * or negative infinity, this method returns false; otherwise, it returns true.
         * </p>
         * 
         * @param value The number to evaluate.
         * @return A result.
         */
        public String isFinite(float value) {
            return "isFinite(" + param(0) + ")";
        }

        /**
         * <p>
         * Evaluates an argument to determine whether it is a finite number.
         * </p>
         * <p>
         * You can use this function to determine whether a number is a finite number. The isFinite
         * function examines the number in its argument. If the argument is NaN, positive infinity,
         * or negative infinity, this method returns false; otherwise, it returns true.
         * </p>
         * 
         * @param value The number to evaluate.
         * @return A result.
         */
        public String isFinite(double value) {
            return "isFinite(" + param(0) + ")";
        }

        /**
         * <p>
         * Evaluates an argument to determine whether it is a finite number.
         * </p>
         * <p>
         * You can use this function to determine whether a number is a finite number. The isFinite
         * function examines the number in its argument. If the argument is NaN, positive infinity,
         * or negative infinity, this method returns false; otherwise, it returns true.
         * </p>
         * 
         * @param value The number to evaluate.
         * @return A result.
         */
        public String isFinite(Object value) {
            return "isFinite(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether its argument is a number.
         * </p>
         * 
         * @param character The value to be tested.
         * @return A result.
         */
        public String isNumeric(char value) {
            return "boot.isNumeric(" + param(0) + ")";
        }

        /**
         * <p>
         * Determines whether its argument is a string.
         * </p>
         * 
         * @param value The value to be tested.
         * @return A result.
         */
        public String isString(Object value) {
            return "boot.isString(" + param(0) + ")";
        }

        /**
         * <p>
         * Convert the specified value to signed integer (32bit).
         * </p>
         * 
         * @param value A target value to convert.
         * @return A signed integer.
         */
        public String toSignedInteger(int value) {
            return param(0) + "|0";
        }

        /**
         * <p>
         * Convert the specified value to signed integer (32bit).
         * </p>
         * 
         * @param value A target value to convert.
         * @return A signed integer.
         */
        public String toSignedInteger(double value) {
            return toSignedInteger((int) value);
        }

        /**
         * <p>
         * Throw native error to build stack trace.
         * </p>
         * 
         * @return
         */
        public String error() {
            return "boot.error()";
        }
    }

    /**
     * @version 2013/12/17 22:28:15
     */
    @Managed(Singleton.class)
    private static class TaskScheduler {

        /** The service. */
        private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

        /** The task id counter. */
        private static long id = 0;

        /** The scheduled tasks. */
        private final Map<Long, Future> tasks = new ConcurrentHashMap();

        /**
         * <p>
         * Add background task.
         * </p>
         * 
         * @param runnable
         * @param delay
         * @return
         */
        private long add(NativeFunction runnable, long delay) {
            Task task = new Task(runnable, id++);

            tasks.put(task.id, scheduler.schedule(task, delay, TimeUnit.MILLISECONDS));

            return task.id;
        }

        /**
         * <p>
         * Remove background task.
         * </p>
         * 
         * @param id
         */
        private void remove(long id) {
            Future task = tasks.remove(id);

            if (task != null && !task.isCancelled() && !task.isDone()) {
                task.cancel(false);
            }
        }

        /**
         * @version 2013/08/18 14:45:14
         */
        private class Task implements Runnable {

            /** The actual task. */
            private final NativeFunction task;

            /** The task id. */
            private final long id;

            /**
             * @param task
             */
            private Task(NativeFunction task, long id) {
                this.task = task;
                this.id = id;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    task.apply(task, new Object[0]);
                } finally {
                    remove(id);
                }
            }
        }
    }
}
