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

import static booton.Obfuscator.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import booton.BootonConfiguration;
import booton.BootonLog;
import booton.Necessary;
import booton.Unnecessary;
import io.github.classgraph.ClassGraph;
import js.lang.NativeString;
import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import psychopath.Location;
import psychopath.Locator;

/**
 * <h2>The Reserved words in ECMA Script Third Edition</h2>
 * <p>
 * The following words are reserved.
 * </p>
 * <p>
 * abstract, boolean, break, byte, case, catch, char, class, const, continue, debugger, default,
 * delete, do, double, else, enum, export, extends, false, final, finally, float, for, function,
 * goto, if, implements, import, in, instanceof, int, interface, label, long, native, new, null,
 * package, private, protected, public, return, short, static, super, switch, synchronized, this,
 * throw, throws, transient, true, try, typeof, var, void, volatile, while, with
 * <p>
 * <p>
 * But the following words can use safely in many javascript engines (IE10+, Gecko10+, Webkit5+).
 * <p>
 * <p>
 * abstract, boolean, byte, char, double, final, float, goto, implements, int, interface, long,
 * native, package, private, protected, public, short, static, synchronized, throws, transient,
 * volatile
 * </p>
 * 
 * @version 2014/03/08 11:26:32
 */
public class Javascript {

    /** The primitive long class for javascript runtime. */
    static final Class PrimitiveLong;

    /** The root class of javascript model. */
    private static final Class RootClass;

    /** The primitive long constructor for reuse. */
    private static final Constructor primitiveLongConstructor;

    /** The primitive types. */
    private static final List<Class<?>> primitives = Arrays
            .asList(int.class, long.class, float.class, double.class, boolean.class, byte.class, short.class, char.class, void.class);

    /** The fixed id for primitives. */
    private static final List<Integer> primitiveIds = Arrays.asList(8, 9, 5, 3, 25, 1, 18, 2, 21);

    /** The all cached scripts. */
    private static final Map<Class, Javascript> scripts = new ConcurrentHashMap();

    /** The method list. Method signature must have identity in compiling environment */
    private static final List<Integer> methods = new ArrayList();

    /** The local identifier counter for {@link Javascript}. */
    private static int counter = 0;

    /** The build configuration. */
    private static BootonConfiguration configuration = I.make(BootonConfiguration.class);

    private static CompiledCodeRepository repository = I.make(CompiledCodeRepository.class);

    // initialization
    static {
        try {
            RootClass = Class.forName("js.lang.JSObject");
            PrimitiveLong = Class.forName("js.lang.JSLong$Primitive");
            primitiveLongConstructor = PrimitiveLong.getDeclaredConstructor(int.class, int.class);
            primitiveLongConstructor.setAccessible(true);
        } catch (Exception e) {
            throw I.quiet(e);
        }

        // Load Booton module
        I.load(Translator.class);

        // Define Class class at first. It is ensured that Class definition is
        // assigned in 'boot.A' variable.
        getScript(Class.class);
        getScript(Object.class);
    }

    /** The actual script class to translate. */
    public final Class<?> source;

    /** The identifier of this script. */
    private final int id;

    /** The constructor list of this script. */
    private final List<Integer> constructors = new ArrayList();

    /** The field list of this script. */
    private final List<Integer> fields = new ArrayList();

    /** The dependencies. */
    private final Set<Class> dependencies = new LinkedHashSet();

    /** The actual Javascript source code to be translated. This is initialized lazy. */
    private String code;

    /**
     * Create Javascript as the specified Java class is source.
     * 
     * @param source A Java class as source.
     */
    private Javascript(Class source) {
        BootonLog.JavascriptConstructor.start(source);

        this.source = source;

        Class reverted = JavaAPIProviders.revert(source);

        if (reverted.isPrimitive()) {
            this.id = primitiveIds.get(primitives.indexOf(reverted));
        } else {
            while (primitiveIds.indexOf(counter) != -1) {
                // skip preserved id for primitives
                counter++;
            }
            this.id = counter++;
        }

        // copy all member fields for override mechanism
        if (source != RootClass) {
            Javascript script = getScript(source.getSuperclass());

            if (script != null) {
                fields.addAll(script.fields);
            }
        }

        // define all declared member fields
        for (Field field : source.getDeclaredFields()) {
            order(fields, field.getName().hashCode() + source.hashCode());
        }

        if (Extensible.class.isAssignableFrom(source)) {
            // check wether it is extension point or not
            if (Arrays.asList(source.getInterfaces()).contains(Extensible.class)) {

                for (Class extension : I.findAs((Class<Extensible>) source)) {
                    require(extension);
                }
            }
        }

        BootonLog.JavascriptConstructor.stop();
    }

    /**
     * <p>
     * Compute the unique name of this scripted class.
     * </p>
     * 
     * @return
     */
    private String computeUniqueName() {
        return !configuration.compression ? mung32(id) : source.getName().replaceAll("\\.", "_");
    }

    /**
     * <p>
     * Write this script. This method write out dependency scripts of this script too.
     * </p>
     * 
     * @param defined A list of compiled script classes.
     * @param necessaries A list of required script classes.
     * @return A script output.
     */
    public String write() {
        return write(null);
    }

    /**
     * <p>
     * Write this script. This method write out dependency scripts of this script too.
     * </p>
     * 
     * @param defined A list of compiled script classes.
     * @param necessaries A list of required script classes.
     * @return A script output.
     */
    public String write(Set<Class> defined) {
        StringBuilder builder = new StringBuilder();

        writeTo(builder, defined);

        return builder.toString();
    }

    /**
     * <p>
     * Write this script into the specified output. This method write out dependency scripts of this
     * script too.
     * </p>
     * 
     * @param outout A script output.
     * @param requirements A list of required script classes.
     */
    public void writeTo(Path output, Set<Class> defined) {
        try {
            writeTo(Files.newBufferedWriter(output, StandardCharsets.UTF_8), defined);
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Write this script into the specified output. This method write out dependency scripts of this
     * script too.
     * </p>
     * 
     * @param outout A script output.
     * @param defined A list of compiled script classes.
     * @param necessaries A list of required script classes.
     */
    public void writeTo(Appendable output, Set<Class> defined) {
        BootonLog.WriteTo.start(source);

        if (defined == null) {
            defined = new HashSet();
        }

        // write this script
        write(output, defined);

        // find all necessaries and write it
        for (Class necessary : I.make(NecessaryManager.class).collect()) {
            getScript(necessary).write(output, defined);
        }

        // write bootstrap method if needed.
        try {
            String main = writeMethodCode(source, "main", String[].class, null);
            String error = writeMethodCode(Thread.class, "handleUncaughtException", Object.class, "e");

            ScriptWriter code = new ScriptWriter();
            code.write("window.onerror", "=", "function(m,f,l,c,e)", "{", error, "}", ";").line();
            code.write(main, ";");
            output.append(code.toString());
        } catch (Exception e) {
            // ignore missing "main" method
        }

        // close stream
        I.quiet(output);

        BootonLog.WriteTo.stop();
    }

    /**
     * <p>
     * Helper method to write dependency scripts into the specified output.
     * </p>
     * 
     * @param output A script output.
     * @param defined
     */
    private void write(Appendable output, Set<Class> defined) {
        BootonLog.WriteJS.start(source);

        // record compile route
        CompilerRecorder.startCompiling(this);

        try {
            // compile script
            compile();

            // write super class and interfaces
            if (source != RootClass && !isEnumSubType(source)) {
                BootonLog.WriteSuperClass.start(source, () -> {
                    write(output, defined, source.getSuperclass());
                });

                BootonLog.WriteInterface.start(source, () -> {

                    for (Class interfaceType : source.getInterfaces()) {
                        write(output, defined, interfaceType);
                    }
                });
            }
            if (defined.add(source)) {
                // write sub type enum class
                if (source.getSuperclass() == Enum.class) {
                    for (Object constant : source.getEnumConstants()) {
                        Class sub = constant.getClass();

                        if (sub != source) {
                            write(output, defined, sub);
                        }
                    }
                }

                // write this class
                try {
                    BootonLog.WriteJSActually.start(source);
                    output.append(code);
                } catch (IOException e) {
                    throw I.quiet(e);
                } finally {
                    BootonLog.WriteJSActually.stop();
                }

                // write dependency classes
                BootonLog.WriteDependency.start(source, () -> {
                    for (Class dependency : dependencies) {
                        write(output, defined, dependency);
                    }
                });
            }
        } finally {
            // record compile route
            CompilerRecorder.finishCompiling(this);

            BootonLog.WriteJS.stop();
        }
    }

    private boolean isEnumSubType(Class type) {
        if (Enum.class.isAssignableFrom(type)) {
            return type.getSuperclass() != Enum.class;
        }
        return false;
    }

    /**
     * <p>
     * Write code of the specified class.
     * </p>
     * 
     * @param output
     * @param defined
     * @param type
     * @throws IOException
     */
    private void write(Appendable output, Set<Class> defined, Class type) {
        Javascript script = Javascript.getScript(type);

        if (script != null && !defined.contains(script.source)) {
            script.write(output, defined);
        }
    }

    /**
     * <p>
     * Translate the java byte code to the javascript code.
     * </p>
     */
    private synchronized void compile() {
        if (code == null) {
            BootonLog.Compile.start(source, () -> {

                code = repository.findCodeBy(source, () -> {
                    ScriptWriter code = new ScriptWriter();

                    // compute related class names
                    Class parent = source.getSuperclass();
                    String className = '"' + computeSimpleClassName(source) + '"';
                    String parentName = '"' + (parent == null || parent == Object.class ? "" : computeSimpleClassName(parent)) + '"';
                    StringJoiner interfaces = new StringJoiner(" ", "\"", "\"");

                    for (Class type : source.getInterfaces()) {
                        interfaces.add(computeSimpleClassName(type));
                    }

                    // write class definition
                    code.comment(source + " " + className);
                    code.append("boot.define(", className, ",", parentName, ",", interfaces, ",{");

                    // write constructors, fields and methods
                    try {
                        if (!TranslatorManager.hasTranslator(source)) {
                            if (source.isAnnotation()) {
                                compileAnnotation(code);
                            } else {
                                try {
                                    BootonLog.PraseByteCode.start(source);
                                    new ClassReader(source.getName()).accept(new JavaClassCompiler(this, code), 0);
                                } finally {
                                    BootonLog.PraseByteCode.stop();
                                }
                            }
                        }
                    } catch (TranslationError e) {
                        e.write("\r\n");

                        throw CompilerRecorder.rethrow(e);
                    } catch (Throwable e) {
                        TranslationError error = new TranslationError(e);
                        error.write("Can't compile ", source.getName() + ".");

                        throw CompilerRecorder.rethrow(error);
                    }

                    // write metadata
                    code.append("},", new JavaMetadataCompiler(source));

                    // write native class enhancement
                    JavascriptAPIProvider provider = source.getAnnotation(JavascriptAPIProvider.class);

                    if (provider != null) {
                        String JSClassName = provider.targetJavaScriptClassName();

                        if (JSClassName.length() != 0) {
                            code.append(",").string(JSClassName);
                        }
                    }

                    if (Extensible.class.isAssignableFrom(source)) {
                        code.append(",").string("e");
                    }

                    // End class definition
                    code.append(");");
                    code.line();

                    // create cache
                    return code.toString();
                });
            });
        }
    }

    /**
     * <p>
     * Compile annotation body.
     * </p>
     * 
     * @param code
     */
    private void compileAnnotation(ScriptWriter code) {
        BootonLog.CompileAnnotation.start(source, () -> {
            Method[] methods = source.getDeclaredMethods();

            for (int i = 0; i < methods.length; i++) {
                code.comment(methods[i]);
                code.write(computeMethodName(methods[i]), ":");

                Object value = methods[i].getDefaultValue();

                if (value == null) {
                    code.write("null");
                } else {
                    code.write("function()", "{return " + JavaMetadataCompiler.compileValue(value) + ";}");
                }

                if (i < methods.length - 1) {
                    code.separator();
                }
            }
        });
    }

    /**
     * <p>
     * Create primitve long as javascript runtime expression in Java runtime.
     * </p>
     * 
     * @param value
     * @return
     */
    public static final String writePrimitiveCode(long value) {
        return writeMethodCode(PrimitiveLong, "fromBits", int.class, (int) (value), int.class, (int) (value >> 32));
    }

    /**
     * <p>
     * Write method calling code.
     * </p>
     * 
     * @param type A target class.
     * @param name A target method name.
     * @param contextAndParameters A parameter code.
     * @return
     */
    public static final String writeMethodCode(Class type, String name, Object... contextAndParameters) {
        Object context = null;
        Object[] parameters = contextAndParameters;

        if (contextAndParameters.length % 2 == 1) {
            context = contextAndParameters[0];
            parameters = Arrays.copyOfRange(contextAndParameters, 1, contextAndParameters.length);
        }

        try {
            Class[] types = new Class[parameters.length / 2];
            String[] params = new String[parameters.length / 2];

            for (int i = 0; i < parameters.length; i = i + 2) {
                types[i / 2] = (Class) parameters[i];
                params[i / 2] = String.valueOf(parameters[i + 1]);
            }

            Class source = getScript(type).source;

            // Search original or alias method by name and parameter types.
            Method method;

            try {
                method = source.getDeclaredMethod(name, types);
            } catch (NoSuchMethodException e) {
                try {
                    method = source.getDeclaredMethod("$alias$" + name, types);
                } catch (NoSuchMethodException error) {
                    throw I.quiet(e);
                }
            }

            // Write method invocation code.
            String code;

            if (Modifier.isStatic(method.getModifiers())) {
                code = computeClassName(source, true);
            } else if (context == null) {
                code = "new " + computeClassName(source) + "(0)";
            } else {
                code = context.toString();
            }
            return code + "." + computeMethodName(method) + "(" + String.join(",", params) + ")";
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Require the specified java source code.
     * </p>
     * 
     * @param dependency A dependency class.
     */
    public static final void require(Class dependency) {
        while (dependency.isArray()) {
            dependency = dependency.getComponentType();
        }

        if (!dependency.isAnnotationPresent(Unnecessary.class)) {
            Javascript context = CompilerRecorder.getScript();

            if (context.source != dependency) {
                context.dependencies.add(dependency);
            }
        }
    }

    /**
     * <p>
     * Compile the specified Java class to Javascript source code.
     * </p>
     * 
     * @param source A Java class to compile.
     * @return A compiled Javascript source.
     */
    public static final Javascript getScript(Class source) {
        source = JavaAPIProviders.convert(source);

        // check Native Class
        if (source == null || source.isArray() || TranslatorManager
                .hasTranslator(source) && !source.isAnnotationPresent(JavascriptAPIProvider.class)) {
            return null;
        }

        // check cache
        Javascript script = scripts.get(source);

        if (script == null) {
            script = new Javascript(source);

            // cache it
            scripts.put(source, script);
        }

        // API definition
        return script;
    }

    /**
     * <p>
     * Compute the identified qualified class object for ECMAScript.
     * </p>
     * 
     * @param clazz A class with fully qualified class name(e.g. java.lang.String).
     * @return An identified class object for ECMAScript.
     */
    public static final String computeClass(Class clazz) {
        int dimension = 0;

        while (clazz.isArray()) {
            dimension++;
            clazz = clazz.getComponentType();
        }

        String type = computeClassName(clazz) + ".$";

        if (dimension != 0) {
            for (int i = 0; i < dimension; i++) {
                type = writeMethodCode(Class.class, "getArrayClass", type);
            }
        }
        return type;
    }

    /**
     * <p>
     * Compute the identified qualified class name for ECMAScript.
     * </p>
     * 
     * @param clazz A class with fully qualified class name(e.g. java.lang.String).
     * @return An identified class name for ECMAScript.
     */
    public static final String computeClassName(Class<?> clazz) {
        return computeClassName(clazz, false);
    }

    /**
     * <p>
     * Compute the identified qualified class name for ECMAScript.
     * </p>
     * 
     * @param clazz A class with fully qualified class name(e.g. java.lang.String).
     * @return An identified class name for ECMAScript.
     */
    public static final String computeClassName(Class<?> clazz, boolean useAtStaticMemberAccess) {
        JavascriptAPIProvider js = clazz.getAnnotation(JavascriptAPIProvider.class);

        if (useAtStaticMemberAccess || js == null || TranslatorManager.hasTranslator(clazz)) {
            return "boot." + computeSimpleClassName(clazz);
        } else {
            return js.targetJavaScriptClassName();
        }
    }

    /**
     * <p>
     * Compute the identified simple class name for ECMAScript.
     * </p>
     * 
     * @param clazz A class with fully qualified class name(e.g. java.lang.String).
     * @return An identified class name for ECMAScript.
     */
    public static final String computeSimpleClassName(Class clazz) {
        if (clazz == NativeString.class) {
            clazz = String.class;
        }

        String prefix = "";

        while (clazz.isArray()) {
            prefix += "[";
            clazz = clazz.getComponentType();
        }

        Javascript script = getScript(clazz);

        if (script == null) {
            return clazz.getSimpleName();
        } else {
            return prefix + script.computeUniqueName();
        }
    }

    /**
     * <p>
     * Compute the identified qualified method name for ECMAScript.
     * </p>
     * 
     * @param owner A {@link Class} object representing the class or interface that declares the
     *            specified method.
     * @param methodName A method name(e.g. toString, <init> and <clinit>).
     * @param description A method description of parameter types and return type. (e.g.
     *            (Ljava/lang/String;)V)
     * @return An identified class name for ECMAScript.
     */
    public static final String computeMethodName(Constructor<?> constructor) {
        return computeMethodName(constructor.getDeclaringClass(), "<init>", Type.getConstructorDescriptor(constructor));
    }

    /**
     * <p>
     * Compute the identified qualified method name for ECMAScript.
     * </p>
     * 
     * @param owner A {@link Class} object representing the class or interface that declares the
     *            specified method.
     * @param methodName A method name(e.g. toString, <init> and <clinit>).
     * @param description A method description of parameter types and return type. (e.g.
     *            (Ljava/lang/String;)V)
     * @return An identified class name for ECMAScript.
     */
    public static final String computeMethodName(Method method) {
        return computeMethodName(method.getDeclaringClass(), method.getName(), Type.getMethodDescriptor(method));
    }

    /**
     * <p>
     * Compute the identified qualified method name for ECMAScript.
     * </p>
     * 
     * @param owner A {@link Class} object representing the class or interface that declares the
     *            specified method.
     * @param methodName A method name(e.g. toString, <init> and <clinit>).
     * @param description A method description of parameter types and return type. (e.g.
     *            (Ljava/lang/String;)V)
     * @return An identified class name for ECMAScript.
     */
    public static final String computeMethodName(Class owner, String name, String description) {
        // convert an alias to an actual method name
        if (name.startsWith("$alias$")) {
            name = name.substring(7);
        }

        if (TranslatorManager.isNativeMethod(owner, name, description)) {
            return name;
        }

        if (name.charAt(0) == '<') {
            if (name.charAt(1) == 'c') {
                // class initializer
                return "";
            } else {
                // constructor
                return "$" + order(getScript(owner).constructors, description.hashCode());
            }
        } else {
            // method
            description = JavaAPIProviders.validateMethod(owner, name, description);

            return mung32(order(methods, name.concat(description).hashCode()));
        }
    }

    /**
     * <p>
     * Compute the identified qualified field name for ECMAScript.
     * </p>
     * 
     * @param owner A owner class of the specified field.
     * @param fieldName A field name in Java source code.
     * @return An identified field name for ECMAScript.
     */
    public static final String computeFieldName(Field field) {
        return computeFieldName(field.getDeclaringClass(), field.getName());
    }

    /**
     * <p>
     * Compute the identified qualified field name for ECMAScript.
     * </p>
     * 
     * @param owner A owner class of the specified field.
     * @param fieldName A field name in Java source code.
     * @return An identified field name for ECMAScript.
     */
    public static final String computeFieldName(Class owner, String fieldName) {
        if (TranslatorManager.isNativeField(owner, fieldName)) {
            return fieldName;
        }

        try {
            Field field = owner.getDeclaredField(fieldName);

            // validate field declaration
            JavaAPIProviders.validateField(owner, field);

            Javascript js = getScript(owner);

            return mung16(order(js.fields, fieldName.hashCode() + js.source.hashCode()));
        } catch (NoSuchFieldException e) {
            return computeFieldName(owner.getSuperclass(), fieldName);
        }
    }

    /**
     * <p>
     * Compute the accessible qualified field name for ECMAScript.
     * </p>
     * 
     * @param owner A owner class of the specified field.
     * @param fieldName A field name in Java source code.
     * @return An accessible field name for ECMAScript.
     */
    public static final String computeFieldFullName(Class owner, String fieldName) {
        return computeClassName(owner) + "." + computeFieldName(owner, fieldName);
    }

    /**
     * <p>
     * Helper method to do numbering for the specified member's id.
     * </p>
     * 
     * @param members
     * @param id
     * @return
     */
    private static final int order(List<Integer> members, int id) {
        // check cache
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).intValue() == id) {
                return i;
            }
        }

        // register as new member
        members.add(id);

        // API definition
        return members.size() - 1;
    }

    /**
     * @version 2013/11/05 9:58:05
     */
    @Managed(Singleton.class)
    private static class NecessaryManager {

        /** The extensions. */
        private final List<Class<?>> classes = new ClassGraph().scan().getClassesWithAnnotation(Necessary.class.getName()).loadClasses();

        private NecessaryManager() {
        }

        /**
         * <p>
         * Collect all necessaries.
         * </p>
         * 
         * @param necessaries
         * @return
         */
        private Set<Class> collect() {
            Set<Class> set = new CopyOnWriteArraySet();
            set.addAll(classes);

            for (Class clazz : classes) {
                set.add(clazz);

                if (Extensible.class.isAssignableFrom(clazz)) {
                    for (Class<Extensible> extension : I.findAs((Class<Extensible>) clazz)) {
                        set.add(extension);
                    }
                }
            }
            return set;
        }
    }

    /**
     * @version 2016/01/19 15:17:07
     */
    private static class CompiledCodeRepository {

        /** The cached codes. */
        public Map<Class, CompiledCode> caches = new HashMap();

        /**
         * <p>
         * Find cached code if it exists.
         * </p>
         * 
         * @param clazz
         * @return
         */
        private String findCodeBy(Class clazz, Supplier<String> coder) {
            return caches.computeIfAbsent(clazz, c -> new CompiledCode()).getCode(clazz, coder);
        }
    }

    /**
     * @version 2016/01/19 15:18:17
     */
    private static class CompiledCode {

        public FileTime modified;

        public String code;

        private String getCode(Class clazz, Supplier<String> coder) {
            try {
                Location location = Locator.locate(clazz);

                if (location == null) {
                    code = coder.get();
                } else if (location.isDirectory()) {
                    // source files
                    Path source = location.asDirectory().file(clazz.getName().replaceAll("\\.", "/").concat(".class")).asJavaPath();
                    FileTime sourceTime = Files.getLastModifiedTime(source);

                    if (!sourceTime.equals(modified)) {
                        modified = sourceTime;
                        code = coder.get();
                    }
                } else {
                    // jar archive
                    FileTime sourceTime = Files.getLastModifiedTime(location.asJavaPath());

                    if (!sourceTime.equals(modified)) {
                        modified = sourceTime;
                        code = coder.get();
                    }
                }
                return code;
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
    }
}
