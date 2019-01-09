package jaysc.example.com.chess.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jaysc.example.com.chess.Adapters.ChessboardAdapter;
import jaysc.example.com.chess.R;
import jaysc.example.com.chess.Pieces.*;

public class PVPGameActivity extends GameActivity {
    protected int drawRequest;//keeps track of which turn wants to draw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //have pvp xml
        setContentView(R.layout.activity_pvp_game);
        drawRequest = -2;
        //have listener/game loop for two people...i think doesnt change
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, chessboard);
        //fetch gridview from xml variable name
        chessboardAdapter.selectedPieceIndex = -1;
        chessboardView = findViewById(R.id.chessboard);
        //connect gridview to its adapter
        chessboardView.setAdapter(chessboardAdapter);
        //touch logic and game loop
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

    //switches turn, redraws chessboard, sees if next player is screwed
    protected void concludeTurn() {
        toggleTurn();
        turnCount++;
        chessboardAdapter.notifyDataSetChanged();
        evaluateTurn();
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

}