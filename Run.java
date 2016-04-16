import java.util.Arrays;
import java.util.*;
import java.io.*;


////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
///////////////////												  //////////////////////
///////////////////			   SUDOKU - Rouvem Pishchik		      //////////////////////
///////////////////												  //////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

/*
	Loads Sudoku game from puzzle*.sudoku file, and solves it.
	All classes implemented in this Run.java source code file.
	Loaded game must be a valid sudoku puzzle in order to solve
	correctly. A valid sudoku game has only 1 solution, and greater
	than or equal to 17 initial non-contradicting entries. The solver
	algorithm uses backtracking.
*/





/*
	CurrentGame class is the main class that holds all important information
	for the current game, through a character array (keeps data low! and it works...).
*/

class CurrentGame{
	private char[][] sudoku;
	public CurrentGame(){ 			// constructor, initializes all elements to 0. 0 simply resembles no entry!
		sudoku = new char[9][9];
		for(int i = 0; i < 9; i++){
			Arrays.fill(sudoku[i], '0');
		}
	}
	public void Show(){				// outputs to standard output, don't mess with this!
		System.out.println("-------------------------------");
		for(int i = 0; i < 9; i++){
			if(i == 3 || i == 6){
				System.out.println("|---------+---------+---------|");
			}
			for(int j = 0; j < 9; j++){
				if(j == 0 || j == 3 || j == 6){
					System.out.print("| ");
				}
					
				if(j == 2 || j == 5){
					System.out.print(sudoku[i][j] + " ");
				}
				else if(j == 8){
					System.out.print(sudoku[i][j] + " |");
				}
				else{
					System.out.print(sudoku[i][j] + "  ");
				}
			}
			System.out.println();
		}
		System.out.println("-------------------------------");
	}
	
	// To change value, input row, column, and value; note that you can't input 0!
	public void Change(int row, int column, char value){  
		sudoku[row-1][column-1] = value;
	}
	
	// To download an array of values into the current one. Uses previous method.
	public void Download(char[][] download){
		for(int row = 1; row <= 9; row++){
			for(int column = 1; column <= 9; column++){
				Change(row, column, download[row-1][column-1]);
			}
		}
	}
	
	// Simple method to access given elements
	public int Access(int row, int column){
		return (int) sudoku[row][column] - '0';
	}
	
}



class LevelLoader{
	char buffer;
	Scanner fileIn;

	public LevelLoader()throws IOException{
		fileIn = null;
	}
	public void loadFile(String fileName)throws IOException{
		try{
			fileIn = new Scanner(new FileReader(fileName));
		
		}
		catch(IOException exc){
			System.out.println("File I/O Exception with: " + exc);
			return;
		}
	}
	public char[][] upload()throws IOException{
		char[][] temp = new char[9][9];
		int row = 0;
		int column = 0;
		while(fileIn.hasNextInt()){
			buffer = (char)('0' + fileIn.nextInt());
			temp[row][column] = buffer;
			column++;
			if(column == 9){
				row++;
				column = 0;
			}		
		}
		return temp;
	}
	public void closeFile() throws IOException{
		fileIn.close();
	}
}

class DataTransfer {
	public DataTransfer(){}
	// Transfers file's char array to current game
	public void dataTransfer(LevelLoader level, CurrentGame game)throws IOException{
		char[][] download;
		download = level.upload();
		game.Download(download);
	}
}

class Validate{
	public Validate(){}
	public boolean validateSudoku(CurrentGame game){
		boolean temp1, temp2, temp3;
		temp1 = temp2 = temp3 = true;
		int row, column, topLeftR, topLeftC;
		row = column = topLeftR = topLeftC = 0;
		for(int iteration = 0; iteration < 9; iteration++){
			temp1 = validateGrid(game, topLeftR, topLeftC);
			temp2 = validateRow(game, row);
			temp3 = validateColumn(game, column);
			if(!temp1 || !temp2 || !temp3){
				return false;
			}
			//System.out.println(topLeftR + " " + topLeftC);
			topLeftC += 3;
			
			if(topLeftC == 9){
				topLeftR += 3;
			}
			topLeftC %= 9;
			//System.out.println(topLeftR + " " + topLeftC);
			
			row += 1;
			column += 1;
		}
		return true;
	}
	public boolean validateGrid(CurrentGame game, int topLeftRow, int topLeftColumn){
		int sum = 0;
		for(int row = topLeftRow; row < topLeftRow + 3; row++){
			for(int column = topLeftColumn; column < topLeftColumn + 3; column++){
				
				sum += game.Access(row, column);
			}
		}
		//System.out.println("ValidGrid: " + (sum == 45) + " " + sum);
		return sum == 45;
	}
	public boolean validateRow(CurrentGame game, int rowIndex){
		int sum = 0;
		for(int column = 0; column < 9; column++){
			sum += game.Access(rowIndex, column);
		}
		//System.out.println("ValidRow: " + (sum == 45));
		return sum == 45;
	}
	public boolean validateColumn(CurrentGame game, int columnIndex){
		int sum = 0;
		for(int row = 0; row < 9; row++){
			sum += game.Access(row, columnIndex);
		}
		//System.out.println("ValidColumn: " + (sum == 45));
		return sum == 45;
	}
}

class Solver{
	public boolean Solve(CurrentGame game){
		if(isFull(game)){
			return true;
		}
		else{
			int[] indices = new int[2];
			indices = nextEmptySquare(game);
			for(int i = 1; i <= 9; i++){
				if(canGoIn(game, indices[0], indices[1], i)){
					game.Change(indices[0]+1, indices[1]+1, (char)(i + '0'));
					//game.Show();
					if(Solve(game)){
						return true;
					}
					game.Change(indices[0]+1, indices[1]+1, '0');
					//game.Show();
				}
				else{
					continue;
				}
			}
			
		}
		return false;
	}
	
	public boolean canGoIn(CurrentGame game, int row, int column, int number){
		//System.out.println("Checking if " + number + " will fit in " + row + " " + column);
		boolean check = columnCheck(game, row, column, number) && rowCheck(game, row, column, number) && gridCheck(game, row, column, number);
		//System.out.println(check);
		return check;
	}
	public int[] nextEmptySquare(CurrentGame game){
		int[] indices = new int[2];
		for(int row = 0; row < 9; row++){
			for(int column = 0; column < 9; column++){
				if(game.Access(row, column) == 0){
					indices[0] = row;
					indices[1] = column;
					return indices;
				}
			}
		}
		indices[0] = 9; // these 3 should never occur...
		indices[1] = 9;
		return indices;
	}
	
	public boolean isFull(CurrentGame game){
		for(int row = 0; row < 9; row++){
			for(int column = 0; column < 9; column++){
				if(game.Access(row, column) == 0){
					return false;
				}
			}
		}
		return true;
	}
		
	public boolean isEmpty(CurrentGame game, int row, int column){
		return game.Access(row, column) > 0;
	}
	public boolean rowCheck(CurrentGame game, int row, int column, int number){
		for(int i = 0; i < 9; i++){
			if(game.Access(i, column) == (number)){
				return false;
			}
		}
		//System.out.println("Checking row, for number: " + number + " the row: " + row + " the column: " + column + " Will Fit!");
		return true;
	}
	public boolean columnCheck(CurrentGame game, int row, int column, int number){
		for(int i = 0; i < 9; i++){
			if(game.Access(row, i) == (number)){
				return false;
			}
		}
		//System.out.println("Checking column, for number: " + number + " the row: " + row + " the column: " + column + "  Will Fit!");
		return true;
	}
	public boolean gridCheck(CurrentGame game, int row, int column, int number){
		// given a row and column number, check the corresponding grid...
		if(0 < row && row < 3){
			row = 0;
		}
		else if(3 < row && row < 6){
			row = 3;
		}
		else if(6 < row && row < 9){
			row = 6;
		}
		else{
			//nothing, row is either 0, 3, or 6 already
		}
		if(0 < column && column < 3){
			column = 0;
		}
		else if(3 < column && column < 6){
			column = 3;
		}
		else if(6 < column && column < 9){
			column = 6;
		}
		else{
			//nothing, column is either 0, 3, or 6 already
		}
		//System.out.print("Checking grid, for number: " + number + " the row: " + row + " column: " + column + " ");
		for(int i = row; i < row + 3; i++){
			for(int j = column; j < column + 3; j++){
				if(game.Access(i, j) == number){
					//System.out.println("  Cannot fit!");
					return false;
				}
			}
		}
		//System.out.println(" Will fit!");
		return true;
	}
}

public class Run {
	public static void main(String []args) throws IOException{
		LevelLoader level1 = new LevelLoader();
		level1.loadFile("puzzle1.sudoku");
		DataTransfer data = new DataTransfer();
		CurrentGame game2 = new CurrentGame();
		data.dataTransfer(level1, game2);
		level1.closeFile();
		//game2.Show();
		Validate validGame = new Validate();
		System.out.println(validGame.validateSudoku(game2));
		
		
		CurrentGame game3 = new CurrentGame();
		LevelLoader level2 = new LevelLoader();
		level2.loadFile("puzzle3.sudoku");
		DataTransfer data2 = new DataTransfer();
		data2.dataTransfer(level2, game3);
		level2.closeFile();
		
		
		game3.Show();
		
		Solver solve = new Solver();
		solve.Solve(game3);
		game3.Show();
		System.out.print(validGame.validateSudoku(game3));
		
		
		
		
		
		
		
		/*
		Scanner in = new Scanner(System.in);
		CurrentGame game = new CurrentGame();
		char temp;
		int row, column;
		while(true){
			System.out.println("Input the indexes and values (row, column, value): ");
			row = in.nextInt();
			column = in.nextInt();
			
			temp = in.next().charAt(0);
			game.Change(row, column, (char) temp);
			game.Show();
		}
		*/
	}
}
