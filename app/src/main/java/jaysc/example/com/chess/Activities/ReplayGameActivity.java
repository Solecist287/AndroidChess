package jaysc.example.com.chess.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import jaysc.example.com.chess.Adapters.ChessboardAdapter;
import jaysc.example.com.chess.R;
import jaysc.example.com.chess.Pieces.*;

public class ReplayGameActivity extends AppCompatActivity {
    public static final String MOVES = "moves";
    public ArrayList<String>moves;
    public Piece [] chessboard;
    public ChessboardAdapter chessboardAdapter;
    public int moveIndex;
    public GridView gridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay_game);
        //retrieve moves
        moves = getIntent().getStringArrayListExtra(MOVES);
        moveIndex = -1;//start at before first move
        //make room for pieces
        chessboard = initBoard();
        //fetch gridview from xml variable name
        gridview = findViewById(R.id.chessboard_replay);
        updateGridview();
    }
    public void launchRecordedGamesScreen(View view) {
        Intent intent = new Intent(this, RecordedGamesActivity.class);
        startActivity(intent);
    }

    public void nextMove(View view) {
        if (moveIndex == moves.size()-1){//at last move
            return;
        }
        moveIndex++;
        doMove();
    }

    public void prevMove(View view) {
        if (moveIndex == -1){//at first move
            return;
        }//ENDS
        moveIndex--;
        if (moveIndex == -1){
            chessboard = initBoard();
            updateGridview();
        }else {
            doMove();
        }
    }
    private void doMove(){
        //brings board back to start
        chessboard = initBoard();
        updateGridview();
        char turn;
        for (int i = 0; i <= moveIndex; i++){
            turn = (i%2==0)? 'w': 'b';
            String curMove = moves.get(i);
            String[] args = curMove.split(Pattern.quote(","));
            //move piece like normal
            int start = Integer.parseInt(args[0]);
            int end = Integer.parseInt(args[1]);
            Piece p = chessboard[start];
            p.move(end,chessboard);
            //pawn promotion
            if (args.length == 3){
                int index = Integer.parseInt(args[2]);
                chessboard[end] = PVPGameActivity.promotionConstructors.get(index).apply(end,turn);
            }
        }
        chessboardAdapter.notifyDataSetChanged();
    }
    private void updateGridview(){
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, chessboard);
        //connect gridview to its adapter
        gridview.setAdapter(chessboardAdapter);
    }
    private static Piece[] initBoard() {
        Piece[] board = new Piece[64];
        //black pieces
        board[0] = new Rook(0, 'b');
        board[1] = new Knight(1, 'b');
        board[2] = new Bishop(2, 'b');
        board[3] = new Queen(3, 'b');
        board[4] = new King(4, 'b');
        board[5] = new Bishop(5, 'b');
        board[6] = new Knight(6, 'b');
        board[7] = new Rook(7, 'b');
        //pawns
        for (int i = 8; i < 16; i++) {
            board[i] = new Pawn(i, 'b');//black pawns
            board[i+40] = new Pawn(i+40,'w');//white pawns
        }
        //white pieces
        board[56] = new Rook(56, 'w');
        board[57] = new Knight(57, 'w');
        board[58] = new Bishop(58, 'w');
        board[59] = new Queen(59, 'w');
        board[60] = new King(60, 'w');
        board[61] = new Bishop(61, 'w');
        board[62] = new Knight(62, 'w');
        board[63] = new Rook(63, 'w');
        return board;
    }

}