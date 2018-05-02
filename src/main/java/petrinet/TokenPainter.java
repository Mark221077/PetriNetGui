package petrinet;


import java.awt.*;

//paints the tokens as balls
public class TokenPainter {

    private Point src;
    private int tokens;

    private final int TOKEN_SIZE = 8;

    public TokenPainter(Point src, int tokens) {
        this.src = src;
        this.tokens = tokens;
    }

    public void paint(Graphics g) {

        int[][][] gridCoords = new int[3][3][2];

        final int margin = NetElement.ELEMENT_SIZE / 5;         //margin so we dont go outside the outer circle
        final int cellSize = (NetElement.ELEMENT_SIZE - 2 * margin) / 3;      //the size of the balls

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                gridCoords[row][col][0] = src.x + col * cellSize + margin;        //construct the grid to which we paint the balls
                gridCoords[row][col][1] = src.y + row * cellSize + margin;
            }
        }

        //paint the balls to the grid
        switch (tokens) {
            case 0:
                break;
            case 1:
                g.fillOval(gridCoords[1][1][0], gridCoords[1][1][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 2:
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 3:
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][1][0], gridCoords[1][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 4:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 5:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][1][0], gridCoords[1][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 6:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][0][0], gridCoords[1][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][2][0], gridCoords[1][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 7:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][0][0], gridCoords[1][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][1][0], gridCoords[1][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][2][0], gridCoords[1][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 8:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][1][0], gridCoords[0][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][0][0], gridCoords[1][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][2][0], gridCoords[1][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][1][0], gridCoords[2][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            case 9:
                g.fillOval(gridCoords[0][0][0], gridCoords[0][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][1][0], gridCoords[0][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[0][2][0], gridCoords[0][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][0][0], gridCoords[1][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][1][0], gridCoords[1][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[1][2][0], gridCoords[1][2][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][0][0], gridCoords[2][0][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][1][0], gridCoords[2][1][1], TOKEN_SIZE, TOKEN_SIZE);
                g.fillOval(gridCoords[2][2][0], gridCoords[2][2][1], TOKEN_SIZE, TOKEN_SIZE);
                break;

            default:
                g.drawString(String.valueOf(tokens), src.x + margin, src.y + NetElement.ELEMENT_SIZE / 2);
        }
    }
}
