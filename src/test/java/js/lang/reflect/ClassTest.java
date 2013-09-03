/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.RunWith;

import booton.translator.ScriptRunner;

/**
 * @version 2013/09/03 21:43:55
 */
@RunWith(ScriptRunner.class)
public class ClassTest {

    @Test
    public void modifierPrivate() throws Exception {
        int modifier = Private.class.getModifiers();
        assert Modifier.isPrivate(modifier);
    }

    @Test
    public void modifierPackage() throws Exception {
        int modifier = Package.class.getModifiers();
        assert !Modifier.isPrivate(modifier);
        assert !Modifier.isProtected(modifier);
        assert !Modifier.isPublic(modifier);
    }

    @Test
    public void modifierProtected() throws Exception {
        int modifier = Protected.class.getModifiers();
        assert Modifier.isProtected(modifier);
    }

    @Test
    public void modifierPublic() throws Exception {
        int modifier = Public.class.getModifiers();
        assert Modifier.isPublic(modifier);
    }

    /**
     * @version 2013/08/03 0:25:14
     */
    private static class Private {
    }

    /**
     * @version 2013/08/03 0:25:14
     */
    static class Package {
    }

    /**
     * @version 2013/08/03 0:25:14
     */
    protected static class Protected {
    }

    /**
     * @version 2013/08/03 0:25:14
     */
    public static class Public {
    }

    @Test
    public void getSuperclass() throws Exception {
        assert ExtendedClass.class.getSuperclass() == SuperClass.class;
        assert SuperClass.class.getSuperclass() == Object.class;
        assert Object.class.getSuperclass() == null;

        assert Interface.class.getSuperclass() == null;
        assert ExtendedInterface.class.getSuperclass() == null;
    }

    @Test
    public void getInterfaces() throws Exception {
        assert ExtendedClass.class.getInterfaces().length == 0;
        assert SuperClass.class.getInterfaces().length == 0;
        assert Object.class.getInterfaces().length == 0;
        assert Interface.class.getInterfaces().length == 0;

        Class[] interfaces = ExtendedInterface.class.getInterfaces();
        assert interfaces.length == 1;
        assert interfaces[0] == Interface.class;

        interfaces = ImplementdClass.class.getInterfaces();
        assert interfaces.length == 1;
        assert interfaces[0] == ExtendedInterface.class;

        interfaces = ImplementdExtendedClass.class.getInterfaces();
        assert interfaces.length == 1;
        assert interfaces[0] == ExtendedInterface.class;
    }

    /**
     * @version 2013/09/03 19:53:20
     */
    @SuppressWarnings("unused")
    private static interface Interface {

        int interfaceStaticField = 10;

        /**
         * 
         */
        void interfaceMethod();
    }

    /**
     * @version 2013/09/03 20:10:51
     */
    @SuppressWarnings("unused")
    private static interface ExtendedInterface extends Interface {

        int extendedInterfaceStaticField = 100;

        /**
         * 
         */
        void extendedInterfaceMethod();
    }

    /**
     * @version 2013/09/03 21:44:04
     */
    private static class SuperClass {
    }

    /**
     * @version 2013/09/03 21:44:08
     */
    private static class ExtendedClass extends SuperClass {
    }

    /**
     * @version 2013/09/03 21:40:21
     */
    private static class ImplementdClass implements ExtendedInterface {

        /**
         * {@inheritDoc}
         */
        @Override
        public void interfaceMethod() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void extendedInterfaceMethod() {
        }
    }

    /**
     * @version 2013/09/03 21:40:21
     */
    private static class ImplementdExtendedClass extends ExtendedClass implements ExtendedInterface {

        /**
         * {@inheritDoc}
         */
        @Override
        public void interfaceMethod() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void extendedInterfaceMethod() {
        }
    }

    @Test
    public void getMethods() throws Exception {
        Method[] methods = Methods.class.getMethods();
        assert methods != null;
        assert methods.length == 10;

        // extends
        methods = ExtendedMethods.class.getMethods();
        assert methods != null;
        assert methods.length == 12;

        // check defensive copy
        methods[0] = methods[1];
        assert methods[0] == methods[1];

        methods = ExtendedMethods.class.getMethods();
        assert methods[0] != methods[1];

        // implements
        methods = ImplementdClass.class.getMethods();
        assert methods != null;
        assert methods.length == 11;

        // interface
        methods = Interface.class.getMethods();
        assert methods != null;
        assert methods.length == 1;

        // extended interface
        methods = ExtendedInterface.class.getMethods();
        assert methods != null;
        assert methods.length == 2;
    }

    @Test
    public void getDeclaredMethods() throws Exception {
        Method[] methods = Methods.class.getDeclaredMethods();
        assert methods != null;
        assert methods.length == 4;

        // extends
        methods = ExtendedMethods.class.getDeclaredMethods();
        assert methods != null;
        assert methods.length == 4;

        // check defensive copy
        methods[0] = methods[1];
        assert methods[0] == methods[1];

        methods = ExtendedMethods.class.getDeclaredMethods();
        assert methods[0] != methods[1];

        // implements
        methods = ImplementdClass.class.getDeclaredMethods();
        assert methods != null;
        assert methods.length == 2;

        // interface
        methods = Interface.class.getDeclaredMethods();
        assert methods != null;
        assert methods.length == 1;

        // extended interface
        methods = ExtendedInterface.class.getDeclaredMethods();
        assert methods != null;
        assert methods.length == 1;
    }

    /**
     * @version 2013/09/03 15:04:55
     */
    private static class Methods {

        /**
         * 
         */
        public native void publicMethod();

        /**
         * 
         */
        protected native void protectedMethod();

        /**
         * 
         */
        native void packageMethod();

        /**
         * 
         */
        private native void privateMethod();
    }

    /**
     * @version 2013/09/03 15:14:17
     */
    private static class ExtendedMethods extends Methods {

        /**
         * 
         */
        public native void extendedMethod();

        /**
         * {@inheritDoc}
         */
        @Override
        public void publicMethod() {
            super.publicMethod();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void protectedMethod() {
            super.protectedMethod();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void packageMethod() {
            super.packageMethod();
        }
    }

    @Test
    public void getFields() throws Exception {
        Field[] fields = Fields.class.getFields();
        assert fields != null;
        assert fields.length == 2;

        // extends
        fields = ExtendedFields.class.getFields();
        assert fields != null;
        assert fields.length == 4;

        // check defensive copy
        fields[0] = fields[1];
        assert fields[0] == fields[1];

        // interface
        fields = Interface.class.getFields();
        assert fields != null;
        assert fields.length == 1;

        // extended interface
        fields = ExtendedInterface.class.getFields();
        assert fields != null;
        assert fields.length == 2;
    }

    @Test
    public void getFields2() throws Exception {
        Field[] fields = ExtendedInterface.class.getFields();
        assert fields != null;
        assert fields.length == 2;
    }

    @Test
    public void getDeclaredFields() throws Exception {
        Field[] fields = Fields.class.getDeclaredFields();
        assert fields != null;
        assert fields.length == 5;

        // extends
        fields = ExtendedFields.class.getDeclaredFields();
        assert fields != null;
        assert fields.length == 2;

        // check defensive copy
        fields[0] = fields[1];
        assert fields[0] == fields[1];

        // interface
        fields = Interface.class.getDeclaredFields();
        assert fields != null;
        assert fields.length == 1;

        // extended interface
        fields = ExtendedInterface.class.getDeclaredFields();
        assert fields != null;
        assert fields.length == 1;
    }

    /**
     * @version 2013/09/03 15:04:55
     */
    @SuppressWarnings("unused")
    private static class Fields {

        public static int staticField;

        public int publicField;

        protected int protectedField;

        int packageField;

        private int privateField;
    }

    /**
     * @version 2013/09/03 15:14:17
     */
    @SuppressWarnings("unused")
    private static class ExtendedFields extends Fields {

        public int extendedField;

        public int protectedField;
    }
}
