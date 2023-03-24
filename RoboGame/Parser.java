import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.\
 *
 * PROG  ::= [ STMT ]*
 * STMT  ::= ACT ";" | LOOP | IF | WHILE
 * ACT   ::= "move" | "turnL" | "turnR" | "turnAround" | "shieldOn" |
 *           "shieldOff" | "takeFuel" | "wait"
 * LOOP  ::= "loop" BLOCK
 * IF    ::= "if" "(" COND ")" BLOCK
 * WHILE ::= "while" "(" COND ")" BLOCK
 * BLOCK ::= "{" STMT+ "}"
 * COND  ::= RELOP "(" SENS "," NUM ")
 * RELOP ::= "lt" | "gt" | "eq"
 * SENS  ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" |
 *           "barrelLR" | "barrelFB" | "wallDist"
 * NUM   ::= "-?[1-9][0-9]*|0"
 */
public class Parser {


    // Useful Patterns

    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");
    //----------------------------------------------------------------

    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE

        ProgramNode tree = parseProg(s); //Initiate PROG parsing
        return tree;

        // Call the parseProg method for the first grammar rule (PROG) and return the node
    }

    /**
     * PROG  ::= [ STMT ]*
     */
    public ProgramNode parseProg(Scanner s) {
        List<ProgramNode> children = new ArrayList<>(); //List of child nodes

        while (s.hasNext()) {
            ProgramNode node = parseSTMT(s);
            if (node != null)
                children.add(node);
            else
                fail("STMT Error", s);
        }

        return new ProgramNode() {
            public void execute(Robot robot) {
                for (ProgramNode child : children)
                    child.execute(robot);
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (ProgramNode child : children)
                    sb.append(child.toString());
                return sb.toString();
            }
        };
    }

    /**
     * STMT  ::= ACT ";" | LOOP | IF | WHILE
     */
    private static ProgramNode parseSTMT(Scanner s) {
        //Attempt all possible things it could be

        ProgramNode stmt = parseAct(s);
        if (stmt != null) {
            require(";", "Need semicolon", s);
            return stmt;
        }

        stmt = parseLoop(s);
        if (stmt != null) {
            return stmt;
        }

        stmt = parseIf(s);
        if (stmt != null) {
            return stmt;
        }

        stmt = parseWhile(s);
        if (stmt != null) {
            return stmt;
        }

        //default to null otherwise
        return null;
    }

    /**
     * ACT   ::= "move" | "turnL" | "turnR" | "turnAround" | "shieldOn" | "shieldOff" | "takeFuel" | "wait"
     */
    private static ProgramNode parseAct(Scanner s) {
        //Switching through every action the robot can perform

        if (checkFor("move", s)) {
            //Has argument
            if (checkFor(OPENPAREN, s)) {
                NumberNode expr = parseEXPR(s);
                require(CLOSEPAREN, "Missing ')'", s);

                return new ProgramNode() {
                    public void execute(Robot robot) {
                        int dist = expr.execute(robot);
                        for (int i = 0; i < dist; i++)
                            robot.move();
                    }

                    public String toString() {
                        return "move(" + expr + ");\n";
                    }
                };
            }

            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.move();
                }

                public String toString() {
                    return "move;\n";
                }
            };
        }
        if (checkFor("wait", s)) {
            //Has argument
            if (checkFor(OPENPAREN, s)) {
                NumberNode expr = parseEXPR(s);
                require(CLOSEPAREN, "Missing ')'", s);

                return new ProgramNode() {
                    public void execute(Robot robot) {
                        int dist = expr.execute(robot);
                        for (int i = 0; i < dist; i++)
                            robot.idleWait();
                    }

                    public String toString() {
                        return "wait(" + expr + ");\n";
                    }
                };
            }

            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.idleWait();
                }

                public String toString() {
                    return "wait;\n";
                }
            };
        }
        if (checkFor("turnL", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.turnLeft();
                }

                public String toString() {
                    return "turnL;\n";
                }
            };
        if (checkFor("turnR", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.turnRight();
                }

                public String toString() {
                    return "turnR;\n";
                }
            };
        if (checkFor("turnAround", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.turnAround();
                }

                public String toString() {
                    return "turnAround;\n";
                }
            };
        if (checkFor("shieldOn", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.setShield(true);
                }

                public String toString() {
                    return "shieldOn;\n";
                }
            };
        if (checkFor("shieldOff", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.setShield(false);
                }

                public String toString() {
                    return "shieldOff;\n";
                }
            };
        if (checkFor("takeFuel", s))
            return new ProgramNode() {
                public void execute(Robot robot) {
                    robot.takeFuel();
                }

                public String toString() {
                    return "takeFuel;\n";
                }
            };

        return null;
    }

    /**
     * LOOP  ::= "loop" BLOCK
     */
    private static ProgramNode parseLoop(Scanner s) {
        if (checkFor("loop", s)) {
            BlockNode block = parseBlock(s);
            if (block == null)
                return null;

            return new ProgramNode() {
                public void execute(Robot robot) {
                    block.preExec(robot);
                    //Keep going until out of fuel
                    while (robot.getFuel() > 0)
                        block.execute(robot);
                    block.postExec(robot);
                }

                public String toString() {
                    return "loop " + block;
                }
            };
        }

        return null;
    }

    /**
     * IF    ::= "if" "(" COND ")" BLOCK [ "else" BLOCK ]
     *
     * @param s
     * @return
     */
    private static ProgramNode parseIf(Scanner s) {
        if (checkFor("if", s)) {
            List<ConditionNode> conds = new ArrayList<>();
            List<BlockNode> blocks = new ArrayList<>();

            //Parse if and elifs
            do {
                require(OPENPAREN, "Missing '('", s);

                conds.add(parseCond(s));

                require(CLOSEPAREN, "Missing ')'", s);

                blocks.add(parseBlock(s));
            } while (checkFor("elif", s));

            //Check for optional else block
            BlockNode elseBlock = BlockNode.NULL;
            if (checkFor("else", s)) {
                elseBlock = parseBlock(s);
                if (elseBlock == null)
                    elseBlock = BlockNode.NULL;
            }

            if (s.hasNext("elif"))
                fail("elif must come before else", s);

            //Copy the else block so it can be passed into the anonymous class
            final BlockNode finalElseBlock = elseBlock;
            return new ProgramNode() {
                public void execute(Robot r) {
                    for (int i = 0; i < conds.size(); i++) {
                        if (conds.get(i).execute(r)) {
                            BlockNode block = blocks.get(i);
                            block.preExec(r);
                            block.execute(r);
                            block.postExec(r);
                            return;
                        }
                    }

                    finalElseBlock.preExec(r);
                    finalElseBlock.execute(r);
                    finalElseBlock.postExec(r);
                }

                public String toString() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("if (");
                    sb.append(conds.get(0));
                    sb.append(") ");
                    sb.append(blocks.get(0));

                    for (int i = 1; i < conds.size(); i++) {
                        sb.append("if (");
                        sb.append(conds.get(i));
                        sb.append(") ");
                        sb.append(blocks.get(i));
                    }

                    if (finalElseBlock != BlockNode.NULL) {
                        sb.append("else ");
                        sb.append(finalElseBlock);
                    }

                    return sb.toString();
                }
            };
        }

        return null;
    }

    /**
     * WHILE ::= "while" "(" COND ")" BLOCK
     *
     * @param s
     * @return
     */
    private static ProgramNode parseWhile(Scanner s) {
        if (checkFor("while", s)) {
            require(OPENPAREN, "Missing '('", s);

            ConditionNode cond = parseCond(s);
            if (cond == null)
                return null;

            require(CLOSEPAREN, "Missing ')'", s);

            BlockNode block = parseBlock(s);
            if (block == null)
                return null;

            return new ProgramNode() {
                public void execute(Robot r) {
                    block.preExec(r);
                    while (cond.execute(r))
                        block.execute(r);
                    block.postExec(r);
                }

                public String toString() {
                    return "while (" + cond + ") " + block;
                }
            };
        }
        return null;
    }

    /**
     * COND  ::= "and" "(" COND "," COND ")" | "or" "(" COND "," COND ")" | "not" "(" COND ")"  | RELOP "(" EXPR "," EXPR ")
     *
     * @param s
     * @return
     */
    private static ConditionNode parseCond(Scanner s) {
        RelopNode relop = parseRelop(s);
        if (relop == null) {
            ConditionNode cond = buildCond(s);
            if (cond == null)
                fail("Unknown condition", s);

            return cond;
        }

        require(OPENPAREN, "Missing '('", s);

        NumberNode a = parseEXPR(s);
        if (a == null)
            return null;

        require(",", "Missing ','", s);

        NumberNode b = parseEXPR(s);
        if (b == null)
            return null;

        require(CLOSEPAREN, "Missing ')'", s);

        final NumberNode numA = a, numB = b;
        return new ConditionNode() {
            public boolean execute(Robot r) {
                return relop.execute(numA.execute(r), numB.execute(r));
            }

            public String toString() {
                return relop.toString() + "(" + numA + ", " + numB + ")";
            }
        };
    }

    /**
     * Build condition node
     *
     * @param s
     * @return
     */
    private static ConditionNode buildCond(Scanner s) {
        //Pre-builds a lot of needed code
        String type = null;
        if (checkFor("and", s))
            type = "and";
        else if (checkFor("or", s))
            type = "or";

        if (type != null) {
            require(OPENPAREN, "Missing '('", s);

            ConditionNode a = parseCond(s);
            if (a == null)
                return null;

            require(",", "Missing ','", s);

            ConditionNode b = parseCond(s);
            if (b == null)
                return null;

            require(CLOSEPAREN, "Missing ')'", s);

            if (type.equals("and"))
                return new ConditionNode() {
                    public boolean execute(Robot r) {
                        return a.execute(r) && b.execute(r);
                    }

                    public String toString() {
                        return "(" + a + " && " + b + ")";
                    }
                };
            else
                return new ConditionNode() {
                    public boolean execute(Robot r) {
                        return a.execute(r) || b.execute(r);
                    }

                    public String toString() {
                        return "(" + a + " || " + b + ")";
                    }
                };
        }
        else if (checkFor("not", s)) {
            require(OPENPAREN, "Missing '('", s);

            ConditionNode cond = parseCond(s);
            if (cond == null)
                return null;

            require(CLOSEPAREN, "Missing ')'", s);

            return new ConditionNode() {
                public boolean execute(Robot r) {
                    return !cond.execute(r);
                }

                public String toString() {
                    return "!(" + cond + ")";
                }
            };
        }

        return null;
    }

    /**
     * RELOP ::= "lt" | "gt" | "eq"
     *
     * @param s
     * @return
     */
    private static RelopNode parseRelop(Scanner s) {
        //Check through all inputs
        if (checkFor("lt", s))
            return RelopNode.LT;
        if (checkFor("gt", s))
            return RelopNode.GT;
        if (checkFor("eq", s))
            return RelopNode.EQ;
        //else null
        return null;
    }

    /**
     * SENS  ::= "fuelLeft" | "oppLR" | "oppFB" | "numBarrels" | "barrelLR" | "barrelFB" | "wallDist"
     *
     * @param s
     * @return
     */
    private static NumberNode parseSens(Scanner s) {
        if (checkFor("barrelLR", s)) {
            //Check for optional argument
            if (checkFor(OPENPAREN, s)) {
                NumberNode expr = parseEXPR(s);
                require(CLOSEPAREN, "Missing ')'", s);

                return new NumberNode() {
                    public int execute(Robot robot) {
                        return robot.getBarrelLR(expr.execute(robot));
                    }

                    public String toString() {
                        return "barrelLR(" + expr + ");\n";
                    }
                };
            }

            return NumberNode.BARREL_LR;
        }
        if (checkFor("barrelFB", s)) {
            //Optional argument
            if (checkFor(OPENPAREN, s)) {
                NumberNode expr = parseEXPR(s);
                require(CLOSEPAREN, "Missing ')'", s);

                return new NumberNode() {
                    public int execute(Robot robot) {
                        return robot.getBarrelFB(expr.execute(robot));
                    }

                    public String toString() {
                        return "barrelFB(" + expr + ");\n";
                    }
                };
            }

            return NumberNode.BARREL_FB;
        }
        if (checkFor("fuelLeft", s))
            return NumberNode.FUEL_LEFT;
        if (checkFor("oppLR", s))
            return NumberNode.OPP_LR;
        if (checkFor("oppFB", s))
            return NumberNode.OPP_FB;
        if (checkFor("numBarrels", s))
            return NumberNode.NUM_BARRELS;
        if (checkFor("wallDist", s))
            return NumberNode.WALL_DIST;

        return null;
    }

    /**
     * BLOCK ::= "{" STMT+ "}"
     */
    private static BlockNode parseBlock(Scanner s) {
        require(OPENBRACE, "Missing '{'", s);

        //Parse children
        List<ProgramNode> children = new ArrayList<>();

        while (!s.hasNext(CLOSEBRACE)) {
            ProgramNode node = parseSTMT(s);
            if (node != null)
                children.add(node);
            else if (!s.hasNext(CLOSEBRACE))
                fail("Missing '}'", s);
            else
                fail("Unknown stmt", s);
        }

        require(CLOSEBRACE, "Missing '}'", s);

        if (children.size() == 0) {
            fail("Requires at least one child", s);
            return null;
        }

        return new BlockNode() {

            public void preExec(Robot robot) {  }
            public void execute(Robot robot) {
                for (ProgramNode child : children)
                    child.execute(robot);
            }

            public void postExec(Robot robot) {  }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("{\n");
                for (ProgramNode child : children)
                    sb.append(child.toString());
                sb.append("}\n");
                return sb.toString();
            }
        };
    }

    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

    /**
     * Parse expression
     *
     * @param s
     * @return
     */
    private static NumberNode parseEXPR(Scanner s) {
        NumberNode expr = buildEXPR(s);
        if (expr != null)
            return expr;
        expr = parseSens(s);
        if (expr != null)
            return expr;

        //Parse as number otherwise
        return parseNUM(s);
    }

    /**
     * expression builder for number parsing
     *
     * @param s
     * @return
     */
    private static NumberNode buildEXPR(Scanner s) {
        String type = null;
        if (checkFor("add", s))
            type = "add";
        else if (checkFor("sub", s))
            type = "sub";
        else if (checkFor("mul", s))
            type = "mul";
        else if (checkFor("div", s))
            type = "div";

        if (type != null) {
            require(OPENPAREN, "Missing '('", s);

            NumberNode a = parseEXPR(s);
            if (a == null)
                return null;

            require(",", "Missing ','", s);

            NumberNode b = parseEXPR(s);
            if (b == null)
                return null;

            require(CLOSEPAREN, "Missing ')'", s);

            switch (type) {
                case "add":
                    return new NumberNode() {
                        public int execute(Robot robot) {
                            return a.execute(robot) + b.execute(robot);
                        }

                        public String toString() {
                            return "(" + a + " + " + b + ")";
                        }
                    };
                case "sub":
                    return new NumberNode() {
                        public int execute(Robot robot) {
                            return a.execute(robot) - b.execute(robot);
                        }

                        public String toString() {
                            return "(" + a + " - " + b + ")";
                        }
                    };
                case "mul":
                    return new NumberNode() {
                        public int execute(Robot robot) {
                            return a.execute(robot) * b.execute(robot);
                        }

                        public String toString() {
                            return "(" + a + " * " + b + ")";
                        }
                    };
                case "div":
                    return new NumberNode() {
                        public int execute(Robot robot) {
                            return a.execute(robot) / b.execute(robot);
                        }

                        public String toString() {
                            return "(" + a + " / " + b + ")";
                        }
                    };
            }
        }

        return null;
    }

    /**
     * NUM   ::= "-?[1-9][0-9]*|0"
     *
     * @param s
     * @return
     */
    private static NumberNode parseNUM(Scanner s) {
        int num = requireInt(NUMPAT, "Error, malformed number", s);
        return new NumberNode() {
            public int execute(Robot robot) {
                return num;
            }
            public String toString() {
                return Integer.toString(num);
            }
        };
    }
}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//
