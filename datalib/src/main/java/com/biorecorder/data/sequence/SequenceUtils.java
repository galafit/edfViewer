package com.biorecorder.data.sequence;



public class SequenceUtils {
    /**
     * based on:
     * http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/src/share/classes/java/util/Arrays.java
     *<p>
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     *
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>fromIndex + length - 1</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    public static int binarySearch(IntSequence data, int value, int fromIndex, int length) {
        int low = fromIndex;
        int high = fromIndex + length -1;
        while (low <= high) {
            int mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                return mid; // Key found
            }
        }
        return -(low + 1);  // key not found.
    }
}