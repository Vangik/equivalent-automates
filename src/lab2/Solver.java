package lab2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import lab2.automata.Automata;

/**
 * @author WslF
 */
public class Solver {

    public boolean solve(String fileAutomata1, String fileAutomata2) {
        Automata automata1 = new Automata();
        try {
            automata1.readAutomata(fileAutomata1);
        } catch (Exception e) {
            System.err.println("Error reading automata1");
            return false;
        }

        Automata automata2 = new Automata();
        try {
            automata2.readAutomata(fileAutomata2);
        } catch (Exception e) {
            System.err.println("Error reading automata2");
            return false;
        }

/*        Automata dfa1 = automata1.buildDFA();
        Automata dfa2 = automata2.buildDFA();

        try {
            dfa1.printAutomata(""+new File("automates\\dfa1$.$tmp") );
            dfa2.printAutomata(""+new File("automates\\dfa2$.$tmp") );
        } catch (Exception e) {
            return false;
        }
        
        return dfa1.equals(dfa2);
        */
        return automata1.equals(automata2);
    }
}
