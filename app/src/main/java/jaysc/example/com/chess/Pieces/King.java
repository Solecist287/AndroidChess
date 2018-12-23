package jaysc.example.com.chess.Pieces;

public class King extends Piece{
    public King(int index, char owner){super('K',index,owner);}
    public King(King k) {super(k);}

    public boolean isMoveValid(int destIndex,Piece[]board){
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //used for possible castling (knowing which direction)
        int rowDiff = this.row - destRow;
        int colDiff = this.column - destCol;

        //find out if dest is in king moveset
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        if ((absRowDiff == 1 && absColDiff == 1)||
                (absRowDiff == 1 && absColDiff == 0)||
                (absRowDiff == 0 && absColDiff == 1)) {
            //if piece exists and on same team, return false
            if (destPiece!=null && destPiece.owner == this.owner) {
                //System.out.println("cant kill yer own piece");
                return false;
            }
            return true;
        }else if (absRowDiff==0 && absColDiff==2 && this.moves == 0) {//CASTLINGGGGGG: same row, two cols away
            //piece must exist and be rook
            Piece rook = null;
            //test if king's start position, in between, and dest are in check
            for (int i = Math.min(this.column,destCol); i <= Math.max(this.column,destCol); i++) {
                //if any position from start to end is in check OR if there is obstructing piece(besides king lol)
                if (positionInCheck(this.row,i,board) || (board[(this.row*8)+i] != null && i!=this.column)) {
                    return false;
                }
            }
            //store past and future rook position (only need column since same row as king)
            if (colDiff > 0) {//left rook castling for white, right rook castling for black
                rook = board[(this.row*8)+0];//could be a rook, gotta check
            }else if (colDiff < 0) {//right rook castling for white, left rook castling for black
                rook = board[(this.row*8)+7];
            }
            //[can assume that king never moved and no places in check]
            //if there is a rook who is ours and never moved(including our king)
            if (rook!=null && rook instanceof Rook && rook.owner == this.owner && rook.moves == 0) {
                //change rook and king position
                //System.out.println("Castling success!!!");
                return true;
            }
        }
        return false;
    }

    public boolean positionInCheck(int row, int column, Piece[]board) {
        for (int i = 0; i < 64; i++) {
            Piece curPiece = board[i];
            //if enemy piece exists and can make a move (attack) on king position
            if (curPiece!=null && curPiece.getOwner()!=this.owner && curPiece.isMoveValid(index,board)) {
                return true;
            }
        }
        return false;
    }

    public boolean inCheck(Piece[]board) {
        return positionInCheck(this.row, this.column,board);
    }

    public void move(int destIndex,Piece[]board) {
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //used for possible castling (knowing which direction)
        int rowDiff = this.row - destRow;
        int colDiff = this.column - destCol;

        //find out if dest is in king moveset
        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        if (absRowDiff==0 && absColDiff==2 && this.moves == 0) {//castling
            if (colDiff > 0) {//left rook castling for white, right rook castling for black
                board[(this.row*8)+0].move((this.row*8)+destCol+1,board);

            }else if (colDiff < 0) {//right rook castling for white, left rook castling for black
                board[(this.row*8)+7].move((this.row*8)+destCol-1,board);
            }
        }
        //do normal stuff for move
        super.move(destIndex,board);
    }
}
