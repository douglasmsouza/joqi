package br.com.joqi.testes;

import java.util.Comparator;

public class MergeSort {
	/**
	 * Sorts the specified array of objects according to the order induced by
	 * the specified comparator. All elements in the array must be
	 * <i>mutually comparable</i> by the specified comparator (that is,
	 * <tt>c.compare(e1, e2)</tt> must not throw a <tt>ClassCastException</tt>
	 * for any elements <tt>e1</tt> and <tt>e2</tt> in the array).
	 * <p>
	 * 
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort.
	 * <p>
	 * 
	 * The sorting algorithm is a modified mergesort (in which the merge is
	 * omitted if the highest element in the low sublist is less than the lowest
	 * element in the high sublist). This algorithm offers guaranteed n*log(n)
	 * performance, and can approach linear performance on nearly sorted lists.
	 * 
	 * @param a the array to be sorted.
	 * @param c the comparator to determine the order of the array.
	 * @throws ClassCastException if the array contains elements that are
	 *             not <i>mutually comparable</i> using the specified
	 *             comparator.
	 * @see Comparator
	 */
	public static void sort(Object[] a, Comparator c) {
		Object aux[] = (Object[]) a.clone();
		mergeSort(aux, a, 0, a.length, c);
	}

	private static void mergeSort(Object src[],Object dest[],int low,int high,Comparator c) {
		int length = high - low;

		// Insertion sort on smallest arrays
		if (length < 7) {
			for (int i = low; i < high; i++)
				for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--)
					swap(dest, j, j - 1);
			return;
		}

		// Recursively sort halves of dest into src
		int mid = (low + high) / 2;
		mergeSort(dest, src, low, mid, c);
		mergeSort(dest, src, mid, high, c);

		// If list is already sorted, just copy from src to dest. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (c.compare(src[mid - 1], src[mid]) <= 0) {
			System.arraycopy(src, low, dest, low, length);
			return;
		}

		// Merge sorted halves (now in src) into dest
		for (int i = low, p = low, q = mid; i < high; i++) {
			if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}

	/**
	 * Swaps x[a] with x[b].
	 */
	private static void swap(Object x[], int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
}