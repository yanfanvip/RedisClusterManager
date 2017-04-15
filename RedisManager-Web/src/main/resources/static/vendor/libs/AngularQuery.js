var MyQuery = {
	param : function(a) {
		var prefix, s = [], add = function(key, value) {
			value = MyQuery.isFunction(value) ? value() : (value == null ? "" : value);
			s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
		};
		if (MyQuery.isArray(a)) {
			MyQuery.each(a, function() {
				add(this.name, this.value);
			});
		} else {
			for (prefix in a) {
				MyQuery.buildParams(prefix, a[prefix], add);
			}
		}
		return s.join("&").replace(/%20/g, "+");
	},
	isFunction : function(obj) {
		return MyQuery.type(obj) === "function";
	},
	isArray : Array.isArray || function(obj) {
		return MyQuery.type(obj) === "array";
	},
	isNumeric : function(obj) {
		return !isNaN(parseFloat(obj)) && isFinite(obj);
	},
	type : function(obj) {
		if (obj == null) {
			return String(obj);
		}
		return typeof obj === "object" || typeof obj === "function" ? MyQuery.class2type[MyQuery.class2type.toString.call(obj)] || "object" : typeof obj;
	},
	isEmptyObject : function(obj) {
		var name;
		for (name in obj) {
			return false;
		}
		return true;
	},
	isArraylike : function ( obj ) {
		var length = obj.length, type = MyQuery.type( obj );
		if ( obj.nodeType === 1 && length ) {
			return true;
		}
		return type === "array" || type !== "function" && ( length === 0 || typeof length === "number" && length > 0 && ( length - 1 ) in obj );
	},
	each : function(obj, callback, args) {
		var value, length = obj.length, isArray = MyQuery.isArraylike(obj);
		if (isArray) {
			for (var i=0; i < length; i++) {
				value = callback.call(obj[i], i, obj[i]);
				if (value === false) {
					break;
				}
			}
		} else {
			for (var i in obj) {
				value = callback.call(obj[i], i, obj[i]);
				if (value === false) {
					break;
				}
			}
		}
		return obj;
	},
	buildParams : function(prefix, obj, add) {
		if(obj === null || obj === ''){
			return;
		}
		var name;
		if (MyQuery.isArray(obj)) {
			MyQuery.each(obj, function(i, v) {
				if(v !== null && v !== ''){
					MyQuery.buildParams(prefix + "[" + i + "]", v, add);
				}
			});
		} else if (MyQuery.type(obj) === "object") {
			for (name in obj) {
				if(obj[name] !== null && obj[name] !== ''){
					MyQuery.buildParams(prefix + "." + name, obj[name], add);
				}
			}
		} else {
			if(obj !== null || obj !== ''){
				add(prefix, obj);
			}
		}
	},
	add : function(key, value) {
		value = MyQuery.isFunction(value) ? value() : (value == null ? "" : value);
		s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
	},
	class2type : []
}
MyQuery.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(i, name) {
	MyQuery.class2type[ "[object " + name + "]" ] = name.toLowerCase();
});