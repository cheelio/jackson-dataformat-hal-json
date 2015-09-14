package com.tensorwrench.jackson.hal.serializer;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;

import java.util.ArrayList;
import java.util.List;

class EmptyClass {
	
}


public abstract class BaseHalPropertyWriter extends BeanPropertyWriter{
	protected final List<BeanPropertyWriter> properties=new ArrayList<BeanPropertyWriter>();
	
	public BaseHalPropertyWriter(String name) {
		super(
				new SimpleBeanPropertyDefinition(createAnnotatedMethod(),name),
				createAnnotatedMethod(),
				new AnnotationMap(), 
				TypeFactory.defaultInstance().constructType(EmptyClass.class), 
				null, 
				null, 
				TypeFactory.defaultInstance().constructType(EmptyClass.class), 
				false, 
				null);
	}
	
	private static AnnotatedMember createAnnotatedMethod() {
		try {
			return new AnnotatedMethod(BaseHalPropertyWriter.class.getMethod("dummyMethod"),	new AnnotationMap(), new AnnotationMap[0]);
		} catch (NoSuchMethodException e){
			// shouldn't happen... so don't worry about it too much
			return null;
		} catch (SecurityException e) {
			// shouldn't happen... so don't worry about it too much
			return null;
		}
	}

	public void addProp(BeanPropertyWriter p) {
		properties.add(p);
	}

	public boolean hasProperties(){
		return !properties.isEmpty();
	}

	public Object dummyMethod() {
		return null;
	}
	
}
