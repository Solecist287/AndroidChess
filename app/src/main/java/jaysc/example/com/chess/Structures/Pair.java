package jaysc.example.com.chess.Structures;

public class Pair<T> {
    private T o1,o2;
    public Pair(T o1, T o2){
        this.o1 = o1;
        this.o2 = o2;
    }
    //getters and setters for o's
    public T getO1() {return o1;}
    public void setO1(T o1) {this.o1 = o1;}
    public T getO2() {return o2;}
    public void setO2(T o2) {this.o2 = o2;}
}
