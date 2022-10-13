import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.text.html.HTMLDocument.Iterator;

class Clue {
	public boolean isMarked;
	public int value;
	public int i, j;
	public int colored; // for exemple colored=1 if its in the backtracking check and colored=2 if its
						// for sure a part of the solution
    public Pair partOf;
    
    
	Clue(int value, int i, int j) {
		isMarked = value != 0;
		this.value = value;
		this.i = i;
		this.j = j;
		colored = 0;
		partOf=new Pair(-1,-1);
	}

	void Mark() {
		isMarked = true;
	}

	void Color() {
		colored = 1;
	}

	void Discolor() {
		colored = 0;
	}

	void Dismark() {
		isMarked = false;
	}


    boolean Equals(Clue clue) {
    	return this.i == clue.i && this.j == clue.j ;
    }
    
}
class Puzzle {
	public int height, width;
	public Clue[][] puzzle;
	public String[][] buttons;

	Puzzle(Clue[][] puzzle) {
		this.puzzle = puzzle;
		this.height = this.puzzle.length;
		this.width = this.puzzle[0].length;
		this.buttons = new String[this.height][this.width];
	}

	Puzzle(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		this.width = Integer.parseInt(scan.nextLine());
		this.height = Integer.parseInt(scan.nextLine());
		Clue[][] puzzle = new Clue[this.height][this.width];
		int i = 0;
		while (scan.hasNextLine()) {
			String[] L = scan.nextLine().split(" ");
			for (int j = 0; j < this.width; j++) {
				puzzle[i][j] = new Clue(Integer.parseInt(L[j]), i, j);
			}
			i++;
		}
		scan.close();
		this.puzzle = puzzle;
	}

	void Display() {
		Puzzle p = this;
		String[][] L = new String[p.height][p.width];

		for (int i = 0; i < p.height; i++) {          // create buttons to display later on our JFrame interface
			for (int j = 0; j < p.width; j++) {
				if (p.puzzle[i][j].isMarked) {
					String a = Integer.toString(p.puzzle[i][j].value);
					L[i][j] = a;
				} else {
					String a = "";
					L[i][j] = a;
				}

			}

		}
		this.buttons = L;
	}

	void addX(int i, int j) {    // add "X" in the cell 
		this.buttons[i][j] += "X";
	}

	void fillframe(JFrame frame) { 
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				JButton a = new JButton(this.buttons[i][j]);
				frame.add(a);
			}
		}
	}

	ArrayList<String> BrokenLines(Clue clue) {    //give all broken lines of a certain clue 
		ArrayList<String> pathList = new ArrayList<String>();
		getBrokenlines(clue,clue.value, clue.i, clue.j, "", pathList);
		return pathList;
	}

	void getBrokenlines(Clue clue,int n, int i, int j, String path, ArrayList<String> pathList) {
		path += String.format("%d,%d ", i, j);
		if (this.puzzle[i][j].value == n && path.split(" ").length == n) {
			pathList.add(path);
		} else if (path.split(" ").length == n) {
			return;
		} else {                  // many of this conditions will be needed later in backtracking or in exclusion and combination
			if (j < this.width - 1 ) {
				Clue c = this.puzzle[i][j + 1];                                         // it should be unmarked unless its the last clue
				if ( (c.colored == 0 || (c.partOf.a==clue.i && c.partOf.b==clue.j ))	&& (!c.isMarked || c.value == n)) {
				     boolean b = Arrays.asList(path.split(" ")).contains(String.format("%d,%d", i, j + 1));   //checks if we have already passed through this clue in this path
				     if (!b)  getBrokenlines(clue,n, i, j + 1, path, pathList);		
			    }
			}
			if (i < this.height - 1 ) {
				Clue c = this.puzzle[i+1][j];
				if ( (c.colored == 0 || (c.partOf.a==clue.i && c.partOf.b==clue.j ))	&& (!c.isMarked || c.value == n)) {
				     boolean b = Arrays.asList(path.split(" ")).contains(String.format("%d,%d", i+1, j ));
				     if (!b)  getBrokenlines(clue,n, i+1, j , path, pathList);		
			    }
			}
			if (j >=1 ) {
				Clue c = this.puzzle[i][j-1];
				if ( (c.colored == 0 || (c.partOf.a==clue.i && c.partOf.b==clue.j ))	&& (!c.isMarked || c.value == n)) {
				     boolean b = Arrays.asList(path.split(" ")).contains(String.format("%d,%d", i, j-1 ));
				     if (!b)  getBrokenlines(clue,n, i, j-1 , path, pathList);		
			    }
			}
			if (i >=1 ) {
				Clue c = this.puzzle[i-1][j];
				if ( (c.colored == 0 || (c.partOf.a==clue.i && c.partOf.b==clue.j ))	&& (!c.isMarked || c.value == n)) {
				     boolean b = Arrays.asList(path.split(" ")).contains(String.format("%d,%d", i-1, j ));
				     if (!b)  getBrokenlines(clue,n, i-1, j , path, pathList);		
			    }
			}
		}
	}

	public ArrayList<Clue> stringToPath(String s) { 
		ArrayList<Clue> r = new ArrayList<Clue>();
		String[] L = s.split(" "); 
		for (String cor : L) {
			r.add(this.puzzle[Integer.parseInt(cor.split(",")[0])][Integer.parseInt(cor.split(",")[1])]);
		}
		return r;
	}

	boolean SolveNaive() { //naive solution with normal backtracking
		for (int i = 0; i < this.height; i++) { 
			for (int j = 0; j < this.width; j++) { 
				if (this.puzzle[i][j].value != 0 && this.puzzle[i][j].colored == 0) { 
					if (this.BrokenLines(this.puzzle[i][j]).isEmpty()) // if its empty there is no solution (dead end)
						return false;
					for (String path : this.BrokenLines(this.puzzle[i][j])) {
						ArrayList<Clue> L = this.stringToPath(path);
						for (Clue c : L) {  
							this.puzzle[c.i][c.j].Color();  
							this.buttons[c.i][c.j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X";
							this.buttons[L.get(0).i][L.get(0).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X"
									+ " Initial";
							this.buttons[L.get(L.size() - 1).i][L
									.get(L.size() - 1).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X" + " Final";
						}
						if (this.SolveNaive()) {
							return true;
						}
						// The backtracking part
						for (Clue c : L) {
							this.puzzle[c.i][c.j].Discolor();
							this.buttons[c.i][c.j].substring(0, this.buttons[c.i][c.j].indexOf("X")); //remove all the symbols from the interface
						}

					}
					return false;
				}

			}
		}
		return true;
	}
	// merge sorting to minimize the complexity of our sorting
	public ArrayList<Clue> cluesSorted() { //take the marked clues and sort them by their value
		ArrayList<Clue> res = new ArrayList<Clue>();
		for (int j = 0; j < this.width; j++) {
			for (int i = 0; i < this.height; i++) {
				if(this.puzzle[i][j].value!=0 ) 
				res.add(this.puzzle[i][j]);
			}
		}
		Clue[] tableau = res.toArray(new Clue[res.size()]);
		triFusion(tableau);
		ArrayList<Clue> res2 = new ArrayList<Clue>(Arrays.asList(tableau));
		return res2;
	}
    // sorting code ( skip to line 260 )
	public static void triFusion(Clue[] tableau) {
		int longueur = tableau.length;
		if (longueur > 0) {
			triFusion(tableau, 0, longueur - 1);
		}
	}

	private static void triFusion(Clue[] tableau, int deb, int fin) {
		if (deb != fin) {
			int milieu = (fin + deb) / 2;
			triFusion(tableau, deb, milieu);
			triFusion(tableau, milieu + 1, fin);
			fusion(tableau, deb, milieu, fin);
		}
	}

	private static void fusion(Clue[] tableau, int deb1, int fin1, int fin2) {
		int deb2 = fin1 + 1;

		Clue[] table1 = new Clue[fin1 - deb1 + 1];
		for (int i = deb1; i <= fin1; i++) {
			table1[i - deb1] = tableau[i];
		}

		int compt1 = deb1;
		int compt2 = deb2;

		for (int i = deb1; i <= fin2; i++) {
			if (compt1 == deb2) {
				break;
			} else if (compt2 == (fin2 + 1)) {
				tableau[i] = table1[compt1 - deb1];
				compt1++;
			} else if (table1[compt1 - deb1].value <= tableau[compt2].value) {
				tableau[i] = table1[compt1 - deb1];
				compt1++;
			} else {
				tableau[i] = tableau[compt2];
				compt2++;
			}
		}
	}
    
	
    public ArrayList<Clue> deadEnds(ArrayList<Clue> Clues) {  
    	LinkedList<Clue> clues = new LinkedList<>(Clues);
    	LinkedList<Clue> toremove = new LinkedList<>();
    	
    	for(Clue clue : clues) {                             
    		if(this.BrokenLines(clue).size()==1) {
    			toremove.add(clue);
    		}
    	}
    	
    	clues.removeAll(toremove);
    	for(Clue clue : toremove) {
    		clues.addFirst(clue);
    	}
    	ArrayList<Clue> res = new ArrayList<>(clues);
    	return res;
    }
	
	boolean Solve() { // sort and treat dead ends then it does the backtracking 
		ArrayList<Clue> clues = this.deadEnds(this.cluesSorted());

		for (Clue clue : clues) {
			if ( clue.colored == 0) {
				if (this.BrokenLines(clue).isEmpty())
					return false;
				for (String path : this.BrokenLines(clue)) {
					ArrayList<Clue> L = this.stringToPath(path);
					for (Clue c : L) {
						this.puzzle[c.i][c.j].Color();
						this.buttons[c.i][c.j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X";
						this.buttons[L.get(0).i][L.get(0).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X"
								+ " Initial";
						this.buttons[L.get(L.size() - 1).i][L
								.get(L.size() - 1).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X" + " Final";
					}
					if (this.Solve()) {
						return true;
					}
					// The backtracking part
					for (Clue c : L) {
						this.puzzle[c.i][c.j].Discolor();
						this.buttons[c.i][c.j].substring(0, this.buttons[c.i][c.j].indexOf("X"));
					}
				}
				return false;
			}
		}
		return true;
	}
	
	
	boolean Solve(ArrayList<Clue> clues) { // copy of the solve algorithm but takes a list of clues that we will make later on 
		
		for (Clue clue : clues) {
			if ( clue.value != 0 && clue.colored==0) {
				if (this.BrokenLines(clue).isEmpty())
					return false;
				for (String path : this.BrokenLines(clue)) {
					ArrayList<Clue> L = this.stringToPath(path);
					for (Clue c : L) {
						this.puzzle[c.i][c.j].Color();
						this.buttons[c.i][c.j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X";
						this.buttons[L.get(0).i][L.get(0).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X"
								+ " Initial";
						this.buttons[L.get(L.size() - 1).i][L
								.get(L.size() - 1).j] = this.puzzle[L.get(0).i][L.get(0).j].value + "X" + " Final";
					}
					if (this.Solve(clues)) {
						return true;
					}
					// The backtracking part
					for (Clue c : L) {
						this.puzzle[c.i][c.j].Discolor();
						this.buttons[c.i][c.j].substring(0, this.buttons[c.i][c.j].indexOf("X"));
					}
				}
				return false;
			}
		}
		return true;
	}

	public void combination(Clue c) {

		ArrayList<String> Brokenlines = this.BrokenLines(c);
		

		if (Brokenlines.isEmpty());

		else if (Brokenlines.size() == 1) { // if there is only one possible broken line, its all part of the solution
											// for sure
			ArrayList<Clue> L = this.stringToPath(Brokenlines.get(0));
			for (Clue clue : L) {
				clue.colored = 2;
				clue.partOf = new Pair(L.get(0).i,L.get(0).j);
			}
		
		}

		else {   // otherwise color(2) all the common clues
			ArrayList<Clue> L0 = this.stringToPath(Brokenlines.get(0));

			for (int k = 1; k < c.value; k++) {
				boolean existInAll = true;
				for (String S : Brokenlines) {
					ArrayList<Clue> L = this.stringToPath(S);
					if (L.get(k).i != L0.get(k).i || L.get(k).j != L0.get(k).j)
						existInAll = false;
				}
				if (existInAll) {
					this.puzzle[L0.get(k).i][L0.get(k).j].colored = 2;
					this.puzzle[L0.get(k).i][L0.get(k).j].partOf=new Pair(L0.get(0).i,L0.get(0).j);
					
				}
			}
         
		}
				
	}
	
   
	public ArrayList<Clue> Colored2(){  // stores all the colored with 2 in a ArrayList<Clue>
		ArrayList<Clue> res = new ArrayList<Clue>();
		for (int j = 0; j < this.width; j++) {
			for (int i = 0; i < this.height; i++) {
				if (this.puzzle[i][j].colored==2) {
					res.add(this.puzzle[i][j]);
				}
			}
		}
		return res;	
	}
	
	public void exclusionAndCombination() {  
		int k = this.Colored2().size();
		boolean sizechanged=true;
		while(sizechanged) {
			for (int j = 0; j < this.width; j++) {
				for (int i = 0; i < this.height; i++) {
					if(this.puzzle[i][j].isMarked){
						this.combination(this.puzzle[i][j]);
					}
				}
			sizechanged= k!=this.Colored2().size()	;
		}
	}
	
}
	
	public ArrayList<Clue> notColored2(){   // stores all the uncolored with 2 in a ArrayList<Clue>
		ArrayList<Clue> res = new ArrayList<Clue>();
		for (int j = 0; j < this.width; j++) {
			for (int i = 0; i < this.height; i++) {
				if (this.puzzle[i][j].colored!=2) {
					res.add(this.puzzle[i][j]);
				}
			}
		}
		return res;	
	}
	
	public void Solverwithexclusion() {  
		this.exclusionAndCombination();
		ArrayList<Clue> clues=this.notColored2(); 
		this.Solve(clues);
	} 	
	
}