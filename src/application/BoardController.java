package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;

public class BoardController implements Initializable{
	
	@FXML
    private ScrollPane scrollpane;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private GridPane gridboard;

	@FXML
	private TextField test_file;

	@FXML
	private Button select;

	@FXML
	private Button solve;

	@FXML
	private Button check;

	@FXML
	private Button generate;

	@FXML
	private RadioButton easy;

	@FXML
	private RadioButton hard;

	@FXML
	private Button save;

	@FXML
	private TextField file_name;
	
    @FXML
    private TextField attempts_num;

    @FXML
    private TextField checks_num;

	public static Scanner in = null;
	public static String file_path = "";
	private int[][] board = new int[9][9];
	private static TextField[][] texts = new TextField[9][9];
	public static int attempts = 0;
	public static int checks = 0;
	private int[][] test = new int[9][9];

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ToggleGroup toggleGroup = new ToggleGroup();
		easy.setSelected(true);
		easy.setToggleGroup(toggleGroup);
		hard.setToggleGroup(toggleGroup);
	}
	static List<TextField> lfLetters = new ArrayList<>();
	@FXML
	void selectFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File defaultDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setInitialDirectory(defaultDirectory);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		Stage stage = (Stage)scrollpane.getScene().getWindow();
		stage.getIcons().add(new Image("file:browse.png"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null && file.isFile()) {
			gridboard.getChildren().retainAll(gridboard.getChildren().get(0));
			file_path = file.getAbsoluteFile().toString();
			test_file.setText(file.getName());
			try {
				attempts=0;
				checks=0;
				in = new Scanner(file);
				for(int i=0;i<9;i++) { //rows
					for(int j=0;j<9;j++) { //columns
						board[i][j] = in.nextInt(); // from file
						texts[i][j] = new TextField(String.valueOf(board[i][j])); //from file
						texts[i][j].setAlignment(Pos.CENTER);
						texts[i][j].setFont(new Font("Arial", 24));
						texts[i][j].setMaxWidth(50);
						texts[i][j].setMaxHeight(50);
						final int index = i;
						final int Jindex =j;
						texts[i][j].textProperty().addListener((obs, oldVal, newVal) -> { //listener when the content change
					        System.out.println("Text of Textfield on index " + index + " changed from " + oldVal
					                + " to " + newVal);
					        texts[index][Jindex].setStyle("-fx-background-color: #ffff4d;");
					        
					        attempts++;
					        attempts_num.setText(attempts+"");
					    });
					    lfLetters.add(texts[i][j]); //add to list
						GridPane.setHalignment(texts[i][j], HPos.CENTER);
						GridPane.setFillWidth(texts[i][j], true);
						GridPane.setConstraints(texts[i][j], i, j);
						StackPane stackpane = new StackPane(texts[i][j]);
						GridPane.setFillHeight(stackpane, true);
						GridPane.setFillWidth(stackpane, true);
						addBackground(stackpane, i, j);
						gridboard.add(stackpane, j, i);
					}
					in.nextLine();
				}
				System.out.println("Data:");
				printData(board); // print the board from file

			} catch (FileNotFoundException e) {
				Alert("File not found!");
			}
		}
	}

	private void addBackground(StackPane cell, int row, int col) {
		String[] colors = {"#b0cbe1", "#4dff4d", "#ff66d9","#ff4d4d", "#a64dff", "#5c5cd6", "#8cff66", "#ff8533", "#66d9ff"};
		int colorIndex = 3 * (row / 3) + (col / 3);
		cell.setStyle("-fx-background-color: " + colors[colorIndex] + ";");
	}

	private void Highlight(int[][] array) {
		for(int i=0;i<array.length;i++) {
			for(int j=0;j<array.length;j++) {
				if(array[i][j] != board[i][j]) //compare it with the solution
					texts[i][j].setStyle("-fx-background-color: #ffff4d;");
				else
					texts[i][j].setStyle("-fx-background-color: white;");
			}
		}
	}

	@FXML
	void checkSolution(ActionEvent event) {
		try {
			int[][] array = new int[9][9]; 
			for(int i=0;i<array.length;i++) { //rows
				for(int j=0;j<array.length;j++) { //columns
					array[i][j] = Integer.parseInt(texts[i][j].getText()); //take the result from text field
				}
			}
			if(!isValid(array)) { //if the whole array is not valid 
				Alert("Wrong Solution");
				solveSudoku(board); //prepare the solution (backtracking)
				Highlight(array); // highlight the error (array does not match with board)
				checks++;
				checks_num.setText(checks+"");
			}
			else {
				Alert("Correct Solution");
				Highlight(array);
			}
		}
		catch (Exception e) {
			Alert("Select a Test File First!");
		}
	}

	@FXML
	void solution(ActionEvent event) {
		try {
			//gridboard.getChildren().retainAll(gridboard.getChildren().get(0));
			Highlight(board);
			if (solveSudoku(board)) { // if the board can be solved
				for(int i=0;i<9;i++) { //rows
					for(int j=0;j<9;j++) { //columns
						texts[i][j] = new TextField(String.valueOf(board[i][j])); //from solution
						texts[i][j].setAlignment(Pos.CENTER);
						texts[i][j].setFont(new Font("Arial", 24));
						texts[i][j].setMaxWidth(50);
						texts[i][j].setMaxHeight(50);
						texts[i][j].setEditable(false);
						GridPane.setHalignment(texts[i][j], HPos.CENTER);
						GridPane.setFillWidth(texts[i][j], true);
						GridPane.setConstraints(texts[i][j], i, j);
						StackPane stackpane = new StackPane(texts[i][j]);
						GridPane.setFillHeight(stackpane, true);
						GridPane.setFillWidth(stackpane, true);
						addBackground(stackpane, i, j);
						gridboard.add(stackpane, j, i);
					}
				}
				System.out.println("Solution:");
				printData(board);
			}
			else 
				Alert("No solution");
		}
		catch (Exception e) {
			Alert("Select a Test File First!");
		}
	}

	public static boolean solveSudoku(int[][] board) {
		int row = -1, col = -1;
		boolean isEmpty = true;
		for (int i = 0; i < board.length; i++) { //rows
			for (int j = 0; j < board.length; j++) //columns
				if (board[i][j] == 0) { //if the square is empty (does not a value already)
					row = i; 
					col = j;
					isEmpty = false;
					break; //take the first empty square
				}
			if (!isEmpty)
				break;
		}
		if (isEmpty) 
			return true;

		for (int num = 1; num <= board.length; num++) 
			if (isSafe(board, row, col, num)) { //if the value does not exist in the row or column or same subgrid
				board[row][col] = num; //set the value
				if (solveSudoku(board)) // recursion to other empty squares
					return true;
				else
					board[row][col] = 0; 
			}
		return false;
	}


	public static boolean isSafe(int[][] board, int row, int col, int num) { 
		for (int i = 0; i < board.length; i++) 
			if (board[row][i] == num)  //the value exists in same row
				return false;
		for (int j = 0; j < board.length; j++) { //the value exists in same column
			if (board[j][col] == num) 
				return false;
		}

		//int sqrt = (int)Math.sqrt(board.length);
		
		/* this checks if the number 
		 * exists in same subgrid*/
		int boxRowStart = row - row % 3;
		int boxColStart = col - col % 3;

		for (int i = boxRowStart;i < boxRowStart + 3; i++) //row in subgrid
			for (int j = boxColStart; j < boxColStart + 3; j++) //column in subgrid
				if (board[i][j] == num) //if it exists
					return false; 
		return true;
	}

	public boolean isValid(int[][] array) {
		boolean[][] row = new boolean[9][9];
		boolean[][] column = new boolean[9][9];
		boolean[][] square = new boolean[9][9];
		int value, square_no;

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				value = array[x][y] - 1;

				// Check for valid values
				if ((value < 0) || (value >= 9)) { 
					return false;
				}

				// Calculate the square number using mod and div
				// NB! This should be generalised somewhat...
				square_no = (x / 3) + (y / 3) * 3;

				// Check if this value has been seen in row, column or square
				if (row[y][value] ||
						column[x][value] ||
						square[square_no][value] ) {

					return false;

				} else {
					// if not, mark it as seen
					row[y][value] = true;
					column[x][value] = true;
					square[square_no][value] = true;
				}
			}
		}

		return true;
	}

	@FXML
	void saveFile(ActionEvent event) throws IOException {
		if(file_name.getText().isEmpty())
			Alert("Please Enter File's Name");
		else {
			File file = new File(file_name.getText());
			if(file.exists()) {
				Alert("File Already Exists!\nPlease Choose Another Name");
				return;
			}
			if(!file_name.getText().contains(".txt")) {
				Alert("Should be a txt file");
				return;
			}
			file.createNewFile();
			FileWriter out = new FileWriter(file);
			for(int i=0;i<9;i++) {
				for(int j=0;j<9;j++) {
					out.write(temp[i][j]+" ");
				}
				out.write("\n");
			}
			out.close();
		}
	}
	
	public int[][] temp;
	@FXML
	void generateFile(ActionEvent event) {
		attempts=0;
		checks=0;
		int p = 1;
		Random r = new Random();
		int i1=r.nextInt(8);
		int firstval = i1;
		while (p == 1) {
			int x = firstval, v = 1;
			int a[][] = new int[9][9];
			int b[][] = new int[9][9];
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if ((x + j + v) <= 9)
						a[i][j] = j + x + v;
					else
						a[i][j] = j + x + v - 9;
					if (a[i][j] == 10)
						a[i][j] = 1;
					// System.out.print(a[i][j]+" ");
				}
				x += 3;
				if (x >= 9)
					x = x - 9;
				// System.out.println();
				if (i == 2) {
					v = 2;
					x = firstval;
				}
				if (i == 5) {
					v = 3;
					x = firstval;
				}

			}
			if(easy.isSelected()) {
				b[0][3] = a[0][3];
				b[0][4] = a[0][4];
				b[1][2] = a[1][2];
				b[1][3] = a[1][3];
				b[1][6] = a[1][6];
				b[1][7] = a[1][7];
				b[1][8] = a[1][8];
				b[2][0] = a[2][0];
				b[2][4] = a[2][4];
				b[2][8] = a[2][8];
				b[3][2] = a[3][2];
				b[3][5] = a[3][5];
				b[3][8] = a[3][8];
				b[4][0] = a[4][0];
				b[4][2] = a[4][2];
				b[4][3] = a[4][3];
				b[4][4] = a[4][4];
				b[4][5] = a[4][5];
				b[4][6] = a[4][6];
				b[5][0] = a[5][0];
				b[5][1] = a[5][1];
				b[5][4] = a[5][4];
				b[5][6] = a[5][6];
				b[6][0] = a[6][0];
				b[6][4] = a[6][4];
				b[6][6] = a[6][6];
				b[6][8] = a[6][8];
				b[7][0] = a[7][0];
				b[7][1] = a[7][1];
				b[7][2] = a[7][2];
				b[7][5] = a[7][5];
				b[7][6] = a[7][6];
				b[8][2] = a[8][2];
				b[8][4] = a[8][4];
				b[8][5] = a[8][5];
			}
			else {
				b[0][0] = a[0][0];
				b[8][8] = a[8][8];
				b[0][3] = a[0][3];
				b[0][4] = a[0][4];
				b[1][2] = a[1][2];
				b[1][3] = a[1][3];
				b[1][6] = a[1][6];
				b[1][7] = a[1][7];
				b[2][0] = a[2][0];
				b[2][4] = a[2][4];
				b[2][8] = a[2][8];
				b[3][2] = a[3][2];
				b[3][8] = a[3][8];
				b[4][2] = a[4][2];
				b[4][3] = a[4][3];
				b[4][5] = a[4][5];
				b[4][6] = a[4][6];
				b[5][0] = a[5][0];
				b[5][6] = a[5][6];
				b[6][0] = a[6][0];
				b[6][4] = a[6][4];
				b[6][8] = a[6][8];
				b[7][1] = a[7][1];
				b[7][2] = a[7][2];
				b[7][5] = a[7][5];
				b[7][6] = a[7][6];
				b[8][4] = a[8][4];
				b[8][5] = a[8][5];
				b[0][0] = a[0][0];
				b[8][8] = a[8][8];
			}
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					board[i][j] = b[i][j];
					texts[i][j] = new TextField(String.valueOf(board[i][j]));
					texts[i][j].setAlignment(Pos.CENTER);
					texts[i][j].setFont(new Font("Arial", 24));
					texts[i][j].setMaxWidth(50);
					texts[i][j].setMaxHeight(50);
					final int index = i;
					final int Jindex = j;
					texts[i][j].textProperty().addListener((obs, oldVal, newVal) -> {
				        System.out.println("Text of Textfield on index " + index + " changed from " + oldVal
				                + " to " + newVal);
				        texts[index][Jindex].setStyle("-fx-background-color: #ffff4d;");
				        attempts++;
				        attempts_num.setText(attempts+"");
				    });
				    lfLetters.add(texts[i][j]);
					GridPane.setHalignment(texts[i][j], HPos.CENTER);
					GridPane.setFillWidth(texts[i][j], true);
					GridPane.setConstraints(texts[i][j], i, j);
					StackPane stackpane = new StackPane(texts[i][j]);
					GridPane.setFillHeight(stackpane, true);
					GridPane.setFillWidth(stackpane, true);
					addBackground(stackpane, i, j);
					gridboard.add(stackpane, j, i);
				}
			}
			temp = b;
			System.out.println("Generated Data");
			printData(b);
			break;
		}
	}


	public static void printData(int[][] board) {
		System.out.println("------------------------------------");
		for (int i = 0; i < board.length; i++) {
			if (i == 3 || i == 6)
				System.out.println("------------------------------------");
			for (int j = 0; j < board[i].length; j++) {
				System.out.format("%-3s", board[i][j]);
				if (j == 2 || j == 5 || j == 8)
					System.out.print(" | ");
			}           
			System.out.println();   
		}      
		System.out.println("------------------------------------");
	}

	public static void Alert(String message) {
		javafx.scene.control.Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(message);
		alert.setTitle("Message");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}


}
