package jaysc.example.com.chess.Pieces;

import jaysc.example.com.chess.Activities.GameActivity;

public class Pawn extends Piece{

    protected int moved_2_spaces; //set equal to turnCount whenever a pawn moves 2 spaces forward

    public Pawn(int index, char owner){
        super('p',index,owner);
        moved_2_spaces = -1;
    }

    public Pawn(Pawn p) {
        super(p);
        moved_2_spaces = p.moved_2_spaces;
    }
    //funcs
    public boolean isMoveValid(int destIndex,Piece[]board){ //knows if a piece is in destination coordinate
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];

        //find differences between rows and columns
        int rowDiff = this.row - destRow;//x
        int colDiff = this.column - destCol;//y

        //cannot capture own piece
        if (destPiece != null && destPiece.owner == this.owner) {
            return false;
        }
        int sign;//1 for white, -1 for black
        if (owner == 'w')sign=1; else sign=-1;
        //pawn may move exactly one space forward
        if (destRow == this.row - (1*sign) && destCol == this.column && destPiece == null) {
            return true;
        }
        //pawn may move exactly two spaces forward
        else if (destRow == this.row - (2*sign) && destCol == this.column && board [((this.row - (1*sign))*8)+this.column] == null
                    && destPiece == null && moves==0) {
                return true;
        }
        //pawn may move exactly one space diagonally in either forward direction (must capture or en passant)
        else if (rowDiff == (1*sign) && Math.abs(colDiff) == 1 && destPiece != null && owner!=destPiece.owner) { //direct capture
            return true;
        }else if (rowDiff == (1*sign) && Math.abs(colDiff) == 1 && destPiece == null) { //en passant capturing white's left
            if (board[((destRow + (1*sign))*8)+destCol] != null && board[((destRow + (1*sign))*8)+destCol] instanceof Pawn && board[((destRow + (1*sign))*8)+destCol].moves == 1 && ((Pawn)board[((destRow + (1*sign))*8)+destCol]).moved_2_spaces == GameActivity.turnCount - 1) {
                return true;
            }
        }
        return false;
    }

    public void move(int destIndex,Piece[]board) {
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];

        //find differences between rows and columns
        int rowDiff = this.row - destRow;//x
        int colDiff = this.column - destCol;//y

        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1 && destPiece == null) {//en passant
            board[(this.row*8)+destCol] = null;
        }else if (Math.abs(rowDiff) == 2) {//two spaces
            moved_2_spaces = GameActivity.turnCount;
        }
        //do normal move
        super.move(destIndex,board);
    }
}
