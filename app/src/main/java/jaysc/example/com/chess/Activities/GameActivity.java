package jaysc.example.com.chess.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import jaysc.example.com.chess.Adapters.ChessboardAdapter;
import jaysc.example.com.chess.R;
import jaysc.example.com.chess.Pieces.*;

public class GameActivity extends AppCompatActivity {
    private char turn;
    private int drawRequest;
    private int undo;
    public static int turnCount;
    private ArrayList<String> moves;
    private Piece[] pieces;
    private King currentKing;
    private King whiteKing;
    private King blackKing;
    private ChessboardAdapter chessboardAdapter;
    private static String[] promotionLevels = {"(Q) Queen", "(R) Rook", "(B) Bishop", "(N) Knight"};//used for pawn promotion display
    private final static List<BiFunction<Integer,Character,Piece>> promotionConstructors = Arrays.asList(
            Queen::new,
            Rook::new,
            Bishop::new,
            Knight::new);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        turn = 'w';
        drawRequest = -1;
        undo = -1;//arbitrary val
        turnCount = 0;
        moves = new ArrayList<>();
        //create piece array to hold pieces
        pieces = new Piece[64];
        //puts pieces in original spot
        resetBoard(pieces);
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, pieces);
        //fetch gridview from xml variable name
        chessboardAdapter.selectedPieceIndex = -1;
        GridView gridview = findViewById(R.id.chessboard);
        //connect gridview to its adapter
        gridview.setAdapter(chessboardAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //add piece to selected var?
                if (pieces[position] != null && pieces[position].getOwner() == turn) {
                    //setting selectedpiece only if it is owned by player
                    chessboardAdapter.selectedPieceIndex = position;
                } else if (chessboardAdapter.selectedPieceIndex != -1) {
                    //entertains move if there is a selectedpiece and clicked destination
                    Piece selectedPiece = pieces[chessboardAdapter.selectedPieceIndex];

                    if (selectedPiece.isMoveValid(position, pieces)
                            && !checkAfterMove(pieces, selectedPiece.getIndex(), position)) {//MOVE IS VALID
                        //add move to list
                        moves.add(selectedPiece.getIndex() + "," + position);
                        //move piece
                        selectedPiece.move(position, pieces);
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
            }
        });
        //initial currentKing
        currentKing = whiteKing;
        //initial message
        Toast.makeText(GameActivity.this, "White's turn", Toast.LENGTH_LONG).show();
    }

    private void resetBoard(Piece[] board) {
        //black pieces
        board[0] = new Rook(0, 'b');
        board[1] = new Knight(1, 'b');
        board[2] = new Bishop(2, 'b');
        board[3] = new Queen(3, 'b');
        blackKing = new King(4, 'b');
        board[4] = blackKing;
        board[5] = new Bishop(5, 'b');
        board[6] = new Knight(6, 'b');
        board[7] = new Rook(7, 'b');
        //black pawns
        for (int i = 8; i < 16; i++) {
            board[i] = new Pawn(i, 'b');
        }
        //blank squares
        for (int i = 16; i < 48; i++) {
            board[i] = null;
        }
        //white pawns
        for (int i = 48; i < 56; i++) {
            board[i] = new Pawn(i, 'w');
        }
        //white pieces
        board[56] = new Rook(56, 'w');
        board[57] = new Knight(57, 'w');
        board[58] = new Bishop(58, 'w');
        board[59] = new Queen(59, 'w');
        whiteKing = new King(60, 'w');
        board[60] = whiteKing;
        board[61] = new Bishop(61, 'w');
        board[62] = new Knight(62, 'w');
        board[63] = new Rook(63, 'w');
    }

    //switches turn, redraws chessboard, sees if next player is screwed
    private void concludeTurn() {
        toggleTurn();
        //only reset drawrequest if it was not done on this turn
        if (drawRequest != turnCount) {drawRequest = -1;}
        turnCount++;
        chessboardAdapter.notifyDataSetChanged();
        evaluateTurn();
    }

    private void toggleTurn() {
        if (turn == 'w') {
            turn = 'b';
            currentKing = blackKing;
        } else {
            turn = 'w';
            currentKing = whiteKing;
        }
    }
    private void evaluateTurn() {
        //check if next guy is in trouble
        if (currentKing == null){return;}
        if (noSafeMoves()) {//stalemate or checkmate for next guy
            String popupMessage = "";
            if (currentKing.inCheck(pieces)) {//checkmate
                if (turn == 'w') {
                    //Toast.makeText(GameActivity.this, "Checkmate, Black wins", Toast.LENGTH_LONG).show();
                    popupMessage = "Checkmate, Black wins";
                } else if (turn == 'b') {
                    //Toast.makeText(GameActivity.this, "Checkmate, White wins", Toast.LENGTH_LONG).show();
                    popupMessage = "Checkmate, White wins";
                }
            } else {//stalemate
                //Toast.makeText(GameActivity.this, "Stalemate", Toast.LENGTH_LONG).show();
                popupMessage = "Stalemate";
            }
            //END GAMMMMEEEEE HEEERRREEE
            showSavePopup(popupMessage);
        } else {//normal move but next guy may be in check...
            if (currentKing.inCheck(pieces)) {
                if (turn == 'w') {
                    if (drawRequest != -1) {//also draw
                        Toast.makeText(GameActivity.this, "White's turn, CHECK, Black offers DRAW", Toast.LENGTH_LONG).show();
                    } else {//only check
                        Toast.makeText(GameActivity.this, "White's turn, CHECK", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (drawRequest != -1) {//also draw
                        Toast.makeText(GameActivity.this, "Black's turn, CHECK, White offers DRAW", Toast.LENGTH_LONG).show();
                    } else {//only check
                        Toast.makeText(GameActivity.this, "Black's turn, CHECK", Toast.LENGTH_LONG).show();
                    }
                }
            } else {//normal move but may be draw request sent
                if (turn == 'w') {
                    if (drawRequest != -1) {//draw request sent!
                        Toast.makeText(GameActivity.this, "White's turn, Black offers DRAW", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GameActivity.this, "White's turn", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (drawRequest != -1) {//draw request sent!
                        Toast.makeText(GameActivity.this, "Black's turn, White offers DRAW", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GameActivity.this, "Black's turn", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private boolean noSafeMoves() {
        for (int i = 0; i < 64; i++) {
            Piece curPiece = pieces[i];
            //if there's a piece that current player owns
            if (curPiece != null && curPiece.getOwner() == turn) {
                for (int j = 0; j < 64; j++) {
                    //current piece is able to move somewhere
                    if (curPiece.isMoveValid(j, pieces)) {
                        //check if move takes king out of check
                        if (!checkAfterMove(pieces, i, j)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean checkAfterMove(Piece[] board, int startIndex, int endIndex) {
        Piece[] b = duplicateBoard(board);
        King k = (King)(b[currentKing.getIndex()]);
        //get piece of hypothetical pieces
        Piece chosenPiece = b[startIndex];
        //move this piece
        chosenPiece.move(endIndex, b);
        //return if king is in check or not
        return k.inCheck(b);
    }

    private Piece[] duplicateBoard(Piece[] p) {
        //THIS MAKES A SEPARATE CHESSBOARD
        final Piece[] result = new Piece[64];
        for (int i = 0; i < 64; i++) {
            if (p[i] == null) {
                result[i] = null;
            } else if (p[i] instanceof Pawn) {
                result[i] = new Pawn((Pawn) (p[i]));
            } else if (p[i] instanceof Rook) {
                result[i] = new Rook((Rook) (p[i]));
            } else if (p[i] instanceof Knight) {
                result[i] = new Knight((Knight) (p[i]));
            } else if (p[i] instanceof Bishop) {
                result[i] = new Bishop((Bishop) (p[i]));
            } else if (p[i] instanceof Queen) {
                result[i] = new Queen((Queen) (p[i]));
            } else if (p[i] instanceof King) {
                result[i] = new King((King) (p[i]));
            }
        }
        return result;
    }

    private void showPawnPopup(final int selectedPieceIndex, final char owner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Promote Pawn...");
        builder.setItems(promotionLevels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on promotionLevels[which]
                pieces[selectedPieceIndex] = promotionConstructors.get(which).apply(selectedPieceIndex,owner);
                //add move to list
                String entry = moves.get(moves.size() - 1);//get current move string
                entry+=","+promotionLevels[which].charAt(1);
                moves.set(moves.size() - 1, entry);
                chessboardAdapter.notifyDataSetChanged();
            }
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if EditText is empty disable closing on possitive button
                if (!(input.getText().toString().trim().isEmpty())) {//do something if edittext not empty
                    String name = input.getText().toString();//set name
                    //write file to disk
                    writeToFile(name);
                }
                //return to main menu
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //return to main menu
                finish();
                dialog.cancel();
            }
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
        if (drawRequest == -1) {
            drawRequest = turnCount;
        } else if (drawRequest == turnCount - 1) {//previous turn already wants to draw
            //DRAW!!!
            turnCount++;
            showSavePopup("Draw");
        }
    }

    //AI button: make random move and conclude turn
    public void aiMove(View view) {
        //array of pairs(piece index and destination index)
        ArrayList <ArrayList<Integer>> generatedMoves = new ArrayList<>();
        for (int i = 0; i < 64; i++){
            Piece curPiece = pieces[i];
            if (curPiece!=null && curPiece.getOwner() == turn){
                for (int j = 0; j < 64; j++){
                    if (curPiece.isMoveValid(j,pieces) && !checkAfterMove(pieces,i,j)){
                        generatedMoves.add(new ArrayList<Integer>(Arrays.asList(i,j)));
                    }
                }
            }
        }
        int randomMoveIndex = (int)(Math.random()*generatedMoves.size());
        Piece randomPiece = pieces[generatedMoves.get(randomMoveIndex).get(0)];
        int randomDest = generatedMoves.get(randomMoveIndex).get(1);
        //update move list
        moves.add(randomPiece.getIndex() + "," + randomDest);
        //move piece
        randomPiece.move(randomDest,pieces);
        //if pawn promotion is being done, choose random promotion
        if (randomPiece instanceof Pawn && (randomPiece.getIndex() < 8 || randomPiece.getIndex() > 55)) {
            //pawn promotion
            int randomLevel = (int) (Math.random() * (promotionLevels.length));
            pieces[randomDest] = promotionConstructors.get(randomLevel).apply(randomDest,turn);
            //add move to list
            String entry = moves.get(moves.size() - 1);//get current move string
            entry+=","+promotionLevels[randomLevel].charAt(1);
            moves.set(moves.size() - 1, entry);
        }
        //change turn, redraw chessboard, evaluate if next guy is screwed
        concludeTurn();
    }

    //undo button: undo last move. does not allow second chance to draw
    public void undoMove(View view) {
        //case: first move ever
        if (turnCount == 0 || undo == turnCount) {
            return;
        }
        //change turn
        toggleTurn();
        //update turncount
        turnCount--;
        chessboardAdapter.selectedPieceIndex = -1;
        drawRequest = -1;
        //mark turn that did undo
        undo = turnCount;
        //pop last move's end and start coord
        moves.remove(moves.size() - 1);
        //update board
        resetBoard(pieces);
        for (int i = 0; i < moves.size(); i++) {
            String curMove = moves.get(i);
            String args[] = curMove.split(Pattern.quote(","));
            //move piece like normal
            int start = Integer.parseInt(args[0]);
            int end = Integer.parseInt(args[1]);

            pieces[start].move(end, pieces);
            //pawn promotion
            if (args.length == 3) {
                char type = args[2].charAt(0);
                char owner = (i%2==0) ? 'w' : 'b';
                int constrIndex = IntStream.range(0,promotionLevels.length)
                        .filter(k->type == promotionLevels[k].charAt(1))
                        .limit(1)
                        .sum();
                pieces[end] = promotionConstructors.get(constrIndex).apply(end,owner);
            }
        }
        chessboardAdapter.notifyDataSetChanged();
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
}