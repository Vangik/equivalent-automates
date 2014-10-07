/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author WslF
 */
public class State {

    /**
     * number of state
     */
    private int n;
    /**
     * states, that able to be visited from current state
     */
    private Map<Character, State> nextStates;
    /**
     * if state is final variable= true, else false
     */
    private final boolean isFinal;

    public State(int n0, boolean isFinal0) {
        n = n0;
        isFinal = isFinal0;
        nextStates = new TreeMap<Character, State>();
    }

    /**
     * add new state, in wich we can go from current by the letter
     *
     * @param c letter
     * @param state - state
     */
    public void addNextState(Character c, State state) {
        nextStates.put(c, state);
    }

    /**
     * @return number of state
     */
    public int getNumber() {
        return n;
    }

    /**
     * @return number of states that next to current (including current if it
     * has eps-conversion)
     */
    public int getNumberOfConnectedStates() {
        return nextStates.size();
    }

    /**
     * @return if state is fainale return true, else return false
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * @param ch letter to conversion from current to next sate
     * @return state that connected with current by latter ch if it doesn't
     * exist return null
     */
    public State getNextState(Character ch) {
        if (!nextStates.containsKey(ch)) {
            return null;
        }
        return nextStates.get(ch);
    }

    /**
     * @return string, that discribing state
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("state " + n + ": ");
        for (Character character : nextStates.keySet()) {
            s.append("(" + character + "," + nextStates.get(character).getNumber() + ") ");
        }
        s.append(";\n");
        return s.toString();
    }

    /**
     * @return true if this and obj are equivalents
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        State state2 = (State) obj;
        if (this.isFinal ^ state2.isFinal()
                || this.getNumberOfConnectedStates() != state2.getNumberOfConnectedStates()) {
            return false;
        }

        if (getNumberOfConnectedStates() == 0) {
            return true;
        }

        for (Character ch : nextStates.keySet()) {
            if (!nextStates.get(ch).equals(state2.getNextState(ch))) {
                return false;
            }
        }
        
        return true;
    }
}
