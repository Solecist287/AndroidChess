package jaysc.example.com.chess.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jaysc.example.com.chess.Adapters.ChessboardAdapter;
import jaysc.example.com.chess.Pieces.King;
import jaysc.example.com.chess.Pieces.Pawn;
import jaysc.example.com.chess.Pieces.Piece;
import jaysc.example.com.chess.R;

public class PVCGameActivity extends GameActivity {
    public static String PLAYER = "player";
    private char player,computer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player = getIntent().getCharExtra(PLAYER,'w');
        computer = (player == 'w')?'b':'w';
        //Toast.makeText(PVCGameActivity.this, "player is "+player, Toast.LENGTH_LONG).show();
        //initial computer turn if computer is white
        if (computer == 'w'){computerMove();}

        chessboardView.setOnItemClickListener((parent, v, position, id) -> {
            //prevent interaction with board if computer's turn
            if (turn!=player){return;}
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
                    computerMove();
                }
                //clear selectedpieceindex
                chessboardAdapter.selectedPieceIndex = -1;
            }
            chessboardAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void undoMove(View view) {
        //case: first move ever
        if (turnCount == 0 || undo == turnCount || turn == computer) {return;}
        turnCount-=2;
        chessboardAdapter.selectedPieceIndex = -1;
        //mark turn that did undo
        undo = turnCount;
        //pop last move's end and start coord
        moves.remove(moves.size() - 1);
        moves.remove(moves.size() - 1);
        //update board
        chessboard = lastChessboard;
        chessboardAdapter = new ChessboardAdapter(this,chessboard);
        chessboardView.setAdapter(chessboardAdapter);
        evaluateTurn();
    }

    @Override
    public void resign(View view) {
        super.resign(view);
    }

    //AI button: make random move and conclude turn
    private void computerMove() {
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
        //computer reached a stalemate or checkmate
        if (generatedMoves.size() == 0){return;}
        //otherwise proceed with making the move and recording it
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
            Toast.makeText(PVCGameActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getChessboardId() {
        return R.id.pvc_chessboard;
    }

    @Override
    public int getViewId() {
        return R.layout.activity_pvc_game;
    }

}
