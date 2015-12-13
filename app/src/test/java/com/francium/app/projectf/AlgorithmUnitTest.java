package com.francium.app.projectf;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AlgorithmUnitTest {

    @Test
    public void TestIsInLineX() throws Exception {
        int testArray[][] = new int[7][7];;
        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }
        //Check
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                assertTrue(!GameEngine.isInLineX(testArray, i, j));
            }
        }

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = 1;
            }
        }
        //Check
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                assertTrue(GameEngine.isInLineX(testArray, i, j));
            }
        }

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }

        testArray[0][0] = 1;
        testArray[1][0] = 1;
        testArray[2][0] = 1;

        //Check
        assertTrue(GameEngine.isInLineX(testArray, 0, 0));
        assertTrue(GameEngine.isInLineX(testArray, 1, 0));
        assertTrue(GameEngine.isInLineX(testArray, 2, 0));

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }

        testArray[4][6] = 3;
        testArray[5][6] = 3;
        testArray[6][6] = 3;

        //Check
        assertTrue(GameEngine.isInLineX(testArray, 4, 6));
        assertTrue(GameEngine.isInLineX(testArray, 5, 6));
        assertTrue(GameEngine.isInLineX(testArray, 6, 6));
    }

    @Test
    public void TestIsInLineY() throws Exception {
        int testArray[][] = new int[7][7];;
        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }
        //Check
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                assertTrue(!GameEngine.isInLineY(testArray, i, j));
            }
        }

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = 1;
            }
        }
        //Check
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                assertTrue(GameEngine.isInLineY(testArray, i, j));
            }
        }

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }

        testArray[0][0] = 1;
        testArray[0][1] = 1;
        testArray[0][2] = 1;

        //Check
        assertTrue(GameEngine.isInLineY(testArray, 0, 0));
        assertTrue(GameEngine.isInLineY(testArray, 0, 1));
        assertTrue(GameEngine.isInLineY(testArray, 0, 2));

        //Make no possible move
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testArray[i][j] = ((i + j) % 7) + 1;
            }
        }

        testArray[1][3] = 1;
        testArray[1][4] = 1;
        testArray[1][5] = 1;

        //Check
        assertTrue(GameEngine.isInLineY(testArray, 1, 3));
        assertTrue(GameEngine.isInLineY(testArray, 1, 4));
        assertTrue(GameEngine.isInLineY(testArray, 1, 5));

    }
}
