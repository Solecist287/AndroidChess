package jaysc.example.com.chess.Pieces;


public class Queen extends Piece{

    public Queen(int index, char owner){super(index,owner);}
    private Queen(Queen q) {super(q);}

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        return new Rook(index, owner).isMoveValid(destIndex,board) ||
                new Bishop(index, owner).isMoveValid(destIndex,board);
    }
    public int getImageIndex(){
        if (owner == 'b') return 8; else return 9;
    }
    public Piece makeCopy() {
        return new Queen(this);
    }
}