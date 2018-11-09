package com.biorecorder.data.sequence;


public class SequenceUtils {
    /******************************************************************
     *                         BINARY SEARCH
     ******************************************************************/
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
    public static long binarySearch(DoubleSequence data, double value, long fromIndex, int length) {
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
    public static long binarySearch(FloatSequence data, float value, long fromIndex, int length) {
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
    public static long binarySearch(IntSequence data, int value, long fromIndex, int length) {
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
    public static long binarySearch(LongSequence data, long value, long fromIndex, int length) {
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
    public static long binarySearch(ShortSequence data, short value, long fromIndex, int length) {
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
     * Lower bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>FIRST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then (<b>fromIndex -1</b>) will be returned</li>
      *
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     */
    public static long lowerBound(DoubleSequence data, double value, long fromIndex, int length) {
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
            return high;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>FIRST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then (<b>fromIndex -1</b>) will be returned</li>
      *
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     */
    public static long lowerBound(FloatSequence data, float value, long fromIndex, int length) {
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
            return high;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>FIRST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then (<b>fromIndex -1</b>) will be returned</li>
      *
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     */
    public static long lowerBound(IntSequence data, int value, long fromIndex, int length) {
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
            return high;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>FIRST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then (<b>fromIndex -1</b>) will be returned</li>
      *
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     */
    public static long lowerBound(LongSequence data, long value, long fromIndex, int length) {
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
            return high;
        }
        return index;
    }

    /**
     * Lower bound search algorithm.<br>
     * Lower bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>FIRST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then (<b>fromIndex -1</b>) will be returned</li>
      *
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * Complexity O(log n).
     */
    public static long lowerBound(ShortSequence data, short value, long fromIndex, int length) {
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
            return high;
        }
        return index;
    }

    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>LAST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then (<b>fromIndex + length</b>) will be returned</li>
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * <p>
     * Complexity O(log n).
     */
    public static long upperBound(DoubleSequence data, double value, long fromIndex, int length) {
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
            return low;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>LAST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then (<b>fromIndex + length</b>) will be returned</li>
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * <p>
     * Complexity O(log n).
     */
    public static long upperBound(FloatSequence data, float value, long fromIndex, int length) {
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
            return low;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>LAST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then (<b>fromIndex + length</b>) will be returned</li>
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * <p>
     * Complexity O(log n).
     */
    public static long upperBound(IntSequence data, int value, long fromIndex, int length) {
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
            return low;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>LAST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then (<b>fromIndex + length</b>) will be returned</li>
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * <p>
     * Complexity O(log n).
     */
    public static long upperBound(LongSequence data, long value, long fromIndex, int length) {
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
            return low;  // key not found.
        }
        return index;
    }


    /**
     * Upper bound search algorithm.<br>
     * Upper bound is kind of binary search algorithm but:
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>LAST</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then (<b>fromIndex + length</b>) will be returned</li>
     * </ul>
     * Behaviour for unsorted arrays is unspecified.
     * <p>
     * Complexity O(log n).
     */
    public static long upperBound(ShortSequence data, short value, long fromIndex, int length) {
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
            return low;  // key not found.
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

        IntSequence data = new IntSequence() {
            @Override
            public long size() {
                return a.length;
            }

            @Override
            public int get(long index) {
                return a[(int)index];
            }
        };

        long lower = SequenceUtils.lowerBound(data, 5, 0, (int)data.size());
        long upper = SequenceUtils.upperBound(data, 5, 0, (int)data.size());
        long bs = SequenceUtils.binarySearch(data, 5, 0, (int)data.size());

        System.out.println("lower(5)= "+lower + ",  upper(5) = "+ upper + ",  binary search(5) = "+bs);


        lower = SequenceUtils.lowerBound(data, -1, 0, (int)data.size());
        upper = SequenceUtils.upperBound(data, -1, 0, (int)data.size());
        bs = SequenceUtils.binarySearch(data, -1, 0, (int)data.size());

        System.out.println("lower(-1) = "+lower + ",  upper(-1) = "+ upper + ",  binary search(-1) = "+bs);

        lower = SequenceUtils.lowerBound(data, 3, 0, (int)data.size());
        upper = SequenceUtils.upperBound(data, 3, 0, (int)data.size());
        bs = SequenceUtils.binarySearch(data, 3, 0, (int)data.size());

        System.out.println("lower(3) = "+lower + ",  upper(3) = "+ upper + ",  binary search(3) = "+bs);

        lower = SequenceUtils.lowerBound(data, -3, 0, (int)data.size());
        upper = SequenceUtils.upperBound(data, -3, 0, (int)data.size());
        bs = SequenceUtils.binarySearch(data, -3, 0, (int)data.size());

        System.out.println("lower(-3) = "+lower + ",  upper(-3) = "+ upper + ",  binary search(-3) = "+bs);

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

        FloatSequence data1 = new FloatSequence() {
            @Override
            public long size() {
                return b.length;
            }

            @Override
            public float get(long index) {
                return b[(int)index];
            }
        };

        lower = SequenceUtils.lowerBound(data1, 5.3f, 0, (int)data1.size());
        upper = SequenceUtils.upperBound(data1, 5.3f, 0, (int)data1.size());
        bs = SequenceUtils.binarySearch(data1, 5.3f, 0, (int)data1.size());

        System.out.println("lower(5.3)= "+lower + ",  upper(5.3) = "+ upper + ",  binary search(5.3) = "+bs);

        lower = SequenceUtils.lowerBound(data1, 5.0f, 0, (int)data1.size());
        upper = SequenceUtils.upperBound(data1, 5.0f, 0, (int)data1.size());
        bs = SequenceUtils.binarySearch(data1, 5.0f, 0, (int)data1.size());

        System.out.println("lower(5.0)= "+lower + ",  upper(5.0) = "+ upper + ",  binary search(5.0) = "+bs);

        lower = SequenceUtils.lowerBound(data1, -1.2f, 0, (int)data1.size());
        upper = SequenceUtils.upperBound(data1, -1.2f, 0, (int)data1.size());
        bs = SequenceUtils.binarySearch(data1, -1.2f, 0, (int)data1.size());

        System.out.println("lower(-1.2) = "+lower + ",  upper(-1.2) = "+ upper + ",  binary search(-1.2) = "+bs);

        lower = SequenceUtils.lowerBound(data1, -3, 0, (int)data1.size());
        upper = SequenceUtils.upperBound(data1, -3, 0, (int)data1.size());
        bs = SequenceUtils.binarySearch(data1, -3, 0, (int)data1.size());

        System.out.println("lower(-3) = "+lower + ",  upper(-3) = "+ upper + ",  binary search(-3) = "+bs);

        lower = SequenceUtils.lowerBound(data1, 9, 0, (int)data1.size());
        upper = SequenceUtils.upperBound(data1, 9, 0, (int)data1.size());
        bs = SequenceUtils.binarySearch(data1, 9, 0, (int)data1.size());

        System.out.println("lower(9) = "+lower + ",  upper(9) = "+ upper + ",  binary search(9) = "+bs);

    }
}
