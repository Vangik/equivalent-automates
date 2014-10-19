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

    private int alphavetSize;
    private TreeSet<Character> alphavet = new TreeSet<Character>();
    private int stateSize;
    private TreeSet<Integer> numbersStates = new TreeSet<Integer>();
    //private TreeSet<State> finalStates= new TreeSet<State>();
    private State[] states;

    private State state0;

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

    public void minimaze() {
        deleteUnattainableStates();
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
