package jaysc.example.com.chess.Structures;

import android.support.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

public class GameEntry {
    private String name;
    private LocalDate date;
    private ArrayList<String> moves;

    public GameEntry(String name, LocalDate date, ArrayList<String>moves){
        this.name = name;
        this.date = date;
        this.moves = moves;
    }

    //getters and setters
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public LocalDate getDate(){ return date; }
    public void setDate (LocalDate date){ this.date = date; }
    public ArrayList<String> getMoves(){ return moves; }
    //toString for listview
    @NonNull
    @Override
    public String toString(){ return name+" ("+date.toString()+")"; }
}

