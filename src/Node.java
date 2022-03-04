import java.awt.*;

public class Node {
    int xPos;
    int yPos;
    int size;
    int numOfOpens;
    Rectangle box;
    double fScore = 0; // total score
    double gScore = 0; // distance from start node
    double hScore = 0; // heuristic distance from end node
    Node previous;

    public Node(int xPos, int yPos, int size) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.size = size;
        this.box = new Rectangle(xPos, yPos, size, size);
    }
    

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Node)) {
            return false;
        }

        Node c = (Node) o;

        return xPos == c.xPos && yPos == c.yPos;
    }
}
