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
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // This is the main file for the program.

// FileGenerator fg = new FileGenerator(args[0], 100);
// fg.generateFile(FileType.ASCII);

        RandomAccessFile file = new RandomAccessFile(args[0], "rw");

        // new file with a name and size based on the arguments
        File output = new File(args[2], "w");
        FileWriter myWriter = new FileWriter(args[2]);

        BufferPool pool = new BufferPool(Integer.parseInt(args[1]), file);

        long begTime = System.currentTimeMillis();
        // calls the recursive quicksort, won't sort if less than 9 records
        vQuicksort(pool, 0, ((int)file.length() / 4) - 1);
        long endTime = System.currentTimeMillis();
        // writes all the remaining buffers to file
        writePool(pool, file);
        long duration = endTime - begTime;

        myWriter.write(args[0] + "\n");
        myWriter.write("cache hits:" + pool.getCacheHits() + "\n");
        myWriter.write("disk reads:" + pool.getDiskReads() + "\n");
        myWriter.write("disk writes:" + pool.getDiskWrites() + "\n");
        myWriter.write("Time to execute:" + duration + "ms\n");
        myWriter.write(Boolean.toString(CheckFile.check(args[0])));
        myWriter.close();

        // generates new Binary File

        // test line try token new

        // Jordan testing commit

        // Dan Testing commit

    }


    /**
     * the recursive quicksort function
     * 
     * @param pool
     *            takes in a buffer pool
     * @param i
     *            the first index
     * @param j
     *            the last index
     * @throws IOException
     *             throws exception in case you cant pull file
     */
    public static void vQuicksort(BufferPool pool, int i, int j)
        throws IOException {
        // obtains the pivot based on left and right trackers
        int pivotIndex = findpivot(i, j);

        // swaps the pivot to the right
        byte[] rightArr = new byte[4];
        byte[] pivotArr = new byte[4];
        pool.getbytes(rightArr, 4, j * 4);
        pool.getbytes(pivotArr, 4, pivotIndex * 4);
        swap(pool, pivotIndex, j, pivotArr, rightArr);

        int k = partition(pool, i, j - 1, j);

        if (k == i) {
            if (checkDuplicates(pool, i, j)) {
                return;
            }
        }

        byte[] kArr = new byte[4];
        byte[] jArr = new byte[4];
        pool.getbytes(kArr, 4, k * 4);
        pool.getbytes(jArr, 4, j * 4);
        swap(pool, k, j, kArr, jArr);

        // recursively calls quicksort
        if ((k - i) > 13) {
            vQuicksort(pool, i, k - 1);
        }
        else {
            vInsertionsort(pool, i, k - 1);
        }
        if ((j - k) > 13) {
            vQuicksort(pool, k + 1, j);
        }
        else {
            vInsertionsort(pool, k + 1, j);
        }

    }


    /**
     * checks if a list of terms is all duplicates
     * 
     * @param pool
     *            the pool we are checking data from
     * @param i
     *            the beginning of data we are checking
     * @param j
     *            the end of data we are checking
     * @return
     *         returns true if all duplicates, false otherwise
     * @throws IOException
     *             throws exception if file cant be read
     */
    private static boolean checkDuplicates(BufferPool pool, int i, int j)
        throws IOException {
        byte[] iArr = new byte[4];
        byte[] jArr = new byte[4];
        pool.getbytes(iArr, 4, i * 4);

        for (int q = i + 1; q < j; q++) {
            pool.getbytes(jArr, 4, q * 4);
            short iShort = getShort(iArr);
            short jShort = getShort(jArr);
            if (jShort != iShort) {
                return false;
            }
        }
        return true;

    }


    /**
     * insertion sort to be used for when there are less than 9 records
     * 
     * @param pool
     *            the pool we are getting data from
     * @param low
     *            the low position we are sorting from
     * @param high
     *            the high we are sorting from
     * @throws IOException
     *             in case we cant read file
     */
    public static void vInsertionsort(BufferPool pool, int low, int high)
        throws IOException {

        for (int i = low + 1; i <= high; i++) {
            byte[] key = new byte[4];
            pool.getbytes(key, 4, i * 4);
            short keyShort = getShort(key);

            int j = i - 1;
            byte[] temp = new byte[4];

            // Shift elements of the sorted segment forward if they're larger
            // than keyShort
            while (j >= low) {
                pool.getbytes(temp, 4, j * 4);
                short tempShort = getShort(temp);

                if (tempShort > keyShort) {
                    pool.insert(temp, 4, (j + 1) * 4);
                    pool.insert(key, 4, j * 4);
                }
                else {
                    break; // Found the correct position for keyShort
                }
                j--;
            }
            // Insert the key at its correct position
            pool.insert(key, 4, (j + 1) * 4);
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
            byte[] leftArr = new byte[4];
            byte[] rightArr = new byte[4];
            byte[] pivotArr = new byte[4];
            pool.getbytes(leftArr, 4, left * 4);
            pool.getbytes(rightArr, 4, right * 4);
            pool.getbytes(pivotArr, 4, pivotIndex * 4);

            short leftShort = getShort(leftArr);
            short rightShort = getShort(rightArr);
            short pivotShort = getShort(pivotArr);

            while (leftShort < pivotShort) {
                left++;
                pool.getbytes(leftArr, 4, left * 4);
                leftShort = getShort(leftArr);
            }
            while ((right >= left) && (rightShort >= pivotShort)) {
                right--;
                if (right >= 0) {
                    pool.getbytes(rightArr, 4, right * 4);
                    rightShort = getShort(rightArr);
                }

            }
            if (right > left) {

                swap(pool, left, right, leftArr, rightArr);

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
    private static void swap(
        BufferPool pool,
        int src,
        int dest,
        byte[] srcArr,
        byte[] destArr)
        throws IOException {

        // wont swap if duplicates
        if (getShort(srcArr) == getShort(destArr)) {
            return;
        }

        // swaps the bytes
        pool.insert(srcArr, 4, dest * 4);
        pool.insert(destArr, 4, src * 4);

    }


    private static short getShort(byte[] bytes) {

        return ByteBuffer.wrap(bytes).getShort();
    }


    /**
     * writes all the remaining changed bytes to the file
     * 
     * @param pool
     *            takes in the buffer pool
     * @param file
     *            takes in the file to over right
     * @throws IOException
     *             throws exception in case not found
     */
    public static void writePool(BufferPool pool, RandomAccessFile file)
        throws IOException {
        for (int i = 0; i < pool.getBuffers().length; i++) {
            if (pool.getBuffers()[i] == null) {
                return;
            }
            if (pool.getBuffers()[i].getDirtyBit() == 1) {
                file.seek(pool.getBuffers()[i].getBlockID() * 4096);
                file.write(pool.getBuffers()[i].getByteArr(), 0, pool
                    .getBuffers()[i].getByteArr().length);
                pool.addDiskWrite();
            }

        }
    }

}
