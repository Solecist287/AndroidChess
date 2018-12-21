package jaysc.example.com.chess.Pieces;

public class Rook extends Piece{

    public Rook(int index, char owner){
        super('R',index,owner);
    }

    public Rook(Rook r) {
        super(r);
    }

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //if destination has a piece, then destpiece and currentpiece must be on different teams
        if (destPiece!=null && destPiece.owner == this.owner) {
            //System.out.println("cant kill yer own piece");
            return false;
        }
        //check if dest is in moveset of piece
        if (this.row == destRow) {//dest in same row
            //check spaces in between for obstructions
            for (int i = Math.min(destCol, this.column)+1; i < Math.max(destCol, this.column); i++) {
                if (board[(this.row*8)+i]!=null) {//piece obstructing
                    //System.out.println("row: piece obstructing at: ("+this.row+","+i+")");
                    return false;
                }
            }
            return true;
        }else if (this.column == destCol) {//dest in same row
            //check spaces in between for obstructions
            for (int i = Math.min(destRow, this.row)+1; i < Math.max(destRow, this.row); i++) {
                if (board[(i*8)+this.column]!=null) {//piece obstructing
                    //System.out.println("col: piece obstructing at: ("+i+","+this.column+")");
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
