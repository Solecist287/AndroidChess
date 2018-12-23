package jaysc.example.com.chess.Pieces;


public class Queen extends Piece{

    public Queen(int index, char owner){super('Q',index,owner);}
    public Queen(Queen q) {super(q);}

    public boolean isMoveValid(int destIndex,Piece[]board){//knows if a piece is in destination coord
        return new Rook(index, this.owner).isMoveValid(destIndex,board) ||
                new Bishop(index, this.owner).isMoveValid(destIndex,board);
    }
}
