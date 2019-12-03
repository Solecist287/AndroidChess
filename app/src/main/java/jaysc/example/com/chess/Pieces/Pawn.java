package jaysc.example.com.chess.Pieces;

import jaysc.example.com.chess.Activities.GameActivity;

public class Pawn extends Piece{
    private int moved_2_spaces; //set equal to turnCount whenever a pawn moves 2 spaces forward

    public Pawn(int index, char owner){
        super(index,owner);
        moved_2_spaces = -1;
    }

    private Pawn(Pawn p) {
        super(p);
        moved_2_spaces = p.moved_2_spaces;
    }
    //funcs
    public boolean isMoveValid(int destIndex,Piece[]board){ //knows if a piece is in destination coordinate
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];

        //find differences between rows and columns
        int rowDiff = row - destRow;//x
        int colDiff = column - destCol;//y

        //cannot capture own piece
        if (destPiece != null && destPiece.owner == owner) {
            return false;
        }
        int sign = owner == 'w' ? 1 : -1;//1 for white, -1 for black
        //pawn may move exactly one space forward
        if (destRow == row - (sign) && destCol == column && destPiece == null) {
            return true;
        }
        //pawn may move exactly two spaces forward
        else if (destRow == row - (2*sign) && destCol == column && board [((row - (sign))*8)+ column] == null
                    && destPiece == null && moves==0) {
                return true;
        }
        //pawn may move exactly one space diagonally in either forward direction (must capture or en passant)
        else if (rowDiff == (sign) && Math.abs(colDiff) == 1 && destPiece != null && owner!=destPiece.owner) { //direct capture
            return true;
        }else if (rowDiff == (sign) && Math.abs(colDiff) == 1 && destPiece == null) { //en passant
            return board[((destRow + (sign)) * 8) + destCol] != null
                    && board[((destRow + (sign)) * 8) + destCol] instanceof Pawn
                    && board[((destRow + (sign)) * 8) + destCol].moves == 1
                    && ((Pawn) board[((destRow + (sign)) * 8) + destCol]).moved_2_spaces == GameActivity.turnCount - 1;
        }
        return false;
    }

    public void move(int destIndex,Piece[]board) {
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];

        //find differences between rows and columns
        int rowDiff = row - destRow;//x
        int colDiff = column - destCol;//y

        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1 && destPiece == null) {//en passant
            board[(row*8)+destCol] = null;
        }else if (Math.abs(rowDiff) == 2) {//two spaces
            moved_2_spaces = GameActivity.turnCount;
        }
        //do normal move
        super.move(destIndex,board);
    }
    public int getImageIndex(){
        if (owner == 'b') return 6; else return 7;
    }
    public Piece makeCopy() {
        return new Pawn(this);
    }
}