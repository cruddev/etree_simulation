package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class TopoUtil {

//    private static int[][] g = {
//        {0, 1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE},
//        {1, 0, 1, 3, Integer.MAX_VALUE, Integer.MAX_VALUE},
//        {1, 1, 0, Integer.MAX_VALUE, 3, Integer.MAX_VALUE},
//        {Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 0, 1, 1},
//        {Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 1, 0, 1},
//        {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 1, 0}
//    };

//    private static int[][] g = {
//            {0, 1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE},
//            {1, 0, 1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE},
//            {1, 1, 0, Integer.MAX_VALUE, 1, Integer.MAX_VALUE},
//            {Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 0, 1, 1},
//            {Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 1, 0, 1},
//            {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 1, 0}
//    };
    public static int[][] g;

    private static int[][] minDelayMatrix;

    /**
     * Requires numpy and cyaron,
     * Use command to install them,
     * pip(or conda) install numpy
     * pip(or conda) install cyaron
     * @param n
     * @param delayMean
     * @param delayVar
     */
    public static void generatedGraph(int n, int delayMean, int delayVar) {
        Process pr;
        String exe = "python";

        // It must be the absolute path where the python script in.
        String command = "D:/koori/JavaDevelopment/etree/data/gen.py";

        String[] cmd = new String[] {exe, command, String.valueOf(n),
                String.valueOf(delayMean), String.valueOf(delayVar)};

        try {
            pr = Runtime.getRuntime().exec(cmd);

            InputStream is = pr.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            String str = dis.readLine();
            pr.waitFor();
            System.out.println(str);

        } catch (Exception e) { }
    }

    /**
     * Returns the adjacency matrix of the network,
     * If value is 0x7fffffff, then there is no edge between two nodes
     * else it represents the delay between two nodes.
     *
     * Notice that the index of node starts with 0.
     *
     * @param n Network size
     * @param filePath The path of graph data file
     * @return Adjacency matrix
     */
    public static void getGraph(int n, String filePath) {
        g = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                g[i][j] = i == j ? 0 : 0x7fffffff;

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(fr);
            String str;

            while ((str = bf.readLine()) != null) {
                String[] temp = str.split(" ");
                int from = Integer.parseInt(temp[0])-1;
                int to = Integer.parseInt(temp[1])-1;
                g[from][to] = Integer.parseInt(temp[2]);
                g[to][from] = Integer.parseInt(temp[2]);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (g[i][j] > 0 && g[i][j] < 0x7fffffff) {
                    g[i][j] = 1;
                }
//                System.out.printf("%15d", g[i][j]);
            }
//            System.out.println();
         }
    }

    /**
     * Returns the minimum delay from start(node index) to end(node index),
     * if minDelay = 0x7ffffff, it means that message can not from start to end.
     *
     * Implemented by Dijkstra with heap
     *
     * @param start message from
     * @return the minimum delay
     */
    private static int[] getSingelNodeMinDelay(int start) {
        class Edge implements Comparable<Edge>{
            int to , cost;
            Edge(int to_,int cost_){
                to = to_;
                cost = cost_;
            }
            @Override
            public int compareTo(Edge o) {
                return this.cost - o.cost;
            }
        }

        int n = g.length;
        boolean[] vis = new boolean[n];
        int[] dis = new int[n];

        for (int i = 0; i < n; i++) dis[i] = 0x7fffffff;
        Queue<Edge> que = new PriorityQueue<>();
        que.add(new Edge(start, 0));
        dis[start] = 0;
        while (!que.isEmpty()) {
            Edge top = que.poll();
            int u = top.to;

            if (dis[u] < top.cost) continue;
            if (vis[u]) continue;

            vis[u] = true;

            for (int to = 0; to < n; to++) {
                if (u != to && g[u][to] != 0x7fffffff) {
                    int delay = g[u][to];

                    if (!vis[to] && dis[to] > dis[u] + delay) {
                        dis[to] = dis[u]+delay;
                        que.add(new Edge(to, dis[to]));
                    }
                }
            }
        }
        return dis;
    }

    public static void generateMinDelayMatrix() {
        minDelayMatrix = new int[g.length][g.length];
        for (int nodeIndex = 0; nodeIndex < g.length; nodeIndex++) {
            int[] singleNodeDelayArray = getSingelNodeMinDelay(nodeIndex);
            for (int i = 0; i < g.length; i++) {
                minDelayMatrix[nodeIndex][i] = singleNodeDelayArray[i];
            }
        }
    }

    public static int getMinDelay(int start, int end) {
        return minDelayMatrix[start][end];
    }

    public static void main(String[] args) {
        // getGraph(100, "D:/koori/JavaDevelopment/etree/data/data100.in");
        getGraph(100, "/Users/xiyu/Downloads/data100.in");
        generateMinDelayMatrix();
        System.out.println("minDelay: from 0 -> 3: " + getMinDelay(0, 3));
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print(getMinDelay(i, j) + " ");
            }
            System.out.println();
        }
    }
}

