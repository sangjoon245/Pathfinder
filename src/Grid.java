import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;


public class Grid extends JPanel implements ActionListener {
    static final int GRID_WIDTH = 600;
    static final int GRID_HEIGHT = 400;
    static final int UNIT_SIZE = 20;
    static final int MAX_ITERATION = 2000;
    
    static boolean startSearch = false;
    static ArrayList<Node> nodePath = new ArrayList<>();

    static ArrayList<Node> nodeWall = new ArrayList<>();
    static Node[][] nodeArray = new Node[(GRID_WIDTH/UNIT_SIZE) + 2][(GRID_HEIGHT/UNIT_SIZE) + 1];
    Node nodeToFill;
    Node start;
    Node end;
    static Node testnode;
    
    static boolean reached_end = false;
    static boolean terminate = false;
    
    
    private class MyListener extends MouseInputAdapter {
        
        public void mousePressed(MouseEvent e) {
            
            
            if(!startSearch){
                String currentMode = MainFrame.comboBox.getSelectedItem().toString();    
                int x = e.getX() / UNIT_SIZE;
                int y = e.getY() / UNIT_SIZE;
                nodeToFill = nodeArray[x][y];
                repaint();

                if(currentMode=="Starting Point"){
                    if(SwingUtilities.isRightMouseButton(e)){
                        if(start != null && (x == start.xPos) && (y == start.yPos)){ //if a start position already exists
                            start = null;
                        }
                    } else {
                        start = new Node(x, y, UNIT_SIZE);
                    }

                }
                if(currentMode=="Ending Point"){
                    if(SwingUtilities.isRightMouseButton(e)){
                        if(end != null && (x == end.xPos) && (y == end.yPos)){ //if a start position already exists
                            end = null;
                        }
                    } else {
                        end = new Node(x, y, UNIT_SIZE);
                    }

                }
                if(currentMode=="Wall"){
                    int nodeIndex = inArrayListCheck(nodeWall, nodeArray[x][y]);

                    if (SwingUtilities.isRightMouseButton(e)) {
                        // erases node wall on right click

                       if (nodeIndex != -1) {
                           nodeWall.remove(nodeIndex);
                       }
                    }
                    else {
                        // adds node to node wall
                        if (nodeWall.isEmpty()) {
                            nodeWall.add(nodeArray[x][y]);
                        }
                        if (nodeIndex == -1) {
                            // only adds new nodes to node wall

                            nodeWall.add(nodeArray[x][y]);
                        }
                    }
                    repaint();
                }
            }
            
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

        public void mouseDragged(MouseEvent e) {
            if(!startSearch){
                String currentMode = MainFrame.comboBox.getSelectedItem().toString();    
                int x = e.getX() / UNIT_SIZE;
                int y = e.getY() / UNIT_SIZE;

                if(currentMode=="Wall"){
                int nodeIndex = inArrayListCheck(nodeWall, nodeArray[x][y]);
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // erases node wall on right click

                       if (nodeIndex != -1) {
                           nodeWall.remove(nodeIndex);
                       }
                    }
                    else {
                        // adds node to node wall

                        if (nodeWall.isEmpty()) {
                            nodeWall.add(nodeArray[x][y]);
                        }
                        if (nodeIndex == -1) {
                            // only adds new nodes to node wall

                            nodeWall.add(nodeArray[x][y]);
                        }
                    }
                    repaint();
                }
            }
        }
    }
    
    private class Clicklistener implements ActionListener{ //Our new "Clicklistener" so we know whic hbuttons being pressed.
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == MainFrame.startButton){
                startSearch = true;
                AStar astar = new AStar(nodeArray, start, end);
                
                int iteration = 0;

                do{
                    astar.startSearch();
                    iteration++;
                }
                while(!((testnode.xPos == end.xPos) && (testnode.yPos == end.yPos)) && iteration < MAX_ITERATION && terminate == false);
                if(reached_end = true){
                    System.out.println("Reached end!!");
                } else {
                    System.out.println("There is no solution!");
                }
                repaint();
            }
            
            if(e.getSource() == MainFrame.resetButton){
                if(!startSearch){
                    start = null;
                    end = null;
                    nodeWall.clear();
                    repaint();
                }
            }
            
            repaint();
        }

    }

    Grid() {
        this.setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
       // this.setFocusable(true); //figure out what this means
        MyListener myListener = new MyListener();
        addMouseListener(myListener);
        addMouseMotionListener(myListener);
        Clicklistener click= new Clicklistener();
        MainFrame.startButton.addActionListener(click);
        MainFrame.resetButton.addActionListener(click);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
    }


    
    public void drawGrid(Graphics g) {
        g.setColor(Color.black);
        for (int i = 0; i < (GRID_WIDTH / UNIT_SIZE) + 1; i++) {
            for (int j = 0; j < (GRID_HEIGHT / UNIT_SIZE) + 1; j++) {
                Node addNode = new Node(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE);
                nodeArray[i][j] = addNode;
                g.drawRect(
                        (int) addNode.box.getX(),
                        (int) addNode.box.getY(),
                        (int) addNode.box.getWidth(),
                        (int) addNode.box.getHeight()
                );
            }
        }

        if (!AStar.open.isEmpty()) {
            for (int i = 0; i < AStar.open.size(); i++){
                if(!((AStar.open.get(i).xPos == start.xPos) && (AStar.open.get(i).yPos) == start.yPos)){
                    //fillBox(g, new Node(AStar.open.get(i).xPos * UNIT_SIZE, AStar.open.get(i).yPos * UNIT_SIZE, UNIT_SIZE - 1), Color.YELLOW);
                }
            }
            for (int i = 0; i < AStar.closed.size(); i++){
                if(!((AStar.closed.get(i).xPos == start.xPos) && (AStar.closed.get(i).yPos) == start.yPos)){
                    fillBox(g, new Node(AStar.closed.get(i).xPos * UNIT_SIZE, AStar.closed.get(i).yPos * UNIT_SIZE, UNIT_SIZE - 1), Color.DARK_GRAY);
                }
            }
           fillBox(g, new Node(testnode.xPos * UNIT_SIZE, testnode.yPos * UNIT_SIZE, UNIT_SIZE - 1), Color.PINK);
            
        }
        
        for(int i = 0; i < AStar.visited.size(); i++){
            fillBox(g, new Node(AStar.visited.get(i).xPos * UNIT_SIZE, AStar.visited.get(i).yPos * UNIT_SIZE, UNIT_SIZE - 1), Color.BLACK);
        }
        for(int i = 0; i < AStar.fastestPath.size(); i++){
            fillBox(g, new Node(AStar.fastestPath.get(i).xPos * UNIT_SIZE, AStar.fastestPath.get(i).yPos * UNIT_SIZE, UNIT_SIZE - 1), Color.CYAN);
        }
        if (!nodeWall.isEmpty()) {
            for (int i = 0; i < nodeWall.size(); i++) {
                fillBox(g, new Node(nodeWall.get(i).xPos, nodeWall.get(i).yPos, UNIT_SIZE), Color.ORANGE);
            }
        }
        if(start != null){
            fillBox(g, new Node(start.xPos * UNIT_SIZE, start.yPos * UNIT_SIZE, UNIT_SIZE), Color.BLUE);
        }
        if(end != null){
            fillBox(g, new Node(end.xPos * UNIT_SIZE, end.yPos * UNIT_SIZE, UNIT_SIZE), Color.RED);
        }
    }
    
    public void fillBox(Graphics g, Node fillNode, Color fillColor) {
        g.setColor(fillColor);
        g.fillRect(
                (int) fillNode.box.getX(),
                (int) fillNode.box.getY(),
                (int) fillNode.box.getWidth(),
                (int) fillNode.box.getHeight()
        );
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {

    }
    

}