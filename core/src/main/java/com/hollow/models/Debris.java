package com.hollow.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Debris {
    public Vector2 position;
    public Vector2 velocity;
    public float lifeTime;
    public float maxLifeTime;
    public float rotation;
    public float angularVelocity;
    public TextureRegion texture;
    public float scale;

    public Debris(TextureRegion texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);

        this.velocity = new Vector2(MathUtils.random(-10f, 10f), MathUtils.random(5f, 15f));

        this.maxLifeTime = MathUtils.random(0.3f, 0.6f);
        this.lifeTime = this.maxLifeTime;

        this.rotation = MathUtils.random(0, 360);
        this.angularVelocity = MathUtils.random(-400f, 400f);

        this.scale = MathUtils.random(0.5f, 1.2f);
    }

    public void update(float delta) {
        velocity.y += -30f * delta;

        position.add(velocity.x * delta, velocity.y * delta);
        rotation += angularVelocity * delta;

        lifeTime -= delta;
    }

    public boolean isDead() {
        return lifeTime <= 0;
    }
}
