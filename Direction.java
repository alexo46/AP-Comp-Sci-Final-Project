// Alex Oliva
// 6/3/2022

public class Direction {
    public double x;
    public double y;

    // constructor that initalizes the variables
    public Direction(double inputX, double inputY) {
        x = inputX;
        y = inputY;
    }
    
    //changes the x and y variables to the variables passed
    public void set(double inputX, double inputY) {
        x = inputX;
        y = inputY;
    }
    
    // returns the x variable
    public double getX() {
        return x;
    }
    // returns the y variable
    public double getY() {
        return y;
    }
}
