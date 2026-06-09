package com.matrix.matrixgame.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;

public abstract class BfsMovementStrategy implements MovementStrategy {

    @Override
    public final Position nextMove(Position from, Board board) {
        List<Position> targets = getTargets(board);
        if (targets.isEmpty()) return from;
        return bfsFirstStep(from, targets, board);
    }

    protected abstract List<Position> getTargets(Board board);

    private Position bfsFirstStep(Position from, List<Position> targets, Board board) {
        Set<Position> targetSet = new HashSet<>(targets);
        if (targetSet.contains(from)) return from;

        Map<Position, Position> parent = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            for (Position neighbor : getNeighbors(current, board)) {
                if (!parent.containsKey(neighbor) && !board.isWall(neighbor)) {
                    parent.put(neighbor, current);
                    if (targetSet.contains(neighbor)) {
                        return firstStep(from, neighbor, parent);
                    }
                    queue.add(neighbor);
                }
            }
        }
        return from;
    }

    // Walks the parent map backward from target to find the immediate next step from `from`
    private Position firstStep(Position from, Position target, Map<Position, Position> parent) {
        Position step = target;
        Position prev = parent.get(step);
        while (prev != null && !prev.equals(from)) {
            step = prev;
            prev = parent.get(step);
        }
        return step;
    }

    private List<Position> getNeighbors(Position pos, Board board) {
        List<Position> neighbors = new ArrayList<>();
        int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : dirs) {
            Position n = new Position(pos.row() + d[0], pos.col() + d[1]);
            if (board.isInside(n)) neighbors.add(n);
        }
        return neighbors;
    }
}
