package com.bankOfHyrule.util;

import java.util.Arrays;

/**
 * a very simple custom arraylist that can add elements, get element at index, and check if it's empty
 * */
public class CustomArrayList<T> implements CustomList<T> {
	
	
		protected int size;
		protected Object[] elements;
		
		public CustomArrayList() {
			elements = new Object[16];
		}
		
		public CustomArrayList(int intitialCapacity) {
			elements = new Object[intitialCapacity];
		}
		
		/**
		 * Appends the specified element to the end of this list.
		 * 
		 * @param element = the element to be appended to this list
		 * @return true
		 **/
		@Override
		public boolean add(T element) {
			elements[size] = element;
			size++;
			resizeBackingArrayIfNeeded();
			return true;
		}
		
		 /**
	     * Returns true if this list contains no elements.
	     *
	     * @return true if this list contains no elements
	     */
		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		/**
	     * Returns the number of elements in this list.
	     *
	     * @return the number of elements in this list
	     */
		@Override
		public int size() {
			return size;
		}

		/**
	     * Returns the element at the specified position in this list.
	     *
	     * @param index index of the element to return
	     * @return the element at the specified position in this list
	     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
	     */
		@Override
		@SuppressWarnings({"unchecked"})
		public T get(int index) {
			if(notInRange(index)) {
				throw new IndexOutOfBoundsException();
			}
			return (T) elements[index];
		}

		/**
	     * Inserts the specified element at the specified position in this list. Shifts
	     * the element currently at that position (if any) and any subsequent elements
	     * to the right (adds one to their indices).
	     *
	     * @param index index at which the specified element is to be inserted
	     * @param element element to be inserted
	     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
	     */
		@Override
		public void add(int index, T element) {
			if(index < 0 || index > size) throw new IndexOutOfBoundsException();
			Object[] newElements = resizeWillBeNeeded(size + 1) ? new Object[nextSize()] : new Object[elements.length];
			System.arraycopy(elements, 0, newElements, 0, index);
			System.arraycopy(elements, index, newElements, index + 1, elements.length - index - 1);
			newElements[index] = element;
			size++;
			elements = newElements;
		}

		

	    protected boolean notInRange(int index) {
	        return index < 0 || index >= size;
	    }

	    protected void resizeBackingArrayIfNeeded() {
	        if (size >= elements.length * 0.75) {
	            Object[] newBackingArray = new Object[nextSize()];
	            System.arraycopy(elements, 0, newBackingArray, 0, elements.length);
	            elements = newBackingArray;
	        }
	    }

	    protected boolean resizeWillBeNeeded(int nextSize) {
	        return (nextSize >= elements.length * 0.75);
	    }

	    protected int nextSize() {
	        return (int) (elements.length * 0.5) + elements.length;
	    }

	    @Override
	    public String toString() {
	        return Arrays.toString(elements);
	    }

}
