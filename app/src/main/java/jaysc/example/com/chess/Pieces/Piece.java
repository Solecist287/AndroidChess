package jaysc.example.com.chess.Pieces;


public abstract class Piece{

    protected int row, column, index;
    protected char owner; //can be 'w' or 'b' AKA white or black
    protected char type;//e.g. 'N' knight
    protected int moves;

    public Piece(char type, int index, char owner){
        this.type = type;
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
        type = p.type;
        moves = p.moves;
    }
    //funcs
    public abstract boolean isMoveValid(int destIndex, Piece[]board);//knows if a piece is in destination coord

    public void move(int destIndex, Piece[]board) {
        //clear piece's prev location
        board[index] = null;
        //update piece position vars
        index = destIndex;
        row = index/8;
        column = index%8;
        //update board's vars for moved piece
        board[index] = this;
        moves++;
    }

    public int getIndex() {
        return this.index;
    }
    public int getColumn() {
        return this.column;
    }
    public int getRow() {
        return this.row;
    }
    public char getOwner() {
        return this.owner;
    }
    public int getMoves() {
        return this.moves;
    }
    //might wanna change this for saving state?
    public String toString(){
        return ""+owner + type;
    }
}
