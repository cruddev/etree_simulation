package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

public class KMedoideDelayClustering {
    static {
        TopoUtil.getGraph(1000, "/Users/xiyu/Downloads/data1000.in");
        TopoUtil.generateMinDelayMatrix();
    }
    public static ArrayList<ArrayList<Integer>> getGraphPartitionResult(ArrayList<Integer> nodeIdList, int k, float aggregationRatio) {
        ArrayList<ArrayList<Integer>> clusterList = new ArrayList<>(3);
        for (int i = 0; i < k; i++) {
            ArrayList<Integer> cluster = new ArrayList<>(nodeIdList.size());
            clusterList.add(cluster);
        }
        Random random = new Random();
        int[] clusterCenterNodeId = new int[k];
        HashSet<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < k; i++) {
            int randomNodeIndex = random.nextInt(nodeIdList.size());
            while (hashSet.contains(randomNodeIndex)) {
                randomNodeIndex = random.nextInt(nodeIdList.size());
            }
            hashSet.add(randomNodeIndex);
            clusterCenterNodeId[i] = nodeIdList.get(randomNodeIndex);
        }
        boolean terminateFlag = false;
        int count = 0;
        while (!terminateFlag) {
            count++;
            terminateFlag = true;
            for (int i = 0; i < k; i++) {
                clusterList.get(i).clear();
            }
            for (int i = 0; i < nodeIdList.size(); i++) {
                int nearestClusterCenter = 0;
                int minDelay = TopoUtil.getMinDelay(nodeIdList.get(i), clusterCenterNodeId[0]);
                for (int j = 1; j < k; j++) {
                    if (TopoUtil.getMinDelay(nodeIdList.get(i), clusterCenterNodeId[j]) < minDelay) {
                        nearestClusterCenter = j;
                        minDelay = TopoUtil.getMinDelay(nodeIdList.get(i), clusterCenterNodeId[j]);
                    }
                }
                clusterList.get(nearestClusterCenter).add(nodeIdList.get(i));
            }
            int maxClusterDelay = 0;
            for (int i = 0; i < k; i++) {
                // int minTotalDelay = Integer.MAX_VALUE;
                int maxDelay0 = Integer.MAX_VALUE;
                int newCenterNodeId = clusterCenterNodeId[i];
                int maxDelay = 0;
                int n = Math.round(nodeIdList.size() * (1 - aggregationRatio)) + 1;
                PriorityQueue<Integer> largeK = new PriorityQueue<>(n + 1);
                for (int j = 0; j < clusterList.get(i).size(); j++) {
                    // int totalDelay = 0;
                    for (Integer nodeId : clusterList.get(i)) {
                        if (nodeId == clusterList.get(i).get(j)) {
                            continue;
                        }
                        // totalDelay += TopoUtil.getMinDelay(nodeId, clusterList.get(i).get(j));
                        if (TopoUtil.getMinDelay(nodeId, clusterList.get(i).get(j)) > maxDelay) {
                            maxDelay = TopoUtil.getMinDelay(nodeId, clusterList.get(i).get(j));
                        }
                        largeK.add(TopoUtil.getMinDelay(nodeId, clusterList.get(i).get(j)));
                        if (largeK.size() > n) {
                            largeK.poll();
                        }
                    }
                    if (maxDelay < maxDelay0) {
                        maxDelay0 = maxDelay;
                        newCenterNodeId = clusterList.get(i).get(j);
                    }
                }
                if (newCenterNodeId != clusterCenterNodeId[i]) {
                    terminateFlag = false;
                    clusterCenterNodeId[i] = newCenterNodeId;
                }
                Object tmp = largeK.poll();
                int tmpMaxDelay = 0;
                if (tmp != null) {
                    tmpMaxDelay = (Integer)tmp;
                }
                if (tmpMaxDelay > maxClusterDelay) {
                    maxClusterDelay = tmpMaxDelay;
                }
            }
//            System.out.println(clusterList);
            System.out.println(maxClusterDelay);
        }
        //System.out.println("count is " + count);
        return clusterList;
    }

    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            arrayList.add(i);
        }
        for (int i = 0; i < 10; i++) {
            getGraphPartitionResult(arrayList, 200, 0.8f);
            System.out.println("-----------------------------------------------");
        }
    }
}
