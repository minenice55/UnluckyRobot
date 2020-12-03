/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unluckyrobot;

import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Devrin Aiden Tiongson
 */
public class UnluckyRobot {
    //non mandatory game constants
    final static int MAX_ITERS = 20;
    final static int MIN_SCORE = -1000;
    final static int MAX_SCORE = 2000;
    final static int GRID_SIZE_X = 4;
    final static int GRID_SIZE_Y = 4;
    //inputs
    final static char INPUT_UP = 'w';
    final static char INPUT_LEFT = 'a';
    final static char INPUT_DOWN = 's';
    final static char INPUT_RIGHT = 'd';
    /*
    grid looks like:
    ----W
    -----
    -----
    S---W
    */
    public static void main(String[] args) {
        //global variable declares
        int totalScore = 300;
        int itrCount = 0;
        int reward;
        char direction; //W,A,S,D instead of U,D,L,R
        //coords
        int x = 0;
        int y = 0;
        
        //main game loop
        do {
            displayInfo(x, y, itrCount, totalScore);
            direction = inputDirection();
            if (doesExceed(x, y, direction)) {
                System.out.println("Exceeded boundary, -2000 damage applied");
                totalScore -= 2000;
            }
            else {
                switch (direction) {
                    case INPUT_UP:
                        y++;
                        totalScore -= 10;
                        break;
                    case INPUT_DOWN:
                        y--;
                        totalScore -= 50;
                        break;
                    case INPUT_RIGHT:
                        x++;
                        totalScore -= 50;
                        break;
                    case INPUT_LEFT:
                        x--;
                        totalScore -= 50;
                        break;
                }
            }
            reward = reward();
            totalScore += punishOrMercy(direction, reward);
            System.out.println("");
            itrCount++;
        } while (!isGameOver(x, y, totalScore, itrCount));
        //game end
        evaluation(totalScore);
    }
    
    /**
     * displays the player's current info
     * @param x player X coordinate
     * @param y player Y coordinate
     * @param iterCount player amount of game iterations
     * @param score 
     */
    public static void displayInfo(int x, int y, int iterCount, int score) {
        System.out.printf("At point (X = %d, Y = %d) at %d iterations, "
                + "the current score is: %d\n", x, y, iterCount, score);
    }
    
    /**
     * checks if a move would make the player fall off-grid
     * @param x player X coordinate
     * @param y player Y coordinate
     * @param dir direction input (w,a,s,d)
     * @return true if the player is going OoB, false if not
     */
    public static boolean doesExceed(int x, int y, char dir) {
        return (y >= GRID_SIZE_Y && Character.toLowerCase(dir) == INPUT_UP)
                || (y <= 0 && Character.toLowerCase(dir) == INPUT_DOWN)
                || (x >= GRID_SIZE_X && Character.toLowerCase(dir) == INPUT_RIGHT)
                || (x <= 0 && Character.toLowerCase(dir) == INPUT_LEFT);
    }
    
    /**
     * calls prng to give the player a """"""reward"""""" (dice roll)
     * @return the rolled """"""reward""""""
     */
    public static int reward(){
        Random prng = new Random();
        int die = prng.nextInt(6) + 1;
        switch (die) {
            //the ones that make you die inside
            case 1:
                System.out.printf("Dice roll: %d, reward: -100\n", die);
                return -100;
            case 2:
                System.out.printf("Dice roll: %d, reward: -200\n", die);
                return -200;
            case 3:
                System.out.printf("Dice roll: %d, reward: -300\n", die);
                //lol git gud
                return -300;
            //the ones that give a false sense of security
            case 4:
                System.out.printf("Dice roll: %d, reward: 300\n", die);
                return 300;
            case 5:
                System.out.printf("Dice roll: %d, reward: 400\n", die);
                return 400;
            case 6:
                System.out.printf("Dice roll: %d, reward: 600\n", die);
                return 600;
        }
        //how the hell did you get here
        return Integer.MIN_VALUE;   //lol
    }
    
    /**
     * determines if to let the player off or to stomp on their fingers
     * @param dir player direction, cause we're rewarding getting high(er) for some reason
     * @param reward player reward on this turn, we ain't gonna let the player take seconds
     * @return the rigged reward if mercy, or the original punishment if not
     */
    public static int punishOrMercy(char dir, int reward) {
        //how dare you try to take more from me get lost scrub
        if ((reward < 0) && (Character.toLowerCase(dir) == INPUT_UP)) {
            Random prng = new Random();
            boolean flip = prng.nextBoolean();
            if (flip) {
                System.out.println(
                        "Coin: heads | No mercy, punishment applied");
                return reward;
            }
            System.out.println("Coin: tails | Mercy, punishment removed");
            return 0; //smh
        }
        return reward;
    }
    
    /**
     * prints the win / loss string
     * @param totalScore player score
     */
    public static void evaluation(int totalScore) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = toTitleCase(input.nextLine());
        if (totalScore >= 2000) {
            System.out.printf("YOU THE WINNER %s, YOUR SCORE IS %d\n",
                    name, totalScore);
            return;
        }
        System.out.printf("MISSION FAILED %s, YOUR SCORE IS %d\n",
                name, totalScore);
    }
    
    /**
     * waits for player to input a valid direction (/\ = w, < = a, \/ = s, > = d )
     * @return the validated input
     */
    public static char inputDirection() {
        Scanner input = new Scanner(System.in);
        char charOut = ' ';
        do {            
            System.out.print("Input a valid direction: ");
            char testChar = input.nextLine().toLowerCase().charAt(0);   //this is ugly
            if (testChar == INPUT_UP
                    || testChar == INPUT_LEFT
                    || testChar == INPUT_DOWN
                    || testChar == INPUT_RIGHT) {
                charOut = testChar;
            }
        } while (charOut == ' ');
        return charOut;
    }
    
    /**
     * checks if the win / loss conditions are reached
     * @param x player X coordinate
     * @param y player Y coordinate
     * @param totalScore player score
     * @param itrCount player game iterations
     * @return true if game termination conditions are reached, false if not
     */
    public static boolean isGameOver(int x, int y,
            int totalScore, int itrCount) {
        boolean reachedGoalPos = (x == GRID_SIZE_X
                && (y == 0 || y == GRID_SIZE_Y));
        boolean playDied = (totalScore < MIN_SCORE);
        boolean playReachedScore = (totalScore > MAX_SCORE);
        boolean playTooLong = (itrCount > MAX_ITERS);
        
        return reachedGoalPos || playTooLong || playDied || playReachedScore;
    }
    
    /**
     * brings a string of two words to Title Case
     * @param string the string, two words separated by a space
     * @return the converted string
     */
    public static String toTitleCase(String string) {
        if (!string.contains(" ")) {
            return toTitleCaseWord(string);
        }
        int spaceIndex = string.indexOf(" ") + 1;
        return toTitleCaseWord(string.substring(0, spaceIndex))
                + toTitleCaseWord(string.substring(spaceIndex));
    }
    
    /**
     * brings a single word to title case
     * @param string the ****SINGULAR**** word
     * @return the ****SINGULAR**** word in TitleCase
     */
    public static String toTitleCaseWord(String string) {
        return Character.toUpperCase(string.charAt(0)) + 
                string.substring(1).toLowerCase();
    }
}