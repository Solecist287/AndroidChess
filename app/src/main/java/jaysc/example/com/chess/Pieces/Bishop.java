package jaysc.example.com.chess.Pieces;

public class Bishop extends Piece{

    public Bishop(int index, char owner){super(index,owner);}
    private Bishop(Bishop b) {super(b);}

    public boolean isMoveValid(int destIndex, Piece[]board){//knows if a piece is in destination coord
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //also checks if dest is diagonal and occupied by fellow piece
        if (Math.abs(row - destRow) != Math.abs(column - destCol) || (destPiece!=null && destPiece.owner == owner)) {
            return false;
        }
        //increments to use to search squares between start and dest
        int rowIncr = 1;
        int colIncr = Math.min(index, destIndex)%8 < Math.max(index, destIndex)%8 ? 1 : -1;
        //row and col loop vars, start at square ahead of start
        int r = Math.min(index, destIndex)/8 + rowIncr;
        int c = Math.min(index, destIndex)%8 + colIncr;
        //check if spaces between self and dest are empty
        while (r * 8 + c < Math.max(index, destIndex)){
            if (board[r * 8 + c] != null){
                return false;
            }
            r += rowIncr;
            c += colIncr;
        }
        return true;
    }
    public int getImageIndex(){
        if (owner == 'b') return 0; else return 1;
    }
    public Piece makeCopy() {
        return new Bishop(this);
    }
}