package com.hollow.views.ui;

public interface UI {
    void setupUI();
    void act(float delta);
    void draw();
    void dispose();
    void resize(int width, int height);
}
