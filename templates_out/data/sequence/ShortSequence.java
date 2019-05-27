package java.com.biorecorder.data.sequence;

/**
 * Interface that represents a set of indexed data of type short (like array)
 * that can be accessed but can not be modified
 */
public interface ShortSequence {
    public long size();
    public short get(long index);
}
