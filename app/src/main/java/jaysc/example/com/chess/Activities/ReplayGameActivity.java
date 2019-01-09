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

import static jaysc.example.com.chess.Activities.PVPGameActivity.initBoard;

public class ReplayGameActivity extends AppCompatActivity {
    public static final String MOVES = "moves";
    public ArrayList<String>moves;
    public Piece [] pieces;
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
        pieces = initBoard();
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
            pieces = initBoard();
            updateGridview();
        }else {
            doMove();
        }
    }
    private void doMove(){
        //brings board back to start
        pieces = initBoard();
        updateGridview();
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
                pieces[end] = PVPGameActivity.promotionConstructors.get(index).apply(end,turn);
            }
        }
        chessboardAdapter.notifyDataSetChanged();
    }
    private void updateGridview(){
        //create adapter to connect to pieces' images/chessboard positions
        chessboardAdapter = new ChessboardAdapter(this, pieces);
        //connect gridview to its adapter
        gridview.setAdapter(chessboardAdapter);
    }
}

