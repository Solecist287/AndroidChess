package jaysc.example.com.chess.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import jaysc.example.com.chess.R;

public class PregameActivity extends AppCompatActivity {
    Spinner whiteDropDown;
    Spinner modeDropDown;
    LinearLayout PVCLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);
        PVCLayout = findViewById(R.id.PVCLayout);
        whiteDropDown = findViewById(R.id.whiteDropDown);
        modeDropDown = findViewById(R.id.modeDropDown);
        modeDropDown.setSelection(0);//set to name
        modeDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//PVP
                    //make disappear
                    PVCLayout.setVisibility(View.INVISIBLE);
                }else if (position == 1) {//PVC
                    //make appear
                    PVCLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void launchChessMenuScreen(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void playGame(View view) {
        int selectedModeIndex = modeDropDown.getSelectedItemPosition();
        Intent intent;
        //for future
        if (selectedModeIndex == 0) {//PVP
            //later make subclasses of gameactivity and pass info from spinners to it
            intent = new Intent(this, PVPGameActivity.class);
            startActivity(intent);
        }else if (selectedModeIndex == 1){//PVC
            //intent = new Intent(this, PVCGameActivity.class);
            //startActivity(intent);
        }



    }
}
