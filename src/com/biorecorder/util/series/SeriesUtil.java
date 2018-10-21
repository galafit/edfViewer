package com.biorecorder.util.series;

/**
 * based on:
 * http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/src/share/classes/java/util/Arrays.java
 */
public class SeriesUtil {
    /******************************************************************
     *                         BINARY SEARCH
     ******************************************************************/
    /**
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}
     */
    public static long binarySearch(DoubleSeries data, double value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "double");
                long valueBits = getValueBits(value+"", "double");
                if (midBits == valueBits) { // Values are equal
                   return mid; // Key found
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}
     */
    public static long binarySearch(FloatSeries data, float value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "float");
                long valueBits = getValueBits(value+"", "float");
                if (midBits == valueBits) { // Values are equal
                   return mid; // Key found
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}
     */
    public static long binarySearch(IntSeries data, int value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "int");
                long valueBits = getValueBits(value+"", "int");
                if (midBits == valueBits) { // Values are equal
                   return mid; // Key found
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}
     */
    public static long binarySearch(LongSeries data, long value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "long");
                long valueBits = getValueBits(value+"", "long");
                if (midBits == valueBits) { // Values are equal
                   return mid; // Key found
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Searches a range of
     * the specified array of floats for the specified value using
     * the binary search algorithm.
     * The range must be sorted. If
     * it is not sorted, the results are undefined. If the range contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found. This method considers all NaN values to be
     * equivalent and equal.
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}
     */
    public static long binarySearch(ShortSeries data, short value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "short");
                long valueBits = getValueBits(value+"", "short");
                if (midBits == valueBits) { // Values are equal
                   return mid; // Key found
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns FIRST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long lowerBound(DoubleSeries data, double value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "double");
                long valueBits = getValueBits(value+"", "double");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    high = mid - 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return low;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns FIRST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long lowerBound(FloatSeries data, float value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "float");
                long valueBits = getValueBits(value+"", "float");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    high = mid - 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return low;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns FIRST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long lowerBound(IntSeries data, int value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "int");
                long valueBits = getValueBits(value+"", "int");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    high = mid - 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return low;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns FIRST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long lowerBound(LongSeries data, long value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "long");
                long valueBits = getValueBits(value+"", "long");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    high = mid - 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return low;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns FIRST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long lowerBound(ShortSeries data, short value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "short");
                long valueBits = getValueBits(value+"", "short");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    high = mid - 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return low;
        }
        return index;
    }

    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns LAST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long upperBound(DoubleSeries data, double value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "double");
                long valueBits = getValueBits(value+"", "double");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    low = mid + 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return high;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns LAST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long upperBound(FloatSeries data, float value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "float");
                long valueBits = getValueBits(value+"", "float");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    low = mid + 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return high;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns LAST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long upperBound(IntSeries data, int value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "int");
                long valueBits = getValueBits(value+"", "int");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    low = mid + 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return high;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns LAST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long upperBound(LongSeries data, long value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "long");
                long valueBits = getValueBits(value+"", "long");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    low = mid + 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return high;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but
     * if there are many values equals searched value function returns LAST occurrence.<br>
     * <p>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     *
     * @param data the Series to be searched
     * @param fromIndex the index of the first element (inclusive) to be searched
     * @param length number of element to be searched
     * @param value the value to be searched for
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>toIndex</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     * @throws IllegalArgumentException
     *         if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *         if {@code fromIndex < 0 or toIndex > a.length}

     */
    public static long upperBound(ShortSeries data, short value, long fromIndex, int length) {
        long low = fromIndex;
        long high = fromIndex + length -1;
        long index = -1;
        while (low <= high) {
            long mid = (low + high) >>> 1; // the same as (low + high) / 2
            if (value > data.get(mid)) {
                low = mid + 1;
            } else if (value < data.get(mid)) {
                high = mid - 1;
            } else { //  Values are equal but for float and double additional checks is needed
                long midBits = getValueBits(data.get(mid)+"", "short");
                long valueBits = getValueBits(value+"", "short");
                if (midBits == valueBits) { // Values are equal
                    index = mid;
                    low = mid + 1;
                } else if (midBits < valueBits) { // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                }
                else {  // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
                }
            }
        }
        if(index < 0) {
            return high;  // key not found.
        }
        return index;
    }


   public static long getValueBits(String value, String valueType) {
       if(valueType.equals("double")) {
           return Double.doubleToLongBits(Double.valueOf(value));
       } else if(valueType.equals("float")) {
           return Float.floatToIntBits(Float.valueOf(value));
       } else {
           return Integer.valueOf(value);
       }
    }

    /**
     * Unit Test. Usage Example.
     */
    public static void main(String args[]) {
        /******************************
         *   BINARY SEARCH INT TEST
         *****************************/
        System.out.print("INT ARRAY: [");
        int[] a = {-2, -1, 4, 5, 5, 5, 6,  8};
        for (int i = 0; i < a.length -1; i++) {
            System.out.print(a[i] + ", ");
        }
        System.out.println(a[a.length - 1] + "]   size = "+a.length);

        IntSeries data = new IntSeries() {
            @Override
            public long size() {
                return a.length;
            }

            @Override
            public int get(long index) {
                return a[(int)index];
            }
        };

        long lower = SeriesUtil.lowerBound(data, 5, 0, (int)data.size());
        long upper = SeriesUtil.upperBound(data, 5, 0, (int)data.size());

        System.out.println("lower(5)= "+lower + ",  upper(5) = "+ upper);


        lower = SeriesUtil.lowerBound(data, -1, 0, (int)data.size());
        upper = SeriesUtil.upperBound(data, -1, 0, (int)data.size());

        System.out.println("lower(-1) = "+lower + ",  upper(-1) = "+ upper);

        lower = SeriesUtil.lowerBound(data, 3, 0, (int)data.size());
        upper = SeriesUtil.upperBound(data, 3, 0, (int)data.size());

        System.out.println("lower(3) = "+lower + ",  upper(3) = "+ upper);

        lower = SeriesUtil.lowerBound(data, -3, 0, (int)data.size());
        upper = SeriesUtil.upperBound(data, 9, 0, (int)data.size());

        System.out.println("lower(-3) = "+lower + ",  upper(9) = "+ upper);

        System.out.println();

        /************************************
         *   BINARY SEARCH FLOAT TEST
         ************************************/
        System.out.print("FLOAT ARRAY: [");
        float[] b = {-2.5f, -1.1f, 4.5f,  5, 5, 5, 6,  8};
        for (int i = 0; i < b.length -1; i++) {
            System.out.print(b[i] + ", ");
        }
        System.out.println(b[b.length - 1] + "]   size = "+b.length);

        FloatSeries data1 = new FloatSeries() {
            @Override
            public long size() {
                return b.length;
            }

            @Override
            public float get(long index) {
                return b[(int)index];
            }
        };

        lower = SeriesUtil.lowerBound(data1, 5.3f, 0, (int)data1.size());
        upper = SeriesUtil.upperBound(data1, 5.3f, 0, (int)data1.size());

        System.out.println("lower(5.3)= "+lower + ",  upper(5.3) = "+ upper);

        lower = SeriesUtil.lowerBound(data1, 5.0f, 0, (int)data1.size());
        upper = SeriesUtil.upperBound(data1, 5.0f, 0, (int)data1.size());

        System.out.println("lower(5.0)= "+lower + ",  upper(5.0) = "+ upper);

        lower = SeriesUtil.lowerBound(data1, -1.2f, 0, (int)data1.size());
        upper = SeriesUtil.upperBound(data1, -1.2f, 0, (int)data1.size());

        System.out.println("lower(-1.2) = "+lower + ",  upper(-1.2) = "+ upper);

        lower = SeriesUtil.lowerBound(data1, -3, 0, (int)data1.size());
        upper = SeriesUtil.upperBound(data1, 9, 0, (int)data1.size());

        System.out.println("lower(-3) = "+lower + ",  upper(9) = "+ upper);
    }
}
