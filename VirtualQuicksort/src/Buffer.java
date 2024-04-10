
/**
 * This is the class for Buffer
 * 
 * @author Daniel Madden
 * @author Jordan DeNaro
 * 
 * @version 4/9/2024
 */
public class Buffer {

    private byte[] byteArr;
    private int blockID;
    private int dirtyBit = 0;

    /**
     * constructor for the Buffer class
     * 
     * @param iD
     *            is the iD of the buffer
     * @param byteArr
     *            the byteArray the buffer holds
     * 
     */
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
     * flips the dirty bit
     * 
     * @param x
     *            is what we are changing the dirty bit to
     */
    public void flipBit(int x) {
        dirtyBit = x;
    }

}
