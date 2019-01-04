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
import jaysc.example.com.chess.R;
import jaysc.example.com.chess.Pieces.*;

public class GameActivity extends AppCompatActivity {
    private char turn;//white('w') or black('b') player
    private int drawRequest;//keeps track of which turn wants to draw
    private int undo;//keeps track of which turn wants to undo
    public static int turnCount;
    public final static List<BiFunction<Integer,Character,Piece>> promotionConstructors = Arrays.asList(
            Queen::new,
            Rook::new,
            Bishop::new,
            Knight::new);//constructors for pawn promotions
    private final static String[] promotionLevels = {"(Q) Queen", "(R) Rook", "(B) Bishop", "(N) Knight"};//used for pawn promotion display

    private List<String> moves;//list of strings: "start,end(,promotion)"
    private Piece[] lastChessboard;//"snapshot" of last move's chessboard
    private Piece[] chessboard;//main chessboard

    private ChessboardAdapter chessboardAdapter;
    private GridView chessboardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        turn = 'w';
        drawRequest = -2;
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
        chessboardView = findViewById(R.id.chessboard);
        //connect gridview to its adapter
        chessboardView.setAdapter(chessboardAdapter);
        chessboardView.setOnItemClickListener((parent, v, position, id) -> {
            //add piece to selected var?
            if (chessboard[position] != null && chessboard[position].getOwner() == turn) {
                //setting selectedpiece only if it is owned by player
                chessboardAdapter.selectedPieceIndex = position;
            } else if (chessboardAdapter.selectedPieceIndex != -1) {
                //entertains move if there is a selectedpiece and clicked destination
                Piece selectedPiece = chessboard[chessboardAdapter.selectedPieceIndex];

                if (selectedPiece.isMoveValid(position, chessboard)
                        && notCheckAfterMove(chessboard, selectedPiece.getIndex(), position)) {//MOVE IS VALID
                    //make copy of board
                    lastChessboard = duplicateBoard(chessboard);
                    //add move to list
                    moves.add(selectedPiece.getIndex() + "," + position);
                    //move piece
                    selectedPiece.move(position, chessboard);
                    if (selectedPiece instanceof Pawn && (selectedPiece.getIndex() < 8 || selectedPiece.getIndex() > 55)) {
                        //pawn promotion
                        showPawnPopup(selectedPiece.getIndex(), turn);
                    }
                    //end turn
                    concludeTurn();
                }
                //clear selectedpieceindex
                chessboardAdapter.selectedPieceIndex = -1;
            }
            chessboardAdapter.notifyDataSetChanged();
        });
        //initial message
        Toast.makeText(GameActivity.this, "White's turn", Toast.LENGTH_LONG).show();
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

    //switches turn, redraws chessboard, sees if next player is screwed
    private void concludeTurn() {
        toggleTurn();
        turnCount++;
        chessboardAdapter.notifyDataSetChanged();
        evaluateTurn();
    }

    private void toggleTurn() {
        turn = (turn == 'w')? 'b':'w';
    }
    private void evaluateTurn() {
        King currentKing = getCurrentKing(chessboard);
        String message;
        //check if next guy is in trouble
        if (noSafeMoves()) {//stalemate or checkmate for next guy
            if (currentKing!=null && currentKing.inCheck(chessboard)) {//checkmate
                message = (turn == 'w')?"Checkmate, Black wins":"Checkmate, White wins";
            } else {//stalemate
                //Toast.makeText(GameActivity.this, "Stalemate", Toast.LENGTH_LONG).show();
                message = "Stalemate";
            }
            //END GAMMMMEEEEE HEEERRREEE
            showSavePopup(message);
        } else {//normal move but next guy may be in check...
            message = ((turn == 'w')?"White's":"Black's") + " turn";
            if (currentKing!=null && currentKing.inCheck(chessboard)) {
                message+=", CHECK";
            }
            if (drawRequest == turnCount-1) {//normal move but may be draw request sent
                message+= ", " + ((turn == 'w')?"Black":"White") + " offers DRAW";
            }
            Toast.makeText(GameActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean noSafeMoves() {
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

    private boolean notCheckAfterMove(Piece[] board, int startIndex, int endIndex) {
        Piece[] b = duplicateBoard(board);
        King k = getCurrentKing(b);
        //get piece of hypothetical pieces
        Piece chosenPiece = b[startIndex];
        //move this piece
        chosenPiece.move(endIndex, b);
        //return if king is in not in check...or not
        return k!=null && !k.inCheck(b);
    }

    private Piece[] duplicateBoard(Piece[] p) {
        //THIS MAKES A DEEP COPY OF CHESSBOARD
        Piece[] result = new Piece[64];
        for (int i = 0; i < 64; i++) {
            if (p[i] != null) {
                result[i] = p[i].makeCopy();
            }
        }
        return result;
    }

    private void showPawnPopup(final int selectedPieceIndex, final char owner) {
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
        builder.show();
    }

    private void showSavePopup(final String titleMessage) {
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
        builder.show();
    }

    private void writeToFile(String name) {
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
            Toast.makeText(GameActivity.this, "file write failed", Toast.LENGTH_LONG).show();
        }
    }

    //draw button: click before making move so no general confirm button needed?
    public void draw(View view) {
        if (drawRequest == -2) {
            drawRequest = turnCount;
        } else if (drawRequest == turnCount - 1) {//previous turn already wants to draw
            //DRAW!!!
            turnCount++;
            showSavePopup("Draw");
        }
    }

    //AI button: make random move and conclude turn
    public void aiMove(View view) {
        //make copy of board
        lastChessboard = duplicateBoard(chessboard);
        //array of pairs(piece index and destination index)
        List <List<Integer>> generatedMoves = new ArrayList<>();
        for (int i = 0; i < 64; i++){
            Piece curPiece = chessboard[i];
            if (curPiece!=null && curPiece.getOwner() == turn){
                for (int j = 0; j < 64; j++){
                    if (curPiece.isMoveValid(j,chessboard) && notCheckAfterMove(chessboard, i, j)){
                        generatedMoves.add(new ArrayList<>(Arrays.asList(i,j)));
                    }
                }
            }
        }
        int randomMoveIndex = (int)(Math.random()*generatedMoves.size());
        Piece randomPiece = chessboard[generatedMoves.get(randomMoveIndex).get(0)];
        int randomDest = generatedMoves.get(randomMoveIndex).get(1);
        //update move list
        moves.add(randomPiece.getIndex() + "," + randomDest);
        //move piece
        randomPiece.move(randomDest,chessboard);
        //if pawn promotion is being done, choose random promotion
        if (randomPiece instanceof Pawn && (randomPiece.getIndex() < 8 || randomPiece.getIndex() > 55)) {
            //pawn promotion
            int randomLevel = (int) (Math.random() * (promotionLevels.length));
            chessboard[randomDest] = promotionConstructors.get(randomLevel).apply(randomDest,turn);
            //add move to list
            String entry = moves.get(moves.size() - 1);//get current move string
            entry+="," + randomLevel;
            moves.set(moves.size() - 1, entry);
        }
        //change turn, redraw chessboard, evaluate if next guy is screwed
        concludeTurn();
    }

    //undo button: undo last move. does not allow second chance to draw
    public void undoMove(View view) {
        //case: first move ever
        if (turnCount == 0 || undo == turnCount) {return;}
        toggleTurn();
        turnCount--;
        chessboardAdapter.selectedPieceIndex = -1;
        //mark turn that did undo
        undo = turnCount;
        //only remove draw request if turn is where it originated
        if (undo == drawRequest){drawRequest = -2;}
        //pop last move's end and start coord
        moves.remove(moves.size() - 1);
        //update board
        chessboard = lastChessboard;
        chessboardAdapter = new ChessboardAdapter(this,chessboard);
        chessboardView.setAdapter(chessboardAdapter);
        evaluateTurn();
    }

    public void resign(View view) {
        //no saved moves
        if (moves.size() == 0) {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        } else {
            if (turn == 'w') showSavePopup("Black wins"); else showSavePopup("White wins");
        }
    }

    private void returnToMainMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    private King getCurrentKing(Piece[] board){
        for (int i = 0; i < 64; i++){
            Piece curPiece = board[i];
            if (curPiece instanceof King && curPiece.getOwner() == turn){
                return (King)curPiece;
            }
        }
        return null; //shouldnt happen!!!
    }
}