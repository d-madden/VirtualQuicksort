import java.io.File;
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
//                             -Daniel Madden & Jordan DeNaro

public class Quicksort {

    /**
     * @param args
     *      Command line parameters.
     *          args[0]  data file name
     *          args[1]  number of buffers
     *          args[2]  stat file name
     */
    public static void main(String[] args) {
        // This is the main file for the program.
        
        File file = new File(args[0]);
        
        FileGenerator fg = new FileGenerator(args[0], Integer.parseInt(args[1]));
        // new file with a name and size based on the arguments
        
        fg.generateFile(FileType.BINARY);
        // generates new Binary File
        
        //test line try token new
        
        
    }
}
