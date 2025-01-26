package edu.commonwealthu.finalproject.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that is utilized by the weapon class to generate problems and their answers.
 * @author Justin Aul
 */
public class Problem {
    public static enum type {ADD, SUBTRACT, MULTIPLY, DIVIDE}; //Passed to constructor
    private type problemType;
    private int term0, term1, answer;
    private String problem;

    /**
     * @param _problemType The type of problem
     * @param _difficulty The difficulty of the problem
     */
    public Problem(type _problemType, int _difficulty) {
        problemType = _problemType;
        answer = ThreadLocalRandom.current().nextInt(2, _difficulty);

        switch (problemType) {
            case ADD:
                add();
                break;
            case SUBTRACT:
                subtract();
                break;
            case MULTIPLY:
                multiply();
                break;
            case DIVIDE:
                divide();
                break;
        }
    }

    /**
     * Method for generating an addition problem.
     */
    private void add() {
        term0 = ThreadLocalRandom.current().nextInt(1,answer);
        term1 = answer - term0;
        problem = (term0 + " + " + term1);
    }

    /**
     * Method for generating a subtraction problem.
     */
    private void subtract() {
        term0 = answer - ThreadLocalRandom.current().nextInt(1,answer);
        term1 = answer;
        answer = term1 - term0;
        problem = (term1 + " - " + term0);
    }

    /**
     * Method for generating a multiplication problem.
     */
    private void multiply() {
        Integer[] factors = findFactors(answer);

        //Increment if answer is prime
        while(factors.length <= 1) {
            answer++;
            factors = findFactors(answer);
        }

        //Pick a random index from the array of factors, use to calculate other term.
        int factorIndex = ThreadLocalRandom.current().nextInt(1,factors.length);
        term0 = factors[factorIndex];
        term1 = answer / term0;
        problem = (term0 + " x " + term1);
    }

    /**
     * Method for generating a division problem.
     */
    private void divide() {
        term1 = ThreadLocalRandom.current().nextInt(2,11);
        term0 = answer * term1;
        problem = (term0 + " / " + term1);
    }

    /**
     * Getter for the answer.
     * @return The answer
     */
    public int getAnswer() {
        return answer;
    }


    /**
     * Helper method for calculating and returning an array of all given factors of a problem.
     * @param _answer
     * @return
     */
    private Integer[] findFactors(int _answer) {
        Set<Integer> factors = new HashSet<Integer>();

        //Search for all factors between 2 and answer/2, add them to a set to avoid duplicates
        for (int i = 2; i <= (_answer / 2); i++) {
            if (_answer % i == 0) {factors.add(i);
            }
        }

        Integer[] factorsArray = factors.toArray(new Integer[factors.size()]);
        return factorsArray;
    }

    /**
     * Getter for the problem string.
     * @return The problem
     */
    public String getProblem() {
        return problem;
    }
}
