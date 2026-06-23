package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Vector2;

public interface BossBehavior {
    void enter(FalseKnight boss);
    void update(FalseKnight boss, float delta, Vector2 playerPos);
    void exit(FalseKnight boss);
}
