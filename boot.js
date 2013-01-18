"use strict";

function boot(global) {  
  /**
   * Define user properties.
   */
  function define(object, properties) {
    Object.keys(properties).forEach(function(name) {
      if (!object[name]) {
        Object.defineProperty(object, name, {
          configurable: false,
          enumerable: false,
          writable: true,
          value: properties[name]
        });
      }
    });
  }
  
  //====================================================================
  // String Extensions
  //====================================================================
  define(String.prototype, {
    /**
     * Retrieve the object identifier.
     *
     * @return An identifier.
     */
    hashCode: function() {
      if (this.$hashCode !== undefined) return this.$hashCode;

      var hash = 0;
      
      for (var i = 0; i < this.length; i++) {
        hash = 31 * hash + this.charCodeAt(i);
      }
      return this.$hashCode = hash;
    },

    /**
     * <p>
     * Tests if this string starts with the specified prefix.
     * </p>
     * 
     * @param {String} prefix A prefix to test.
     * @return {boolean} true if the character sequence represented by the argument is a prefix of
     *         the character sequence represented by this string; false otherwise. Note also that
     *         true will be returned if the argument is an empty string or is equal to this String
     *         object as determined by the equals(Object) method.
     */
    startsWith: function(prefix) {
        return prefix.length <= this.length && prefix == this.substring(0, prefix.length);
    },

    /**
     * <p>
     * Tests if this string ends with the specified suffix.
     * </p>
     * 
     * @param {String} suffix A suffix to test.
     * @return {boolean} <code>true</code> if the character sequence represented by the argument
     *         is a suffix of the character sequence represented by this object; false otherwise.
     *         Note that the result will be true if the argument is the empty string or is equal to
     *         this String object as determined by the equals(Object) method.
     */
    endsWith: function(suffix) {
        return suffix.length <= this.length && suffix == this.slice(~suffix.length + 1);
    }
  });

  //====================================================================
  // Array Extensions
  //====================================================================

  //====================================================================
  // WebSocket Extensions
  //====================================================================
  define(WebSocket, {
    /**
     * Establish connection by WebSocket.
     */
    connect: function(uri, listener) {
      var connection = new WebSocket(uri);
      connection.onopen = listener.open;
      connection.onclose = listener.close;
      connection.onerror = listener.error;
      connection.onmessage = listener.message;
    }
  });

  
  //====================================================================
  // Object Extensions
  //====================================================================
  // Global object identifier
  var hashcode = 0;
  
  define(Object.prototype, {
    /** The identifier for this object. */
    $hashCode: undefined,
  
    /**
     * Retrieve the object identifier.
     *
     * @return An identifier.
     */
    hashCode: function() {
      return this.$hashCode !== undefined ? this.$hashCode : this.$hashCode = hashcode++;
    },
    
    /**
     * Test whether this object is equals to the specified object or not.
     *
     * @param other A test object.
     * @return A result.
     */
    equals: function(other) {
      return this == other;
    },

    /**
     * Create object expression.
     *
     * @return A string expression.
     */
    toString: function() {
      return this.constructor.name + "#" + this.hashCode();
    },
    
    /**
     * Retrieve the class object.
     * 
     * @return A Class object.
     */
    getClass: function() {
      return this.$.$;
    }
  });


  //====================================================================
  // ECMAScript6 Extensions
  //====================================================================
  
  
  //====================================================================
  // Booton Extensions
  //====================================================================
  define(boot, {
    /**
     * <p>
     * Define class in booton core library namespace.
     * </p>
     * 
     * @param {String} name A simple class name of a class to define.
     * @param {String} superclassName A simple parent class name.
     * @param {Object} definition A class definition.
     * @param {Object} annotation A annotation definition.
     */
    define: function(name, superclassName, definition, annotation) {
      // Default superclass is native Object class.
      var superclass = superclassName.length === 0 ? Object : boot[superclassName];

      // This is actual counstructor of class to define.
      function Class() {
        var params = Array.prototype.slice.call(arguments);

        // invoke specified constructor
        this["$" + params.pop()].apply(this, params);
      }

      // We must store static initialization function.
      var init;

      // We must copy the properties over onto the new prototype.
      // At first, from superclass definition.
      var prototype = Class.prototype = Object.create(superclass.prototype);

      // Then, from user defined class definition.
      for (var i in definition) {
        // static method
        if (i.charAt(0) == "_") {
          if (i.length == 1) {
            // invoke static initializer
            init = definition[i];
          } else {
            // define static method
            Class[i.substring(1)] = definition[i];
          }
        } else {
          // define member method
          prototype[i] = definition[i];
        }
      }

      // Expose and define class at global scope.
      boot[name] = Class;

      // Define class metadata as pseudo Class instance.
      // This variable is lazy initialized because define function requires
      // native Class class (it will be "boot.A") in all classes, but Class
      // class can't be defined at first.
      var metadata;
      
      // "Class" variable (js function) can't directly have functionalities of
      // (Java) Class class. "Class" variable directly has static methods of the
      // defiend class, if there is method name confliction between these and the
      // instance methods of (java) Class class, we can't resolve it correctly.
      Object.defineProperty(Class, "$", {
        get: function() {
          if (!metadata) {
            metadata = new boot.A(name, prototype, annotation || {}, superclass.$, 0)
          }
          return metadata;
        }
      });
      
      Object.defineProperty(Class, "toString", {
        value: function() {
          return "Class " + name;
        }
      });

      
      // Define class object for the reference from instance.
      define(prototype, {
        $: Class
      });

      // Invoke static initialization.
      if (init) init.call(Class);
    },

    /**
     * <p>
     * Define properties in javascript native object prototype.
     * </p>
     * 
     * @param {String} name A fully qualified class name of a class to define.
     * @param {Object} properties A property definition.
     */
    defineNative: function(name, properties) {
      if (global[name]) {
        define(global[name].prototype, properties);
      }
    },
    
    find: function(type) {
      Object.keys(boot).forEach(function(name) {
        console.log(boot[name].$ instanceof type.$);
      });
    }
  });
}

// Activate Initialization
boot(Function("return this")());
