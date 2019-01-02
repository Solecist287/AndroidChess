package jaysc.example.com.chess.Pieces;


public abstract class Piece{

    int row, column, index;
    char owner; //can be 'w' or 'b' AKA white or black
    int moves;

    public Piece(int index, char owner){
        this.index = index;
        this.row = index/8;
        this.column = index%8;
        this.owner = owner;
        moves = 0;
    }

    public Piece(Piece p) {
        column = p.column;
        row = p.row;
        index = p.index;
        owner = p.owner;
        moves = p.moves;
    }
    //funcs
    public abstract boolean isMoveValid(int destIndex, Piece[]board);//knows if a piece is in destination coord
    public abstract int getImageIndex();
    public abstract Piece makeCopy();
    public void move(int destIndex, Piece[]board) {
        //clear piece's prev location
        board[index] = null;
        //update piece position vars
        index = destIndex;
        //translate 2d coord to 1d
        row = index/8;
        column = index%8;
        //update board's vars for moved piece
        board[index] = this;
        moves++;
    }

    public int getIndex() {return index;}
    public char getOwner() {return owner;}
}