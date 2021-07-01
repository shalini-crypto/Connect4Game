package com.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int ROW = 6;
	private static final int COLUMN = 7;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String DISC_COLOUR1 = "#24303E";
	private static final String DISC_COLOUR2 = "#4CAA88";
	private static Disc[][] insertedDiscsArray =  new Disc[ROW][COLUMN];   // For structural Changes : For developers

	private boolean isPLAYER_ONETurn = true;


	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";


	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label PlayerNameLabel;

	@FXML
	public TextField  playerOneTextField, playerTwoTextField;
	@FXML
	public Button setNamesButton;

	private  boolean isAllowedToInsert = true;   //Flag to avoid same color disc being added

	public void createPlayground() {
		setNamesButton.setOnAction(event -> {
			PLAYER_ONE = playerOneTextField.getText();
			PLAYER_TWO = playerTwoTextField.getText();

			isPLAYER_ONETurn = true;
			PlayerNameLabel.setText(PLAYER_ONE);
		});
		Shape rectanglesWithHoles = createStructuralGameGrid();
		rootGridPane.add(rectanglesWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableRectangle();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}

	}

	private  Shape createStructuralGameGrid(){
		Shape rectanglesWithHoles =  new Rectangle( (COLUMN + 1)* CIRCLE_DIAMETER, (ROW + 1)* CIRCLE_DIAMETER);

		for (int row=0; row < ROW; row++){
			for (int column=0 ;column < COLUMN; column++){
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);

				//adding left and top margin and gap between each circle
				circle.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);

				//subtract circle from rectangle to get holes on the grid
				rectanglesWithHoles = Shape.subtract(rectanglesWithHoles, circle);

			}
		}


		rectanglesWithHoles.setFill(Color.WHITE);
		return rectanglesWithHoles;

	}

	private List<Rectangle> createClickableRectangle(){

		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < COLUMN; col++){

			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROW + 1)* CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) +CIRCLE_DIAMETER / 4);

			//change colour when hover
			rectangle.setOnMouseEntered(event -> {
				rectangle.setFill(Color.valueOf("#eeeeee26"));
			});
			rectangle.setOnMouseExited(event -> {
				rectangle.setFill(Color.TRANSPARENT);
			});

			final int column = col;
			//on click discs will be in that column
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert)
					isAllowedToInsert = false; //when disc is dropped no more disc will be inserted
					insertDisc(new Disc(isPLAYER_ONETurn), column);
					});

			rectangleList.add(rectangle);
		}

		return rectangleList;

	}

	private  void insertDisc(Disc disc, int column){
		int row = ROW - 1;
		while (row >= 0){
			if(getDiscIfPresent(row, column) == null)
				break;
			row--;
		}
		if(row < 0)      //If it is full, we cannot insert anymore disc
			return;
		insertedDiscsArray[row] [column] = disc;      // For structural Changes : For developers
		insertedDiscPane.getChildren().add(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4 );

		int currRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4 );
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;   //Finally, when disc is dropped allow nwxt player to insert disc
			if(gameEnded(currRow, column)){
				gameOver();
				return;
			}

			isPLAYER_ONETurn = !isPLAYER_ONETurn;
			PlayerNameLabel.setText(isPLAYER_ONETurn ? PLAYER_ONE : PLAYER_TWO);
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = isPLAYER_ONETurn ? PLAYER_ONE :PLAYER_TWO;
		System.out.println("Winner is :"+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("Winner is :"+ winner);
		alert.setContentText("Want to play again ?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> {
			Optional<ButtonType> btnClicked = alert.showAndWait();

			if (btnClicked.isPresent() && btnClicked.get().equals(yesBtn)){
				//User choice Yes to reset Game
				resetGame();

			}else {
				//User choice No to exit Game
				Platform.exit();
				System.exit(0);

			}

		});

	}

	public void resetGame() {
		insertedDiscPane.getChildren().clear(); //Remove all the inserted discs from the Pane

		//Structurally // Makes all the elements of in insertedDiscsArray[][] = null
		for (int row = 0; row < insertedDiscsArray.length; row++){
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;

			}
		}
		//let Player one start the Game
		isPLAYER_ONETurn = true;
		PlayerNameLabel.setText(PLAYER_ONE);

		createPlayground(); //Prepare a fresh playground
	}

	private boolean gameEnded(int row, int col){
			//Vertical Points. A small example: Player inserted his last disc row = 2 col = 3


		//index of each element present in column [row][column] : 0,3  1,3  2,3  3,3  4,3  5,3  --> Point2D x,y

		List<Point2D> verticalPoints =  IntStream.rangeClosed(row-3, row+3).    //range of row values = 0,1,2,3,4,5
										mapToObj(r ->new Point2D(r,col)).    //0,3  1,3  2,3  3,3  4,3  5,3  --> Point2D x,y
										collect(Collectors.toList());

		List<Point2D> horizontalPoints =  IntStream.rangeClosed(col-3, col+3)    //range of column values = 0,1,2,3,4,5
										.mapToObj(c -> new Point2D(row, c))   //3,0  3,1  3,2  3,3  3,4  3,5  --> Point2D x,y
										.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, col + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
										.mapToObj(i ->startPoint1.add(i, -i))
										.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, col - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
										.mapToObj(i -> startPoint2.add(i, i))
										.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
							|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point: points ) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPLAYER_ONETurn){ //if the last inserted disc belongs to the current player
				chain ++;

				if (chain == 4){
					return true;
				}
			}else{
				chain = 0;

			}

		}
		return  false;
	}

	//to prevent array Index Bound
	private  Disc getDiscIfPresent(int row, int col){
		if(row < 0 || row >= ROW || col < 0 || col >= COLUMN)
			return  null;
		return insertedDiscsArray[row][col];
	}

	//determine the Colour of the disk
	private static class Disc extends  Circle{
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(DISC_COLOUR1) : Color.valueOf(DISC_COLOUR2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
