package lab2.automata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.TreeSet;
import javax.imageio.IIOException;

/**
 *
 * @author WslF
 */
public class Automata {

    private static int maxStateOfNFA = 20;
    private int alphavetSize;
    private TreeSet<Character> alphavet = new TreeSet<Character>();
    private int stateSize;
    private TreeSet<Integer> numbersStates = new TreeSet<Integer>();
    //private TreeSet<State> finalStates= new TreeSet<State>();
    private State[] states;

    private State state0;

    private static int pow[] = new int[maxStateOfNFA];

    static {
        for (int i = 0; i < maxStateOfNFA; i++) {
            pow[i] = (1 << i);
        }
    }

    private TreeSet<Integer> numbersOfFinalStates = new TreeSet<Integer>();

    public boolean readAutomata(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, "Cp1251");
        Scanner in = new Scanner(isr);
        return this.readAutomata(in);
    }

    public boolean readAutomata(Scanner in) {
        if (in == null) {
            return false;
        }
        try {
            alphavetSize = in.nextInt();
            stateSize = in.nextInt();
            states = new State[stateSize];

            state0 = new State(in.nextInt());
            addState(state0);

            readFinalStates(in);

            readConnections(in);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void readFinalStates(Scanner in) throws IIOException {
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            int stateNumb = in.nextInt();
            addState(new State(stateNumb, true));
            numbersOfFinalStates.add(stateNumb);
        }
    }

    private void readConnections(Scanner in) throws IIOException {
        int s, s1;
        Character a;
        while (in.hasNext()) {
            s = in.nextInt();
            createStateIfNeed(s);

            a = in.next().charAt(0);
            addLetterIfNeed(a);

            s1 = in.nextInt();
            createStateIfNeed(s1);

            addConnection(s, s1, a);
        }
    }

    private void addConnection(int numberState1, int numberState2, Character ch) {
        State state1 = states[numberState1];
        State state2 = states[numberState2];

        state1.addNextState(ch, state2);
    }

    private boolean addLetterIfNeed(Character ch) {
        if (alphavet.contains(ch)) {
            return false;
        } else {
            alphavet.add(ch);
            return true;
        }
    }

    private boolean createStateIfNeed(int numberOfState) {
        if (!numbersStates.contains(numberOfState)) {
            addState(new State(numberOfState));
            return true;
        } else {
            return false;
        }
    }

    private void addState(State state) {
        if (numbersStates.contains(state.getNumber())) {
            return;
        }
        numbersStates.add(state.getNumber());
        states[state.getNumber()] = state;
        return;
    }

    private State[] createNewStatesForDFA() {
        int maxMask = (1 << states.length);
        State newStates[] = new State[maxMask];
        for (int mask = 1; mask < maxMask; mask++) {
            newStates[mask] = createStateByMask(mask);
        }

        return newStates;
    }

    private State createStateByMask(int mask) {
        boolean isFinal = false;
        for (int i = 0; i < states.length; i++) {
            if ((mask & pow[i]) != 0 && states[i] != null && states[i].isFinal()) {
                isFinal = true;
                break;
            }
        }

        return new State(mask, isFinal);
    }

    private int getNextStatesOfMaskByCharacter(int mask, Character ch) {
        int nextMask = 0;
        for (int i = 0; i < states.length; i++) {
            if ((mask & pow[i]) != 0 && states[i] != null) {//mask contain state i
                State state = states[i].getNextState(ch);
                if (state != null) {
                    nextMask |= pow[state.getNumber()];
                }
                {
                    state = states[i].getNextState('e');
                    if (state != null) {
                        boolean visited[] = new boolean[states.length];
                        for (int j = 0; j < states.length; j++) {
                            visited[j] = false;
                        }
                        visited[i] = true;
                        while (state != null) {
                            if (visited[state.getNumber()]) {
                                break;
                            }
                            visited[state.getNumber()] = true;
                            if (state.getNextState(ch) != null) {
                                nextMask |= pow[state.getNextState(ch).getNumber()];
                                break;
                            } else {
                                state = state.getNextState('e');
                            }
                        }
                    }
                }
            }
        }
        return nextMask;
    }

    public Automata buildDFA() {
        if (states.length > maxStateOfNFA) {
            return null;
        }

        Automata a = new Automata();
        a.alphavet = alphavet;
        int maxMask = (1 << states.length);
        State newStates[] = createNewStatesForDFA();
        a.states = newStates;

        Queue<Integer> queue = new LinkedList<>();
        boolean was[] = new boolean[maxMask];
        for (int i = 0; i < maxMask; i++) {
            was[i] = false;
        }

        queue.add(pow[state0.getNumber()]);
        was[pow[state0.getNumber()]] = true;
        while (!queue.isEmpty()) {
            int mask = queue.poll();
            State curState = newStates[mask];

            for (Character ch : alphavet) {
                if (ch == 'e') {
                    continue;
                }
                int nextMask = getNextStatesOfMaskByCharacter(mask, ch);
                if (nextMask != 0) {
                    if (!was[nextMask]) {
                        was[nextMask] = true;
                        queue.add(nextMask);
                    }
                    curState.addNextState(ch, newStates[nextMask]);
                }
            }

        }
        return a;
    }

    public Automata minimaze() {
        deleteUnattainableStates();
        /*
         int numberOfClasses = 2;
         int stateClass[] = new int[states.length];
         for (int i = 0; i < states.length; i++) {
         if (states[i] == null) {
         stateClass[i] = -1;
         }

         }*/
        return buildDFA();
    }

    private void deleteUnattainableStates() {
        boolean attainable[] = new boolean[stateSize];
        for (int i = 0; i < stateSize; i++) {
            attainable[i] = false;
        }

        State state;
        Queue<State> queue = new LinkedList<State>();

        queue.clear();
        queue.add(state0);
        attainable[state0.getNumber()] = true;
        while (!queue.isEmpty()) {
            state = queue.poll();
            if (state == null) {
                break;
            }
            for (Character ch : alphavet) {
                State st = state.getNextState(ch);
                if (st != null && !attainable[st.getNumber()]) {
                    attainable[st.getNumber()] = true;
                    queue.add(st);
                }
            }
        }

        for (int i = 0; i < stateSize; i++) {
            if (!attainable[i]) {
                states[i] = null;
            }
        }
    }

}
