package jaysc.example.com.chess.Activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jaysc.example.com.chess.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void launchGameScreen(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchRecordedGamesScreen(View view) {
        Intent intent = new Intent(this, RecordedGamesActivity.class);
        startActivity(intent);
        finish();
    }
}

