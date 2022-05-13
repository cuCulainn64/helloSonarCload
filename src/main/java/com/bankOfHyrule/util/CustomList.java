package com.bankOfHyrule.util;

public interface CustomList<T> extends CustomCollection<T> {

	T get(int index);
	void add(int index, T element);
	
}
