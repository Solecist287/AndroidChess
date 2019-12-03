package jaysc.example.com.chess.Pieces;

public class Rook extends Piece{

    public Rook(int index, char owner){super(index,owner);}
    private Rook(Rook r) {super(r);}

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //if destination has a piece, then destpiece and currentpiece must be on different teams
        if (destPiece!=null && destPiece.owner == owner) {return false;}
        //destination can only differ by row or col, not both
        if (row != destRow && column != destCol){return false;}
        //increments to use to search squares between start and dest
        int colIncr = column != destCol ? 1 : 0;
        int rowIncr = row != destRow ? 1 : 0;
        //row and col loop vars, start at square ahead of start
        int r = Math.min(row, destRow) + rowIncr;
        int c = Math.min(column, destCol) + colIncr;
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
        if (owner == 'b') return 10; else return 11;
    }
    @Override
    public Piece makeCopy() {
        return new Rook(this);
    }
}