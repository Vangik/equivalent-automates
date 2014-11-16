package lab2.automata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.util.*;
import java.util.TreeSet;
import javax.imageio.IIOException;
import wslf.Pair;

/**
 *
 * @author WslF
 */
public class Automata {

    private static final int maxStateOfNFA = 14;
    //private int alphavetSize;
    private TreeSet<Character> alphavet = new TreeSet<Character>();
    //private int stateSize;
    private TreeSet<Integer> numbersStates = new TreeSet<Integer>();
    //private TreeSet<State> finalStates= new TreeSet<State>();
    private State[] states;

    private State state0;

    private static final int pow[] = new int[maxStateOfNFA];

    public void printAutomata(String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        PrintWriter out = new PrintWriter(osw);
        printAutomata(out);
        out.flush();
        out.close();
    }

    public void printAutomata(PrintWriter out) {
        out.println(alphavet.size());
        out.println(states.length);
        out.println(state0.getNumber());
        int numberOfFinal = 0;
        for (int i = 0; i < states.length; i++) {
            if (states[i].isFinal()) {
                numberOfFinal++;
            }
        }
        out.print(numberOfFinal);
        for (int i = 0; i < states.length; i++) {
            if (states[i].isFinal()) {
                out.print(" " + states[i].getNumber());
            }
        }
        out.println();

        for (int i = 0; i < states.length; i++) {
            for (Character character : alphavet) {
                State st = states[i].getNextState(character);
                if (st != null) {
                    out.println("" + i + " " + character + " " + st.getNumber());
                }
            }
        }
    }

    static {
        for (int i = 0; i < maxStateOfNFA; i++) {
            pow[i] = (1 << i);
        }
    }

    // private TreeSet<Integer> numbersOfFinalStates = new TreeSet<Integer>();
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
            /*alphavetSize = */
            in.nextInt();
            int stateSize = in.nextInt();
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
//            numbersOfFinalStates.add(stateNumb);
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

    private Automata createTemplateForDFA() {
        Automata a = new Automata();
        a.alphavet = new TreeSet<>();
        for (Character character : alphavet) {
            if (character != 'e') {
                a.alphavet.add(character);
            }
        }

        State newStates[] = createNewStatesForDFA();
        a.states = newStates;
        return a;
    }

    public Automata buildDFA() {
        if (states.length > maxStateOfNFA) {
            return null;
        }
        optimaze();
        Automata a = createTemplateForDFA();
        int maxMask = (1 << states.length);

        Queue<Integer> queue = new LinkedList<>();
        boolean was[] = new boolean[maxMask];
        for (int i = 0; i < maxMask; i++) {
            was[i] = false;
        }

        queue.add(pow[state0.getNumber()]);
        was[pow[state0.getNumber()]] = true;
        while (!queue.isEmpty()) {
            int mask = queue.poll();
            State curState = a.states[mask];

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
                    curState.addNextState(ch, a.states[nextMask]);
                }
            }

        }
        a.state0 = a.states[pow[state0.getNumber()]];
        a.optimaze();
        return a;
    }

    private void optimaze() {
        deleteUnattainableStates();
        int number = 0;
        for (State state : states) {
            if (state != null) {
                state.setNumber(number++);
            }
        }

        if (number < states.length) {
            int stateSize = number;
            State[] newStates = new State[stateSize];
            for (State state : states) {
                if (state != null) {
                    newStates[state.getNumber()] = state;
                }
            }

            states = newStates;
        }
    }

    public Automata minimazeDFA() {
        optimaze();
        /*
         deleteUnattainableStates();
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
        boolean attainable[] = new boolean[states.length];
        for (int i = 0; i < states.length; i++) {
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

        for (int i = 0; i < states.length; i++) {
            if (!attainable[i]) {
                states[i] = null;
            }
        }
    }

    private void createFictionalState() {
        State fictionalState = new State(states.length, false);
        for (Character ch : alphavet) {
            fictionalState.addNextState(ch, fictionalState);
        }

        boolean flag = alphavet.contains('e');
        if (flag) {
            alphavet.remove('e');
        }

        for (State state : states) {
            for (Character ch : alphavet) {
                if (state.getNextState(ch) == null) {
                    state.addNextState(ch, fictionalState);
                }
            }
        }

        if (flag) {
            alphavet.add('e');
        }
    }

    public boolean equal(Automata automata2) {
        optimaze();
        createFictionalState();
        automata2.optimaze();
        automata2.createFictionalState();
        Queue<Pair<State>> queue = new LinkedList<>();

        queue.add(new Pair<>(state0, automata2.state0));
        boolean used1[] = new boolean[states.length + 1];
        boolean used2[] = new boolean[automata2.states.length + 1];
        used1[state0.getNumber()] = true;
        used2[automata2.state0.getNumber()] = true;

        alphavet.remove('e');

        while (!queue.isEmpty()) {
            Pair<State> pair = queue.poll();
            State state1 = pair.first;
            State state2 = pair.second;
            if (state1.isFinal() != state2.isFinal()) {
                return false;
            }

            for (Character ch : alphavet) {
                addNextStateToQueue(ch, state1, state2, used1, used2, queue);
            }

            if (state1.getNextState('e') != null) {
                addNextStateToQueue('e', state1.getNextState('e'), state2, used1, used2, queue);
            }

            if (state2.getNextState('e') != null) {
                addNextStateToQueue('e', state1, state2.getNextState('e'), used1, used2, queue);
            }
        }

        return false;
    }

    private boolean addNextStateToQueue(Character ch, State state1, State state2,
            boolean[] used1, boolean[] used2, Queue<Pair<State>> queue) {

        if (!used1[state1.getNextState(ch).getNumber()]
                || !used2[state2.getNextState(ch).getNumber()]) {
            queue.add(new Pair<>(state1.getNextState(ch), state2.getNextState(ch)));
            used1[state1.getNextState(ch).getNumber()] = true;
            used2[state2.getNextState(ch).getNumber()] = true;
            return true;
        }

        return false;
    }
}
