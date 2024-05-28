package com.byr.project.domain.po;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Graph {
    // 存储图的边，键为起始顶点，值为从该顶点出发的所有边
    private Map<Integer, List<Edge>> edges = new HashMap<>();
    // 存储图的顶点，键为顶点id，值为顶点对象
    private Map<Integer, Vertex> vertices = new HashMap<>();

// 添加一条边到图中
public void addEdge(int start, int end, BigDecimal distance, int roadId, double startX, double startY, double endX, double endY) {
    // 如果起始顶点不存在，则添加
    edges.putIfAbsent(start, new ArrayList<>());
    // 检查是否已经存在从同一起点到同一终点的边
    List<Edge> startEdges = edges.get(start);
    Edge oldEdge = startEdges.stream().filter(edge -> edge.end == end).findFirst().orElse(null);
    // 如果存在并且新的边的距离更短，那么就删除旧的边
    if (oldEdge != null && oldEdge.distance.compareTo(distance) > 0) {
        startEdges.remove(oldEdge);
    }
    // 添加新的边
    startEdges.add(new Edge(start, end, distance, roadId));

    // 如果终止顶点不存在，则添加
    edges.putIfAbsent(end, new ArrayList<>());
    // 检查是否已经存在从同一终点到同一起点的边
    List<Edge> endEdges = edges.get(end);
    oldEdge = endEdges.stream().filter(edge -> edge.start == start).findFirst().orElse(null);
    // 如果存在并且新的边的距离更短，那么就删除旧的边
    if (oldEdge != null && oldEdge.distance.compareTo(distance) > 0) {
        endEdges.remove(oldEdge);
    }
    // 添加新的边
    endEdges.add(new Edge(end, start, distance, roadId));

    // 如果起始顶点不存在，则添加
    vertices.putIfAbsent(start, new Vertex(start, startX, startY));
    // 如果终止顶点不存在，则添加
    vertices.putIfAbsent(end, new Vertex(end, endX, endY));
}

    // 使用A*算法找到从start到end的最短路径
    public List<Integer> shortestPath(int start, int end) {
        // gScores存储从起点到每个顶点的最短距离
        Map<Integer, BigDecimal> gScores = new HashMap<>();
        // fScores存储从起点通过每个顶点到终点的预估距离
        Map<Integer, BigDecimal> fScores = new HashMap<>();
        // cameFrom存储每个顶点的前一个顶点
        Map<Integer, Edge> cameFrom = new HashMap<>();
        // 优先队列，根据fScores的值决定顶点的优先级
        PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.comparing(edge -> fScores.get(edge.end)));

        // 初始化起点的gScore和fScore
        gScores.put(start, BigDecimal.ZERO);
        fScores.put(start, heuristicCostEstimate(start, end));
        // 将起点添加到队列中
        queue.add(new Edge(-1, start, BigDecimal.ZERO, -1));

        // 当队列不为空时，继续搜索
        while (!queue.isEmpty()) {
            // 取出队列中fScore最小的顶点
            Edge current = queue.poll();
            int currentNode = current.end;

            // 如果当前顶点是终点，那么找到了最短路径
            if (currentNode == end) {
                List<Integer> path = new ArrayList<>();
                // 通过cameFrom回溯找到最短路径
                while (cameFrom.containsKey(currentNode)) {
                    path.add(cameFrom.get(currentNode).roadId);
                    log.info("最短路径中的顶点id: " + currentNode);
                    currentNode = cameFrom.get(currentNode).start;
                }
                Collections.reverse(path);
                return path;
            }

            // 如果当前顶点没有出边，跳过
            if (!edges.containsKey(currentNode)) {
                continue;
            }

            // 遍历当前顶点的所有邻居
            for (Edge neighbor : edges.get(currentNode)) {
                // 计算从起点到邻居的tentative gScore
                BigDecimal tentativeGScore = gScores.get(currentNode).add(neighbor.distance);
                // 如果tentative gScore小于邻居的当前gScore，更新邻居的gScore和fScore，将邻居添加到队列中
                if (!gScores.containsKey(neighbor.end) || tentativeGScore.compareTo(gScores.get(neighbor.end)) < 0) {
                    cameFrom.put(neighbor.end, neighbor);
                    gScores.put(neighbor.end, tentativeGScore);
                    fScores.put(neighbor.end, tentativeGScore.add(heuristicCostEstimate(neighbor.end, end)));
                    queue.add(new Edge(currentNode, neighbor.end, tentativeGScore, neighbor.roadId));
                }
            }
        }

        // 如果没有找到路径，返回null
        return null;
    }

    // 计算从start到end的启发式距离（直线距离）
    private BigDecimal heuristicCostEstimate(int start, int end) {
        Vertex startVertex = vertices.get(start);
        Vertex endVertex = vertices.get(end);
        double dx = endVertex.x - startVertex.x;
        double dy = endVertex.y - startVertex.y;
        return BigDecimal.valueOf(Math.sqrt(dx * dx + dy * dy));
    }

    public static Graph fromListByDistance(List<? extends Road> roads, List<? extends Building> buildings) {
        return fromList(roads, buildings, false);
    }

    public static Graph fromListByTime(List<? extends Road> roads, List<? extends Building> buildings) {
        return fromList(roads, buildings, true);
    }

    // 从给定的道路和建筑列表创建图
        public static <R extends Road, B extends Building> Graph fromList(List<R> roads, List<B> buildings, boolean isTimeBased) {
            Graph graph = new Graph();
            Map<Integer, B> buildingsMap = buildings.stream()
                    .collect(Collectors.toMap(Building::getId, Function.identity()));
            for (R road : roads) {
                B startBuilding = buildingsMap.get(road.getStartPoint());
                B endBuilding = buildingsMap.get(road.getEndPoint());
                if (isTimeBased) {
                    graph.addEdge(road.getStartPoint(), road.getEndPoint(), road.getTime(), road.getId(),
                            startBuilding.getX(), startBuilding.getY(), endBuilding.getX(), endBuilding.getY());
                } else {
                    graph.addEdge(road.getStartPoint(), road.getEndPoint(), road.getDistance(), road.getId(),
                            startBuilding.getX(), startBuilding.getY(), endBuilding.getX(), endBuilding.getY());
                }
            }
            return graph;
        }

    // 内部类，表示图的边
    private static class Edge {
        int start;
        int end;
        BigDecimal distance;
        int roadId;

        Edge(int start, int end, BigDecimal distance, int roadId) {
            this.start = start;
            this.end = end;
            this.distance = distance;
            this.roadId = roadId;
        }
    }

    // 内部类，表示图的顶点
    private static class Vertex {
        int id;
        double x;
        double y;

        Vertex(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}


