package jaysc.example.com.chess.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jaysc.example.com.chess.R;

public class PVCGameActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void undoMove(View view) {

    }

    @Override
    public void resign(View view) {
        returnToMainMenu();
    }

    @Override
    public int getChessboardId() {
        return R.id.pvc_chessboard;
    }

    @Override
    public int getViewId() {
        return R.layout.activity_pvc_game;
    }

}
