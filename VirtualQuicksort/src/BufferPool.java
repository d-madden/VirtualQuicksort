import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Daniel Madden
 * @author Jordan DeNaro
 * 
 * @version 4/9/2024
 */
public class BufferPool {

    private Buffer[] buffers;
    private int diskReads;
    private int diskWrites;
    private int cacheHits;
    private RandomAccessFile file;

    /**
     * constructor BufferPool
     * 
     * @param numBuffers
     *            Is the number of buffers in the buffer pool
     * @param fg
     *            The random access File given
     */
    public BufferPool(int numBuffers, RandomAccessFile fg) {
        buffers = new Buffer[numBuffers];

        diskReads = 0;
        diskWrites = 0;
        cacheHits = 0;
        file = fg;
    }


    /**
     * inserts some given record into a given position
     * 
     * @param space
     *            this is the 4 byte array we are adding from
     * @param sz
     *            size of the array we are adding to
     * @param pos
     *            the position we are adding to
     * @throws IOException
     *             throws exception if file cant be found
     */
    public void insert(byte[] space, int sz, int pos) throws IOException {
        // obtains the bufferID of the buffer we want to pull
        int buffID = getBufferID(pos);
        if (containsBuffer(buffID) != -1) {
            // since its already present this will simply reorganize the pool
            // with the given buffer at the front
            Buffer store = buffers[containsBuffer(buffID)];
            this.reorganize(buffID);
            buffers[0] = store;
            System.arraycopy(space, 0, buffers[0].getByteArr(), pos % 4096, sz);
            buffers[0].flipBit(1);

            this.cacheHits++;
        }

        else {

            byte[] reading = new byte[4096];

            file.seek(buffID * 4096);
            file.read(reading);

            if (fullBuffer()) {
                if (buffers[buffers.length - 1].getDirtyBit() == 1) {
                    Buffer toWrite = buffers[buffers.length - 1];
                    file.seek(toWrite.getBlockID() * 4096);
                    file.write(toWrite.getByteArr(), 0, toWrite
                        .getByteArr().length);
                    diskWrites++;
                    toWrite.flipBit(0);
                }
            }

            this.reorganize(buffID);
            buffers[0] = new Buffer(buffID, reading);
            System.arraycopy(space, 0, buffers[0].getByteArr(), pos % 4096, sz);
            this.diskReads++;
            buffers[0].flipBit(1);
        }

    }


    /**
     * Obtains the bytes from pos in the file and adds it to the byte array
     * space
     * 
     * @param space
     *            the byte array we are adding to record to
     * @param sz
     *            the size of the array we are adding to
     * @param pos
     *            the position from where we grab the bytes
     * @throws IOException
     *             the exception in case you cant pull the file
     */
    public void getbytes(byte[] space, int sz, int pos) throws IOException {

        // obtains the ID of the position we are getting from
        int buffID = getBufferID(pos);
        if (containsBuffer(buffID) != -1) {
            // since its already present this will simply reorganize the pool
            // with the given buffer at the front
            Buffer store = buffers[containsBuffer(buffID)];
            this.reorganize(buffID);
            buffers[0] = store;
            System.arraycopy(buffers[0].getByteArr(), pos % 4096, space, 0, sz);
            this.cacheHits++;

        }
        else {

            byte[] reading = new byte[4096];

            file.seek(buffID * 4096);
            file.read(reading);

            if (fullBuffer()) {
                if (buffers[buffers.length - 1].getDirtyBit() == 1) {
                    Buffer toWrite = buffers[buffers.length - 1];
                    file.seek(toWrite.getBlockID() * 4096);
                    file.write(toWrite.getByteArr(), 0, toWrite
                        .getByteArr().length);
                    diskWrites++;
                    toWrite.flipBit(0);
                }
            }

            this.reorganize(buffID);
            buffers[0] = new Buffer(buffID, reading);

            System.arraycopy(buffers[0].getByteArr(), pos % 4096, space, 0, sz);
            this.diskReads++;
        }

    }


    /**
     * 
     * @param pos
     *            is the position of the bytes we are trying to pull
     * @return
     *         returns the bufferID we want to obtain
     */
    public int getBufferID(int pos) {
        return (pos / 4096);

    }


    /**
     * 
     * @return
     *         returns true if the buffers array is full and false otherwise
     */
    private boolean fullBuffer() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] == null) {
                return false;
            }
        }
        return true;
    }


    /**
     * re-organizes the buffer pool so that the most recently accessed buffer is
     * up top and everything else is pushed down
     * 
     * @param buffID
     *            takes in the ID of the most recently accessed Buffer
     */
    private void reorganize(int buffID) {
        int currPos = containsBuffer(buffID);
        if (buffers[0] == null) {
            return;
        }
        if (currPos == -1) {
            currPos = getEnd();
        }

        for (int i = currPos - 1; i >= 0; i--) {
            buffers[i + 1] = buffers[i];
        }
    }


    /**
     * 
     * @param iD
     *            takes in an iD we are looking for
     * @return
     *         returns position in the bufferArray -1 if not
     */
    public int containsBuffer(int iD) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] == null) {
                return -1;
            }
            if (buffers[i].getBlockID() == iD) {
                return i;
            }
        }
        return -1;
    }


    private int getEnd() {
        if (fullBuffer()) {
            return buffers.length - 1;
        }
        int i = 0;
        while (buffers[i] != null) {
            i++;
        }
        return i;
    }


    /**
     * 
     * @return
     *         returns the buffer pool
     */
    public Buffer[] getBuffers() {
        return buffers;
    }


    /**
     * 
     * @return
     *         returns the disk Reads
     */
    public int getDiskReads() {
        return diskReads;
    }


    /**
     * 
     * @return
     *         returns the disk writes
     */
    public int getDiskWrites() {
        return diskWrites;
    }


    /**
     * increments the disk writes
     */
    public void addDiskWrite() {
        diskWrites++;
    }


    /**
     * 
     * @return
     *         returns the cache hits
     */
    public int getCacheHits() {
        return cacheHits;
    }

}
