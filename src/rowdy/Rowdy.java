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

  public static final int PRULE_START=0, PRULE_STMT_LIST=1, PRULE_STMT_TAIL=2,
	 PRULE_IF_STMT=4, PRULE_LOOP_STMT=5, PRULE_BREAK_STMT=6,PRULE_ASSIGN_STMT=7,
	 PRULE_READ_STMT=8, PRULE_PRINT_STMT=9, PRULE_IF=11, PRULE_ELSE=12,
	 PRULE_LOOP=14, PRULE_BREAK=15, PRULE_ID=16, PRULE_ASSIGN=18,
	 PRULE_PRINT=19, PRULE_READ=20, PRULE_ATOM_LIST_TAIL=21, PRULE_BOOL_TERM=23,
	 PRULE_BOOL_TERM_OR=24, PRULE_BOOL_FACTOR=26, PRULE_BOOL_FACTOR_AND=27,
	 PRULE_ARITHM_EXPR=29, PRULE_ARITHM_LESS=30, PRULE_ARITHM_LESSEQUAL=31,
	 PRULE_ARITHM_EQUAL=32,PRULE_ARITHM_GREATEREQUAL=33,PRULE_ARITHM_GREATER=34,
	 PRULE_ARITHM_NOTEQUAL=35, PRULE_TERM=37, PRULE_TERM_PLUS=38,
	 PRULE_TERM_MINUS=39, PRULE_FACTOR=41, PRULE_MUL=42, PRULE_DIV=43,
	 PRULE_MINUS=45, PRULE_ATOMIC=46, PRULE_PAREN_EXPR=47, PRULE_CONST=49,
	 PRULE_FACTOR_POW=50, PRULE_FACTOR_MOD=51, PRULE_CONCAT=52, PRULE_SLICE=53,
	 PRULE_STRCMP=54, PRULE_FUNCTION_DEF=55, PRULE_ASSIGN_GLOBAL=56,
	 PRULE_FUNCTION=58, PRULE_ID_PARAM=59, PRULE_ID_PARAM_TAIL=61,
	 PRULE_FUNCTION_CALL=63, PRULE_FUNC_CALL_STMT=64, PRULE_RETURN_STMT=65,
	 PRULE_RETURN=66, PRULE_FUNC_ASSIGN_GLOBAL=67, PRULE_EXPR_LIST=68,
	 PRULE_ISSET=70, PRULE_ROUND_STMT=71, PRULE_ROUND=72, PRULE_ISSET_EXPR=73,
	 PRULE_STMT_BLOCK=74, PRULE_FUNCTION_BODY=75, PRULE_ANONYMOUS_FUNC=76,
	 PRULE_FUNC_BODY_STMT=77;
  
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
            new int[][]{{ID, 16}, {CONST, 49}}),
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
    new Grammar(PRULE_START, new int[]{DEFINITION}),
    new Grammar(PRULE_STMT_LIST, new int[]{STATEMENT, STMT_TAIL}),
    new Grammar(PRULE_STMT_TAIL, new int[]{SEMICOLON, STATEMENT, STMT_TAIL}),
    new Grammar(PRULE_IF_STMT, new int[]{IF_STMT}),
    new Grammar(PRULE_LOOP_STMT, new int[]{LOOP_STMT}),
    new Grammar(PRULE_BREAK_STMT, new int[]{BREAK_STMT}),
    new Grammar(PRULE_ASSIGN_STMT, new int[]{ASSIGN_STMT}),
    new Grammar(PRULE_READ_STMT, new int[]{READ_STMT}),
    new Grammar(PRULE_PRINT_STMT, new int[]{PRINT_STMT}),
    new Grammar(PRULE_IF, new int[]{IF, EXPRESSION, STMT_BLOCK, ELSE_PART}),
    new Grammar(PRULE_ELSE, new int[]{ELSE, STMT_BLOCK}),
    new Grammar(PRULE_LOOP, new int[]{LOOP, ID, COLON, STMT_BLOCK}),
    new Grammar(PRULE_BREAK, new int[]{BREAK, ID_OPTION}),
    new Grammar(PRULE_ID, new int[]{ID}),
    new Grammar(PRULE_ASSIGN, new int[]{ID, BECOMES, EXPRESSION}),
    new Grammar(PRULE_PRINT, new int[]{PRINT, EXPRESSION, EXPR_LIST}),
    new Grammar(PRULE_READ, new int[]{READ, ID, PARAMS_TAIL}),
    new Grammar(PRULE_ATOM_LIST_TAIL, new int[]{COMMA, ATOMIC, ATOM_LIST_TAIL}),
    new Grammar(PRULE_BOOL_TERM, new int[]{BOOL_TERM, BOOL_TERM_TAIL}),
    new Grammar(PRULE_BOOL_TERM_OR, new int[]{OR, BOOL_TERM, BOOL_TERM_TAIL}),
    new Grammar(PRULE_BOOL_FACTOR, new int[]{BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new Grammar(PRULE_BOOL_FACTOR_AND, new int[]{AND, BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new Grammar(PRULE_ARITHM_EXPR, new int[]{ARITHM_EXPR, RELATION_OPTION}),
    new Grammar(PRULE_ARITHM_LESS, new int[]{LESS, ARITHM_EXPR}),
    new Grammar(PRULE_ARITHM_LESSEQUAL, new int[]{LESSEQUAL, ARITHM_EXPR}),
    new Grammar(PRULE_ARITHM_EQUAL, new int[]{EQUAL, ARITHM_EXPR}),
    new Grammar(PRULE_ARITHM_GREATEREQUAL, new int[]{GREATEREQUAL, ARITHM_EXPR}),
    new Grammar(PRULE_ARITHM_GREATER, new int[]{GREATER, ARITHM_EXPR}),
    new Grammar(PRULE_ARITHM_NOTEQUAL, new int[]{NOTEQUAL, ARITHM_EXPR}),
    new Grammar(PRULE_TERM, new int[]{TERM, TERM_TAIL}),
    new Grammar(PRULE_TERM_PLUS, new int[]{PLUS, TERM, TERM_TAIL}),
    new Grammar(PRULE_TERM_MINUS, new int[]{MINUS, TERM, TERM_TAIL}),
    new Grammar(PRULE_FACTOR, new int[]{FACTOR, FACTOR_TAIL}),
    new Grammar(PRULE_MUL, new int[]{MULTIPLY, FACTOR, FACTOR_TAIL}),
    new Grammar(PRULE_DIV, new int[]{DIVIDE, FACTOR, FACTOR_TAIL}),
    new Grammar(PRULE_MINUS, new int[]{MINUS, FACTOR}),
    new Grammar(PRULE_ATOMIC, new int[]{ATOMIC}),
    new Grammar(PRULE_PAREN_EXPR, new int[]{OPENPAREN, EXPRESSION, CLOSEDPAREN}),
    new Grammar(PRULE_CONST, new int[]{CONST}),
    new Grammar(PRULE_FACTOR_POW, new int[]{POW, FACTOR, FACTOR_TAIL}),
    new Grammar(PRULE_FACTOR_MOD, new int[]{MOD, FACTOR, FACTOR_TAIL}),
    new Grammar(PRULE_CONCAT, new int[]{CONCAT, EXPRESSION, EXPR_LIST}),
    new Grammar(PRULE_SLICE, new int[]{SLICE, EXPRESSION, COMMA, ARITHM_EXPR, COMMA, ARITHM_EXPR}),
    new Grammar(PRULE_STRCMP, new int[]{STRCMP, EXPRESSION, COMMA, EXPRESSION}),
    new Grammar(PRULE_FUNCTION_DEF, new int[]{FUNCTION, DEFINITION}),
    new Grammar(PRULE_ASSIGN_GLOBAL, new int[]{ASSIGN_STMT, SEMICOLON, DEFINITION}),
    new Grammar(PRULE_FUNCTION, new int[]{FUNC, ID, FUNCTION_BODY}),
    new Grammar(PRULE_ID_PARAM, new int[]{ID, PARAMS_TAIL}),
    new Grammar(PRULE_ID_PARAM_TAIL, new int[]{COMMA, ID, PARAMS_TAIL}),
    new Grammar(PRULE_FUNCTION_CALL, new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN}),
    new Grammar(PRULE_FUNC_CALL_STMT, new int[]{FUNC_CALL}),
    new Grammar(PRULE_RETURN_STMT, new int[]{RETURN_STMT}),
    new Grammar(PRULE_RETURN, new int[]{RETURN, EXPRESSION}),
    new Grammar(PRULE_FUNC_ASSIGN_GLOBAL, new int[]{FUNC_CALL, SEMICOLON, DEFINITION}),
    new Grammar(PRULE_EXPR_LIST, new int[]{COMMA, EXPRESSION, EXPR_LIST}),
    new Grammar(PRULE_ISSET, new int[]{ISSET, ID}),
    new Grammar(PRULE_ROUND_STMT, new int[]{ROUND_STMT}),
    new Grammar(PRULE_ROUND, new int[]{ROUND, ID, COMMA, ARITHM_EXPR}),
    new Grammar(PRULE_ISSET_EXPR, new int[]{ISSET_EXPR}),
    new Grammar(PRULE_STMT_BLOCK, new int[]{RCURLY, STMT_LIST, LCURLY}),
    new Grammar(PRULE_FUNCTION_BODY, new int[]{OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK}),
    new Grammar(PRULE_ANONYMOUS_FUNC, new int[]{FUNC, ANONYMOUS_FUNC}),
    new Grammar(PRULE_FUNC_BODY_STMT, new int[]{FUNCTION_BODY}),
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
