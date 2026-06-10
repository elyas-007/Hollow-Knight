package com.hollow.utils;

public class Constants {
    private Constants() {}


    public static final class Knight {
        public static final int MAX_MASK = 5;
        public static final int MAX_SOUL = 99;
        public static final int SOUL_PER_HIT = 11;
        public static final int SPELL_SOUL_COST = 33;
        public static final float FOCUS_HEAL_TIME = 1.5f;
        public static final float INVINCIBILITY_DURATION = 1.0f;
        public static final int MAX_CHARM_NOTCHES = 3;
        public static final float MOVE_SPEED = 200f;
        public static final float JUMP_FORCE = 500f;
        public static final float GRAVITY = -1000f;
        public static final float DASH_DURATION = 0.2f;
        public static final float DASH_SPEED_MULTIPLIER = 2.5f;
    }

    public static final class Boss {
        public static final float STUN_HP = 0.5f;
        public static final float STUN_DURATION = 4.0f;
    }
}
