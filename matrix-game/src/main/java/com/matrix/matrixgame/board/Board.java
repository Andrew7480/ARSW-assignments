package com.matrix.matrixgame.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        Position position;
        do {

            int row = random.nextInt(size);

            int col = random.nextInt(size);

            position = new Position(row, col);

        } while (!isFree(position));

        return position;
    }
}