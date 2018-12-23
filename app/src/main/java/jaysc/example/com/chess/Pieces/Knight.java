package jaysc.example.com.chess.Pieces;

public class Knight extends Piece{
    public Knight(int index, char owner){super('N',index,owner);}
    public Knight(Knight k){super(k);}

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //check if dest is in moveset
        if ((Math.abs(this.row-destRow) == 1 && Math.abs(this.column-destCol) == 2)
                ||(Math.abs(this.row-destRow) == 2 && Math.abs(this.column-destCol) == 1)) {
            if (destPiece!=null && destPiece.owner == this.owner) {//if piece in dest, piece must be enemy
                return false;
            }
            return true;
        }
        return false;
    }
}
