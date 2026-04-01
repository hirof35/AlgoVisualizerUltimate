package algoVisualizerUltimate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class AlgoVisualizerUltimate extends JFrame {
    // --- データ構造 ---
    private List<Integer> arrayData = new ArrayList<>();
    private TreeNode treeRoot = null;
    private List<GNode> graphNodes = new ArrayList<>();
    private int[][] maze;
    private final int WALL = 1, PATH = 0;
    private final int mazeW = 31, mazeH = 21; // 迷路サイズ（奇数）

    // --- 状態管理 ---
    private enum Mode { ARRAY, TREE, GRAPH, MAZE }
    private Mode currentMode = Mode.ARRAY;
    private Object activeElement = null;
    private List<Object> highlightPath = Collections.synchronizedList(new ArrayList<>());
    private int rangeL = -1, rangeR = -1;

    private JPanel visualPanel;
    private JTextArea logArea;

    public AlgoVisualizerUltimate() {
        setTitle("Java アルゴリズム・アルティメット・ビジュアライザー");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                switch (currentMode) {
                    case ARRAY -> drawArray(g2);
                    case TREE -> drawTree(g2, treeRoot, getWidth() / 2, 50, getWidth() / 4);
                    case GRAPH -> drawGraph(g2);
                    case MAZE -> drawMaze(g2);
                }
            }
        };
        visualPanel.setBackground(Color.WHITE);

        // --- コントロールパネル ---
        JPanel ctrl = new JPanel(new GridLayout(0, 1, 2, 2));
        ctrl.setBorder(BorderFactory.createTitledBorder("Algorithms"));
        
        addBtn(ctrl, "Sort: Merge Sort", e -> start(() -> runMergeSort(0, arrayData.size()-1)));
        addBtn(ctrl, "Search: Binary", e -> start(() -> runBinarySearch(100)));
        addBtn(ctrl, "Tree: Add Node", e -> { currentMode = Mode.TREE; treeRoot = insertTree(treeRoot, new Random().nextInt(100)); repaint(); });
        addBtn(ctrl, "Graph: BFS", e -> { setupGraph(); start(this::runBFS); });
        addBtn(ctrl, "Graph: Dijkstra", e -> { setupGraph(); start(this::runDijkstra); });
        addBtn(ctrl, "Maze: Generate", e -> { currentMode = Mode.MAZE; start(this::runMazeGen); });
        addBtn(ctrl, "Maze: Solve (BFS)", e -> { if(currentMode==Mode.MAZE) start(this::runMazeSolve); });
        addBtn(ctrl, "Reset All", e -> reset());

        logArea = new JTextArea(8, 50);
        add(ctrl, BorderLayout.WEST);
        add(visualPanel, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        reset();
    }

    // --- マージソート ---
    private void runMergeSort(int l, int r) {
        currentMode = Mode.ARRAY;
        if (l < r) {
            int m = (l + r) / 2;
            runMergeSort(l, m);
            runMergeSort(m + 1, r);
            merge(l, m, r);
        }
    }

    private void merge(int l, int m, int r) {
        List<Integer> temp = new ArrayList<>();
        int i = l, j = m + 1;
        while (i <= m && j <= r) {
            activeElement = i;
            if (arrayData.get(i) <= arrayData.get(j)) temp.add(arrayData.get(i++));
            else temp.add(arrayData.get(j++));
            sleep(50);
        }
        while (i <= m) temp.add(arrayData.get(i++));
        while (j <= r) temp.add(arrayData.get(j++));
        for (int k = 0; k < temp.size(); k++) {
            arrayData.set(l + k, temp.get(k));
            activeElement = l + k;
            sleep(50);
        }
    }

    // --- 迷路生成（穴掘り法） ---
    private void runMazeGen() {
        maze = new int[mazeH][mazeW];
        for (int[] row : maze) Arrays.fill(row, WALL);
        generateMaze(1, 1);
        log("Maze Generated.");
    }

    private void generateMaze(int x, int y) {
        maze[y][x] = PATH;
        Integer[] dirs = {0, 1, 2, 3};
        Collections.shuffle(Arrays.asList(dirs));
        for (int d : dirs) {
            int dx = (d==1?2:d==3?-2:0), dy = (d==2?2:d==0?-2:0);
            int nx = x + dx, ny = y + dy;
            if (ny > 0 && ny < mazeH-1 && nx > 0 && nx < mazeW-1 && maze[ny][nx] == WALL) {
                maze[y + dy/2][x + dx/2] = PATH;
                sleep(10);
                generateMaze(nx, ny);
            }
        }
    }

    // --- 迷路解決 (BFS) ---
    private void runMazeSolve() {
        highlightPath.clear();
        Queue<Point> q = new LinkedList<>();
        Map<Point, Point> parents = new HashMap<>();
        boolean[][] v = new boolean[mazeH][mazeW];
        Point startP = new Point(1, 1), goalP = new Point(mazeW-2, mazeH-2);
        q.add(startP); v[1][1] = true;
        while (!q.isEmpty()) {
            Point c = q.poll(); activeElement = c; sleep(5);
            if (c.equals(goalP)) break;
            int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
            for (int i=0; i<4; i++) {
                Point n = new Point(c.x + dx[i], c.y + dy[i]);
                if (maze[n.y][n.x] == PATH && !v[n.y][n.x]) {
                    v[n.y][n.x] = true; parents.put(n, c); q.add(n);
                }
            }
        }
        Point p = goalP;
        while (p != null) { highlightPath.add(p); p = parents.get(p); repaint(); }
    }

    // --- ダイクストラ ---
    private void runDijkstra() {
        PriorityQueue<GNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
        graphNodes.forEach(n -> { n.dist = 999; n.prev = null; });
        graphNodes.get(0).dist = 0; pq.add(graphNodes.get(0));
        while (!pq.isEmpty()) {
            GNode u = pq.poll(); activeElement = u; sleep(500);
            for (var e : u.neighbors.entrySet()) {
                int alt = u.dist + e.getValue();
                if (alt < e.getKey().dist) {
                    e.getKey().dist = alt; e.getKey().prev = u; pq.add(e.getKey());
                }
            }
        }
        highlightPath.clear();
        GNode c = graphNodes.get(graphNodes.size()-1);
        while(c != null) { highlightPath.add(c); c = c.prev; }
        repaint();
    }

    // --- 描画系 ---
    private void drawArray(Graphics2D g) {
        int w = visualPanel.getWidth() / arrayData.size();
        for (int i = 0; i < arrayData.size(); i++) {
            g.setColor(Objects.equals(activeElement, i) ? Color.RED : new Color(100, 149, 237));
            int h = arrayData.get(i);
            g.fillRect(i * w + 2, visualPanel.getHeight() - h - 20, w - 4, h);
        }
    }

    private void drawMaze(Graphics2D g) {
        int cs = Math.min(visualPanel.getWidth()/mazeW, visualPanel.getHeight()/mazeH);
        for (int y=0; y<mazeH; y++) {
            for (int x=0; x<mazeW; x++) {
                if (maze[y][x] == WALL) g.setColor(Color.BLACK);
                else if (highlightPath.contains(new Point(x, y))) g.setColor(Color.YELLOW);
                else if (new Point(x, y).equals(activeElement)) g.setColor(Color.RED);
                else g.setColor(Color.WHITE);
                g.fillRect(x*cs, y*cs, cs, cs);
            }
        }
    }

    private void drawTree(Graphics2D g, TreeNode n, int x, int y, int o) {
        if (n == null) return;
        g.setColor(Color.GRAY);
        if (n.left != null) { g.drawLine(x, y, x-o, y+60); drawTree(g, n.left, x-o, y+60, o/2); }
        if (n.right != null) { g.drawLine(x, y, x+o, y+60); drawTree(g, n.right, x+o, y+60, o/2); }
        g.setColor(new Color(46, 204, 113)); g.fillOval(x-15, y-15, 30, 30);
        g.setColor(Color.WHITE); g.drawString(String.valueOf(n.val), x-7, y+5);
    }

    private void drawGraph(Graphics2D g) {
        for (GNode n : graphNodes) {
            for (var e : n.neighbors.entrySet()) {
                g.setColor(highlightPath.contains(n) && highlightPath.contains(e.getKey()) ? Color.ORANGE : Color.LIGHT_GRAY);
                g.drawLine(n.x, n.y, e.getKey().x, e.getKey().y);
            }
        }
        for (GNode n : graphNodes) {
            g.setColor(n == activeElement ? Color.RED : Color.CYAN);
            g.fillOval(n.x-15, n.y-15, 30, 30);
            g.setColor(Color.BLACK); g.drawString("ID:"+n.id+" D:"+n.dist, n.x-20, n.y-20);
        }
    }

    // --- その他補助 ---
    private void runBinarySearch(int t) {
        Collections.sort(arrayData);
        int l = 0, h = arrayData.size()-1;
        while(l <= h) {
            int m = (l+h)/2; activeElement = m; sleep(600);
            if(arrayData.get(m) == t) { log("Found "+t); return; }
            if(arrayData.get(m) < t) l = m+1; else h = m-1;
        }
        log("Not Found");
    }

    private void runBFS() {
        Queue<GNode> q = new LinkedList<>();
        graphNodes.get(0).visited = true; q.add(graphNodes.get(0));
        while(!q.isEmpty()){
            GNode c = q.poll(); activeElement = c; sleep(600);
            for(GNode n : c.neighbors.keySet()) if(!n.visited){ n.visited=true; q.add(n); }
        }
    }

    private void setupGraph() {
        currentMode = Mode.GRAPH; if(!graphNodes.isEmpty()) return;
        GNode n0 = new GNode(0, 100, 150), n1 = new GNode(1, 300, 100), n2 = new GNode(2, 300, 200), n3 = new GNode(3, 500, 150);
        n0.addEdge(n1, 4); n0.addEdge(n2, 2); n1.addEdge(n2, 1); n1.addEdge(n3, 3); n2.addEdge(n3, 7);
        graphNodes.addAll(Arrays.asList(n0, n1, n2, n3));
    }

    private void reset() {
        arrayData.clear(); for(int i=0; i<20; i++) arrayData.add(new Random().nextInt(200)+20);
        treeRoot = null; graphNodes.clear(); maze = new int[mazeH][mazeW]; highlightPath.clear();
        currentMode = Mode.ARRAY; activeElement = null; repaint();
    }

    private void start(Runnable r) { new Thread(r).start(); }
    private void sleep(int ms) { try { Thread.sleep(ms); } catch (Exception e) {} visualPanel.repaint(); }
    private void addBtn(JPanel p, String t, java.awt.event.ActionListener a) { JButton b = new JButton(t); b.addActionListener(a); p.add(b); }
    private void log(String m) { logArea.append(m + "\n"); }
    private TreeNode insertTree(TreeNode r, int v) {
        if(r == null) return new TreeNode(v);
        if(v < r.val) r.left = insertTree(r.left, v); else r.right = insertTree(r.right, v);
        return r;
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new AlgoVisualizerUltimate().setVisible(true)); }

    static class TreeNode { int val; TreeNode left, right; TreeNode(int v){val=v;} }
    static class GNode {
        int id, x, y, dist = 999; boolean visited = false; GNode prev = null;
        Map<GNode, Integer> neighbors = new HashMap<>();
        GNode(int id, int x, int y){this.id=id; this.x=x; this.y=y;}
        void addEdge(GNode n, int w){neighbors.put(n, w); n.neighbors.put(this, w);}
    }
}
