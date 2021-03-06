package com.francium.app.projectf;

import android.graphics.Color;

public class Configuration {

    public static final float GRID_NUM = 7.0f;
    public static int UNIT_SIZE = (int) (88 * Configuration.GRID_NUM);
    public static int VIEW_WIDTH = 480;
    public static int VIEW_HEIGHT = 800;
    public static int REAL_WIDTH = 480;
    public static int REAL_HEIGHT = 800;
    public static int ADP_SIZE = 0;
    public static float screentRatio = 0;
    public static float translateRatio = 0;
    public static int MOVE_THRESDHOLDER = 5;

    public static int DELAY_MS = 20;
    public static int AWARD_MAX_COUNT = 5;
    public static int BONUS_MAX_COUNT = 9;
    public static int MAX_TOKEN = 6;
    public static int NUMBER_OF_BUG = 8;

    public static int AUTOTIP_DELAY = 5 * 1000 / DELAY_MS;
    public static int MAX_TIME = 0;
    public static int MAX_TIME_SINGLE_PLAYER = 30;
    public static int MAX_TIME_MULTIPLAYER = 180;
    public static int MAX_AWARD_RATIO = 3 * 3;
    public static int MAX_SUCCESSFUL_COUNT = 3 * 3;
    public static int MAX_SINGLE_SCORE = 1000;
    public static int MAX_HEALTH_POINT = 100;
    public static int ATTACK_POWER_1 = 8;
    public static int ATTACK_POWER_2 = 5;
    public static int ATTACK_POWER_3 = 3;
    public static int ATTACK_POWER_4 = 1;
    public static int HEAL_POWER = 3;

    public static int ACHIEVEMENT_LEVEL_1_SCORE = 5000;
    public static int ACHIEVEMENT_LEVEL_2_SCORE = 10000;
    public static int ACHIEVEMENT_LEVEL_3_SCORE = 25000;
    public static int ACHIEVEMENT_LEVEL_4_SCORE = 50000;
    public static int ACHIEVEMENT_LEVEL_5_SCORE = 75000;
    public static int ACHIEVEMENT_LEVEL_6_SCORE = 100000;

    public static int MSG_SIZE = 16;

    public static int BUG_ID_BLANK = 0;
    public static int BUG_ID_GREEN = 1;
    public static int BUG_ID_YELLOW = 2;
    public static int BUG_ID_RED = 3;
    public static int BUG_ID_WHITE = 4;
    public static int BUG_ID_BLUE = 5;
    public static int BUG_ID_CYAN = 6;
    public static int BUG_ID_PURPLE = 7;

    public static int BUG_ID_ATTACK_1 = BUG_ID_RED;
    public static int BUG_ID_ATTACK_2 = BUG_ID_CYAN;
    public static int BUG_ID_ATTACK_3 = BUG_ID_BLUE;
    public static int BUG_ID_ATTACK_4 = BUG_ID_WHITE;
    public static int BUG_ID_HEAL = BUG_ID_GREEN;
    public static int BUG_ID_AWARD = BUG_ID_YELLOW;
    public static int BUG_ID_BONUS = BUG_ID_PURPLE;

    public static int COLOR_OWN_RESULT = Color.BLACK;
    public static int COLOR_PEER_RESULT = Color.BLACK;
    public static int COLOR_OWN_SCORE = Color.BLACK;
    public static int COLOR_PEER_SCORE = Color.BLACK;
    public static int COLOR_UNIT_SCORE = Color.BLACK;

    public enum E_SOUND {
        SLIDE,
        DISAPPEAR3,
        DISAPPEAR4,
        DISAPPEAR5,
        SUPER,
        SPECIALITEM,
        INVALID,
        END
    }

    public enum E_SCENARIO {
        MENU,
        GAME,
        RESULT,
    }


}
