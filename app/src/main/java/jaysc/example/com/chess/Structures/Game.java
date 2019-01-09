package jaysc.example.com.chess.Structures;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import jaysc.example.com.chess.Activities.PVPGameActivity;
import jaysc.example.com.chess.Pieces.Bishop;
import jaysc.example.com.chess.Pieces.King;
import jaysc.example.com.chess.Pieces.Knight;
import jaysc.example.com.chess.Pieces.Pawn;
import jaysc.example.com.chess.Pieces.Piece;
import jaysc.example.com.chess.Pieces.Queen;
import jaysc.example.com.chess.Pieces.Rook;

public class Game {
    private char turn;//white('w') or black('b') player
    private int undo;//keeps track of which turn wants to undo
    public static int turnCount;
    private List<String> moves;//list of strings: "start,end(,promotion)"
    private Piece[] lastChessboard;//"snapshot" of last move's chessboard
    private Piece[] chessboard;//main chessboard
    private final static List<BiFunction<Integer,Character,Piece>> promotionConstructors = Arrays.asList(Queen::new, Rook::new, Bishop::new, Knight::new);//constructors for pawn promotions
    public Game(){
        turn = 'w';
        undo = -1;//arbitrary val
        turnCount = 0;
        moves = new ArrayList<>();
        lastChessboard = null;
        //create piece array to hold pieces
        chessboard = initBoard();
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

    private King getCurrentKing(Piece[] board){
        for (int i = 0; i < 64; i++){
            Piece curPiece = board[i];
            if (curPiece instanceof King && curPiece.getOwner() == turn){
                return (King)curPiece;
            }
        }
        return null; //shouldnt happen!!!
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

    public void promotePawn(int selectedPieceIndex, int which, char owner){
        // the user clicked on promotionLevels[which]
        chessboard[selectedPieceIndex] = promotionConstructors.get(which).apply(selectedPieceIndex,owner);
        //add move to list
        String entry = moves.get(moves.size() - 1);//get current move string
        entry+=","+which;
        moves.set(moves.size() - 1, entry);
    }
    //getters
    public Piece[] getChessboard(){
        return chessboard;
    }
    public List<String> getMoves(){
        return moves;
    }
    public int getTurnCount(){
        return turnCount;
    }
    public char getTurn(){
        return turn;
    }
}
