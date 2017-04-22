package org.redis.manager.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

public class BeanUtils extends BeanUtilsBean{
	
	static BeanUtils util = new BeanUtils();

	public static void copyNotNullProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (orig instanceof DynaBean) {
            final DynaProperty[] origDescriptors =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if (util.getPropertyUtils().isReadable(orig, name) &&
                		util.getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = ((DynaBean) orig).get(name);
                    if(value != null) util.copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> propMap = (Map<String, Object>) orig;
            for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
                final String name = entry.getKey();
                if (util.getPropertyUtils().isWriteable(dest, name)) {
                	final Object value = entry.getValue();
                    if(value != null) util.copyProperty(dest, name, value);
                }
            }
        } else{
            final PropertyDescriptor[] origDescriptors = util.getPropertyUtils().getPropertyDescriptors(orig);
            for (PropertyDescriptor origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if ("class".equals(name)) {
                    continue;
                }
                if (util.getPropertyUtils().isReadable(orig, name) &&
                		util.getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        final Object value = util.getPropertyUtils().getSimpleProperty(orig, name);
                        if(value != null) util.copyProperty(dest, name, value);
                    } catch (final NoSuchMethodException e) { }
                }
            }
        }
	}
}
