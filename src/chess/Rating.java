package chess;

// attribute to https://www.chessprogramming.org/Simplified_Evaluation_Function

interface Rater {
    int rateMaterial();

    int rateAttack();

    int rateMovablitly(int listLength, int depth, int material);

    int ratePositional(int material);
}

public class Rating implements Rater {
    // Encourage the pawns to advance
    static int[][] pawnBoard = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    // Occupy the 7th rank and avoid a, h columns
    static int[][] rookBoard = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};

    // With knights, we simply encourage them to go to the center. Standing on the edge is a bad idea.
    static int[][] knightBoard = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}};

    // We avoid corners and borders. Additionally, we prefer squares like b3, c4, b5, d3 and the central ones.
    static int[][] bishopBoard = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};
    static int[][] queenBoard = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};
    static int[][] kingMidBoard = {
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}};
    static int[][] kingEndBoard = {
            {-50, -40, -30, -20, -20, -30, -40, -50},
            {-30, -20, -10, 0, 0, -10, -20, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -30, 0, 0, 0, 0, -30, -30},
            {-50, -30, -30, -30, -30, -30, -30, -50}};

    public static int rating(int list, int depth) {

        Rating r = new Rating();

        int counter = 0;

        int material = r.rateMaterial();
        counter += r.rateAttack();
        counter += material;
        counter += r.rateMovablitly(list, depth, material);
        counter += r.ratePositional(material);

        HadesChess.flipBoard();

        material = r.rateMaterial();
        counter -= r.rateAttack();
        counter -= material;
        counter -= r.rateMovablitly(list, depth, material);
        counter -= r.ratePositional(material);

        HadesChess.flipBoard();

        return -(counter + depth * 50);
    }

    @Override
    public int rateMaterial() {
        int counter = 0, bishopCounter = 0;
        for (int i = 0; i < 64; i++) {
            switch (HadesChess.chessBoard[i / 8][i % 8]) {
                case "P":
                    counter += 100;
                    break;
                case "R":
                    counter += 500;
                    break;
                case "K":
                    counter += 300;
                    break;
                case "B":
                    bishopCounter += 1;
                    break;
                case "Q":
                    counter += 900;
                    break;
            }
        }
        if (bishopCounter >= 2) {
            counter += 300 * bishopCounter;
        } else if (bishopCounter == 1) {
            counter += 250;
        }

        return counter;
    }


    @Override
    public int rateAttack() {
        int counter = 0;
        int tempPositionC = HadesChess.kingPositionC;
        for (int i = 0; i < 64; i++) {
            switch (HadesChess.chessBoard[i / 8][i % 8]) {
                case "P": {
                    HadesChess.kingPositionC = i;
                    if (!HadesChess.kingSafe()) {
                        counter -= 64;
                    }
                }
                break;
                case "R": {
                    HadesChess.kingPositionC = i;
                    if (!HadesChess.kingSafe()) {
                        counter -= 500;
                    }
                }
                break;
                case "K", "B": {
                    HadesChess.kingPositionC = i;
                    if (!HadesChess.kingSafe()) {
                        counter -= 300;
                    }
                }
                break;
                case "Q": {
                    HadesChess.kingPositionC = i;
                    if (!HadesChess.kingSafe()) {
                        counter -= 900;
                    }
                }
                break;
            }
        }
        HadesChess.kingPositionC = tempPositionC;
        if (!HadesChess.kingSafe()) {
            counter -= 200;
        }
        return counter / 2;
    }


    @Override
    public int rateMovablitly(int listLength, int depth, int material) {
        int counter = 0;
        counter += listLength; // 5 points per valid move
        if (listLength == 0) { // current side is in checkmate or stalemate
            if (!HadesChess.kingSafe()) { // if checkmate
                counter += -200000 * depth;
            } else { // if stalemate
                counter += -150000 * depth;
            }
        }

        return counter; // return 0;
    }


    @Override
    public int ratePositional(int material) {
        int counter = 0;
        for (int i = 0; i < 64; i++) {
            switch (HadesChess.chessBoard[i / 8][i % 8]) {
                case "P":
                    counter += pawnBoard[i / 8][i % 8];
                    break;
                case "R":
                    counter += rookBoard[i / 8][i % 8];
                    break;
                case "K":
                    counter += knightBoard[i / 8][i % 8];
                    break;
                case "B":
                    counter += bishopBoard[i / 8][i % 8];
                    break;
                case "Q":
                    counter += queenBoard[i / 8][i % 8];
                    break;
                case "A":
                    if (material >= 1750) {
                        counter += kingMidBoard[i / 8][i % 8];
                        counter += HadesChess.possibleA(HadesChess.kingPositionC).length() * 10;
                    } else {
                        counter += kingEndBoard[i / 8][i % 8];
                        counter += HadesChess.possibleA(HadesChess.kingPositionC).length() * 30;
                    }
                    break;
            }
        }

        return counter;
    }
}