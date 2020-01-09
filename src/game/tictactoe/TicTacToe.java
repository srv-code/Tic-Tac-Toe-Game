package game;

import java.util.Scanner;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


/**	Basic Tic Tac Toe game
*/
public class TicTacToe {
	private final static float APP_VERSION = 0.01f;
	public static boolean debugModeEnabled = false; // default value set

	/**	A 3x3 board with the following values:
			0 = Cell not marked yet
			1 = Cell marked 'x'
			2 = Cell marked 'o'
			3 = Cell marked striked 'x', represented as (x)
			4 = Cell marked striked 'o', represented as (o)
	*/
	private byte[][] board = new byte[3][3];

	/**	Will be true till the game has not drawn or won 
		by either of the party 
	*/
	private boolean continueInputs;

	/** Shows thr winnnig status of the game.
		Can be within any of the below values:
			0 = None won, if at the end of the game
					means match has drawn
			1 = Team 'x' won
			2 = Team 'x' won
	*/
	private byte winStatus;

	final InputStream  istream;
	final OutputStream ostream;

	public TicTacToe(final InputStream istream, final OutputStream ostream) throws IOException {
		this.istream = istream;
		this.ostream = ostream;
		continueInputs = true; // start receiving inputs from user
		winStatus = 0; // none has won initially

		if(debugModeEnabled)
			writeOut("  // TicTacToe(InputStream (%s), OutputStream (%s)) called \n", 
					istream.getClass().getName(), ostream.getClass().getName());
	}

	public void start() throws IOException {
		getInputs();
	}

	public void displayBoard() throws IOException {
		if(debugModeEnabled) 
			writeOut("  // displayBoard() called \n");

		writeOut("\n--- Match board ---\n");

		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				switch(board[i][j]) {
					case 0: writeOut("   "); break;
					case 1: writeOut(" x "); break;
					case 2: writeOut(" o "); break;
					case 3: writeOut("(x)"); break;
					case 4: writeOut("(o)"); break;
					default: // Should not get here, programmer's error
						throw new AssertionError(
							String.format("Invalid value (%d) in board at co-ordinate (%d,%d) !", 
									board[i][j], i,j));
				}

				if(j<2) {
					// print vertical table border
					writeOut("|");
				}
			}

			if(i<2) {
				// print horizontal table border
				writeOut("\n---|---|---\n");
			}
		}

		writeOut("\n\nResult: %s \n", (winStatus == 0 ? "Matchc draw." : (winStatus == 1 ? "Team 'x' won." : "Team 'o' won.")));
	}

	private void writeOut(final String formatString, final Object... args) throws IOException {
		ostream.write( String.format(formatString, args).getBytes() );
	}

	private void getInputs() throws IOException {
		if(debugModeEnabled) 
			writeOut("  // getInputs() called \n");

		Scanner scanner = new Scanner(istream);
		boolean askInput;
		boolean nextMoveIsX = true; // set to 'x' as first move
		byte coordX=-1, coordY=-1; // input coordinates
		while(continueInputs) {
			askInput = true;
			while(askInput) {
				writeOut("Coord for move '%s':  ", nextMoveIsX ? 'x' : 'o');
				String strCoord = scanner.next();
				try {
					if(strCoord.length()!=2)
						throw new IllegalArgumentException("Invalid coordinate input length: " + strCoord.length());

					coordX = Byte.parseByte(strCoord.substring(0, 1));
					coordY = Byte.parseByte(strCoord.substring(1, 2));
					if(coordX<0 || coordX>2 || coordY<0 || coordY>2)
						throw new IllegalArgumentException("Coordinates out of range ("+coordX+","+coordY+")");

					if(board[coordX][coordY] != 0) 
						throw new IllegalStateException("Location already specified as " + (board[coordX][coordY] == 1 ? 'x' : 'o'));

					board[coordX][coordY] = (byte) (nextMoveIsX ? 1 : 2);
					nextMoveIsX = !nextMoveIsX; // flip

					askInput = false; // received valid input 
				} catch(NullPointerException|IllegalArgumentException|IllegalStateException e) {
					writeOut("Err: Invalid input (%s) \n", e.getMessage());
					askInput = true;
				}
			}
			checkNewEntry(coordX, coordY);
		}
	}

	/**	Called after a new entry is made on the board cells.
		Checks for any strikes/match draw
		Also sets the variable continueInputs approriately
	*/ // STUB
	int counter = 0; // test
	public void checkNewEntry(final byte coordX, final byte coordY) throws IOException {
		if(debugModeEnabled)
			writeOut("  // checkNewEntry(%d,%d) called \n", coordX,coordY);

		if(++counter == 9)  // test
			continueInputs = false;
	}

	private void showAppState() throws IOException {
		writeOut("App State: \n");
		writeOut("    board[3][3]: \n");
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				writeOut("%d  ", board[i][j]);
			}
			writeOut("\n");
		}

		writeOut("    continueInputs=%b \n", continueInputs);
		writeOut("    winStatus=%b \n", winStatus);
		writeOut("    istream=%s \n", istream.getClass().getName());
		writeOut("    ostream=%s \n", ostream.getClass().getName());
		writeOut("\n\n");
	}

	public static void main(final String[] args) throws Exception {
		checkOptions(args);

		TicTacToe game = new TicTacToe(System.in, System.out);		
		if(debugModeEnabled) 
			game.showAppState();

		game.start();
		game.displayBoard();

		if(debugModeEnabled) 
			game.showAppState();
	}

	/** test for any command line switches */
	private static void checkOptions(final String[] args) {
		for(String arg : args) {
			switch(arg) {
				case "-d": debugModeEnabled = true; break;
				case "-h": showHelp(); System.exit(0);
				default: 
					System.err.println("Invalid option: " + arg + " (ignored)");
			}
		}
	}

	private static void showHelp() {
		System.out.println("Tic Tac Toe:");
		System.out.println("Description: A simple tic tac toe game.");
		System.out.printf ("Version: %.2f \n", APP_VERSION);
		System.out.println("Options:");
		System.out.println("  -h      Shows this help menu");
		System.out.println("  -d      Enable debugging mode");
	}
}