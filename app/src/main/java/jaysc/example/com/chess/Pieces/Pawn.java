package jaysc.example.com.chess.Pieces;

import jaysc.example.com.chess.Activities.GameActivity;

public class Pawn extends Piece{

    protected int moved_2_spaces; //set equal to turnCount whenever a pawn moves 2 spaces forward
    protected boolean enPassant;//true so move() can delete victim and set back to false
    protected int ep_row;//en passant victim row
    protected int ep_col;//en passant victim col

    public Pawn(int index, char owner){
        super('p',index,owner);
        moved_2_spaces = -1;
        enPassant = false;
        ep_row = -1;
        ep_col = -1;
    }

    public Pawn(Pawn p) {
        super(p);
        moved_2_spaces = p.moved_2_spaces;
        enPassant = p.enPassant;
        ep_row = p.ep_row;
        ep_col = p.ep_col;
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

        if (this.owner == 'w' && moves == 0) {
            //pawn may move exactly one space forward
            if (destRow == this.row - 1 && destCol == this.column && destPiece == null) {
                return true;
            }

            //pawn may move exactly two spaces forward
            if (destRow == this.row - 2 && destCol == this.column && board [((this.row - 1)*8)+this.column] == null && destPiece == null) {
                moved_2_spaces = GameActivity.turnCount;
                return true;
            }

            //pawn may move exactly one space diagonally in either forward direction (must capture)
            if (rowDiff == 1 && Math.abs(colDiff) == 1 && destPiece != null && destPiece.owner == 'b') {
                return true;
            }

        } else if (this.owner == 'w' && moves != 0) {
            //pawn may move exactly one space forward
            if (destRow == this.row - 1 && destCol == this.column && destPiece == null) {
                return true;
            }

            //pawn may move exactly one space diagonally in either forward direction (must capture or en passant)
            else if (rowDiff == 1 && Math.abs(colDiff) == 1 && destPiece != null && destPiece.owner == 'b') { //direct capture
                return true;
            } else if (rowDiff == 1 && colDiff == 1 && destPiece == null) { //en passant capturing white's left
//				board[destRow + 1][destCol]
                if (board[((destRow + 1)*8)+destCol] != null && board[((destRow + 1)*8)+destCol] instanceof Pawn && board[((destRow + 1)*8)+destCol].moves == 1 && ((Pawn)board[((destRow + 1)*8)+destCol]).moved_2_spaces == GameActivity.turnCount - 1) {
                    //board[((destRow + 1)*8)+destCol] = null;

                    ep_row = destRow + 1;
                    ep_col = destCol;
                    enPassant = true;
                    return true;
                }
            } else if (rowDiff == 1 && colDiff == -1  && destPiece == null) { //en passant capturing white's right
//				board[destRow + 1][destCol]
                if (board[((destRow + 1)*8)+destCol] != null && board[((destRow + 1)*8)+destCol] instanceof Pawn && board[((destRow + 1)*8)+destCol].moves == 1 && ((Pawn)board[((destRow + 1)*8)+destCol]).moved_2_spaces == GameActivity.turnCount - 1) {
                    //board[((destRow + 1)*8)+destCol] = null;

                    ep_row = destRow + 1;
                    ep_col = destCol;
                    enPassant = true;
                    return true;
                }
            }
        } else if (this.owner == 'b' && moves == 0) {
            //pawn may move exactly one space forward
            if (destRow == this.row + 1 && destCol == this.column && destPiece == null) {
                return true;
            }

            //pawn may move exactly two spaces forward
            if (destRow == this.row + 2 && destCol == this.column && board [((this.row + 1)*8)+this.column] == null && destPiece == null) {
                moved_2_spaces = GameActivity.turnCount;
                return true;
            }

            //pawn may move exactly one space diagonally in either forward direction (must capture)
            if (rowDiff == -1 && Math.abs(colDiff) == 1 && destPiece != null && destPiece.owner == 'w') {
                return true;
            }

        } else if (this.owner == 'b' && moves != 0) {
            //pawn may move exactly one space forward
            if (destRow == this.row + 1 && destCol == this.column && destPiece == null) {
                return true;
            }

            //pawn may move exactly one space diagonally in either forward direction (must capture or en passant)
            else if (rowDiff == -1 && Math.abs(colDiff) == 1 && destPiece != null && destPiece.owner == 'w') { //direct capture
                return true;
            } else if (rowDiff == -1 && colDiff == 1 && destPiece == null) { //en passant capturing black's right
//				board[destRow - 1][destCol]
                if (board[((destRow - 1)*8)+destCol] != null && board[((destRow - 1)*8)+destCol] instanceof Pawn && board[((destRow - 1)*8)+destCol].moves == 1 && ((Pawn)board[((destRow - 1)*8)+destCol]).moved_2_spaces == GameActivity.turnCount - 1) {
                    //board[((destRow - 1)*8)+destCol] = null;

                    ep_row = destRow - 1;
                    ep_col = destCol;
                    enPassant = true;
                    return true;
                }
            } else if (rowDiff == -1 && colDiff == -1 && destPiece == null) {//en passant capturing black's left
//				board[destRow - 1][destCol]
                if (board[((destRow - 1)*8)+destCol] != null && board[((destRow - 1)*8)+destCol] instanceof Pawn && board[((destRow - 1)*8)+destCol].moves == 1 && ((Pawn)board[((destRow - 1)*8)+destCol]).moved_2_spaces == GameActivity.turnCount - 1) {
                    //board[((destRow - 1)*8)+destCol] = null;

                    ep_row = destRow - 1;
                    ep_col = destCol;
                    enPassant = true;
                    return true;
                }
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
            //revert values
            ep_row = -1;
            ep_col = -1;
            enPassant = false;
        }
        //do normal move
        super.move(destIndex,board);
    }
}
