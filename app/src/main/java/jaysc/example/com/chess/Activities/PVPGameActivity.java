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

public class PVPGameActivity extends GameActivity {
    private int drawRequest;//keeps track of which turn wants to draw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawRequest = -2;
        //things initialized in super
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

    public void resign(View view){super.resign(view);}

    protected void evaluateTurn() {
        King currentKing = getCurrentKing(chessboard);
        String message;
        //check if next guy is in trouble
        if (noSafeMoves()) {//stalemate or checkmate for next guy
            if (currentKing!=null && currentKing.inCheck(chessboard)) {//checkmate
                message = (turn == 'w')?"Checkmate, Black wins":"Checkmate, White wins";
            } else {//stalemate
                //Toast.makeText(PVPGameActivity.this, "Stalemate", Toast.LENGTH_LONG).show();
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
            Toast.makeText(PVPGameActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    //id funcs
    public int getChessboardId(){return R.id.pvp_chessboard;}
    public int getViewId(){return R.layout.activity_pvp_game;}
}