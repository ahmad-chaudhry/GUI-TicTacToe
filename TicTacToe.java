/**
* Tic-tac-toe (also known as noughts and crosses or Xs and Os) is a paper-and-pencil game 
* for two players, X and O, who take turns marking the spaces in a 3x3 grid. The player who 
* succeeds in placing three of their marks in a horizontal, vertical, or diagonal row wins the game
*
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe
{
    //the winning combinations
    private static int[][] wincombinations = new int[][] 
    {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, //horizontal wins
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, //vertical wins
            {0, 4, 8}, {2, 4, 6}             //diagonal wins
    };
    //strings for x,o, and empty
    private static final String cross = "X";
    private static final String nought = "O";
    private static final String empty = "";

    //font settings
    private static final Font font = new Font("Arial", Font.BOLD, 60);
    private static final Font normal = new Font("Arial", Font.BOLD, 12);
    
    //used to track turn and output option
    private int turns, option, option1;
    //used to track if gameover or not
    private boolean gameover;
    //game status variables
    private int xWon, oWon, tie, gamesplayed;
    //determines who turn it is 
    int pturn;
    
    //used to create buttons
    private JButton buttons[];
    //Frame of the game
    private JFrame frame;
    //panels for game
    private JPanel bpanel, status, gamestatus;
    
    //The newgame and quit item 
    private JMenuItem quitItem;
    private JMenuItem newgameItem;
    
    private JLabel statusLabel[];
    private JLabel turnLabel;
    

    /**
    * creates TicTacToe game and plays it.
    */
    public TicTacToe()
    {
        //initializes game to play 
        drawBoard();  
        startingPiece();
    }
    
    /**
    * Draws the tictactoe game with GUI components
    * Uses JFrame, JPanel.
    */
    private void drawBoard()
    {
        //new Frame for game
        frame = new JFrame("Tic-Tac-Toe");
        
        //new Panel for buttons
        bpanel = new JPanel();
        //buttons are in a 3x3 gridlayout
        bpanel.setLayout(new GridLayout(3,3));
        bpanel.setSize(500, 500);

        //adds buttons to the b panel
        initializebuttons();
        
        //new Panel for keeping track of score
        status = new JPanel();
        status.setLayout(new GridLayout(1,4));
        status.setSize(500, 150);
        resetStatus();
        
        //new Panel for that display whos turn it is and winners.
        gamestatus = new JPanel();
        gamestatus.setLayout(new GridLayout(1,1));
        gamestatus.setSize(500, 150);
        resetTurnLabel();
        
        //new Menubar  
        JMenuBar menubar = new JMenuBar();
        //add menubar to our frame
        frame.setJMenuBar(menubar);
        //add fileMenu to menubar
        JMenu fileMenu = new JMenu("Options");
        menubar.add(fileMenu);
        //add New Game to fileMenu
        newgameItem = new JMenuItem("New Game");
        fileMenu.add(newgameItem);
        //add Quit to fileMenu
        quitItem = new JMenuItem("Quit");
        fileMenu.add(quitItem);
        
        // this allows us to use shortcuts (e.g. Ctrl-N and Ctrl-Q)
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // to save typing
        newgameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
      
        //actionListener to track any menu inputs
        menuTracker();
        
        //sets default close operation and adds content to frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamestatus, BorderLayout.SOUTH);
        frame.getContentPane().add(bpanel, BorderLayout.CENTER);
        frame.getContentPane().add(status, BorderLayout.NORTH);
        
        //sets frame size, makes visible and disables sizeable
        frame.setSize(550, 550);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    /**
    * Creates the list of buttons represented by a 3x3 grid 
    * 
    * Uses JButton at every index with a ActionListener and checks if 
    * its player x or player o's turn. Then checks if they can click the grid section
    * requested. Disables the button, then updates all displays and checks if the current
    * player has won. If so declares winner and shows options for new game, etc...
    */
    private void initializebuttons()
    {      
        //create 9 new buttons
        buttons = new JButton[9];
        //set game over to false to begin the game
        gameover = false;
        //sets turns to 0 as its a new game being played
        turns = 0;
        //for each button, player selects their spot on the playing grid
        for(int i = 0; i <= 8; i++)
        {
            //new button
            buttons[i] = new JButton();
            //makes button empty
            buttons[i].setText(empty);
            //adds button to panel
            bpanel.add(buttons[i]);
            //action for the button
            buttons[i].addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e) 
                {
                    //get the particular button that was clicked
                    JButton buttonClicked = (JButton)e.getSource(); 

                    //if turn is even then X is playing else O is playing
                    if(pturn%2 == 0)
                    {
                        buttonClicked.setText("X");
                    }
                    else
                    {
                        buttonClicked.setText("O");
                    }
                    //diables the button pushed so that it cannot be clicked again for the duration of the game
                    if(pturn%2 == 0 && buttonClicked.isEnabled() && !gameover)
                    {
                        //first player turn
                        buttonClicked.setText(cross);
                        buttonClicked.setFont(font);
                        buttonClicked.setEnabled(false);
                    } else if(pturn%2 != 0 && buttonClicked.isEnabled() && !gameover){
                        //second player turn
                        buttonClicked.setText(nought);
                        buttonClicked.setFont(font);
                        buttonClicked.setEnabled(false);
                    }
                    //increases turn by 1
                    turns++;
                    //update turn panel
                    updateTurnLabel();
                    //checks if either player won
                    checkForWin();
                    //if a player one shows game options
                    gameoveroptions();
                    //next player
                    pturn++;
                    
                } 
            });
        }
        
    }
    
    /**
    * Tracks any inputs a user makes to the menu 
    * 
    * User can request to start a new game or quit. 
    */
    private void menuTracker()
    {
        //create an anonymous inner class 
        newgameItem.addActionListener(new ActionListener() 
        { 
            public void actionPerformed(ActionEvent e)
            {
                // get the action 
                Object o = e.getSource();  
                
                JMenuItem item = (JMenuItem)o;
                //if user action presses New Game, resets the whole game
                if (item == newgameItem) 
                { 
                    //reset all stats
                    xWon = 0;
                    oWon = 0;
                    tie = 0;
                    gamesplayed = 0;
                    startingPiece();
                    //buttons reset
                    resetButtons();
                    //gameover is reset for next game
                    gameover = false;
                }
        }
        } 
        ); 
        quitItem.addActionListener(new ActionListener() 
        { 
            public void actionPerformed(ActionEvent event)
            {
                // quits the game
                System.exit(0); 
            }
        }
        ); 
    }
    
    /**
    * Player 1 decides if they want to start with X or O. 
    */
    private void startingPiece()
    {
        //player 1 decides if they want to start with the X or O piece
        pturn = JOptionPane.showConfirmDialog(null, "Would player 1 like to be X (Choose No for O)", "Starting Game", JOptionPane.YES_NO_OPTION);
    }
        
    /**   
     * Check if a player has won using the winning combinations, goes through each potential win
     * and see if a players combination matches
    */    
    private void checkForWin()
    {
        //goes through board and checks if a player has won, if so return gameover as true
        for (int i = 0; i < 8; i++)
        {
            if(buttons[wincombinations[i][0]].getText().equals(buttons[wincombinations[i][1]].getText()) &&
            buttons[wincombinations[i][1]].getText().equals(buttons[wincombinations[i][2]].getText()) &&
            !buttons[wincombinations[i][0]].getText().equals(empty))
            {
                gameover = true;
            }
        }
    }
          
    /**
    * After a game has finished, pop ups a screen displaying options for the user to select, 
    * these include starting a new game or ending. 
    */
    private void gameoveroptions()
    {
        String statement = null;
        //if gameover is true shows winner and options
        if (gameover == true)
        {
            //updates turn label to indicate who won
            updateTurnLabel();
            //outputs who won in a optionpane
            if (pturn%2 != 0) 
            {
                statement = "Player O has won the match. Continue?";
            }
            else 
            {
                statement = "Player X has won the match. Continue?";
            }
            option = JOptionPane.showConfirmDialog(null, statement, "Game Over", JOptionPane.YES_NO_OPTION);
            //if player x won, else player o won
            if ( pturn%2 == 0 ) 
            { 
                xWon++; 
            } 
            else 
            { 
                oWon++; 
            }
        }
        //no player won, game was a tie
        else if (turns == 9 && !gameover)
        {
            //gameover becomes true
            gameover = true;
            //turns are reset 
            turns = 0;
            //updates turn label 
            updateTurnLabel();
            //shows dialogue screen with options
            option = JOptionPane.showConfirmDialog(null, "This game tied. Continue?", "GAME OVER", JOptionPane.YES_NO_OPTION);
            //tie count increases
            tie++;
        }
        //user chooses yes for option 
        if (option == JOptionPane.YES_OPTION && gameover)
        {
            //game count increases
            gamesplayed+=1;
            //buttons reset
            resetButtons();
            //gameover is reset for next game
            gameover = false;
            //updates turn label 
            updateTurnLabel();
        } 
        //user chooses no and game quits
        else if(option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION)
        {
            System.exit(0);
        }
    }
    
    /**
    * Reset the buttons for a new game, also updates the game status and turn status 
    */
    private void resetButtons()
    {
        //resets the buttons to empty for new game
        for(int i = 0; i <= 8; i++)
        {
            buttons[i].setText(empty);
            buttons[i].setEnabled(true);
        }
        //resets turns
        turns = 0;
        //updates the game status
        updateStatus();
        //updates turn status 
        updateTurnLabel();
    }        
    
    /**
    * Shows x,o, and tie winnings, and reset all to 0
    */
    private void resetStatus()
    {
        //creates four new labels
        statusLabel = new JLabel[4];
        //sets all to 0
        xWon = 0;
        oWon = 0;
        tie = 0;
        gamesplayed = 0;
        //initializes the labels
        for(int i = 0; i < 4; i++){
        statusLabel[i] = new JLabel();
        }
        //Label for x winnings
        statusLabel[0].setText("X-Wins: \n\t\t"+xWon + "");
        statusLabel[0].setFont(normal);
        status.add(statusLabel[0]);
        //Label for o winnings
        statusLabel[1].setText("O-Wins: \n\t\t"+oWon + "");
        statusLabel[1].setFont(normal);
        status.add(statusLabel[1]);
        //Label for tie winnings 
        statusLabel[2].setText("Ties: \n\t\t"+tie + "");
        statusLabel[2].setFont(normal);
        status.add(statusLabel[2]);
        //Label for Games Played 
        statusLabel[3].setText("Games Played: \n\t\t"+gamesplayed + "");
        statusLabel[3].setFont(normal);
        status.add(statusLabel[3]);
    }
        
    /**
    * Updates the game status labels
    */
    private void updateStatus()
    {
        //updates x winnings
        statusLabel[0].setText("X-Wins: \n\t\t"+xWon + "");
        statusLabel[0].setFont(normal);
        //updates o winnings
        statusLabel[1].setText("O-Wins: \n\t\t"+oWon + "");
        statusLabel[1].setFont(normal);
        //updates tie winnings
        statusLabel[2].setText("Ties: \n\t\t"+tie + "");
        statusLabel[2].setFont(normal);
        //updates games played winnings
        statusLabel[3].setText("Games Played: \n\t\t"+gamesplayed + "");
        statusLabel[3].setFont(normal);
    }
    
    /**
    * Shows current progress of game, such as the game being x or o's turn. Who won the game.
    */
    private void resetTurnLabel()
    {
        //creates the turn label
        turnLabel = new JLabel();
        //default for a new game the first time is x turn first
        turnLabel.setText("game in progess and X's Turn");
        turnLabel.setFont(normal);
        //adds label to gamestatus panel
        gamestatus.add(turnLabel);
    }
    
    /**
    * displays x-wins, o-wins, ties and games played and set to 0.
    */
    private void updateTurnLabel()
    {
       // it is currently O's turn
       if (pturn%2 == 0 && !gameover) 
       { 
           turnLabel.setText("game in progess and O's Turn");  
       } 
       // it is currently X's turn
       else if (pturn%2 != 0 && !gameover)
       { 
           turnLabel.setText("game in progess and X's Turn");
       }
       // No turns left, turns were reset and game tied
       else if (gameover && turns == 0)
       {
           turnLabel.setText("Game Tied");
       }
       // X has won the game
       else if (gameover && pturn%2 == 0 && turns <= 9)
       {
           turnLabel.setText("X Wins");
       }
       // O has won the game
       else if (gameover && pturn%2 != 0 && turns <= 9)
       {
           turnLabel.setText("O Wins");
       }
       turnLabel.setFont(normal);
    }
    
}