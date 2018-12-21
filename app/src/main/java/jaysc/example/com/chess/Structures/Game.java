package jaysc.example.com.chess.Structures;

import java.time.LocalDate;
import java.util.ArrayList;

public class Game {
    private String name;
    private LocalDate date;
    private ArrayList<String> moves;


    public Game(String name, LocalDate date, ArrayList<String>moves){
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
    public void setMoves(ArrayList<String>moves){ this.moves = moves; }
    //toString for listview
    @Override
    public String toString(){ return name+" ("+date.toString()+")"; }

    //public String toString(){
    //  String output = name+" ("+date.toString()+")";
    //for (int i = 0; i < moves.size(); i++){
    //  if (i==0){output+="\n";}
    //output+=moves.get(i)+"\n";
    //}
    //return output;
    //}

}

