import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.swing.JFileChooser;

public class ParserTester2{

  /**
   * For testing a parser without requiring the world or the game:
   */
  public static void main(String[] args) {
    System.out.println("Testing parser");
    System.out.println("=======================");
    System.out.println("For each program in a collection of test programs,");
    System.out.println("it attempts to parse the program.");
    System.out.println("It reports the program and what the parser should do");
    System.out.println("and then reports whether the parser failed, generated a null tree, or succeeded.");
    System.out.println("It then prints the tree.");
    System.out.println("================================================================");
    Parser parser = new Parser();

    for (int stage=0; stage<4; stage++){
      System.out.println("\nTesting Parser on Stage "+stage+":\n");
      for (String[] test : programs[stage]){
        boolean valid= test[0]=="GOOD";
        String program = test[1];
        String message = test[2];
        System.out.println("------\nParsing: "+ (valid?"valid: ":"INVALID: ")+program+ " ["+message+"]");
        try{
          ProgramNode ast = parser.parse(new Scanner(program));
          String printedForm = (ast!=null)?ast.toString().replaceAll("\\n", " "):"null tree";
          if (valid){
            if (ast!=null){System.out.println("OK, valid program: "+printedForm);}
            else          {System.out.println("BAD, failed to generate tree for valid program");}
          }
          else {
            if (ast!=null){System.out.println("BAD, program is invalid, parser gave: "+printedForm);}
            else          {System.out.println("???, program is invalid, parser did not throw exception but did not build a tree");}

          }
        }
        catch (ParserFailureException e) {
          System.out.println("  parser exception: "+e.toString().replaceAll("\\n", " "));
          if (valid) {System.out.println("BAD, threw exception for a valid program");}
          else       {System.out.println("OK, parser correctly threw an exception");}
        }
      }
      System.out.println("Done");
    }
  }




  private static final String[][][] programs = new String[][][]{
          {//STAGE 0
                  {"GOOD", "move;",  "move action"},
                  {"GOOD", "turnL;",  "turnL action"},
                  {"GOOD", "turnR;",  "turnR action"},
                  {"GOOD", "takeFuel;",  "takeFuel action"},
                  {"GOOD", "wait;",  "wait action"},
                  {"GOOD", "move; turnL; turnR; move; takeFuel; ",  "sequence of actions"},
                  {"GOOD", "loop{move ;}",  "loop with a Block with one action"},
                  {"GOOD", "loop{move; wait; turnL; turnR;}",  "loop with a Block with four actions"},
                  {"GOOD", "loop{move; loop{turnL;}}",  "nested loop"},
                  {"GOOD", "move; turnL; turnR; move; takeFuel; loop{move; turnR; wait;}",  "all stage 0 elements"},

                  {"BAD", "move; turnR move;",  "missing ;"},
                  {"BAD", "move; turnR: move;",  ": instead of ;"},
                  {"BAD", "move; turnL; turnRight; move;",  "invalid action turnRight"},
                  {"BAD", "loop{}",  "Block in a loop with no statements"},
                  {"BAD", "{move;}",  "Block not inside a loop"},
                  {"BAD", "loop{move; turnL;",  "Block with no close }"},
                  {"BAD", "loop{move; loop{turnL;}",  "nested loop with one missing close } on block "},
                  {"BAD", "loop{move; loop{turnL;",  "nested loop with two missing close } on blocks "},
                  {"BAD", "loop{move; turnL;}}",  "Block with extra close }"},
          },
          {//STAGE 1:
                  {"GOOD", "while(eq(fuelLeft, 2)) { wait; }",   "while and condition using eq and  fuelLeft"},
                  {"GOOD", "if(lt(oppLR, 2)) { wait; }",   "if with condition using lt and oppLR"},
                  {"GOOD", "if(gt(oppFB, 2)) { move; }",   "if with condition using gt and oppFB"},
                  {"GOOD", "if(eq(numBarrels, 1)) {turnL;}",   "if with condition using eq and numbBarrels"},
                  {"GOOD", "while(lt(barrelLR, 1)) {turnR;}",   "while with condition using lt and barrelLR"},
                  {"GOOD", "while(gt(barrelFB, 1)) {wait;}",   "while with condition using gt and barrelFB"},
                  {"GOOD", "while(eq(wallDist, 0)) {turnL; wait;}",   "while with condition using eq and wallDis"},

                  {"GOOD", "while(gt(wallDist, 0)) {while(eq(fuelLeft, 4)) {turnL;}}",   "while with nested while"},
                  {"GOOD", "while(gt(wallDist, 0)) {if(eq(fuelLeft, 4)) {turnL;}}",   "while with nested if"},
                  {"GOOD", "if(gt(wallDist, 0)) {if(eq(fuelLeft, 4)) {turnL;}}",   "if with nested if"},
                  {"GOOD", "if(gt(wallDist, 0)) {while(eq(fuelLeft, 4)) {turnL;}}",   "if with nested while"},
                  {"GOOD", "move; while(gt(wallDist, 0)) {turnL;} if(eq(fuelLeft, 4)) {turnL;} wait;",   "sequence of 4 statements, including an if an a while"},

                  {"BAD", "while{move;}",   "while needs a condition"},
                  {"BAD", "if{move;}",   "if needs a condition"},
                  {"BAD", "while(){move;}",   "while can't have an empty condition"},
                  {"BAD", "if(){move;}",   "if can't have an empty condition"},
                  {"BAD", "if(eq(fuelLeft, 1) {move;}",   "Condition in if must have closing )"},
                  {"BAD", "if eq(fuelLeft, 1) {move;}",   "Condition in if must have opening )"},
                  {"BAD", "while(eq(fuelLeft, 1) {move;}",   "Condition in while must have closing )"},
                  {"BAD", "while eq(fuelLeft, 1) {move;}",   "Condition in while must have opening )"},

                  {"BAD", "while(eq(fuelLeft, 2) move;",   "while must have a block, not a statement."},
                  {"BAD", "if(eq(fuelLeft, 2) move;",   "if must have a block, not a statement."},
                  {"BAD", "while(eq(fuelLeft, 1)){}",   "block in a while must have at least one statement"},
                  {"BAD", "if(eq(fuelLeft, 1)){}",   "block in an if must have at least one statement"},

                  {"BAD", "if(eq(fuelLeft, 2)) move;",   "if must have a block, "},
                  {"BAD", "if(shieldOn){shieldOff;}",   "can't have an action as a boolean"},
                  {"BAD", "if(gt(turnL, 1)) {move;}",   "can't have an action as a sensor."},
                  {"BAD", "loop(gt(turnL, 1)){move; turnL;}",  "loop cannot have a condition"},
          },
          {//STAGE 2

                  {"GOOD", "move(3);",  "move with number argument"},
                  {"GOOD", "move(fuelLeft);",  "move with sensor argument"},
                  {"GOOD", "move(add(fuelLeft,2));",  "move with add argument"},
                  {"GOOD", "move(mul(oppLR,2));",  "move with mul argument"},
                  {"GOOD", "wait(sub(oppFB,2));",  "wait with sub argument"},
                  {"GOOD", "wait(div(oppLR,2));",  "wait with div argument"},
                  {"GOOD", "wait(div(add(3, 5), sub(mul(oppLR,2),sub(5, 6))));",  "wait with complex nested expression"},
                  {"GOOD", "if (lt(add(3,4), sub(10,2))) { wait; } else {move;}",  "lt on expressions, if with else"},
                  {"GOOD", "if (gt(mul(3,4), div(100,2))) { wait; } else {move;}",  "gt on expressions, if with else"},
                  {"GOOD", "while (eq(mul(3,add(1, 4)), 10)) { wait;}",  "eq with nested expression, "},
                  {"GOOD", "if (and(lt(3,4),gt(10,2))) { wait; } else {move;}",  "condition with and"},
                  {"GOOD", "if (or(lt(3,4),gt(10,2))) { wait; } else {move;}",  "condition with or"},
                  {"GOOD", "if (not(lt(4,3))) { wait; } else {move;}",  "condition with not"},
                  {"GOOD", "if (or(and(lt(3,4),gt(10,2)), not(not(lt(4,3))))) { wait; } else {move;}",  "nested ands, ors, nots"},
                  {"GOOD", "if (eq(oppLR,3)) { wait; } else {move;}",  "wait"},
                  {"GOOD", "if (eq(barrelFB,3)) { wait; } else {move;}",  "move"},
                  {"GOOD", "if (eq(3,oppLR)) { wait; } else {move;}",  "wait"},
                  {"GOOD", "if (eq(3,barrelFB)) { wait; } else {move;}",  "move"}

                  // need bad ones: eg
                  //  args for the other actions,
                  //  invalid expressions
                  //  invalid boolean expressions


          },
          {//STAGE 3
          }
  };

}