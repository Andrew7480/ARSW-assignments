package com.matrix.matrixgame.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.matrix.matrixgame.entity.Agent;
import com.matrix.matrixgame.entity.Neo;
import com.matrix.matrixgame.entity.Phone;
import com.matrix.matrixgame.entity.Wall;

public class Board {

    private final Random random = new Random();

    private final int size;

    private Neo neo;

    private final List<Phone> phones;

    private final List<Agent> agents;

    private final List<Wall> walls;

    public Board(int size) {
        this.size = size;
        phones = new ArrayList<>();
        agents = new ArrayList<>();
        walls = new ArrayList<>();
    }

    public int getSize() {
        return size;
    }

    public Neo getNeo() {
        return neo;
    }

    public void setNeo(Neo neo) {
        this.neo = neo;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public boolean isInside(Position position) {
        return position.row() >= 0
                && position.row() < size
                && position.col() >= 0
                && position.col() < size;
    }

    public boolean reachedPhone(Position position) {
        return phones.stream()
                .anyMatch(phone -> phone.getPosition()
                        .equals(position));
    }

    public boolean isFree(Position position) {

        if (neo != null && neo.getPosition().equals(position)) {
            return false;
        }

        for (Phone phone : phones) {
            if (phone.getPosition().equals(position)) {
                return false;
            }
        }

        for (Agent agent : agents) {
            if (agent.getPosition().equals(position)) {
                return false;
            }
        }

        for (Wall wall : walls) {
            if (wall.getPosition().equals(position)) {
                return false;
            }
        }

        return true;
    }

    public Position randomEmptyPosition() {
        int attempts = 0;
        Position position;
        do {

            int row = random.nextInt(size);

            int col = random.nextInt(size);

            position = new Position(row, col);

            attempts++;
            if (attempts > size * size * 2) {
                throw new RuntimeException("No empty positions available");
            }

        } while (!isFree(position));

        return position;
    }

    public boolean pathExists() {

        if (neo == null || phones.isEmpty()) {
            return false;
        }

        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        Position start = neo.getPosition();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (reachedPhone(current)) {
                return true;
            }
            for (Position neighbor : getNeighbors(current)) {

                if (!visited.contains(neighbor) && !isWall(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    private List<Position> getNeighbors(Position current) {

        List<Position> neighbors = new ArrayList<>();

        Position up = new Position(current.row() - 1, current.col());

        Position down = new Position(current.row() + 1, current.col());

        Position left = new Position(current.row(), current.col() - 1);

        Position right = new Position(current.row(), current.col() + 1);

        if (isInside(up)) neighbors.add(up);

        if (isInside(down)) neighbors.add(down);

        if (isInside(left)) neighbors.add(left);

        if (isInside(right)) neighbors.add(right);

        return neighbors;
    }

    public boolean isWall(Position position) {
        return walls.stream()
                .anyMatch(w -> w.getPosition()
                        .equals(position));
    }
}