/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import linaje.tree.TreeNodeVector;
import linaje.statics.Constants;

/**
 * Multiples utilidades para Crear, modificar, manipular, consultar y convertir todo tipo de listas y arrays
 **/
public class Lists {

	public static <T> List<T> enumerationToList(Enumeration<T> enumeration) {
		List<T> list = newList();
		addEnumerationElements(list, enumeration);
		return list;
	}
	
	public static <T> List<T> iteratorToList(Iterator<T> iterator) {
		List<T> list = newList();
		addIteratorElements(list, iterator);
		return list;
	}
	
	public static <T> List<TreeNodeVector<T>> nodeToList(TreeNodeVector<T> node) {
		Enumeration<TreeNodeVector<T>> enumeration = node != null ? node.breadthFirstEnumeration() : null;
		return enumerationToList(enumeration);
	}
	
	public static <T> boolean arrayContains(T[] array, T elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	public static boolean arrayContains(int[] array, int elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	public static boolean arrayContains(long[] array, long elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	public static boolean arrayContains(short[] array, short elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	public static boolean arrayContains(float[] array, float elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	public static boolean arrayContains(char[] array, char elem) {
		return arrayIndexOf(array, elem) >= 0;
	}
	
	public static <T> int arrayIndexOf(T[] array, T elem) {
		if (elem == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null)
					return i;
			}
		}
		else {
			for (int i = 0; i < array.length; i++) {
				if (elem.equals(array[i]))
					return i;
			}
		}
		return -1;
	}	
	public static int arrayIndexOf(int[] array, int elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	public static int arrayIndexOf(long[] array, long elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	public static int arrayIndexOf(double[] array, double elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	public static int arrayIndexOf(short[] array, short elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	public static int arrayIndexOf(float[] array, float elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	public static int arrayIndexOf(char[] array, char elem) {
		for (int i = 0; i < array.length; i++) {
			if (elem == array[i])
				return i;
		}
		return -1;
	}
	
	public static <T extends Comparable<? super T>> Comparator<T> getComparator(boolean useNaturalOrder) {
		
		Comparator<T> comparator;
		if (useNaturalOrder)
			comparator = Comparators.naturalOrder();
		else
			comparator = Comparators.reverseOrder();
		
		//Si se usa el JDK 1.7, recompilar esta clase usando linaje.utils.Comparators en lugar de java.utils.Comparators
		return Comparators.nullsLast(comparator);
	}
	
	public static <T> Comparator<T> getComparatorElemsToString(final boolean useNaturalOrder) {
		
		Comparator<T> comparator = new Comparator<T>() {
			
			public int compare(T elem1, T elem2) {
				
				String s1 = elem1 != null ? elem1.toString() : null;
				String s2 = elem2 != null ? elem2.toString() : null;
				
				//Ponderemos los nulls al final
				return useNaturalOrder ? Strings.compare(s1, s2) : Strings.compare(s2, s1);
			}
		};
		
		return comparator;
	}
	
	public static Comparator<Object> getComparatorUndefined(final boolean useNaturalOrder) {
		
		Comparator<Object> comparator = new Comparator<Object>() {
			
			public int compare(Object elem1, Object elem2) {
				
				try {	
					return useNaturalOrder ? Utils.compare(elem1, elem2) : Utils.compare(elem2, elem1);
				}
				catch (Exception e) {
					return 0;
				}
			}
		};
		
		return comparator;
	}

	public static Comparator<List<?>> getComparatorList(final boolean useNaturalOrder, final int index) {
		
		Comparator<List<?>> comparator = new Comparator<List<?>>() {
			
			public int compare(List<?> list1, List<?> list2) {
				
				try {	
					Object elem1 = list1.get(index);
					Object elem2 = list2.get(index);
					
					return useNaturalOrder ? Utils.compare(elem1, elem2) : Utils.compare(elem2, elem1);
				}
				catch (Exception e) {
					return 0;
				}
			}
		};
		
		return comparator;
	}
	
	public static <T extends Comparable<? super T>> void sort(List<T> list) {
		sort(list, true);
	}
	public static <T extends Comparable<? super T>> void sort(List<T> list, boolean useNaturalOrder) {
		
		Comparator<T> comparator = getComparator(useNaturalOrder);
		Collections.sort(list, comparator);
	}
	
	public static <T> void sortUndefinedList(List<T> list) {
		sortUndefinedList(list, true);
	}
	public static <T> void sortUndefinedList(List<T> list, boolean useNaturalOrder) {
		
		Comparator<Object> comparator = getComparatorUndefined(useNaturalOrder);
		Collections.sort(list, comparator);
	}
	
	/**
	 * Lo usaremos para ordenar listas de objetos no comparables o cuando queramos ordenar por el valor del objeto toString
	 */
	public static <T> void sortElemsToString(List<T> list) {
		sortElemsToString(list, true);
	}
	public static <T> void sortElemsToString(List<T> list, boolean useNaturalOrder) {
		Comparator<T> comparator = getComparatorElemsToString(useNaturalOrder);
		Collections.sort(list, comparator);
	}
	
	/**
	 * Lo usaremos principalmente para ordenar las filas de una tabla por el indice de una columna
	 */
	public static <T extends List<?>> void sortRows(List<T> rows, int index) {
		sortRows(rows, true, index);
	}
	public static <T extends List<?>> void sortRows(List<T> rows, boolean useNaturalOrder, int index) {
		Comparator<List<?>> comparator = getComparatorList(useNaturalOrder, index);
		Collections.sort(rows, comparator);
	}
	
	
	public static <T extends Comparable<? super T>> void sort(T[] array) {
		sort(array, true);
	}
	public static <T extends Comparable<? super T>> void sort(T[] array, boolean useNaturalOrder) {
		 
		Comparator<T> comparator = getComparator(useNaturalOrder);
		Arrays.sort(array, comparator);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(int[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(long[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(double[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(short[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(float[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(byte[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	public static void sort(char[] array, boolean useNaturalOrder) {
		Arrays.sort(array);
		if (!useNaturalOrder)
			reverseArray(array);
	}
	
	public static <T> String arrayToString(T[] array) {
		return arrayToString(array, Constants.COMMA);
	}
	public static <T> String arrayToString(T[] array, String separator) {
		
		StringBuffer sb = new StringBuffer();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (i > 0)
					sb.append(separator);
				sb.append(array[i]);
			}
		}
		return sb.toString();
	}
	
	public static <T> String listToString(List<T> list) {
		return listToString(list, Constants.COMMA);
	}
	public static <T> String listToString(List<T> list, String separator) {
		
		StringBuffer sb = new StringBuffer();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					sb.append(separator);
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> type, int dimension) {
		return (T[]) Array.newInstance(type, dimension);
	}
	@SuppressWarnings("unchecked")
	public static <T> T[][] newArray(Class<T> type, int rows, int columns) {
		return (T[][]) Array.newInstance(type, rows, columns);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> int[] newArrayInt(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		int[] arrayInt = new int[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayInt[i] = number != null ? number.intValue() : 0;
		}
		return arrayInt;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Number> long[] newArrayLong(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		long[] arrayLong = new long[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayLong[i] = number != null ? number.longValue() : (long) 0;
		}
		return arrayLong;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Number> double[] newArrayDouble(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		double[] arrayDouble = new double[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayDouble[i] = number != null ? number.doubleValue() : (double) 0;
		}
		return arrayDouble;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Number> short[] newArrayShort(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		short[] arrayShort = new short[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayShort[i] = number != null ? number.shortValue() : (short) 0;
		}
		return arrayShort;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Number> float[] newArrayFloat(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		float[] arrayFloat = new float[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayFloat[i] = number != null ? number.floatValue() : (float) 0;
		}
		return arrayFloat;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Number> byte[] newArrayByte(T... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		byte[] arrayByte = new byte[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			T number = elements[i];
			arrayByte[i] = number != null ? number.byteValue() : (byte) 0;
		}
		return arrayByte;
	}
	public static boolean[] newArrayBoolean(Boolean... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		boolean[] arrayBoolean = new boolean[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			arrayBoolean[i] = elements[i].booleanValue();
		}
		return arrayBoolean;
	}
	public static char[] newArrayChar(Character... elements) {
		
		int elementsSize = elements != null ? elements.length : 0;
		char[] arrayChar = new char[elementsSize];
		for (int i = 0; i < elementsSize; i++) {
			arrayChar[i] = elements[i].charValue();
		}
		return arrayChar;
	}
	
	@SafeVarargs
	public static <T> List<T> newList(T... elements) {
		return newList(false, elements);
	}
	@SafeVarargs
	public static <T> List<T> newList(boolean synchronizedList, T... elements) {
		
		int length = elements != null ? elements.length : 10;
		List<T> list = synchronizedList ? new CopyOnWriteArrayList<T>() : new ArrayList<T>(length);
		addElements(list, elements);
		
		return list;
	}
	public static <T> Vector<T> newVector() {
    	return new Vector<T>();
    }
	public static <T> Vector<T> newVector(int size) {
    	return new Vector<T>(size);
    }
	
	@SafeVarargs
	public static <T> Vector<T> arrayToVector(T... array) {
		Vector<T> v = new Vector<T>(array.length);
		addElements(v, array);
		return v;
	}
	@SafeVarargs
	public static <T> List<T> arrayToList(T... array) {
		return arrayToList(false, array);
	}
	@SafeVarargs
	public static <T> List<T> arrayToList(boolean synchronizedList, T... array) {
		return newList(synchronizedList, array);
	}
	public static List<Integer> arrayToList(int[] array) {
		List<Integer> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Long> arrayToList(long[] array) {
		List<Long> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Double> arrayToList(double[] array) {
		List<Double> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Short> arrayToList(short[] array) {
		List<Short> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Float> arrayToList(float[] array) {
		List<Float> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Byte> arrayToList(byte[] array) {
		List<Byte> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Boolean> arrayToList(boolean[] array) {
		List<Boolean> list = newList();
		addElements(list, array);
		return list;
	}
	public static List<Character> arrayToList(char[] array) {
		List<Character> list = newList();
		addElements(list, array);
		return list;
	}
	
	public static <T> T[] reverseArray(T[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
			    T leftValue = array[leftIndex];
			    T rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static int[] reverseArray(int[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				int leftValue = array[leftIndex];
				int rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static long[] reverseArray(long[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				long leftValue = array[leftIndex];
				long rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static double[] reverseArray(double[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				double leftValue = array[leftIndex];
				double rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static short[] reverseArray(short[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				short leftValue = array[leftIndex];
				short rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static float[] reverseArray(float[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				float leftValue = array[leftIndex];
				float rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static byte[] reverseArray(byte[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				byte leftValue = array[leftIndex];
				byte rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static boolean[] reverseArray(boolean[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				boolean leftValue = array[leftIndex];
				boolean rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}
	public static char[] reverseArray(char[] array) {
		
		int length = array != null ? array.length : 0;
		if (length > 1) {
			int mid = array.length / 2;
			for(int i = 0; i < mid; i++) {
				int leftIndex = i;
				int rightIndex = length - i - 1;
				char leftValue = array[leftIndex];
				char rightValue = array[rightIndex];
			    array[leftIndex] = rightValue;
			    array[rightIndex] = leftValue;
			}
		}
		return array;
	}

	/**
	 * Es mas seguro utilizar listToArray(List<T> list, Class<T> type)
	 * ya que cuando la lista esté vacía realmente devolverá un array de Object.
	 * También fallará éste método si el primer elemento de la lista es null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] listToArray(List<T> list) {
		if (list == null)
			return null;
		else if (list.isEmpty()) {
			return (T[]) list.toArray();
		}
		else {
			//list.toArray() a veces da problemas de ClassCastException si el contenido de la lista es una subclase de T, por lo que lo resolvemos a mano
			try {
				Class<T> type = (Class<T>) list.get(0).getClass();
				return listToArray(list, type);
			}
			catch (ArrayStoreException ex) {
				//Alguno de los elementos es de distinto tipo del primero por lo que devolvemos un array de Object
				Class<T> type = (Class<T>) Object.class;
				return listToArray(list, type);
			}
		}
    }
	public static <T> T[] listToArray(List<T> list, Class<T> type) {
		T[] array = newArray(type, list.size());
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
    }
	
	public static <T extends Number> int[] listToArrayInt(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		int[] arrayInt = new int[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayInt[i] = number != null ? number.intValue() : 0;
		}
		return arrayInt;
	}
	public static <T extends Number> long[] listToArrayLong(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		long[] arrayLong = new long[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayLong[i] = number != null ? number.longValue() : (long) 0;
		}
		return arrayLong;
	}
	public static <T extends Number> double[] listToArrayDouble(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		double[] arrayDouble = new double[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayDouble[i] = number != null ? number.doubleValue() : (double) 0;
		}
		return arrayDouble;
	}
	public static <T extends Number> short[] listToArrayShort(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		short[] arrayShort = new short[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayShort[i] = number != null ? number.shortValue() : (short) 0;
		}
		return arrayShort;
	}
	public static <T extends Number> float[] listToArrayFloat(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		float[] arrayFloat = new float[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayFloat[i] = number != null ? number.floatValue() : (float) 0;
		}
		return arrayFloat;
	}
	public static <T extends Number> byte[] listToArrayByte(List<T> list) {
		
		int listSize = list != null ? list.size() : 0;
		byte[] arrayByte = new byte[listSize];
		for (int i = 0; i < listSize; i++) {
			T number = list.get(i);
			arrayByte[i] = number != null ? number.byteValue() : (byte) 0;
		}
		return arrayByte;
	}
	public static boolean[] listToArrayBoolean(List<Boolean> list) {
		
		int listSize = list != null ? list.size() : 0;
		boolean[] arrayBoolean = new boolean[listSize];
		for (int i = 0; i < listSize; i++) {
			arrayBoolean[i] = list.get(i).booleanValue();
		}
		return arrayBoolean;
	}
	public static char[] listToArrayChar(List<Character> list) {
		
		int listSize = list != null ? list.size() : 0;
		char[] arrayChar = new char[listSize];
		for (int i = 0; i < listSize; i++) {
			arrayChar[i] = list.get(i).charValue();
		}
		return arrayChar;
	}
	
	@SafeVarargs
	public static <T> void setElements(List<T> list, T... elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Integer> list, int[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Long> list, long[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Double> list, double[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Short> list, short[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Float> list, float[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Byte> list, byte[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Boolean> list, boolean[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static void setElements(List<Character> list, char[] elements) {
		if (!list.isEmpty())
			list.clear();
		addElements(list, elements);
	}
	public static <T> void setElementsEnumeration(List<T> list, Enumeration<T> enumeration) {
		if (!list.isEmpty())
			list.clear();
		addEnumerationElements(list, enumeration);
	}
	
	@SafeVarargs
	public static <T> void addElements(List<T> list, T... elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Integer> list, int[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Long> list, long[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Double> list, double[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Short> list, short[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Float> list, float[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Byte> list, byte[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Boolean> list, boolean[] elements) {
		addElements(0, list, elements);
	}
	public static void addElements(List<Character> list, char[] elements) {
		addElements(0, list, elements);
	}
	
	@SafeVarargs
	public static <T> void addElements(int index, List<T> list, T... elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, elements[i]);
			}
		}
	}
	public static void addElements(int index, List<Integer> list, int[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Integer.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Long> list, long[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Long.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Double> list, double[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Double.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Short> list, short[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Short.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Float> list, float[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Float.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Byte> list, byte[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Byte.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Boolean> list, boolean[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Boolean.valueOf(elements[i]));
			}
		}
	}
	public static void addElements(int index, List<Character> list, char[] elements) {
		
		int length = elements != null ? elements.length : 0;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				list.add(index + i, Character.valueOf(elements[i]));
			}
		}
	}
	
	public static <T> void addEnumerationElements(List<T> list, Enumeration<T> enumeration) {
		addEnumerationElements(0, list, enumeration);
	}
	public static <T> void addEnumerationElements(int index, List<T> list, Enumeration<T> enumeration) {
		
		if (enumeration != null) {
			while (enumeration.hasMoreElements())
				list.add(enumeration.nextElement());
		} 
	}
	
	public static <T> void addIteratorElements(List<T> list, Iterator<T> iterator) {
		addIteratorElements(0, list, iterator);
	}
	public static <T> void addIteratorElements(int index, List<T> list, Iterator<T> iterator) {
		
		if (iterator != null) {
			while (iterator.hasNext())
				list.add(iterator.next());
		} 
	}

	public static <T> T getFirstElement(List<T> list) {
		T firstElement = null;
		if (list != null && !list.isEmpty())
			firstElement = list.get(0);
		
		return firstElement;
	}
	public static <T> T getFirstElement(T[] array) {
		T lastElement = null;
		if (array != null && array.length > 0)
			lastElement = array[0];
		
		return lastElement;
	}
	
	public static <T> T getLastElement(List<T> list) {
		T lastElement = null;
		if (list != null && !list.isEmpty())
			lastElement = list.get(list.size() -1);
		
		return lastElement;
	}
	public static <T> T getLastElement(T[] array) {
		T lastElement = null;
		if (array != null && array.length > 0)
			lastElement = array[array.length -1];
		
		return lastElement;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(Class<T> type, T[] array1, T... array2) {
	    
		int a1Length = array1.length;
	    int a2Length = array2.length;
	    
	    T[] concatArray;
	    if (array2 != null && array2.length > 0 && array2[0] != null) {
		    concatArray = newArray(type, a1Length + a2Length);
		    System.arraycopy(array1, 0, concatArray, 0, a1Length);
		    System.arraycopy(array2, 0, concatArray, a1Length, a2Length);
	    }
	    else {
	    	concatArray = array1;
	    }

	    return concatArray;
	}
}
