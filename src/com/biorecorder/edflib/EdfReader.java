package com.biorecorder.edflib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Permits to read data samples from EDF or BDF file. Also it
 * reads information from the file header and saves it
 * in special {@link EdfHeader} object, that we
 * can get by method {@link #getHeader()}
 * <p>
 * This class is NOT thread safe!
 * <p>
 * EDF/BDF files contains "row" digital (int) data but they can be converted to corresponding
 * real physical floating point data on the base of header information (physical maximum and minimum
 * and digital maximum and minimum specified for every channel (signal)).
 * So we can "read" both digital or physical values.
  */
public class EdfReader {
    private EdfHeader header;
    private FileInputStream fileInputStream;
    private long[] samplesPositionList;
    private final int recordSize;

    /**
     * Creates EdfFileReader to read data from the file represented by the specified
     * File object.
     *
     * @param file Edf or Bdf file to be opened for reading
     * @throws FileNotFoundException if the file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for reading.
     * @throws HeaderException if the the file is not valid EDF/BDF file
     *                               due to some errors in its header record
     * @throws IOException if an I/O error occurs
     */
    public EdfReader(File file) throws FileNotFoundException, HeaderException, IOException {
        fileInputStream = new FileInputStream(file);
        header = new HeaderRecord(file).getHeaderInfo();
        samplesPositionList = new long[header.signalsCount()];
        recordSize = header.getDataRecordSize();
    }

    /**
     * Return the information from the file header stored in the HeaderConfig object
     *
     * @return the object containing EDF/BDF header information
     */
    public EdfHeader getHeader() {
        return header;
    }
    
    /**
     * Set the sample position indicator of the given channel (signal)
     * to the given new position. The position is measured in samples.
     * <p>
     * Note that every signal has it's own independent sample position indicator and
     * setSamplePosition() affects only one of them.
     * Methods {@link #readDigitalSamples(int, int[], int, int)} and
     * {@link #readPhysicalSamples(int, double[], int, int)} (int, double[], int, int)} will startRecording reading
     * samples belonging to a channel from the specified for that channel position.
     *
     * @param signalNumber channel (signal) number whose sample position we change. Numbering starts from 0!
     * @param newPosition  the new sample position, a non-negative integer counting
     *                     the number of samples belonging to the specified
     *                     channel from the beginning of the file
     */
    public void setSamplePosition(int signalNumber, long newPosition) {
        samplesPositionList[signalNumber] = newPosition;
    }

    /**
     * Return the current sample position  of the given channel (signal).
     * The position is measured in samples.
     *
     * @param signalNumber channel (signal) number whose position we want to get. Numbering starts from 0!
     * @return current sample position, a non-negative integer counting
     * the number of samples belonging to the given
     * channel from the beginning of the file
     */
    public long getSamplePosition(int signalNumber) {
        return samplesPositionList[signalNumber];
    }

    /**
     * Read n samples belonging to the  signal
     * starting from the current sample position indicator.
     * The values are the "raw" digital (integer) values.
     * <p>
     * Read samples are saved in the specified array starting at the specified offset.
     * Return the amount of read samples (this can be less than given numberOfSamples or zero!)
     * The sample position indicator of that signal will be increased
     * with the amount of samples read.
     * @param signal    channel (signal) number whose samples must be read. Numbering starts from 0!
     * @param digBuffer          the buffer into which the data is read
     * @param offset          the start offset in array b at which the data is written
     * @param n number of samples to read
     * @return the amount of read samples (this can be less than n or zero!)
     * @throws IOException  if an I/O error occurs
     */
    public int readDigitalSamples(int signal, int[] digBuffer, int offset, int n) throws IOException {
       return readSamples(signal, digBuffer, null, offset, n);
    }


    /**
     * Read n samples belonging to the  signal
     * starting from the current sample position indicator.
     * Converts the read samples
     * to their physical values (e.g. microVolts, beats per minute, etc).
     * <p>
     * Read samples are saved in the specified array starting at the specified offset.
     * Return the amount of read samples (this can be less than given numberOfSamples or zero!)
     * The sample position indicator of that signal will be increased
     * with the amount of samples read.
     * @param signal    channel (signal) number whose samples must be read. Numbering starts from 0!
     * @param physBuffer          the buffer into which the data is read
     * @param offset          the start offset in array b at which the data is written
     * @param n number of samples to read
     * @return the amount of read samples (this can be less than n or zero!)
     * @throws IOException  if an I/O error occurs
     */
    public int readPhysicalSamples(int signal, double[] physBuffer, int offset, int n) throws IOException {
        return readSamples(signal, null, physBuffer, offset, n);
    }

    private int readSamples(int signal, int[] digBuffer, double[] physBuffer, int offset, int n) throws IOException {
        int bytesPerSample = header.getDataFormat().getNumberOfBytesPerSample();
        int samplesPerRecord = header.getNumberOfSamplesInEachDataRecord(signal);
        int nBytes = n * bytesPerSample;
        long recordNumber = samplesPositionList[signal] / samplesPerRecord;
        int signalStartPositionInRecord = 0;
        for (int i = 0; i < signal; i++) {
            signalStartPositionInRecord += header.getNumberOfSamplesInEachDataRecord(i);
        }
        int sampleStartOffset = (int)(samplesPositionList[signal] % samplesPerRecord);
        long fileReadPosition = header.getNumberOfBytesInHeaderRecord() + (recordNumber * recordSize + signalStartPositionInRecord + sampleStartOffset) * bytesPerSample;

        // set file startRecording reading position and read
        fileInputStream.getChannel().position(fileReadPosition);
        byte[] byteData = new byte[samplesPerRecord * bytesPerSample];
        int totalReadBytes = 0;
        int bytesToRead = Math.min((samplesPerRecord - sampleStartOffset) * bytesPerSample, n * bytesPerSample - totalReadBytes) ;

        while (totalReadBytes < nBytes) {
            int readBytes = fileInputStream.read(byteData, 0, bytesToRead);

            if(digBuffer != null) {
                int intCount = 0;
                for (int byteCount = 0; byteCount < readBytes; byteCount += bytesPerSample) {
                    digBuffer[offset + intCount] = EndianBitConverter.littleEndianBytesToInt(byteData, byteCount, bytesPerSample);
                    intCount++;
                }
            }
            if(physBuffer != null) {
                int intCount = 0;
                for (int byteCount = 0; byteCount < readBytes; byteCount += bytesPerSample) {
                    physBuffer[offset + intCount] = header.digitalValueToPhysical(signal, EndianBitConverter.littleEndianBytesToInt(byteData, byteCount, bytesPerSample));
                    intCount++;
                }
            }

            totalReadBytes += readBytes;
            if(readBytes < bytesToRead) { // end of file
                break;
            }
            fileInputStream.skip((recordSize - samplesPerRecord) * bytesPerSample);
            bytesToRead = Math.min(samplesPerRecord * bytesPerSample, n * bytesPerSample - totalReadBytes) ;
        }
        int readSamples = totalReadBytes/bytesPerSample;
        samplesPositionList[signal] += readSamples;
        return readSamples;
    }

    /**
     * Close this reader and releases any system resources associated with
     * it. This method MUST be called after finishing reading data.
     * Failing to do so will cause unnessesary memory usage
     *
     * @throws IOException if an I/O  occurs
     */
    public void close() throws IOException {
        fileInputStream.close();
    }
}

