package jaysc.example.com.chess.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import jaysc.example.com.chess.Adapters.ChessboardAdapter;
import jaysc.example.com.chess.Pieces.Bishop;
import jaysc.example.com.chess.Pieces.King;
import jaysc.example.com.chess.Pieces.Knight;
import jaysc.example.com.chess.Pieces.Pawn;
import jaysc.example.com.chess.Pieces.Piece;
import jaysc.example.com.chess.Pieces.Queen;
import jaysc.example.com.chess.Pieces.Rook;
import jaysc.example.com.chess.R;

public abstract class GameActivity extends AppCompatActivity {
    protected char turn;//white('w') or black('b') player
    protected int undo;//keeps track of which turn wants to undo
    public static int turnCount;
    public final static List<BiFunction<Integer,Character,Piece>> promotionConstructors = Arrays.asList(Queen::new, Rook::new, Bishop::new, Knight::new);//constructors for pawn promotions
    public final static String[] promotionLevels = {"(Q) Queen", "(R) Rook", "(B) Bishop", "(N) Knight"};//used for pawn promotion display
    protected List<String> moves;//list of strings: "start,end(,promotion)"
    protected Piece[] lastChessboard;//"snapshot" of last move's chessboard
    protected Piece[] chessboard;//main chessboard
    protected ChessboardAdapter chessboardAdapter;
    protected GridView chessboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        turn = 'w';
        undo = -1;//arbitrary val
        turnCount = 0;
        moves = new ArrayList<>();
        lastChessboard = null;
        //create piece array to hold pieces
        chessboard = initBoard();
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, chessboard);
        //fetch gridview from xml variable name
        chessboardAdapter.selectedPieceIndex = -1;
        //create and set view
        chessboardView = findViewById(getChessboardId());
        chessboardView.setAdapter(chessboardAdapter);
    }

    public static Piece[] initBoard() {
        Piece[] board = new Piece[64];
        //black pieces
        board[0] = new Rook(0, 'b');
        board[1] = new Knight(1, 'b');
        board[2] = new Bishop(2, 'b');
        board[3] = new Queen(3, 'b');
        board[4] = new King(4, 'b');
        board[5] = new Bishop(5, 'b');
        board[6] = new Knight(6, 'b');
        board[7] = new Rook(7, 'b');
        //pawns
        for (int i = 8; i < 16; i++) {
            board[i] = new Pawn(i, 'b');//black pawns
            board[i+40] = new Pawn(i+40,'w');//white pawns
        }
        //white pieces
        board[56] = new Rook(56, 'w');
        board[57] = new Knight(57, 'w');
        board[58] = new Bishop(58, 'w');
        board[59] = new Queen(59, 'w');
        board[60] = new King(60, 'w');
        board[61] = new Bishop(61, 'w');
        board[62] = new Knight(62, 'w');
        board[63] = new Rook(63, 'w');
        return board;
    }

    public static Piece[] duplicateBoard(Piece[] p) {
        //THIS MAKES A DEEP COPY OF CHESSBOARD
        Piece[] result = new Piece[64];
        for (int i = 0; i < 64; i++) {
            if (p[i] != null) {
                result[i] = p[i].makeCopy();
            }
        }
        return result;
    }

    protected void toggleTurn() {
        turn = (turn == 'w')? 'b':'w';
    }

    protected void concludeTurn() {
        toggleTurn();
        turnCount++;
        chessboardAdapter.notifyDataSetChanged();
        evaluateTurn();
    }

    protected boolean noSafeMoves() {
        for (int i = 0; i < 64; i++) {
            Piece curPiece = chessboard[i];
            //if there's a piece that current player owns
            if (curPiece != null && curPiece.getOwner() == turn) {
                for (int j = 0; j < 64; j++) {
                    //current piece is able to move somewhere
                    if (curPiece.isMoveValid(j, chessboard) && notCheckAfterMove(chessboard, i, j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean notCheckAfterMove(Piece[] board, int startIndex, int endIndex) {
        Piece[] b = duplicateBoard(board);
        King k = getCurrentKing(b);
        //get piece of hypothetical pieces
        Piece chosenPiece = b[startIndex];
        //move this piece
        chosenPiece.move(endIndex, b);
        //return if king is in not in check...or not
        return k!=null && !k.inCheck(b);
    }

    protected King getCurrentKing(Piece[] board){
        for (int i = 0; i < 64; i++){
            Piece curPiece = board[i];
            if (curPiece instanceof King && curPiece.getOwner() == turn){
                return (King)curPiece;
            }
        }
        return null; //shouldnt happen!!!
    }

    //popup funcs
    protected void showPawnPopup(final int selectedPieceIndex, final char owner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Promote Pawn...");
        builder.setItems(promotionLevels, (dialog, which) -> {
            // the user clicked on promotionLevels[which]
            chessboard[selectedPieceIndex] = promotionConstructors.get(which).apply(selectedPieceIndex,owner);
            //add move to list
            String entry = moves.get(moves.size() - 1);//get current move string
            entry+=","+which;
            moves.set(moves.size() - 1, entry);
            chessboardAdapter.notifyDataSetChanged();
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    protected void showSavePopup(final String titleMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleMessage);
        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter name for game");
        builder.setView(input);
        //button handlers
        builder.setPositiveButton("OK", (dialog, which) -> {
            // if EditText is empty disable closing on possitive button
            if (!(input.getText().toString().trim().isEmpty())) {//do something if edittext not empty
                String name = input.getText().toString();//set name
                //write file to disk
                writeToFile(name);
            }
            returnToMainMenu();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            returnToMainMenu();
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    //endgame funcs
    protected void writeToFile(String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.getApplicationContext().openFileOutput(RecordedGamesActivity.PATH, Context.MODE_APPEND | Context.MODE_PRIVATE));
            outputStreamWriter.write(name + "\n");
            outputStreamWriter.write(LocalDate.now().toString() + "\n");
            for (int i = 0; i < moves.size(); i++) {
                outputStreamWriter.write(moves.get(i) + "\n");
            }
            outputStreamWriter.write(RecordedGamesActivity.DIVIDER + "\n");
            outputStreamWriter.close();
        } catch (IOException e) {
            Toast.makeText(this, "file write failed", Toast.LENGTH_LONG).show();
        }
    }

    protected void returnToMainMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    //button functions
    public abstract void undoMove(View view);

    public void resign(View view) {
        //no saved moves
        if (moves.size() == 0) {
            returnToMainMenu();
        } else {
            if (turn == 'w') showSavePopup("Black wins"); else showSavePopup("White wins");
        }
    }

    //function for seeing next turn's situation
    protected abstract void evaluateTurn();

    //id functions
    public abstract int getChessboardId();
    public abstract int getViewId();
}
