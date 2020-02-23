package com.company;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class ParameterServerFinder {

    public static int findParameterServerId(ArrayList<Integer> nodeIdList, float aggregationRatio) {
        ArrayList<Integer> theDelaysAtAggregationRatio = new ArrayList<>();
        int k = Math.round(nodeIdList.size() * (1 - aggregationRatio)) + 1;
        for (int i = 0; i < nodeIdList.size(); i++) {
            PriorityQueue<Integer> largeK = new PriorityQueue<>(k + 1);
            for (int j = 0; j < nodeIdList.size(); j++) {
                if (i == j) {
                    continue;
                }
                largeK.add(TopoUtil.getMinDelay(nodeIdList.get(i), nodeIdList.get(j)));
                if (largeK.size() > k) {
                        largeK.poll();
                }
            }
            theDelaysAtAggregationRatio.add(largeK.poll());
        }
        System.out.println(theDelaysAtAggregationRatio);
        int selectedNodeId = nodeIdList.get(0);
        int minDelay = theDelaysAtAggregationRatio.get(0);
        for (int nodeIndex = 1; nodeIndex < theDelaysAtAggregationRatio.size(); nodeIndex++) {
            if (theDelaysAtAggregationRatio.get(nodeIndex) < minDelay) {
                minDelay = theDelaysAtAggregationRatio.get(nodeIndex);
                selectedNodeId = nodeIdList.get(nodeIndex);
            }
        }
        System.out.println(minDelay);
        return selectedNodeId;
    }
    public static void main(String[] args) {
        TopoUtil.getGraph(1000, "/Users/xiyu/Downloads/data1000.in");
        TopoUtil.generateMinDelayMatrix();
        ArrayList<Integer> testNodeIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            testNodeIds.add(i);
        }
        findParameterServerId(testNodeIds, 0.8f);
    }
}