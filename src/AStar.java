import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.lang.Math;
import java.text.DecimalFormat;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class AStar {
    Node[][] gridNodes = new Node[Grid.GRID_WIDTH/Grid.UNIT_SIZE][Grid.GRID_HEIGHT/Grid.UNIT_SIZE];
    static ArrayList<Node> closed = new ArrayList<>();
    static ArrayList<Node> visited = new ArrayList<>();
    static ArrayList<Node> open = new ArrayList<>();
    static ArrayList<Node> fastestPath = new ArrayList<>();
    Node start;
    Node end;
    boolean firstRun = true;
    int j = 0;
    


    AStar(Node[][] gridNodes, Node start, Node end) {
        this.gridNodes = gridNodes;
        this.closed = closed;
        this.start = start;
        this.end = end;
    }

    /**
     * Helper function to perform heuristics calculation
     * using Pythagorean theorem between current node
     * and end node
     *
     * @param current
     */
    
    public void setHScore(Node current) {
        current.hScore = Math.sqrt(Math.pow(current.xPos - end.xPos, 2) + Math.pow(current.yPos - end.yPos, 2));
    }

    
    /**
     * Helper function to calculate gScore of current
     * node depending on whether it's diagonal to its parent
     *
     * @param current
     * @param diagonal
     */

    public void setGScore(Node current, boolean diagonal) {
        double distance = 1;
        if (diagonal) {
            distance = 1.4;
        }
        if(current.previous != null){
            current.gScore = current.previous.gScore + distance;
        } else {
            current.gScore = distance;
        }
    }

    
    /**
     * Helper function that adds gScore and hScore to get fScore
     *
     * @param current
     */

    public void setFScore(Node current) {
        current.fScore = current.gScore + current.hScore;
    }
    


    /**
     *  Helper function to find the index of the Node in ArrayList,
     *  returns -1 if Node is not found
     *
     * @param list ArrayList of Nodes
     * @param nodeCheck
     * @return index of Node
     */

    public int inArrayListCheck(ArrayList<Node> list, Node nodeCheck) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(nodeCheck)) {
                return i;
            }
        }
        return -1;
    }

    
    /**
     * Helper function to get the node with the lowest
     * fCost from open list
     *
     * @return index of Node
     */

    public int getLowestCostInd() {
        double min = open.get(0).fScore;
        int minIndex = 0;
        for (int i = 0; i < open.size(); i++) {
            if (open.get(i).fScore < min) {
                min = open.get(i).fScore;
                minIndex = i;
            }
            else if (open.get(i).fScore == min) {
                if (open.get(i).hScore < open.get(minIndex).hScore) {
                    min = open.get(i).fScore;
                    minIndex = i;
                }
            }
        }
        return minIndex;
    }
    

    public void startSearch() {

        //in every iteration, open should basically have last iteration's used nodes. So therefore, transfer open to currentOpen (so we can iterate through
        //the last iteration's nodes to figure out the current iteration's nodes), and then clear open so that we can add in the new open's.

        int numberOfResets = 0;
        
        if(firstRun){
            
            closed.add(start);
            firstRun = false;
            Grid.testnode = start;
            
            System.out.println("List of nodeWalls.");
            for(int i = 0; i < Grid.nodeWall.size(); i++){
                System.out.println("(" + Grid.nodeWall.get(i).xPos / Grid.UNIT_SIZE + ", " + Grid.nodeWall.get(i).yPos / Grid.UNIT_SIZE + ") ");
            }
            
            
        } else {
            System.out.print(">>> (" + Grid.testnode.xPos + ", " + Grid.testnode.yPos + "). ");
            open.clear(); //we shouldn't clear. we should add this to closed. but moving on..
                for(int a = -1; a < 2; a++){
                    for(int b = -1; b < 2; b++){
                        if((!(a == 0 && b == 0)) && (Grid.testnode.xPos + a) >= 0 && (Grid.testnode.xPos + a) <= 29 && (Grid.testnode.yPos + b) >= 0 && (Grid.testnode.yPos + b) <= 19){
                            
                            if(
                                (inArrayListCheck(visited, new Node((Grid.testnode.xPos + a), (Grid.testnode.yPos + b), Grid.UNIT_SIZE)) == -1) &&
                                (inArrayListCheck(Grid.nodeWall, new Node((Grid.testnode.xPos + a) * Grid.UNIT_SIZE, (Grid.testnode.yPos + b) * Grid.UNIT_SIZE, Grid.UNIT_SIZE)) == -1)){
                                
                                Node newNode = new Node((Grid.testnode.xPos + a), (Grid.testnode.yPos + b), Grid.UNIT_SIZE - 1);
                                newNode.previous = Grid.testnode;
                                setHScore(newNode);
                                if(((a == 1) || (a == -1)) && (b == 1) || (b == -1)){
                                    setGScore(newNode, true);
                                } else {
                                    setGScore(newNode, false);
                                }
                                setFScore(newNode);
                                open.add(newNode);
                            }

                        }
                    }
                }
                if(Grid.testnode.xPos == end.xPos && Grid.testnode.yPos == end.yPos){
                    Grid.reached_end = true;  
                    Grid.terminate = true;
                    return;
                }
                if(open.isEmpty()){ //Where we are trapped. 
                    System.out.print("Okay! We're stuck. Backtrack one node. ");
                    visited.add(Grid.testnode);

                    System.out.print("Closed size : " + closed.size() + ". ");
                    System.out.print("Num Of Open : " + Grid.testnode.numOfOpens + ". ");
                    System.out.print("So we are back to >> (" + Grid.testnode.previous.xPos + ", " + Grid.testnode.previous.yPos + ") . ");
                    if(Grid.testnode.previous.xPos == start.xPos && Grid.testnode.previous.yPos == start.yPos){
                        Grid.terminate = true;
                        return;
                    }
                    for(int i = 0; i < Grid.testnode.numOfOpens; i++){
                        closed.remove(closed.size() - (1 + i));
                    }
                    Grid.testnode = Grid.testnode.previous;

                    System.out.println("");
                } else {
                
                //Closing things up.
                    Grid.testnode = open.get(getLowestCostInd());
                    Grid.testnode.numOfOpens = open.size();

                    System.out.print("Checking available nodes.");
                    for(int i = 0; i < Grid.testnode.numOfOpens; i++){
                        System.out.print("(" + open.get(i).xPos + ", " + open.get(i).yPos + ") with Fscore of ");
                        System.out.print(new DecimalFormat("#.##").format(open.get(i).fScore) + " ");
                    }
                    System.out.println("");
                    visited.add(Grid.testnode);
                    fastestPath.add(Grid.testnode);
                    for(int i = 0; i < open.size(); i++){ //moving all the used open nodes onto closed nodes.
                        closed.add(open.get(i));
                    }
                
                }
            
        }
        
        
    }

}
