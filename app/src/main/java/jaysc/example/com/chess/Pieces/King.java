package jaysc.example.com.chess.Pieces;

public class King extends Piece{
    public King(int index, char owner){super(index,owner);}
    private King(King k) {super(k);}

    public boolean isMoveValid(int destIndex,Piece[]board){
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //used for possible castling (knowing which direction)
        int rowDiff = row - destRow;
        int colDiff = column - destCol;

        //find out if dest is in king moveset
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        //cannot attack own piece or move to itself
        if (destPiece!=null && destPiece.owner == owner) {
            return false;
        }

        if (!(absRowDiff == 0 && absColDiff == 0) && absRowDiff <= 1 && absColDiff <= 1){
            return true;
        }
        if (absRowDiff==0 && absColDiff==2 && moves == 0) {//CASTLINGGGGGG: same row, two cols away
            //test if king's start position, in between, and dest are in check
            for (int i = Math.min(column,destCol); i <= Math.max(column,destCol); i++) {
                //if any position from start to end is in check OR if there is obstructing piece(besides king lol)
                if (positionInCheck(row,i,board) || (board[(row*8)+i] != null && i!=column)) {
                    return false;
                }
            }
            Piece rook = colDiff > 0 ? board[row*8] : board[(row*8)+7];//possibly castling rook
            //return if a stationary king has an existent, stationary rook
            return rook != null && rook.owner == this.owner && rook instanceof Rook && rook.moves == 0;
        }
        return false;
    }

    private boolean positionInCheck(int row, int column, Piece[]board) {
        for (int i = 0; i < 64; i++) {
            Piece curPiece = board[i];
            //if enemy piece exists and can make a move (attack) on king position
            if (curPiece!=null && curPiece.getOwner()!=owner && curPiece.isMoveValid((row*8)+column,board)) {
                return true;
            }
        }
        return false;
    }

    public boolean inCheck(Piece[]board) {
        return positionInCheck(row, column,board);
    }

    public void move(int destIndex,Piece[]board) {
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        //used for possible castling (knowing which direction)
        int rowDiff = row - destRow;
        int colDiff = column - destCol;

        //find out if dest is in king moveset
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        if (absRowDiff==0 && absColDiff==2 && moves == 0) {//castling
            if (colDiff > 0) {//left rook castling for white, right rook castling for black
                board[row*8].move((row*8)+destCol+1,board);

            }else if (colDiff < 0) {//right rook castling for white, left rook castling for black
                board[(row*8)+7].move((row*8)+destCol-1,board);
            }
        }
        //do normal stuff for move
        super.move(destIndex,board);
    }
    public int getImageIndex(){
        if (owner == 'b') return 2; else return 3;
    }
    public Piece makeCopy() {
        return new King(this);
    }
}