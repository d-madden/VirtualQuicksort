
/**
 * This is the class for Buffer
 * 
 * @author Daniel Madden
 * @author Jordan DeNaro
 */
public class Buffer {

    private byte[] byteArr;
    private int blockID;
    private int dirtyBit = 0;

    public Buffer(int iD, byte[] byteArr) {
        blockID = iD;
        this.byteArr = byteArr;
    }


    /**
     * 
     * @return
     *         returns the block ID of the buffer
     */
    public int getBlockID() {
        return blockID;
    }


    /**
     * 
     * @return
     *         returns the dirty bit
     */
    public int getDirtyBit() {
        return dirtyBit;
    }


    /**
     * 
     * @return
     *         returns the byte array
     */
    public byte[] getByteArr() {
        return byteArr;
    }


    /**
     * flips the dirty bit indicating it is sorted
     */
    public void flipBit() {
        dirtyBit = 1;
    }

}
