package MapService.world;
import com.sg188.real.Char;

public class Match {
    private Char char1;
    private Char char2;
    private int result; // 1: Player1 thắng, 2: Player2 thắng

    public Match(Char p1, Char p2) {
        this.char1 = p1;
        this.char2 = p2;
        this.result = 3;
    }

    public Match(Char p1, Char p2, int charWin) {
        this.char1 = p1;
        this.char2 = p2;
        this.result = charWin;
    }

    public Char getChar1() {
        return char1;
    }

    public Char getChar2() {
        return char2;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    public Char getWinner() {
        return result == 1 ? char1 : char2;
    }
}


