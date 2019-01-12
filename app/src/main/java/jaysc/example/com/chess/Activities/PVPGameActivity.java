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
import jaysc.example.com.chess.Structures.Game;

public class PVPGameActivity extends AppCompatActivity {
    private int drawRequest;//keeps track of which turn wants to draw
    private ChessboardAdapter chessboardAdapter;
    private GridView chessboardView;
    private Game game;
    private final static List<BiFunction<Integer,Character,Piece>> promotionConstructors = Arrays.asList(Queen::new, Rook::new, Bishop::new, Knight::new);//constructors for pawn promotions
    private final static String[] promotionLevels = {"(Q) Queen", "(R) Rook", "(B) Bishop", "(N) Knight"};//used for pawn promotion display
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp_game);
        drawRequest = -2;
        game = new Game();
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, game.getChessboard());
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
        Toast.makeText(PVPGameActivity.this, "White's turn", Toast.LENGTH_LONG).show();
    }

    private void showPawnPopup(final int selectedPieceIndex, final char owner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Promote Pawn...");
        builder.setItems(promotionLevels, (dialog, which) -> {
            game.promotePawn(selectedPieceIndex, which, owner);
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
            List<String> moves = game.getMoves();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.getApplicationContext().openFileOutput(RecordedGamesActivity.PATH, Context.MODE_APPEND | Context.MODE_PRIVATE));
            outputStreamWriter.write(name + "\n");
            outputStreamWriter.write(LocalDate.now().toString() + "\n");
            for (int i = 0; i < moves.size(); i++) {
                outputStreamWriter.write(moves.get(i) + "\n");
            }
            outputStreamWriter.write(RecordedGamesActivity.DIVIDER + "\n");
            outputStreamWriter.close();
        } catch (IOException e) {
            Toast.makeText(PVPGameActivity.this, "file write failed", Toast.LENGTH_LONG).show();
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
    /*
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
    */

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
        if (game.getMoves().size() == 0) {
            returnToMainMenu();
        } else {
            if (game.getTurn() == 'w') showSavePopup("Black wins"); else showSavePopup("White wins");
        }
    }

    private void returnToMainMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}