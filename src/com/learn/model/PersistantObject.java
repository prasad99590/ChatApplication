package com.learn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.List;

public class PersistantObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long persistantId = 0;

	public PersistantObject() {

	}

	public long getPersistantId() {
		return persistantId;
	}

	public void setPersistantId(long id) {
		this.persistantId = id;
	}

	public void detach(Set<Object> detachedValues) {

	}

	@Override
	public boolean equals(Object compare) {

		if (compare == null || !(compare instanceof PersistantObject)) {
			return false;
		}

		return persistantId == ((PersistantObject) compare).persistantId;

	}

	public static <T> List<T> detachList(List<T> values, Set<Object> detachedValues) {

		if (values == null) {
			return values;
		}
		List<T> detached = new ArrayList<>(values);

		detachCollection(detached, detachedValues);

		return detached;
	}

	public static <T> Set<T> detachSet(Set<T> values, Set<Object> detachedValues) {

		if (values == null) {
			return values;
		}
		Set<T> detached = new HashSet<>(values);

		detachCollection(detached, detachedValues);

		return detached;
	}

	public static <T> void detachCollection(Collection<T> values, Set<Object> detachedValues) {

		if (values == null) {
			return;
		}
		for (Object value : values) {
			System.out.println(value);
			detach(value, detachedValues);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Object detach(Object value, Set<Object> detachedValues) {
		if (value == null) {
			return null;
		}
		if(detachedValues.contains(value)) return value;
		detachedValues.add(value);
		if (value instanceof PersistantObject) {
//			System.out.println("It is persistant object...\n\n\n");
			((PersistantObject) value).detach(detachedValues);
			return value;
		}
		if (value instanceof List) {
//			System.out.println("It is list object...\n\n\n");
			return detachList((List<T>) value, detachedValues);
		} 
		else if (value instanceof Set) {
//			System.out.println("It is set object...\n\n\n");
			return detachSet((Set<T>) value, detachedValues);
		}
		else {
//			System.out.println("It is value...\n\n\n");
			return value;
		}
	}
}

