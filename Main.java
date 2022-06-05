// Alex Oliva
// 6/3/2022

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // runs game
        Game gameObj = new Game();
        gameObj.setVisible(true);
        gameObj.run();
    }
}
