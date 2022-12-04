/**
 * Point with (x,y) coordinates and value
 */
public class Point {
    int x, y;
    double value;

    public Point(int x, int y, double value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
        this.value = 0;
    }
}
