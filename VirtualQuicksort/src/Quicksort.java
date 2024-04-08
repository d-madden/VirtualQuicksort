import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Project 3: Virtual Quicksort
 */

/**
 * The class containing the main method.
 * 
 *
 * @author Daniel Madden
 * @author Jordan DeNaro
 * @version 03-28-2024
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.
// -Daniel Madden & Jordan DeNaro

public class Quicksort {

    /**
     * @param args
     *            Command line parameters.
     *            args[0] data file name
     *            args[1] number of buffers
     *            args[2] stat file name
     * @throws IOException
     * @throws NumberFormatException
     */
    public static void main(String[] args)
        throws NumberFormatException,
        IOException {
        // This is the main file for the program.
        FileGenerator fg = new FileGenerator(args[0], Integer.parseInt(
            args[1]));
        fg.generateFile(FileType.BINARY);

        RandomAccessFile file = new RandomAccessFile(args[0], "rw");

        
        // new file with a name and size based on the arguments
        File output = new File(args[2], "w");
        FileWriter myWriter = new FileWriter(args[2]);

        

        BufferPool pool = new BufferPool(Integer.parseInt(args[1]), file);

        long begTime = System.currentTimeMillis();
        Vquicksort(pool, 0, 4096 * Integer.parseInt(args[1]));
        long endTime = System.currentTimeMillis();
        long duration = endTime - begTime;

        myWriter.write(args[0] + "\n");
        myWriter.write("cache hits:" + pool.getCacheHits() + "\n");
        myWriter.write("disk reads:" + pool.getDiskReads() + "\n");
        myWriter.write("disk writes:" + pool.getDiskWrites() + "\n");
        myWriter.write("Time to execute:" + duration + "\n");

        // generates new Binary File

        // test line try token new

        // Jordan testing commit

        // Dan Testing commit

    }


    public static void Vquicksort(BufferPool pool, int i, int j)
        throws IOException {
        // obtains the pivot based on left and right trackers
        int pivotIndex = findpivot(i, j);

        // swaps the pivot to the right
        swap(pool, pivotIndex, j);

        int k = partition(pool, i, j - 1, pivotIndex);

        swap(pool, k, j);

        // recursively calls quicksort
        if ((k - i) > 1) {
            Vquicksort(pool, i, k - 1);
        }
        if ((j - k) > 1) {
            Vquicksort(pool, k + 1, j);
        }

    }


    private static int partition(
        BufferPool pool,
        int left,
        int right,
        int pivotIndex)
        throws IOException {
        while (left <= right) { // Move bounds inward until they meet

            // creates and fills 2-byte arrays with the key
            byte[] leftArr = new byte[2];
            byte[] rightArr = new byte[2];
            byte[] pivotArr = new byte[2];
            pool.getbytes(leftArr, 2, left);
            pool.getbytes(rightArr, 2, right);
            pool.getbytes(pivotArr, 2, pivotIndex);

            short leftShort = getShort(leftArr);
            short rightShort = getShort(rightArr);
            short pivotShort = getShort(pivotArr);

            while (leftShort < pivotShort) {
                left++;
            }
            while ((right >= left) && (rightShort >= pivotShort)) {
                right--;
            }
            if (right > left) {
                swap(pool, left, right);
            } // Swap out-of-place values
        }
        return left;
    }


    /**
     * 
     * @param i
     *            the beginning index
     * @param j
     *            the ending index
     * @return
     *         returns the pivot
     */
    static int findpivot(int i, int j) {
        return (i + j) / 2;
    }


    /**
     * 
     * @param pool
     *            takes in the buffer pool
     * @param src
     *            the source of what we are swapping
     * @param dest
     *            the destination of what we are swapping
     * @throws IOException
     */
    private static void swap(BufferPool pool, int src, int dest)
        throws IOException {
        // creates two arrays to store the bytes we are swapping
        byte[] srcArr = new byte[4];
        byte[] destArr = new byte[4];

        // Fills the arrays with what we want to swap
        pool.getbytes(srcArr, 4, src);
        pool.getbytes(destArr, 4, dest);

        // swaps the bytes
        pool.insert(srcArr, 4, dest);
        pool.insert(destArr, 4, src);

    }


    private static short getShort(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(bytes[0]);
        bb.put(bytes[1]);
        return (bb.getShort(0));
    }

}
