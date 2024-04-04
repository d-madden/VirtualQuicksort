import java.io.File;

/**
 * @author Daniel Madden
 * @author Jordan DeNaro
 */
public class BufferPool {

    private Buffer[] buffers;
    private int discReads;
    private int diskWrites;
    private int cacheHits;

    public BufferPool(int numBuffers) {
        buffers = new Buffer[numBuffers];
        discReads = 0;
        diskWrites = 0;
        cacheHits = 0;
    }


    // Copy "sz" bytes from "space" to position "pos" in the buffered storage
    public void insert(byte[] space, int sz, int pos) {
        // obtains the bufferID of the buffer we want to pull
        int buffID = getBufferID(pos);
        // checks if the buffer is already in the pool
        if (containsBuffer(buffID) != -1) {
            this.reorganize(buffID);
            return;
        }

        if (this.fullBuffer()) {
            // put the new buffer in the last spot
            buffers[buffers.length - 1] = null;

            // this then puts that buffer to the front and shifts everything
            // else down one
            this.reorganize(buffID);
        }

    }


    // Copy "sz" bytes from position "pos" of the buffered storage to "space"
    public void getbytes(byte[] space, int sz, int pos) {

    }


    /**
     * 
     * @param pos
     *            is the position of the bytes we are trying to pull
     * @return
     *         returns the bufferID we want to obtain
     */
    public int getBufferID(int pos) {
        int totalBytes = buffers.length * 4096;
        return (pos / totalBytes);

    }


    /**
     * 
     * @return
     *         returns true if the buffers array is full and false otherwise
     */
    private boolean fullBuffer() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].equals(null)) {
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
        if (currPos == 0) {
            return;
        }
        int i = 0;
        Buffer temp = buffers[i];
        buffers[i] = buffers[currPos];
        while (i < currPos) {
            Buffer temp2 = buffers[i + 1];
            buffers[i + 1] = temp;
            temp = temp2;
            i++;
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
            if (buffers[i].equals(null)) {
                return -1;
            }
            if (buffers[i].getBlockID() == iD) {
                return i;
            }
        }
        return -1;
    }

}
