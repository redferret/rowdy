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
          ISSET = 38, ROUND = 39;

  public static final int PROGRAM = 40, STMT_LIST = 41, STATEMENT = 42,
          STMT_TAIL = 43, IF_STMT = 44, LOOP_STMT = 45, BREAK_STMT = 46,
          ASSIGN_STMT = 47, READ_STMT = 48, PRINT_STMT = 49, EXPRESSION = 50,
          ELSE_PART = 51, ID_OPTION = 52, ATOM_LIST_TAIL = 53, BOOL_TERM = 54,
          BOOL_TERM_TAIL = 55, BOOL_FACTOR = 56, BOOL_FACTOR_TAIL = 57,
          ARITHM_EXPR = 58, RELATION_OPTION = 59, TERM = 60, TERM_TAIL = 61,
          FACTOR = 62, FACTOR_TAIL = 63, ATOMIC = 64, DEFINITION = 65, FUNCTION = 66,
          FUNC_TAIL = 67, PARAMETERS = 68, PARAMS_TAIL = 69, FUNC_CALL = 70, STMT_ID = 71,
          RETURN_STMT = 72, EXPR_LIST = 73, ROUND_STMT = 74, ISSET_EXPR = 75;

  private static final String[] NON_TERMINALS = {"prog", "stmt-list", "stmt", "stmt-tail",
    "if-stmt", "loop-stmt", "break-stmt", "assign-stmt",
    "read-stmt", "print-stmt", "expr", "else-part",
    "id-option", "atom-list-tail", "bool-term",
    "bool-term-tail", "bool-factor", "bool-factor-tail",
    "arith-expr", "relation-option", "term", "term-tail",
    "factor", "factor-tail", "atom", "def", "func", "func-tail",
    "params", "params-tail", "func-call", "stmt-id", "return-stmt",
    "expr-list", "round-stmt", "isset-expr"};

  private static final String[] TERMINALS = {"PERIOD", ";", "if", "then", "else",
    "fi", "loop", "ID", ":", "repeat", "break", "=", "print",
    "read", ",", "or", "and", "<", "<=", "==", ">=",
    ">", "!=", "+", "-", "*", "/", "(", ")", "CONST", "^", "%",
    "concat", "slice", "strcmp", "func", "->", "return", "isset", "round"};

  private static final String SPECIAL_SYMBOLS = ". ( ) ; + - * / != = >= <= < > : == , ^ % ->";

  //Each element on the top most level is a production rule.
  private static final int[][] GRAMMAR_RULES = {
    {DEFINITION, PERIOD},
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
    {IF, EXPRESSION, THEN, STMT_LIST, ELSE_PART},
    {ELSE, STMT_LIST, FI},
    {FI},
    {LOOP, ID, COLON, STMT_LIST, REPEAT},
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
    {FUNC, ID, OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_LIST, PERIOD},//58
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
    {ISSET_EXPR}
  };
  /**
   * Each hint maps respectively to nonTerminals list. {<terminal>,
   * <production rule>}
   */
  private static final int[][][] GRAMMAR_HINTS = {
    // prog
    {{FUNC, 0}, {ID, 0}},
    // stmt-list
    {{PERIOD, 3}, {SEMICOLON, 1}, {IF, 1}, {ELSE, 1}, {FI, 1}, {LOOP, 1}, {ID, 1},
    {REPEAT, 1}, {BREAK, 1}, {PRINT, 1}, {READ, 1}, {CONCAT, 1}, {SLICE, 1},
    {STRCMP, 1}, {CALL, 1}, {RETURN, 1}, {ROUND, 1}},
    // stmt
    {{PERIOD, 10}, {SEMICOLON, 10}, {IF, 4}, {ELSE, 10}, {FI, 10}, {LOOP, 5},
    {ID, 7}, {REPEAT, 10}, {BREAK, 6}, {PRINT, 9}, {READ, 8}, {CALL, 64}, {RETURN, 65},
    {ROUND, 71}, {ISSET, 73}},
    // stmt-tail
    {{PERIOD, 3}, {SEMICOLON, 2}, {ELSE, 3}, {FI, 3}, {REPEAT, 3}, {IF, 1},
    {PRINT, 1}, {READ, 1}, {LOOP, 1}, {ID, 1}, {CALL, 1}, {RETURN, 1}, {ROUND, 1}},
    {{IF, 11}},
    {{LOOP, 14}},
    {{BREAK, 15}},
    {{ID, 18}},
    {{READ, 20}},
    {{PRINT, 19}},
    // expr
    {{ID, 23}, {MINUS, 23}, {OPENPAREN, 23}, {CONST, 23}, {CONCAT, 52},
    {SLICE, 53}, {STRCMP, 54}, {CALL, 64}, {ISSET, 73}},
    {{ELSE, 12}, {FI, 13}},
    {{PERIOD, 17}, {SEMICOLON, 17}, {ELSE, 17}, {FI, 17},
    {ID, 16}, {REPEAT, 17}},
    {{PERIOD, 22}, {SEMICOLON, 22}, {ELSE, 22}, {FI, 22}, {REPEAT, 22}, {COMMA, 21}},
    {{ID, 26}, {MINUS, 26}, {OPENPAREN, 26}, {CONST, 26}},
    {{PERIOD, 25}, {SEMICOLON, 25}, {THEN, 25}, {ELSE, 25}, {FI, 25}, {REPEAT, 25},
    {OR, 24}, {CLOSEDPAREN, 25}},
    {{ID, 29}, {MINUS, 29}, {OPENPAREN, 29}, {CONST, 29}},
    {{PERIOD, 28}, {SEMICOLON, 28}, {THEN, 28}, {ELSE, 28}, {FI, 28}, {REPEAT, 28},
    {OR, 28}, {AND, 27}, {CLOSEDPAREN, 28}},
    {{ID, 37}, {MINUS, 37}, {OPENPAREN, 37}, {CONST, 37}},
    {{PERIOD, 36}, {SEMICOLON, 36}, {THEN, 36}, {ELSE, 36}, {FI, 36}, {REPEAT, 36},
    {OR, 36}, {AND, 36}, {LESS, 30}, {LESSEQUAL, 31}, {EQUAL, 32}, {GREATEREQUAL, 33},
    {GREATER, 34}, {NOTEQUAL, 35}, {CLOSEDPAREN, 36}},
    {{ID, 41}, {MINUS, 41}, {OPENPAREN, 41}, {CONST, 41}},
    {{PERIOD, 40}, {SEMICOLON, 40}, {THEN, 40}, {ELSE, 40}, {FI, 40}, {REPEAT, 40},
    {OR, 40}, {AND, 40}, {LESS, 40}, {LESSEQUAL, 40}, {EQUAL, 40}, {GREATEREQUAL, 40},
    {GREATER, 40}, {NOTEQUAL, 40}, {PLUS, 38}, {MINUS, 39}, {CLOSEDPAREN, 40}},
    {{ID, 46}, {MINUS, 45}, {OPENPAREN, 47}, {CONST, 46}},
    {{PERIOD, 44}, {SEMICOLON, 44}, {THEN, 44}, {ELSE, 44}, {FI, 44}, {REPEAT, 44},
    {OR, 44}, {AND, 44}, {LESS, 44}, {LESSEQUAL, 44}, {EQUAL, 44}, {GREATEREQUAL, 44},
    {GREATER, 44}, {NOTEQUAL, 44}, {PLUS, 44}, {MINUS, 44}, {MULTIPLY, 42}, {DIVIDE, 43},
    {POW, 50}, {MOD, 51}, {CLOSEDPAREN, 44}},
    {{ID, 48}, {CONST, 49}},
    {{FUNC, 55}, {ID, 56}},//def
    {{FUNC, 58}},//func
    {},//func_tail
    {{ID, 59}, {CLOSEDPAREN, 60}},//params
    {{COMMA, 61}, {CLOSEDPAREN, 60}},//params_tail
    {{CALL, 63}},//func_call
    {{BECOMES, 18}, {OPENPAREN, 65}},//stmt_id
    {{RETURN, 66}},// return-stmt
    {{COMMA, 68}},// expr-list
    {{ROUND, 72}},// round-stmt
    {{ISSET, 70}}
  };

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    Language rowdy = Language.build(TERMINALS, NON_TERMINALS,
            GRAMMAR_RULES, GRAMMAR_HINTS);

    ParseTree rowdyProgram = new ParseTree(rowdy);
    rowdyProgram.build(args[0], TERMINALS, SPECIAL_SYMBOLS, CONST, ID);

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

  }

}
