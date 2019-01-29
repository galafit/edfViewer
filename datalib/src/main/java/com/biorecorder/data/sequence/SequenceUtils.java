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
     * It just returns an array of sorted indexes which represents sorted version (view)
     * of the data.
     * @return array of sorted indexes. So that dataSequence.get(sorted[i]) will be sorted for i = 0, 1,..., length - 1
     */
    public static int[] sort(IntSequence dataSequence, int length, boolean isParallel) {
        int[] orderedIndexes = new int[length];

        for (int i = 0; i < length; i++) {
            orderedIndexes[i]  = i;
        }

        IntComparator comparator = new IntComparator() {
            @Override
            public int compare(int index1, int index2) {
                return Comparators.compareInt(dataSequence.get(orderedIndexes[index1]), dataSequence.get(orderedIndexes[index2]));
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
     * Binary search algorithm. The sequence must be sorted! Find some occurrence
     * (if there are multiples, it returns some arbitrary one)
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] < v <= a[i]. If there is no suitable index, return <b>from</b>
     *
     * Complexity O(log n).
     */
    public static int bisect(IntSequence dataSequence, int value, int fromIndex, int length) {
        int low = fromIndex;
        int high = fromIndex + length;
        while (low < high) {
            int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (Comparators.compareInt(value, dataSequence.get(mid)) > 0) {
                low = mid + 1;
            } else if (Comparators.compareInt(value, dataSequence.get(mid)) < 0) {
                high = mid;
            } else { //  Values are equal but for float and double additional checks is needed
                return mid; // Key found
            }
        }
        return low;  // key not found.
    }



    /**
     * Lower bound binary search algorithm. The sequence must be sorted! Find the FIRST occurrence
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] < v <= a[i]. If there is no suitable index, return <b>from</b>
     *
     * Complexity O(log n).
     */
    public static int bisectLeft(FloatSequence dataSequence, float value, int from, int length) {
        int low = from;
        int high = from + length;
        while (low < high) {
            final int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (Comparators.compareFloat(value, dataSequence.get(mid)) <= 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }


    /**
     * Upper bound search algorithm. The sequence must be sorted. Find the LAST occurrence
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] <= v < a[i]. If there is no suitable index, return <b>from + length</b>
     *
     * Complexity O(log n).
     */
    public static int bisectRight(FloatSequence dataSequence, float value, int from, int length) {
        int low = from;
        int high = from + length;
        while (low < high) {
            final int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (Comparators.compareFloat(value, dataSequence.get(mid)) >= 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        if(low > from && Comparators.compareFloat(dataSequence.get(low - 1), value) == 0) {
            return low - 1;
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
        int from = 0;
        int length = 8;
        int[] sorted = sort(dataSequence, length, false);

        System.out.println("\nResultant sorted data: " + "from = " + from + "  length = " + length);
        for (int i = 0; i < sorted.length; i++) {
            System.out.println(i + "  " + dataSequence.get(sorted[i]));
        }
    }


}
