package com.vaibhav.apslabproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

class Comp implements Comparator<Pair> {
    @Override
    public int compare(Pair p1, Pair p2) {
        if(p2.getSecond() < p1.getSecond()) {
            return 1;
        } else {
            return 0;
        }
    }
}
class Algorithms {
    private int INF = 100000000;
    private int N;
    public ArrayList<Integer>[][] paths;
    private boolean negcycle;
    int min(int a, int b) {
        return Math.min(a, b);
    }

    public int getN() {
        return N;
    }

    public boolean isNegcycle() {
        return negcycle;
    }

    public void setN(int n) {
        N = n;
        paths = new ArrayList[N + 2][N + 2];
        for(int i = 0; i < N + 2; i++) {
            for(int j = 0; j < N + 2; j++) {
                paths[i][j] = new ArrayList<>();
            }
        }
    }

    int[] Dijkstra(ArrayList<Pair>[]G,int src) {
        int[] dis = new int[N];
        boolean []vis = new boolean[N];
        int[] par = new int[N];
        for(int i = 0; i < N; i++) {
            dis[i] = INF;
            vis[i] = false;
            par[i] = -1;
        }
        dis[src] = 0;
        vis[src] = true;
        PriorityQueue<Pair> PQ = new PriorityQueue<Pair>(10, new Comp());
        PQ.add(new Pair(src, dis[src]));
        while(!PQ.isEmpty()) {
            int cur = PQ.poll().getFirst();
            vis[cur] = true;
            for(Pair i : G[cur]) {
                if(!vis[i.getFirst()] && (dis[cur]+i.getSecond() < dis[i.getFirst()])) {
                    dis[i.getFirst()] = i.getSecond() + dis[cur];
                    par[i.getFirst()] = cur;
                    PQ.add(new Pair(i.getFirst(), dis[i.getFirst()]));
                }
            }
        }
        for(int i = 0; i < N; i++) {
            if(i == src) {
                continue;
            }
            recpath(i, src, i, par);
        }
        return dis;
    }
    void recPath(int[][]p, int u, int v, int src, int des) {
        if(p[u][v] == u) {
            return;
        }
        recPath(p, u, p[u][v], src, des);
        paths[src][des].add(p[u][v]);
    }

    void recpath(int i, int src, int des, int []parent) {
        if(i < 0) {
            return;
        }
        recpath(parent[i], src, des, parent);
        paths[src][des].add(i);
    }

    int [] Bellman_Ford(ArrayList<Edge> E,int src,int V) {
        int[] dis = new int[V];
        int [] parent = new int[V];
        for(int i = 0; i < V; i++) {
            dis[i] = INF;
            parent[i] = -1;
        }
        dis[src] = 0;
        for(int i = 0; i < V - 1; i++) {
            for(Edge j : E) {
                if(dis[j.getU()]<INF) {
                    if(dis[j.getV()] > dis[j.getU()] + j.getWei()) {
                        parent[j.getV()] = j.getU();
                        dis[j.getV()] = dis[j.getU()] + j.getWei();
                    }
                }
            }
        }
        for(Edge i : E) {
            if(dis[i.getV()] != min(dis[i.getV()], dis[i.getU()] + i.getWei())) { //Negetive Cycle detected
                int[] A = {1};
                negcycle = true;
                return A;
            }
        }
        for(int i = 0; i < V; i++) {
            if(i == src) {
                continue;
            }
            recpath(i, src, i, parent);
        }
        return dis;
    }

    int[][] Floyd_Warshall(ArrayList<Pair> []G) {
        int [][]dis = new int[N][N];
        int [][]p = new int[N][N];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                dis[i][j] = INF;
            }
        }
        for(int i = 0; i < N; i++) {
            for(Pair j : G[i]) {
                dis[i][j.getFirst()] = j.getSecond();
            }
            dis[i][i] = 0;
        }
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(i == j) {
                    p[i][j] = 0;
                    continue;
                }
                if(dis[i][j] != INF){
                    p[i][j] = i;
                } else {
                    p[i][j] = -1;
                }
            }
        }
        for(int k = 0; k < N; k++) {
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(dis[i][k] != INF && dis[k][j] != INF && dis[i][j] > (dis[i][k] + dis[k][j])) {
                        dis[i][j] = dis[i][k] + dis[k][j];
                        p[i][j] = p[k][j];
                    }
                }
            }
        }
        for(int i=0;i<N;i++) {
            if(dis[i][i] < 0) {
                negcycle = true;
                int[][]A = {{1}};
                return A;
            }
        }
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(i != j && dis[i][j] != INF) {
                    paths[i][j].add(i);
                    recPath(p, i, j, i, j);
                    paths[i][j].add(j);
                }
            }
        }
        return dis;
    }

    int[][] Johnson(ArrayList<Pair>[] G, ArrayList<Edge> E) {
        ArrayList<Edge> NE = new ArrayList<Edge>();
        //NE = (ArrayList)E.clone();
        for(int i = 0; i < E.size(); i++) {
            NE.add(E.get(i));
        }

        for(int i = 0; i < N; i++) {
            NE.add(new Edge(N, i, 0));
        }
        int[] A = Bellman_Ford(NE, N, N + 1);
        if(A.length == 1) {
            negcycle = true;
            int[][] ans = {{1}};
            return ans;
        }
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < G[i].size(); j++) {
                int idx = G[i].get(j).getFirst();
                G[i].set(j, new Pair(idx, G[i].get(j).getSecond() + A[i] - A[idx]));
            }
        }
        int[][] dis= new int[N][N];
        for(int i = 0; i < N; i++) {
            dis[i] = Dijkstra(G, i); //N times Dijsktra Called.
        }
        return dis;
    }

    int[][] MDijkstra(ArrayList<Pair>[] G) {
        int[][] dis = new int[N][N];
        for(int i = 0; i < N; i++) {
            dis[i] = Dijkstra(G, i);
        }
        return dis;
    }
    int[][] MBellman(ArrayList<Edge> E) {
        int[][] dis = new int[N][N];
        for(int i = 0; i < N; i++) {
            dis[i] = Bellman_Ford(E, i, N);
            if(dis[i].length == 1) { //Negative Cycle Detected.
                int[][] A = {{1}};
                negcycle = true;
                return A;
            }
        }
        return dis;
    }

}
