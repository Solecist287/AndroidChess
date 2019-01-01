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
    public Piece [] pieces;
    public ChessboardAdapter chessboardAdapter;
    public int moveIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay_game);
        //retrieve moves
        moves = getIntent().getStringArrayListExtra(MOVES);
        moveIndex = -1;//start at before first move
        //make room for pieces
        pieces = new Piece[64];
        resetBoard();
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, pieces);
        //fetch gridview from xml variable name
        GridView gridview = findViewById(R.id.chessboard_replay);
        //connect gridview to its adapter
        gridview.setAdapter(chessboardAdapter);
    }
    private void resetBoard(){
        //black pieces
        pieces[0] = new Rook(0,'b');
        pieces[1] = new Knight(1,'b');
        pieces[2] = new Bishop(2,'b');
        pieces[3] = new Queen(3,'b');
        pieces[4] = new King(4,'b');
        pieces[5] = new Bishop(5,'b');
        pieces[6] = new Knight(6,'b');
        pieces[7] = new Rook(7,'b');
        //black pawns
        for (int i = 8; i < 16; i++){
            pieces[i] = new Pawn(i,'b');
        }
        //blank squares
        for (int i = 16; i <48; i++){
            pieces[i] = null;
        }
        //white pawns
        for (int i = 48; i < 56; i++){
            pieces[i] = new Pawn(i,'w');
        }
        //white pieces
        pieces[56] = new Rook(56,'w');
        pieces[57] = new Knight(57,'w');
        pieces[58] = new Bishop(58,'w');
        pieces[59] = new Queen(59,'w');
        pieces[60] = new King(60,'w');
        pieces[61] = new Bishop(61,'w');
        pieces[62] = new Knight(62,'w');
        pieces[63] = new Rook(63,'w');
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
            resetBoard();
            chessboardAdapter.notifyDataSetChanged();
        }else {
            doMove();
        }
    }
    private void doMove(){
        //brings board back to start
        resetBoard();
        char turn;
        for (int i = 0; i <= moveIndex; i++){
            turn = (i%2==0)? 'w': 'b';
            String curMove = moves.get(i);
            String args[] = curMove.split(Pattern.quote(","));
            //move piece like normal
            int start = Integer.parseInt(args[0]);
            int end = Integer.parseInt(args[1]);
            Piece p = pieces[start];
            p.move(end,pieces);
            //pawn promotion
            if (args.length == 3){
                int index = Integer.parseInt(args[2]);
                pieces[end] = GameActivity.promotionConstructors.get(index).apply(end,turn);
            }
        }
        chessboardAdapter.notifyDataSetChanged();
    }
}

