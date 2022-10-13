import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;


import javax.swing.JFrame;

public class test{
	


	public static void main(String[] args) throws FileNotFoundException {
		
		File file1 = new File("TeaCup.txt");
        Puzzle p1=new Puzzle(file1);
    	JFrame frame1=new JFrame("Logipix");
		frame1.setSize(1500, 1500);
		frame1.setLayout(new GridLayout(p1.height,p1.width));
		p1.Display();
        long startTime1 = System.nanoTime();
        p1.SolveNaive();
        long endTime1   = System.nanoTime();
        long totalTime1 = endTime1 - startTime1;
        System.out.println("first algo took: " + totalTime1 + " ms");
       // p1.fillframe(frame1);
       // frame1.setVisible(true); 
       // frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
		
		
    
        File file2 = new File("LogiX.txt");
        Puzzle p2=new Puzzle(file2);
    	JFrame frame2=new JFrame("Logipix");
		frame2.setSize(1300, 800);
		frame2.setLayout(new GridLayout(p2.height,p2.width));
		p2.Display();
		p2.exclusionAndCombination();
	    long startTime2 = System.nanoTime();
        p2.SolveNaive();
        long endTime2   = System.nanoTime();
        long totalTime2 = endTime2 - startTime2;
        System.out.println("second algo took: " + totalTime2 + " ms");
     
        p2.fillframe(frame2);
        frame2.setVisible(true); 
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
	} 
}
