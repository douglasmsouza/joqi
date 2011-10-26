package br.com.joqi.testes;

import java.util.Comparator;

public class MergeSorteMain {

	/**
	 * Mergesort algorithm.
	 * 
	 * @param a an array of Comparable items.
	 */
	public static void mergeSort(Comparable[] a) {
		Comparable[] tmpArray = new Comparable[a.length];
		mergeSort(a, tmpArray, 0, a.length - 1);
	}

	/**
	 * Internal method that makes recursive calls.
	 * 
	 * @param a an array of Comparable items.
	 * @param tmpArray an array to place the merged result.
	 * @param left the left-most index of the subarray.
	 * @param right the right-most index of the subarray.
	 */
	private static void mergeSort(Comparable[] a, Comparable[] tmpArray,
			int left, int right) {
		if (left < right) {
			int center = (left + right) / 2;
			mergeSort(a, tmpArray, left, center);
			mergeSort(a, tmpArray, center + 1, right);
			merge(a, tmpArray, left, center + 1, right);
		}
	}

	/**
	 * Internal method that merges two sorted halves of a subarray.
	 * 
	 * @param a an array of Comparable items.
	 * @param tmpArray an array to place the merged result.
	 * @param leftPos the left-most index of the subarray.
	 * @param rightPos the index of the start of the second half.
	 * @param rightEnd the right-most index of the subarray.
	 */
	private static void merge(Comparable[] a, Comparable[] tmpArray,
			int leftPos, int rightPos, int rightEnd) {
		int leftEnd = rightPos - 1;
		int tmpPos = leftPos;
		int numElements = rightEnd - leftPos + 1;

		// MergeSorteMain loop
		while (leftPos <= leftEnd && rightPos <= rightEnd)
			if (a[leftPos].compareTo(a[rightPos]) <= 0)
				tmpArray[tmpPos++] = a[leftPos++];
			else
				tmpArray[tmpPos++] = a[rightPos++];

		while (leftPos <= leftEnd)
			// Copy rest of first half
			tmpArray[tmpPos++] = a[leftPos++];

		while (rightPos <= rightEnd)
			// Copy rest of right half
			tmpArray[tmpPos++] = a[rightPos++];

		// Copy tmpArray back
		for (int i = 0; i < numElements; i++, rightEnd--)
			a[rightEnd] = tmpArray[rightEnd];
	}

	public static void main(String[] args) {
		BancoConsulta bancoConsulta = new BancoConsulta();
		Integer[] arrayOrdernacao = bancoConsulta.getInteiros().toArray(new Integer[0]);

		double time = System.currentTimeMillis();

		// mergeSort(arrayOrdernacao);

		MergeSort.sort(arrayOrdernacao, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});

		time = System.currentTimeMillis() - time;

		for (Object o : arrayOrdernacao) {
			System.out.println(o);
		}
		System.out.println("Tempo: " + time + " ms");
	}
}
