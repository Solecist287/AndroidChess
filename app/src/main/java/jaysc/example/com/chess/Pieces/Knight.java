package jaysc.example.com.chess.Pieces;

public class Knight extends Piece{
    public Knight(int index, char owner){super(index,owner);}
    private Knight(Knight k){super(k);}

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        Piece destPiece = board[destIndex];
        if (destPiece!=null && destPiece.owner == owner) {//if piece in dest, piece must be enemy
            return false;
        }
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        int absRowDiff = Math.abs(row-destRow);
        int absColDiff = Math.abs(column-destCol);
        //check if dest is in moveset
        return (absRowDiff == 1 && absColDiff == 2) || (absRowDiff == 2 && absColDiff == 1);
    }
    public int getImageIndex(){
        if (owner == 'b') return 4; else return 5;
    }
    public Piece makeCopy() {
        return new Knight(this);
    }
}