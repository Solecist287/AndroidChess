package jaysc.example.com.chess.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jaysc.example.com.chess.Structures.GameEntry;
import jaysc.example.com.chess.R;

public class RecordedGamesActivity extends AppCompatActivity {
    public static final String PATH = "games.txt";
    public static final String DIVIDER = "******";
    List<GameEntry> gameEntries;
    ArrayAdapter<GameEntry>gameAdapter;
    Spinner dropDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorded_games);
        gameEntries = new ArrayList<>();
        readFromFile();
        //initialize spinner choice to name
        dropDown = findViewById(R.id.gameSortDropDown);
        dropDown.setSelection(0);//set to name
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//name
                    gameEntries.sort(new NameComparator());
                }else if (position == 1) {//date
                    gameEntries.sort(new DateComparator());
                }
                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gameEntries.sort(new NameComparator());
                gameAdapter.notifyDataSetChanged();
            }
        });
        //sort by name initially
        gameEntries.sort(new NameComparator());
        //hook up adapter to gameEntries arraylist
        gameAdapter = new ArrayAdapter<>(this,R.layout.activity_recorded_list_entry, gameEntries);
        ListView gridview = findViewById(R.id.gameList);
        gridview.setAdapter(gameAdapter);
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            GameEntry selectedGameEntry = gameEntries.get(position);
            Intent intent = new Intent(getApplicationContext(),ReplayGameActivity.class);
            intent.putStringArrayListExtra(ReplayGameActivity.MOVES, selectedGameEntry.getMoves());
            startActivity(intent);
        });
    }
    private void readFromFile(){
        //READ
        String name = "";
        LocalDate date = null;
        ArrayList<String>moves = new ArrayList<>();
        try {
            InputStream inputStream = getApplicationContext().openFileInput(RecordedGamesActivity.PATH);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                int i = 0;
                int lastAsteriskLine = -1;
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //Toast.makeText(RecordedGamesActivity.this, "read:"+receiveString, Toast.LENGTH_LONG).show();
                    if (i == lastAsteriskLine + 1) {//first line
                        name = receiveString;
                    }else if (i == lastAsteriskLine + 2){
                        date = LocalDate.parse(receiveString);
                    }else if (receiveString.equals(DIVIDER)){
                        gameEntries.add(new GameEntry(name,date,moves));
                        lastAsteriskLine = i;
                        moves = new ArrayList<>();
                    } else {
                        moves.add(receiveString);
                    }
                    i++;
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(RecordedGamesActivity.this, "file not found", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(RecordedGamesActivity.this, "cannot read file", Toast.LENGTH_LONG).show();
        }
    }
    public void launchChessMenuScreen(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    //comparators used to sort by their respective category when a
    //category is chosen
    public class NameComparator implements Comparator<GameEntry>{
        @Override
        public int compare(GameEntry g1, GameEntry g2) {
            return g1.getName().compareTo(g2.getName());
        }
    }
    public class DateComparator implements Comparator<GameEntry>{
        @Override
        public int compare(GameEntry g1, GameEntry g2) {
            return g1.getDate().compareTo(g2.getDate());
        }
    }
}

