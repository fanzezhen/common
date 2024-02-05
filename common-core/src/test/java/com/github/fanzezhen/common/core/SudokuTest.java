package com.github.fanzezhen.common.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

//注意这个类叫Solution
public class SudokuTest {
    @Test
    public static void main(String[] args) {
        //直接在这里填写数独题目
        char[][] input = new char[][]{
            {'3', '2', '4', '.', '.', '.', '8', '7', '5'},
            {'1', '6', '7', '5', '3', '8', '.', '2', '.'},
            {'.', '.', '.', '7', '4', '2', '3', '1', '6'},
            {'.', '3', '.', '.', '7', '.', '.', '.', '.'},
            {'7', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '5', '6', '.', '3', '7'},
            {'2', '9', '8', '6', '1', '7', '5', '4', '.'},
            {'6', '.', '3', '2', '9', '5', '7', '8', '1'},
            {'5', '7', '1', '.', '.', '.', '.', '.', '.'},
        };
//这里改成你的类名字
        new SudokuTest().solveSudoku(input);
        for (char[] chars : input) {
            System.out.println();
            for (char c : chars) {
                System.out.print(c+ " ");
            }
        }
        Assertions.assertTrue(true);
    }
    private boolean[][] line = new boolean[9][9];
    private boolean[][] column = new boolean[9][9];
    private boolean[][][] block = new boolean[3][3][9];
    private boolean valid = false;
    private List<int[]> spaces = new ArrayList<int[]>();

    public  void solveSudoku(char[][] board) {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] == '.') {
                    spaces.add(new int[] {i, j});
                } else {
                    int digit = board[i][j] - '0' - 1;
                    line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = true;
                }
            }
        }

        dfs(board, 0);
    }

    public void dfs(char[][] board, int pos) {
        if (pos == spaces.size()) {
            valid = true;
            return;
        }

        int[] space = spaces.get(pos);
        int i = space[0], j = space[1];
        for (int digit = 0; digit < 9 && !valid; ++digit) {
            if (!line[i][digit] && !column[j][digit] && !block[i / 3][j / 3][digit]) {
                line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = true;
                board[i][j] = (char) (digit + '0' + 1);
                dfs(board, pos + 1);
                line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = false;
            }
        }
    }
}

