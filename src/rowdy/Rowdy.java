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
          STMT_BLOCK = 751,
          END = 1000;

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
            new int[][]{{RCURLY, 74}, {LCURLY, 3}}),
    new NonTerminal("stmt-list", STMT_LIST, 
            new int[][]{{SEMICOLON, 1}, {IF, 1},{ELSE, 1},{FI, 1}, 
                        {LOOP, 1}, {ID, 1}, {REPEAT, 1}, {BREAK, 1},{PRINT, 1}, 
                        {READ, 1}, {CONCAT, 1}, {SLICE, 1},{STRCMP, 1}, 
                        {CALL, 1}, {RETURN, 1}, {ROUND, 1}, {LCURLY, 3}}),
    new NonTerminal("stmt", STATEMENT, 
            new int[][]{{SEMICOLON, 10}, {IF, 4}, {ELSE, 10}, 
                        {FI, 10}, {LOOP, 5}, {ID, 7}, {REPEAT, 10}, {BREAK, 6}, 
                        {PRINT, 9}, {READ, 8}, {CALL, 64}, {RETURN, 65}, 
                        {ROUND, 71}, {ISSET, 73}, {LCURLY, 3}}),
    new NonTerminal("stmt-tail", STMT_TAIL, 
            new int[][]{{SEMICOLON, 2}, {ELSE, 3}, {FI, 3}, 
                        {REPEAT, 3}, {IF, 1}, {PRINT, 1}, {READ, 1}, {LOOP, 1}, 
                        {ID, 1}, {CALL, 1},{RETURN, 1},{ROUND, 1},{LCURLY, 3}}),
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
                        {ISSET, 73}}),
    new NonTerminal("else-part", ELSE_PART, 
            new int[][]{{ELSE, 12}, {FI, 13}}),
    new NonTerminal("id-option", ID_OPTION, 
            new int[][]{{SEMICOLON, 17}, {ELSE, 17}, {LCURLY, 3}, 
                        {ID, 16}, {REPEAT, 17}}),
    new NonTerminal("atom-list-tail", ATOM_LIST_TAIL, 
            new int[][]{{SEMICOLON, 22}, {ELSE, 22}, {LCURLY, 3}, 
                        {REPEAT, 22}, {COMMA, 21}}),
    new NonTerminal("bool-term", BOOL_TERM, 
            new int[][]{{ID, 26}, {MINUS, 26}, {OPENPAREN, 26}, {CONST, 26}}),
    new NonTerminal("bool-term-tail", BOOL_TERM_TAIL, 
            new int[][]{{SEMICOLON, 25}, {THEN, 25}, {ELSE, 25}, 
                        {LCURLY, 3}, {REPEAT, 25}, {OR, 24}, {CLOSEDPAREN, 25}}),
    new NonTerminal("bool-factor", BOOL_FACTOR, 
            new int[][]{{ID, 29}, {MINUS, 29}, {OPENPAREN, 29}, {CONST, 29}}),
    new NonTerminal("bool-factor-tail", BOOL_FACTOR_TAIL, 
            new int[][]{{SEMICOLON, 28}, {THEN, 28}, {ELSE, 28}, 
                        {LCURLY, 3}, {REPEAT, 28}, {OR, 28}, {AND, 27}, 
                        {CLOSEDPAREN, 28}}),
    new NonTerminal("arith-expr", ARITHM_EXPR, 
            new int[][]{{ID, 37}, {MINUS, 37}, {OPENPAREN, 37}, {CONST, 37}}),
    new NonTerminal("relation-option", RELATION_OPTION, 
            new int[][]{{SEMICOLON, 36}, {THEN, 36}, {ELSE, 36}, 
                        {LCURLY, 3}, {REPEAT, 36}, {OR, 36}, {AND, 36}, {LESS, 30}, 
                        {LESSEQUAL, 31}, {EQUAL, 32}, {GREATEREQUAL, 33}, 
                        {GREATER, 34}, {NOTEQUAL, 35}, {CLOSEDPAREN, 36}}),
    new NonTerminal("term", TERM, 
            new int[][]{{ID, 41}, {MINUS, 41}, {OPENPAREN, 41}, {CONST, 41}}),
    new NonTerminal("term-tail", TERM_TAIL, 
            new int[][]{{SEMICOLON, 40}, {THEN, 40}, {ELSE, 40}, {REPEAT, 40},
                        {OR, 40}, {AND, 40}, {LESS, 40}, {LESSEQUAL, 40}, 
                        {EQUAL, 40}, {GREATEREQUAL, 40},{GREATER, 40}, 
                        {NOTEQUAL, 40}, {PLUS, 38}, {MINUS, 39}, 
                        {CLOSEDPAREN, 40}, {LCURLY, 3}}),
    new NonTerminal("factor", FACTOR, 
            new int[][]{{ID, 46}, {MINUS, 45}, {OPENPAREN, 47}, {CONST, 46}}),
    new NonTerminal("factor-tail", FACTOR_TAIL, 
            new int[][]{{SEMICOLON, 44}, {THEN, 44}, {ELSE, 44}, 
                        {REPEAT, 44}, {OR, 44}, {AND, 44}, {LESS, 44}, 
                        {LESSEQUAL, 44}, {EQUAL, 44}, {GREATEREQUAL, 44}, 
                        {GREATER, 44},{NOTEQUAL, 44}, {PLUS, 44}, {MINUS, 44}, 
                        {MULTIPLY, 42}, {DIVIDE, 43}, {POW, 50}, {MOD, 51}, 
                        {CLOSEDPAREN, 44}, {LCURLY, 3}}),
    new NonTerminal("atom", ATOMIC, 
            new int[][]{{ID, 48}, {CONST, 49}}),
    new NonTerminal("def", DEFINITION, 
            new int[][]{{FUNC, 55}, {ID, 56}, {LCURLY, 3}}),
    new NonTerminal("func", FUNCTION, 
            new int[][]{{FUNC, 58}}),
    new NonTerminal("func-tail", FUNC_TAIL, 
            new int[][]{{LCURLY, 3}}),
    new NonTerminal("params", PARAMETERS, 
            new int[][]{{ID, 59}, {CLOSEDPAREN, 60}}),
    new NonTerminal("params-tail", PARAMS_TAIL, 
            new int[][]{{COMMA, 61}, {CLOSEDPAREN, 60}}),
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
  
  //Each element on the top most level is a production rule.
  private static final int[][] GRAMMAR_RULES = {
    {DEFINITION},
    {STATEMENT, STMT_TAIL},
    {SEMICOLON, STATEMENT, STMT_TAIL},
    {},
    {IF_STMT},
    {LOOP_STMT},
    {BREAK_STMT},
    {ASSIGN_STMT},
    {READ_STMT},
    {PRINT_STMT},
    {},//10
    {IF, EXPRESSION, STMT_BLOCK, ELSE_PART},
    {ELSE, STMT_BLOCK},
    {FI},
    {LOOP, ID, COLON, STMT_BLOCK},
    {BREAK, ID_OPTION},
    {ID},
    {},
    {ID, BECOMES, EXPRESSION},//18
    {PRINT, EXPRESSION, EXPR_LIST},//
    {READ, ID, PARAMS_TAIL},//20
    {COMMA, ATOMIC, ATOM_LIST_TAIL},
    {},
    {BOOL_TERM, BOOL_TERM_TAIL},
    {OR, BOOL_TERM, BOOL_TERM_TAIL},
    {},
    {BOOL_FACTOR, BOOL_FACTOR_TAIL},
    {AND, BOOL_FACTOR, BOOL_FACTOR_TAIL},
    {},
    {ARITHM_EXPR, RELATION_OPTION},
    {LESS, ARITHM_EXPR},//30
    {LESSEQUAL, ARITHM_EXPR},
    {EQUAL, ARITHM_EXPR},
    {GREATEREQUAL, ARITHM_EXPR},
    {GREATER, ARITHM_EXPR},
    {NOTEQUAL, ARITHM_EXPR},
    {},
    {TERM, TERM_TAIL},
    {PLUS, TERM, TERM_TAIL},
    {MINUS, TERM, TERM_TAIL},
    {},//40
    {FACTOR, FACTOR_TAIL},
    {MULTIPLY, FACTOR, FACTOR_TAIL},
    {DIVIDE, FACTOR, FACTOR_TAIL},
    {},
    {MINUS, FACTOR},
    {ATOMIC},
    {OPENPAREN, EXPRESSION, CLOSEDPAREN},
    {ID},
    {CONST},
    {POW, FACTOR, FACTOR_TAIL},// 50
    {MOD, FACTOR, FACTOR_TAIL},
    {CONCAT, EXPRESSION, EXPR_LIST},
    {SLICE, EXPRESSION, COMMA, ARITHM_EXPR, COMMA, ARITHM_EXPR},
    {STRCMP, EXPRESSION, COMMA, EXPRESSION},
    {FUNCTION, DEFINITION},//55
    {ASSIGN_STMT, SEMICOLON, DEFINITION}, //56
    {},//57
    {FUNC, ID, OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK},//58
    {ID, PARAMS_TAIL},//59
    {},//60
    {COMMA, ID, PARAMS_TAIL},//61
    {},//62
    {CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN},//63
    {FUNC_CALL},//64
    {RETURN_STMT},
    {RETURN, EXPRESSION},
    {FUNC_CALL, SEMICOLON, DEFINITION},
    {COMMA, EXPRESSION, EXPR_LIST},//68
    {},//69
    {ISSET, ID},
    {ROUND_STMT},
    {ROUND, ID, COMMA, ARITHM_EXPR},
    {ISSET_EXPR},
    {RCURLY, STMT_LIST, LCURLY}
  };

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    Language rowdy = Language.build(TERMINALS, GRAMMAR_RULES, NONTERMINALS);

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
