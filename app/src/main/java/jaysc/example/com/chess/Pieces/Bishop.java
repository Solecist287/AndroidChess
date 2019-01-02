package jaysc.example.com.chess.Pieces;

public class Bishop extends Piece{

    public Bishop(int index, char owner){super(index,owner);}
    private Bishop(Bishop b) {super(b);}

    public boolean isMoveValid(int destIndex, Piece[]board){//knows if a piece is in destination coord
        int destRow = destIndex/8;
        int destCol = destIndex%8;
        Piece destPiece = board[destIndex];
        //find differences between rows and columns
        int rowDiff = row - destRow;//x
        int colDiff = column - destCol;//y
        //used to check for obstructing pieces
        int curRow;
        int curCol;
        int endRow;
        int endCol;

        //check if dest is diagonal from bishop
        //also checks that if dest is occupied, that it is enemy piece
        if (Math.abs(rowDiff) != Math.abs(colDiff) || (destPiece!=null && destPiece.owner == owner)) {
            return false;
        }
        if ((rowDiff>0 && colDiff>0)||(rowDiff<0 && colDiff<0)) {//top left or bottom right diagonal
            curRow = Math.min(row, destRow)+1;
            curCol = Math.min(column, destCol)+1;
            endRow = Math.max(row, destRow);
            endCol = Math.max(column, destCol);
            while (curRow<endRow && curCol<endCol) {
                int curIndex = (curRow*8)+curCol;
                if (board[curIndex]!=null) {//if piece is obstructing
                    return false;
                }
                curRow++;
                curCol++;
            }
            return true;
        }else if ((rowDiff>0 && colDiff<0)||(rowDiff<0 && colDiff>0)) {//top right or bottom left diagonal
            curRow = Math.max(row, destRow)-1;
            curCol = Math.min(column, destCol)+1;
            endRow = Math.min(row, destRow);
            endCol = Math.max(column, destCol);
            while (curRow>endRow && curCol<endCol) {
                int curIndex = (curRow*8)+curCol;
                if (board[curIndex]!=null) {//if piece is obstructing
                    return false;
                }
                curRow--;
                curCol++;
            }
            return true;
        }
        return false;
    }
    public int getImageIndex(){
        if (owner == 'b') return 0; else return 1;
    }
    public Piece makeCopy() {
        return new Bishop(this);
    }
}