
/**
 * This is the class for Buffer
 * 
 * @author Daniel Madden
 * @author Jordan DeNaro
 */
public class Buffer {

    private byte[] byteArr;
    private int blockID;

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

}
