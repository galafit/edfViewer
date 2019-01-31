package com.biorecorder.data.sequence;


import com.biorecorder.data.frame.IntComparator;
import com.biorecorder.data.frame.Swapper;

/**
 * Based on:
 * <br><a href="https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/search/UpperBound.java">github UpperBound.java</a>
 * <br><a href="https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/search/LowerBound.java">github LowerBound.java</a>
 * <br><a href="https://rosettacode.org/wiki/Binary_search">Binary search</a>
 */
public class SequenceUtils {

    /**
     * This method do not modifying the order of the underlying data!
     * It simply returns an array of sorted indexes which represent sorted version (view)
     * of the data.
     * @return array of sorted indexes. So that data.get(sorted[i]) will be sorted for i = 0, 1,..., length - 1
     */
    public static int[] sort(IntSequence data, int from, int length, boolean isParallel) {
        int[] orderedIndexes = new int[length];

        for (int i = 0; i < length; i++) {
            orderedIndexes[i]  = i + from;
        }

        IntComparator comparator = new IntComparator() {
            @Override
            public int compare(int index1, int index2) {
                return PrimitiveUtils.compareInt(data.get(orderedIndexes[index1]), data.get(orderedIndexes[index2]));
            }
        };
        Swapper swapper = new Swapper() {
            @Override
            public void swap(int index1, int index2) {
                int v1 = orderedIndexes[index1];
                int v2 = orderedIndexes[index2];
                orderedIndexes[index1] = v2;
                orderedIndexes[index2] = v1;
            }
        };
        SortAlgorithm.getDefault(isParallel).sort(0, length, comparator, swapper);

        return orderedIndexes;
    }


    /**
     * Binary search algorithm. The sequence must be sorted!
     * Find the index of the <b>value</b>. If data sequence contains 
     * multiple elements equal to the searched <b>value</b>, there is no guarantee which
     * one will be found. If there is no element equal to the searched value function returns
     * the insertion point for <b>value</b> in the data sequence to maintain sorted order
     * (i.e. index of the first element which is less than the searched value).
     */
    public static int bisect(IntSequence data, int value, int fromIndex, int length) {
        int low = fromIndex;
        int high = fromIndex + length;
        while (low < high) {
            int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (PrimitiveUtils.compareInt(value, data.get(mid)) > 0) {
                low = mid + 1;
            } else if (PrimitiveUtils.compareInt(value, data.get(mid)) < 0) {
                high = mid;
            } else { //  Values are equal but for float and double additional checks is needed
                return mid; // Key found
            }
        }
        return low;  // key not found.
    }



    /**
     * Binary search algorithm. The sequence must be sorted!
     * Finds the insertion point for <b>value</b> in the data sequence to maintain sorted order.
     * Returns index such that: data.get(index - 1) < value <= data.get(index)
     * If <b>value</b> is already present in data sequence, the insertion point will be BEFORE (to the left of)
     * any existing entries.
     */
    public static int bisectLeft(IntSequence data, int value, int from, int length) {
        int low = from;
        int high = from + length;
        while (low < high) {
            final int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (PrimitiveUtils.compareInt(value, data.get(mid)) <= 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }


    /**
     * Similar to {@link #bisectLeft(IntSequence, int, int, int)},
     * but returns an insertion point which comes AFTER (to the right of)
     * any existing entries of <b>value</b> in data sequence: data.get(index - 1) <= value < data.get(index)
     */
    public static int bisectRight(IntSequence data, int value, int from, int length) {
        int low = from;
        int high = from + length;
        while (low < high) {
            final int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (PrimitiveUtils.compareInt(value, data.get(mid)) >= 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }


    public static void main(String[] args) {
        System.out.println("Sort test");

        int[] data = {5, 2, 4, 1, 3, 8, 100, 1, 5, 3, 20};
        IntSequence dataSequence = new IntSequence() {
            @Override
            public int size() {
                return data.length;
            }

            @Override
            public int get(int index) {
                return data[index];
            }
        };


        System.out.println("\nOriginal data:");
        for (int i = 0; i < dataSequence.size(); i++) {
            System.out.println(i + "  " + dataSequence.get(i));
        }
        int from = 2;
        int length = 8;
        int[] sorted = sort(dataSequence, from, length, false);

        System.out.println("\nResultant sorted data: " + "from = " + from + "  length = " + length);
        for (int i = 0; i < sorted.length; i++) {
            System.out.println(i + "  " + dataSequence.get(sorted[i]));
        }
    }
}
