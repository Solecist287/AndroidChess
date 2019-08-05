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
        //check if dest is in moveset of piece
        if (row == destRow) {//dest in same row
            //check spaces in between for obstructions
            for (int i = Math.min(destCol, column)+1; i < Math.max(destCol, column); i++) {
                if (board[(row*8)+i]!=null) {//piece obstructing
                    return false;
                }
            }
            return true;
        }else if (column == destCol) {//dest in same col
            //check spaces in between for obstructions
            for (int i = Math.min(destRow, row)+1; i < Math.max(destRow, row); i++) {
                if (board[(i*8)+column]!=null) {//piece obstructing
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public int getImageIndex(){
        if (owner == 'b') return 10; else return 11;
    }
    @Override
    public Piece makeCopy() {
        return new Rook(this);
    }
}