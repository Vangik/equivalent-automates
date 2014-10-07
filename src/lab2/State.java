/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

import java.util.Map;

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
    private Map<Character, Integer> next;
    /**
     * if state is final variable= true, else false
     */
    private boolean isFinal;
}
