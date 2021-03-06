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

    public void launchPregameScreen(View view) {
        Intent intent = new Intent(this, PregameActivity.class);
        startActivity(intent);
    }

    public void launchRecordedGamesScreen(View view) {
        Intent intent = new Intent(this, RecordedGamesActivity.class);
        startActivity(intent);
    }
}

