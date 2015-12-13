package com.francium.app.projectf;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreUnitTest {
    @Test
    public void TestScore() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(1, mScoreHandler.getOwnScore());
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(4);
        assertEquals(2, mScoreHandler.getOwnScore());
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(5);
        assertEquals(3, mScoreHandler.getOwnScore());
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(6);
        assertEquals(4, mScoreHandler.getOwnScore());
    }

    @Test
    public void TestCombo() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(1, mScoreHandler.getOwnScore());
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(2 + 1, mScoreHandler.getOwnScore());
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(3 + 2 + 1, mScoreHandler.getOwnScore());
        mScoreHandler.resetCombo();
        mScoreHandler.award(3);
        assertEquals(3 + 2 + 1, mScoreHandler.getOwnScore());
    }

    @Test
    public void TestAwardRatio() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(1, mScoreHandler.getOwnScore());
        mScoreHandler.increaseAwardRatio();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(2 + 1, mScoreHandler.getOwnScore());
        mScoreHandler.increaseAwardRatio();
        mScoreHandler.increaseCombo();
        mScoreHandler.award(3);
        assertEquals(8, mScoreHandler.getOwnScore());
    }

    @Test
    public void TestMaxAwardScore() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.award(100);
        assertTrue(Configuration.MAX_SINGLE_SCORE > mScoreHandler.getOwnScore());
    }

    @Test
    public void TestHealthPoint() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.decreaseOwnHealth(1);
        assertEquals(Configuration.MAX_HEALTH_POINT - 1, mScoreHandler.getOwnHealthPoint());
        mScoreHandler.increaseOwnHealth(1);
        assertEquals(Configuration.MAX_HEALTH_POINT, mScoreHandler.getOwnHealthPoint());
        mScoreHandler.increaseOwnHealth(1);
        assertEquals(Configuration.MAX_HEALTH_POINT, mScoreHandler.getOwnHealthPoint());
    }

    @Test
    public void TestAttackPoint() throws Exception {
        ScoreHandler mScoreHandler;
        mScoreHandler = new ScoreHandler();
        mScoreHandler.init();
        mScoreHandler.increaseAttackPoint(10);
        assertEquals(10, mScoreHandler.getAttackPoint());
        mScoreHandler.increaseAttackPoint(20);
        assertEquals(20, mScoreHandler.getAttackPoint());
        assertEquals(0, mScoreHandler.getAttackPoint());
    }
}