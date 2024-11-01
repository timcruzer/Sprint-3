package sprintPackage;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;	
import java.io.IOException;
import javax.swing.border.MatteBorder;


// create initial board parameters
public class SOSBoardGUI extends JFrame {

    public static final int CELL_SIZE = 50; // for UI of board in game
    public static final int GRID_WIDTH = 1; 
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;

    public static final int CELL_PADDING = CELL_SIZE / 5; // for S and O letters in game
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; 
    public static final int SYMBOL_STROKE_WIDTH = 2;

    private gameBoard gameBoard;
    private JLabel gameStatusBar;

    private SOSGamemodes game;

    private boolean gameOver;

    JFrame jf;
    JRadioButton blueButton1;
    JRadioButton blueButton2;
    JRadioButton redButton1;
    JRadioButton redButton2;
    
    class gameBoard extends JPanel {
        gameBoard() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (game.getGameState() == SOSGamemodes.GameState.PLAYING) {
                        int rowSelected = e.getY() / CELL_SIZE;
                        int colSelected = e.getX() / CELL_SIZE;
                        char turn = game.getTurn();
                        int type;
                        if (turn == 'B')
                            type = blueButton1.isSelected() ? 0 : 1;
                        else
                            type = redButton1.isSelected() ? 0 : 1;
                        game.makeMove(rowSelected, colSelected, type);
                    } else {
                        game.resetGame();
                    }
                    repaint();
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            drawGridLines(g);
            drawBoard(g);
            drawLines(g);
            printStatusBar();
        }

        private void drawGridLines(Graphics g) { // draw grids so players can see squares
            g.setColor(Color.LIGHT_GRAY);
            for (int row = 1; row < game.getTotalRows(); ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF, CELL_SIZE * game.getTotalRows() - 1, GRID_WIDTH,
                        GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < game.getTotalColumns(); ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0, GRID_WIDTH,
                        CELL_SIZE * game.getTotalColumns() - 1, GRID_WIDTH, GRID_WIDTH);
            }
        }

        private void drawBoard(Graphics g) { // draw board for players to interact
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int row = 0; row < game.getTotalRows(); ++row) {
                for (int col = 0; col < game.getTotalColumns(); ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (game.getCell(row, col) == SOSGamemodes.Cell.S) {
                        g2d.drawArc(x1 + CELL_SIZE / 5, y1, CELL_SIZE / 2 - CELL_PADDING, CELL_SIZE / 2 - CELL_PADDING,
                                60, 210);
                        g2d.drawArc(x1 + CELL_SIZE / 5, y1 + CELL_SIZE / 2 - CELL_PADDING, CELL_SIZE / 2 - CELL_PADDING,
                                CELL_SIZE / 2 - CELL_PADDING, 240, 210);
                    } else if (game.getCell(row, col) == SOSGamemodes.Cell.O) {
                        g2d.drawOval(x1 + CELL_SIZE / 10, y1, (int) (SYMBOL_SIZE * 0.8), SYMBOL_SIZE);
                    }
                }
            }
        }

        private void drawLines(Graphics g) { // draw line through player's sequences to keep score
            ArrayList<ArrayList<Integer>> info = game.getSosInfo();
            Graphics2D g2d = (Graphics2D) g;
            if (info == null)
                return;
            for (ArrayList<Integer> it : info) {
                if (it.size() > 1) {
                    if (it.get(0) == 0)
                        g2d.setColor(Color.BLUE);
                    else
                        g2d.setColor(Color.RED);
                    for (int i = 1; i < it.size(); i += 4) {
                        int x1 = it.get(i + 1) * CELL_SIZE + CELL_SIZE / 2;
                        int y1 = it.get(i) * CELL_SIZE + CELL_SIZE / 2;
                        int x2 = it.get(i + 3) * CELL_SIZE + CELL_SIZE / 2;
                        int y2 = it.get(i + 2) * CELL_SIZE + CELL_SIZE / 2;
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }

            }
        }

        private void printStatusBar() { // let players know who's turn, and who wins/draws at the end of game
            if (game.getGameState() == SOSGamemodes.GameState.PLAYING) {
                gameStatusBar.setForeground(Color.BLACK);
                if (game.getTurn() == 'B') {
                    gameStatusBar.setText("Blue's Turn");
                    gameStatusBar.setForeground(Color.BLUE);
                } else {
                    gameStatusBar.setText("Red's Turn");
                    gameStatusBar.setForeground(Color.RED);
                }
            } else if (game.getGameState() == SOSGamemodes.GameState.DRAW) {
                gameStatusBar.setForeground(Color.BLACK);
                gameStatusBar.setText("It's a Draw! Click to play again.");
                if (!gameOver) {
                    game.writeLine("It's a Draw!\n\n");
                    gameOver = true;
                }
            } else if (game.getGameState() == SOSGamemodes.GameState.BLUE_WON) {
                gameStatusBar.setForeground(Color.BLUE);
                gameStatusBar.setText("'Blue' Won! Click to play again.");
                if (!gameOver) {
                    game.writeLine("'Blue' Won!\n\n");
                    gameOver = true;
                }
            } else if (game.getGameState() == SOSGamemodes.GameState.RED_WON) {
                gameStatusBar.setForeground(Color.RED);
                gameStatusBar.setText("'Red' Won! Click to play again.");
                if (!gameOver) {
                    game.writeLine("'Red' Won!\n\n");
                    gameOver = true;
                }
            }
        }

    }
    public SOSBoardGUI() { // initialize with 3 board size
        this(new SOSGamemodes(3));
        jf = this;
    }

    public SOSBoardGUI(SOSGamemodes game) {
        this.game = game;
        setBoardGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("SOS");
        setVisible(true);
        jf = this;
        gameOver = false;
    }
    private void setBoardGUI() { // how the GUI will appear to players
        gameBoard = new gameBoard();
        gameBoard.setPreferredSize(new Dimension(CELL_SIZE * game.getTotalRows(), CELL_SIZE * game.getTotalColumns()));
        gameStatusBar = new JLabel("  ");
        gameStatusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        gameStatusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
        p.setLayout(new BorderLayout());
        p.add(gameBoard, BorderLayout.CENTER);
        p.add(gameStatusBar, BorderLayout.SOUTH);
        contentPane.add(p, BorderLayout.CENTER);

        JPanel p1 = new JPanel(); // initialize buttons and features
        p1.setBackground(new Color(255, 255, 255));
        JLabel sosLabel = new JLabel("SOS");
        sosLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        JRadioButton sosButton1 = new JRadioButton("Simple game", true);
        sosButton1.setBackground(new Color(255, 255, 255));
        JRadioButton sosButton2 = new JRadioButton("General game");
        sosButton2.setBackground(new Color(255, 255, 255));
        JLabel boardLabel = new JLabel("Board Size");
        JTextArea text = new JTextArea("");
        text.setBackground(new Color(192, 192, 192));
        text.setFont(new Font("Arial", Font.PLAIN, 13));
        text.setPreferredSize(new Dimension(20, 20));
        JButton confirm = new JButton("Apply Size");
        
        confirm.addActionListener(new ActionListener() { // action listener for players to change board size
            public void actionPerformed(ActionEvent e) {
                try {
                	Integer.valueOf(text.getText());
        	    } catch (NumberFormatException ex) {
        	        JOptionPane.showMessageDialog(null, "Invalid entry please try again.");
        	        return;
        	    }

        	    if (Integer.valueOf(text.getText()) < 3) {
        	        JOptionPane.showMessageDialog(null, "Please enter a size that's at least 3.");
        	        return;
        	    }
                jf.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new SOSBoardGUI(new SOSGamemodes(Integer.valueOf(text.getText())));
                    }
                });

            }
        });
        ButtonGroup sosGroup = new ButtonGroup();
        sosGroup.add(sosButton1);
        sosGroup.add(sosButton2);
        sosButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.setCurrentGameType(SOSGamemodes.GameType.Simple);
            }
        });
        sosButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.setCurrentGameType(SOSGamemodes.GameType.General);
            }
        });
        
        p1.add(sosLabel);
        p1.add(sosButton1);
        p1.add(sosButton2);
        p1.add(boardLabel);
        p1.add(text);
        p1.add(confirm);
        contentPane.add(p1, BorderLayout.NORTH);
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(255, 255, 255));      
        
        blueButton1 = new JRadioButton("S", true); // how blue player's choice will look
        blueButton1.setFont(new Font("Arial", Font.PLAIN, 10));
        blueButton1.setBackground(new Color(255, 255, 255));
        blueButton1.setBounds(10, 163, 75, 31);
        blueButton2 = new JRadioButton("O");
        blueButton2.setFont(new Font("Arial", Font.PLAIN, 10));
        blueButton2.setBackground(new Color(255, 255, 255));
        blueButton2.setBounds(10, 196, 75, 31);
        ButtonGroup bluePlayerGroup = new ButtonGroup();
        bluePlayerGroup.add(blueButton1);
        bluePlayerGroup.add(blueButton2);
        p2.setLayout(null);
        p2.add(blueButton1);
        p2.add(blueButton2);

        contentPane.add(p2, BorderLayout.WEST);
        p2.setPreferredSize(new Dimension(200, 400));
        
        JLabel blueLabel = new JLabel("Blue Player");
        blueLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        blueLabel.setBounds(10, 84, 103, 31);
        p2.add(blueLabel);   

        JPanel p3 = new JPanel(); // initializations for red player
        p3.setBackground(new Color(255, 255, 255));
        redButton1 = new JRadioButton("S", true);
        redButton1.setFont(new Font("Arial", Font.PLAIN, 10));
        redButton1.setBackground(new Color(255, 255, 255));
        redButton1.setBounds(6, 164, 50, 21);
        redButton2 = new JRadioButton("O");
        redButton2.setFont(new Font("Arial", Font.PLAIN, 10));
        redButton2.setBackground(new Color(255, 255, 255));
        redButton2.setBounds(6, 197, 50, 21);
        ButtonGroup redPlayerGroup = new ButtonGroup();
        redPlayerGroup.add(redButton1);
        redPlayerGroup.add(redButton2);
        p3.setLayout(null);
        p3.add(redButton1);
        p3.add(redButton2);

        JButton newGameButton = new JButton("New Game"); // allow player to create new games if necessary
        newGameButton.setFont(new Font("Arial", Font.PLAIN, 10));
        newGameButton.setBounds(0, 369, 142, 21);
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new SOSBoardGUI(new SOSGamemodes(game.TOTALROWS));
                    }
                });
            }

        });
        p3.add(newGameButton);

        contentPane.add(p3, BorderLayout.EAST);
        p3.setPreferredSize(new Dimension(200, 400));
        
        JLabel redLabel = new JLabel("Red Player");
        redLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        redLabel.setBounds(10, 82, 103, 31);
        p3.add(redLabel);

    }


    public static void main(String[] args) { // run the program
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SOSBoardGUI();
            }
        });
    }
}
