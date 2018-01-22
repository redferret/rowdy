package rowdy;

import java.util.ArrayList;
import java.util.List;

/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
 * This whole construction is VERY confusing and needs help. It makes it
 * harder to add to the Rowdy language and needs better abstraction.
 * 
 * @author Richard DeSilvey
 */
public class Rowdy {

  public static final int PERIOD = 0, SEMICOLON = 1, IF = 2, THEN = 3,
          ELSE = 4, FI = 5, LOOP = 6, ID = 7,
          COLON = 8, REPEAT = 9, BREAK = 10, BECOMES = 11, PRINT = 12,
          READ = 13, COMMA = 14, OR = 15, AND = 16,
          LESS = 17, LESSEQUAL = 18, EQUAL = 19, GREATEREQUAL = 20,
          GREATER = 21, NOTEQUAL = 22, PLUS = 23,
          MINUS = 24, MULTIPLY = 25, DIVIDE = 26, OPENPAREN = 27,
          CLOSEDPAREN = 28, CONST = 29, POW = 30, MOD = 31, CONCAT = 32,
          SLICE = 33, STRCMP = 34, FUNC = 35, CALL = 36, RETURN = 37,
          ISSET = 38, ROUND = 39, RCURLY = 40, LCURLY = 41;

  public static final int PROGRAM = 400, STMT_LIST = 410, STATEMENT = 420,
          STMT_TAIL = 430, IF_STMT = 440, LOOP_STMT = 450, BREAK_STMT = 460,
          ASSIGN_STMT = 470, READ_STMT = 480, PRINT_STMT = 490, EXPRESSION = 500,
          ELSE_PART = 510, ID_OPTION = 520, ATOM_LIST_TAIL = 530, BOOL_TERM = 540,
          BOOL_TERM_TAIL = 550, BOOL_FACTOR = 560, BOOL_FACTOR_TAIL = 570,
          ARITHM_EXPR = 580, RELATION_OPTION = 590, TERM = 600, TERM_TAIL = 610,
          FACTOR = 620, FACTOR_TAIL = 630, ATOMIC = 640, DEFINITION = 650, FUNCTION = 660,
          FUNC_TAIL = 670, PARAMETERS = 680, PARAMS_TAIL = 690, FUNC_CALL = 700, STMT_ID = 710,
          RETURN_STMT = 720, EXPR_LIST = 730, ROUND_STMT = 740, ISSET_EXPR = 750,
          STMT_BLOCK = 751, FUNCTION_BODY = 752, ANONYMOUS_FUNC = 753,
          END = 10;

  private static final String[] TERMINALS = {"PERIOD", ";", "if", "then","else",
    "fi", "loop", "ID", ":", "repeat", "break", "=", "print",
    "read", ",", "or", "and", "<", "<=", "==", ">=",
    ">", "!=", "+", "-", "*", "/", "(", ")", "CONST", "^", "%",
    "concat", "slice", "strcmp", "func", "->", "return", "isset", "round",
    "{", "}"};

  private static final String SPECIAL_SYMBOLS = ". ( ) ; + - * / != = >= "
          + "<= < > : == , ^ % -> { }";

  private static final NonTerminal[] NONTERMINALS = {
    new NonTerminal("prog", PROGRAM, 
            new int[][]{{FUNC, 0}, {ID, 0}}),
    new NonTerminal("stmt-block", STMT_BLOCK, 
            new int[][]{{RCURLY, 74}, {LCURLY, END}}),
    new NonTerminal("stmt-list", STMT_LIST, 
            new int[][]{{SEMICOLON, 1}, {IF, 1},{ELSE, 1},{FI, 1}, 
                        {LOOP, 1}, {ID, 1}, {REPEAT, 1}, {BREAK, 1},{PRINT, 1}, 
                        {READ, 1}, {CONCAT, 1}, {SLICE, 1},{STRCMP, 1}, 
                        {CALL, 1}, {RETURN, 1}, {ROUND, 1}, {LCURLY, END}}),
    new NonTerminal("stmt", STATEMENT, 
            new int[][]{{SEMICOLON, END}, {IF, 4}, {ELSE, END}, 
                        {FI, END}, {LOOP, 5}, {ID, 7}, {REPEAT, END}, {BREAK, 6}, 
                        {PRINT, 9}, {READ, 8}, {CALL, 64}, {RETURN, 65}, 
                        {ROUND, 71}, {ISSET, 73}, {LCURLY, END}}),
    new NonTerminal("stmt-tail", STMT_TAIL, 
            new int[][]{{SEMICOLON, 2}, {ELSE, END}, {FI, END}, 
                        {REPEAT, END}, {IF, 1}, {PRINT, 1}, {READ, 1}, {LOOP, 1}, 
                        {ID, 1}, {CALL, 1},{RETURN, 1},{ROUND, 1},{LCURLY, END}}),
    new NonTerminal("if-stmt", IF_STMT, 
            new int[][]{{IF, 11}}),
    new NonTerminal("loop-stmt", LOOP_STMT, 
            new int[][]{{LOOP, 14}}),
    new NonTerminal("break-stmt", BREAK_STMT, 
            new int[][]{{BREAK, 15}}),
    new NonTerminal("assign-stmt", ASSIGN_STMT, 
            new int[][]{{ID, 18}}),
    new NonTerminal("read-stmt", READ_STMT, 
            new int[][]{{READ, 20}}),
    new NonTerminal("print-stmt", PRINT_STMT, 
            new int[][]{{PRINT, 19}}),
    new NonTerminal("expr", EXPRESSION, 
            new int[][]{{ID, 23}, {MINUS, 23}, {OPENPAREN, 23}, {CONST, 23}, 
                        {CONCAT, 52}, {SLICE, 53}, {STRCMP, 54}, {CALL, 64}, 
                        {ISSET, 73}, {FUNC, 76}}),
    new NonTerminal("else-part", ELSE_PART, 
            new int[][]{{ELSE, 12}, {FI, 13}}),
    new NonTerminal("id-option", ID_OPTION, 
            new int[][]{{SEMICOLON, END}, {ELSE, END}, {LCURLY, END}, 
                        {ID, 16}, {REPEAT, END}}),
    
    new NonTerminal("atom-list-tail", ATOM_LIST_TAIL, 
            new int[][]{{SEMICOLON, END}, {ELSE, END}, {LCURLY, END}, 
                        {REPEAT, END}, {COMMA, 21}}),
    
    new NonTerminal("bool-term", BOOL_TERM, 
            new int[][]{{ID, 26}, {MINUS, 26}, {OPENPAREN, 26}, {CONST, 26}}),
    new NonTerminal("bool-term-tail", BOOL_TERM_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, 
                        {LCURLY, END}, {REPEAT, END}, {OR, 24}, {CLOSEDPAREN, END}}),
    
    new NonTerminal("bool-factor", BOOL_FACTOR, 
            new int[][]{{ID, 29}, {MINUS, 29}, {OPENPAREN, 29}, {CONST, 29}}),
    new NonTerminal("bool-factor-tail", BOOL_FACTOR_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, 
                        {LCURLY, END}, {REPEAT, END}, {OR, END}, {AND, 27}, 
                        {CLOSEDPAREN, END}}),
    
    new NonTerminal("arith-expr", ARITHM_EXPR, 
            new int[][]{{ID, 37}, {MINUS, 37}, {OPENPAREN, 37}, {CONST, 37}}),
    new NonTerminal("relation-option", RELATION_OPTION, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, 
                        {LCURLY, END}, {REPEAT, END}, {OR, END}, {AND, END}, {LESS, 30}, 
                        {LESSEQUAL, 31}, {EQUAL, 32}, {GREATEREQUAL, 33}, 
                        {GREATER, 34}, {NOTEQUAL, 35}, {CLOSEDPAREN, END}}),
    
    new NonTerminal("term", TERM, 
            new int[][]{{ID, 41}, {MINUS, 41}, {OPENPAREN, 41}, {CONST, 41}}),
    new NonTerminal("term-tail", TERM_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, {REPEAT, END},
                        {OR, END}, {AND, END}, {LESS, END}, {LESSEQUAL, END}, 
                        {EQUAL, END}, {GREATEREQUAL, END},{GREATER, END}, 
                        {NOTEQUAL, END}, {PLUS, 38}, {MINUS, 39}, 
                        {CLOSEDPAREN, END}, {LCURLY, END}}),
    
    new NonTerminal("factor", FACTOR, 
            new int[][]{{ID, 46}, {MINUS, 45}, {OPENPAREN, 47}, {CONST, 46}}),
    new NonTerminal("factor-tail", FACTOR_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, 
                        {REPEAT, END}, {OR, END}, {AND, END}, {LESS, END}, 
                        {LESSEQUAL, END}, {EQUAL, END}, {GREATEREQUAL, END}, 
                        {GREATER, END},{NOTEQUAL, END}, {PLUS, END}, {MINUS, END}, 
                        {MULTIPLY, 42}, {DIVIDE, 43}, {POW, 50}, {MOD, 51}, 
                        {CLOSEDPAREN, END}, {LCURLY, END}}),
    new NonTerminal("atom", ATOMIC, 
            new int[][]{{ID, 48}, {CONST, 49}}),
    new NonTerminal("def", DEFINITION, 
            new int[][]{{FUNC, 55}, {ID, 56}, {LCURLY, END}}),
    
    new NonTerminal("func", FUNCTION, 
            new int[][]{{FUNC, 58}}),
    new NonTerminal("func-tail", FUNC_TAIL, 
            new int[][]{{LCURLY, END}}),
    new NonTerminal("func-body", FUNCTION_BODY,
            new int[][]{{OPENPAREN, 75}}),
    new NonTerminal("anonymous-func", ANONYMOUS_FUNC,
            new int[][]{{OPENPAREN, 77}}),
    
    new NonTerminal("params", PARAMETERS, 
            new int[][]{{ID, 59}, {CLOSEDPAREN, END}}),
    new NonTerminal("params-tail", PARAMS_TAIL, 
            
            new int[][]{{COMMA, 61}, {CLOSEDPAREN, END}}),
    new NonTerminal("func-call", FUNC_CALL, 
            new int[][]{{CALL, 63}}),
    new NonTerminal("stmt-id", STMT_ID, 
            new int[][]{{BECOMES, 18}, {OPENPAREN, 65}}),
    new NonTerminal("return-stmt", RETURN_STMT, 
            new int[][]{{RETURN, 66}}),
    new NonTerminal("expr-list", EXPR_LIST, 
            new int[][]{{COMMA, 68}}),
    new NonTerminal("round-stmt", ROUND_STMT, 
            new int[][]{{ROUND, 72}}),
    new NonTerminal("isset-expr", ISSET_EXPR, 
            new int[][]{{ISSET, 70}}),
  };
  
  private static final Grammar[] GRAMMAR = {
    new Grammar(0, new int[]{DEFINITION}),
    new Grammar(1, new int[]{STATEMENT, STMT_TAIL}),
    new Grammar(2, new int[]{SEMICOLON, STATEMENT, STMT_TAIL}),
    new Grammar(4, new int[]{IF_STMT}),
    new Grammar(5, new int[]{LOOP_STMT}),
    new Grammar(6, new int[]{BREAK_STMT}),
    new Grammar(7, new int[]{ASSIGN_STMT}),
    new Grammar(8, new int[]{READ_STMT}),
    new Grammar(9, new int[]{PRINT_STMT}),
    new Grammar(11, new int[]{IF, EXPRESSION, STMT_BLOCK, ELSE_PART}),
    new Grammar(12, new int[]{ELSE, STMT_BLOCK}),
    new Grammar(14, new int[]{LOOP, ID, COLON, STMT_BLOCK}),
    new Grammar(15, new int[]{BREAK, ID_OPTION}),
    new Grammar(16, new int[]{ID}),
    new Grammar(18, new int[]{ID, BECOMES, EXPRESSION}),
    new Grammar(19, new int[]{PRINT, EXPRESSION, EXPR_LIST}),
    new Grammar(20, new int[]{READ, ID, PARAMS_TAIL}),
    new Grammar(21, new int[]{COMMA, ATOMIC, ATOM_LIST_TAIL}),
    new Grammar(23, new int[]{BOOL_TERM, BOOL_TERM_TAIL}),
    new Grammar(24, new int[]{OR, BOOL_TERM, BOOL_TERM_TAIL}),
    new Grammar(26, new int[]{BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new Grammar(27, new int[]{AND, BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new Grammar(29, new int[]{ARITHM_EXPR, RELATION_OPTION}),
    new Grammar(30, new int[]{LESS, ARITHM_EXPR}),
    new Grammar(31, new int[]{LESSEQUAL, ARITHM_EXPR}),
    new Grammar(32, new int[]{EQUAL, ARITHM_EXPR}),
    new Grammar(33, new int[]{GREATEREQUAL, ARITHM_EXPR}),
    new Grammar(34, new int[]{GREATER, ARITHM_EXPR}),
    new Grammar(35, new int[]{NOTEQUAL, ARITHM_EXPR}),
    new Grammar(37, new int[]{TERM, TERM_TAIL}),
    new Grammar(38, new int[]{PLUS, TERM, TERM_TAIL}),
    new Grammar(39, new int[]{MINUS, TERM, TERM_TAIL}),
    new Grammar(41, new int[]{FACTOR, FACTOR_TAIL}),
    new Grammar(42, new int[]{MULTIPLY, FACTOR, FACTOR_TAIL}),
    new Grammar(43, new int[]{DIVIDE, FACTOR, FACTOR_TAIL}),
    new Grammar(45, new int[]{MINUS, FACTOR}),
    new Grammar(46, new int[]{ATOMIC}),
    new Grammar(47, new int[]{OPENPAREN, EXPRESSION, CLOSEDPAREN}),
    new Grammar(48, new int[]{ID}),
    new Grammar(49, new int[]{CONST}),
    new Grammar(50, new int[]{POW, FACTOR, FACTOR_TAIL}),
    new Grammar(51, new int[]{MOD, FACTOR, FACTOR_TAIL}),
    new Grammar(52, new int[]{CONCAT, EXPRESSION, EXPR_LIST}),
    new Grammar(53, new int[]{SLICE, EXPRESSION, COMMA, ARITHM_EXPR, COMMA, ARITHM_EXPR}),
    new Grammar(54, new int[]{STRCMP, EXPRESSION, COMMA, EXPRESSION}),
    new Grammar(55, new int[]{FUNCTION, DEFINITION}),
    new Grammar(56, new int[]{ASSIGN_STMT, SEMICOLON, DEFINITION}),
    new Grammar(58, new int[]{FUNC, ID, FUNCTION_BODY}),
    new Grammar(59, new int[]{ID, PARAMS_TAIL}),
    new Grammar(61, new int[]{COMMA, ID, PARAMS_TAIL}),
    new Grammar(63, new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN}),
    new Grammar(64, new int[]{FUNC_CALL}),
    new Grammar(65, new int[]{RETURN_STMT}),
    new Grammar(66, new int[]{RETURN, EXPRESSION}),
    new Grammar(67, new int[]{FUNC_CALL, SEMICOLON, DEFINITION}),
    new Grammar(68, new int[]{COMMA, EXPRESSION, EXPR_LIST}),
    new Grammar(70, new int[]{ISSET, ID}),
    new Grammar(71, new int[]{ROUND_STMT}),
    new Grammar(72, new int[]{ROUND, ID, COMMA, ARITHM_EXPR}),
    new Grammar(73, new int[]{ISSET_EXPR}),
    new Grammar(74, new int[]{RCURLY, STMT_LIST, LCURLY}),
    new Grammar(75, new int[]{OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK}),
    new Grammar(76, new int[]{FUNC, ANONYMOUS_FUNC}),
    new Grammar(77, new int[]{FUNCTION_BODY}),
    new Grammar(END, new int[]{}),
  };

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    Language rowdy = Language.build(TERMINALS, GRAMMAR, NONTERMINALS);

    RowdyParseTree rowdyProgram = new RowdyParseTree(rowdy);
    Tokenizer parser = new Tokenizer(TERMINALS, SPECIAL_SYMBOLS, ID, CONST);
    String programFileName = args[0];
    parser.parse(programFileName);
    
//    try {
      rowdyProgram.build(parser);
      List<Value> programParameters = new ArrayList<>();

      for (int p = 1; p < args.length; p++) {
        String in = args[p];
        if (Character.isDigit(in.charAt(0))) {
          programParameters.add(new Value(Float.parseFloat(args[p])));
        } else {
          programParameters.add(new Value(args[p]));
        }
      }
      rowdyProgram.execute(programParameters);
//    }catch (Exception e) {
//      System.out.println("Runtime Exception: " + e.getMessage());
//      rowdyProgram.dumpCallStack();
//    }

  }

}
