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
	PRULE_ANONYMOUS_FUNC_BODY=77, PRULE_ARRAY_EXPR = 78, PRULE_ARRAY = 79,
    PRULE_ARRAY_LINEAR_BODY = 80, PRULE_ARRAY_LINEAR=81,
    PRULE_ARRAY_LINEAR_TAIL=82, PRULE_ARRAY_KEY_VALUE_BODY=83,
    PRULE_ARRAY_KEY_VALUE_BODY_TAIL=84, PRULE_ARRAY_ACCESS=85;

  public static final int PERIOD = 0, SEMICOLON = 1, IF = 2, THEN = 3,
          ELSE = 4, FI = 5, LOOP = 6, ID = 7,
          COLON = 8, REPEAT = 9, BREAK = 10, BECOMES = 11, PRINT = 12,
          READ = 13, COMMA = 14, OR = 15, AND = 16,
          LESS = 17, LESSEQUAL = 18, EQUAL = 19, GREATEREQUAL = 20,
          GREATER = 21, NOTEQUAL = 22, PLUS = 23,
          MINUS = 24, MULTIPLY = 25, DIVIDE = 26, OPENPAREN = 27,
          CLOSEDPAREN = 28, CONST = 29, POW = 30, MOD = 31, CONCAT = 32,
          SLICE = 33, STRCMP = 34, FUNC = 35, CALL = 36, RETURN = 37,
          ISSET = 38, ROUND = 39, RCURLY = 40, LCURLY = 41, ARRAY = 42,
          GET = 43;

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
          ARRAY_BODY = 754, ARRAY_LINEAR = 755, ARRAY_EXPR = 756, 
          ARRAY_LINEAR_BODY=757,ARRAY_KEY_VALUE_BODY=758, 
          ARRAY_KEY_VALUE_BODY_TAIL=759,ARRAY_ACCESS=760, ID_EXPR=761,
          ID_EXPR_TYPE=62,
          END = 10;
  private static final String[] TERMINALS = {"PERIOD", ";", "if", "then","else",
    "fi", "loop", "ID", ":", "repeat", "break", "=", "print",
    "read", ",", "or", "and", "<", "<=", "==", ">=",
    ">", "!=", "+", "-", "*", "/", "(", ")", "CONST", "^", "%",
    "concat", "slice", "strcmp", "func", "->", "return", "isset", "round",
    "{", "}", "array", "get"};

  private static final String SPECIAL_SYMBOLS = ". ( ) ; + - * / != = >= "
          + "<= < > : == , ^ % -> { }";

  private static final NonTerminal[] NONTERMINALS = {
    new NonTerminal("prog", PROGRAM, 
            new int[][]{{FUNC, PRULE_START}, {ID, PRULE_START}}),
    new NonTerminal("stmt-block", STMT_BLOCK, 
            new int[][]{{RCURLY, PRULE_STMT_BLOCK}, {LCURLY, END}}),
    new NonTerminal("stmt-list", STMT_LIST, 
            new int[][]{{SEMICOLON,PRULE_STMT_LIST}, {IF,PRULE_STMT_LIST},
                        {ELSE,PRULE_STMT_LIST},{FI,PRULE_STMT_LIST}, 
                        {LOOP,PRULE_STMT_LIST}, {ID,PRULE_STMT_LIST}, 
                        {REPEAT,PRULE_STMT_LIST}, {BREAK,PRULE_STMT_LIST},
                        {PRINT,PRULE_STMT_LIST}, 
                        {READ,PRULE_STMT_LIST}, {CONCAT,PRULE_STMT_LIST}, 
                        {SLICE,PRULE_STMT_LIST},{STRCMP,PRULE_STMT_LIST}, 
                        {CALL,PRULE_STMT_LIST}, {RETURN,PRULE_STMT_LIST}, 
                        {ROUND,PRULE_STMT_LIST}, {LCURLY, END}}),
    new NonTerminal("stmt", STATEMENT, 
            new int[][]{{SEMICOLON, END}, {IF, PRULE_IF_STMT}, {ELSE, END}, 
                        {FI, END}, {LOOP, PRULE_LOOP_STMT}, 
                        {ID, PRULE_ASSIGN_STMT}, {REPEAT, END},
                        {BREAK,PRULE_BREAK_STMT}, 
                        {PRINT, PRULE_PRINT_STMT}, {READ, PRULE_READ_STMT}, 
                        {CALL,PRULE_FUNC_CALL_STMT},{RETURN,PRULE_RETURN_STMT}, 
                        {ROUND, PRULE_ROUND_STMT}, {ISSET, PRULE_ISSET_EXPR}, 
                        {LCURLY, END}}),
    new NonTerminal("stmt-tail", STMT_TAIL, 
            new int[][]{{SEMICOLON, PRULE_STMT_TAIL}, {ELSE, END}, {FI, END}, 
                        {REPEAT, END}, {IF,PRULE_STMT_LIST}, 
                        {PRINT,PRULE_STMT_LIST}, {READ,PRULE_STMT_LIST}, 
                        {LOOP,PRULE_STMT_LIST}, 
                        {ID,PRULE_STMT_LIST}, {CALL,PRULE_STMT_LIST},
                        {RETURN,PRULE_STMT_LIST},{ROUND,PRULE_STMT_LIST},
                        {LCURLY, END}}),
    new NonTerminal("if-stmt", IF_STMT, 
            new int[][]{{IF, PRULE_IF}}),
    new NonTerminal("loop-stmt", LOOP_STMT, 
            new int[][]{{LOOP, PRULE_LOOP}}),
    new NonTerminal("break-stmt", BREAK_STMT, 
            new int[][]{{BREAK, PRULE_BREAK}}),
    new NonTerminal("assign-stmt", ASSIGN_STMT, 
            new int[][]{{ID, PRULE_ASSIGN}}),
    new NonTerminal("read-stmt", READ_STMT, 
            new int[][]{{READ, PRULE_READ}}),
    new NonTerminal("print-stmt", PRINT_STMT, 
            new int[][]{{PRINT, PRULE_PRINT}}),
    new NonTerminal("expr", EXPRESSION, 
            new int[][]{{ID,PRULE_BOOL_TERM}, {MINUS,PRULE_BOOL_TERM}, 
                        {OPENPAREN,PRULE_BOOL_TERM}, {CONST,PRULE_BOOL_TERM}, 
                        {CONCAT, PRULE_CONCAT}, {SLICE, PRULE_SLICE}, 
                        {STRCMP, PRULE_STRCMP}, {CALL, PRULE_FUNC_CALL_STMT}, 
                        {ISSET,PRULE_ISSET_EXPR},{FUNC,PRULE_ANONYMOUS_FUNC},
                        {ARRAY, PRULE_ARRAY_EXPR}, {GET, PRULE_ARRAY_ACCESS}}),
    new NonTerminal("else-part", ELSE_PART, 
            new int[][]{{ELSE, PRULE_ELSE}}),
    new NonTerminal("id-option", ID_OPTION, 
            new int[][]{{SEMICOLON, END}, {ELSE, END}, {LCURLY, END}, 
                        {ID, PRULE_ID}}),
    
    new NonTerminal("atom-list-tail", ATOM_LIST_TAIL, 
            new int[][]{{SEMICOLON, END}, {ELSE, END}, {LCURLY, END}, 
                        {COMMA, PRULE_ATOM_LIST_TAIL}}),
    
    new NonTerminal("bool-term", BOOL_TERM, 
            new int[][]{{ID,PRULE_BOOL_FACTOR}, {MINUS,PRULE_BOOL_FACTOR}, 
                        {OPENPAREN,PRULE_BOOL_FACTOR}, 
                        {CONST,PRULE_BOOL_FACTOR}}),
    new NonTerminal("bool-term-tail", BOOL_TERM_TAIL, 
            new int[][]{{SEMICOLON, END}, {ELSE, END},{LCURLY, END}, 
                        {OR, PRULE_BOOL_TERM_OR},{CLOSEDPAREN, END}}),
    
    new NonTerminal("bool-factor", BOOL_FACTOR, 
            new int[][]{{ID,PRULE_ARITHM_EXPR}, {MINUS,PRULE_ARITHM_EXPR}, 
              {OPENPAREN,PRULE_ARITHM_EXPR}, {CONST,PRULE_ARITHM_EXPR}}),
    new NonTerminal("bool-factor-tail", BOOL_FACTOR_TAIL, 
            new int[][]{{SEMICOLON, END}, {ELSE, END},{LCURLY, END}, {OR, END}, 
                        {AND, PRULE_BOOL_FACTOR_AND},{CLOSEDPAREN, END}}),
    
    new NonTerminal("arith-expr", ARITHM_EXPR, 
            new int[][]{{ID,PRULE_TERM}, {MINUS,PRULE_TERM}, 
                        {OPENPAREN,PRULE_TERM}, {CONST,PRULE_TERM}}),
    new NonTerminal("relation-option", RELATION_OPTION, 
            new int[][]{{SEMICOLON, END}, {ELSE, END}, 
                        {LCURLY, END}, {OR, END}, {AND, END}, 
                        {LESS, PRULE_ARITHM_LESS}, 
                        {LESSEQUAL, PRULE_ARITHM_LESSEQUAL}, 
                        {EQUAL, PRULE_ARITHM_EQUAL}, 
                        {GREATEREQUAL, PRULE_ARITHM_GREATEREQUAL}, 
                        {GREATER, PRULE_ARITHM_GREATER}, 
                        {NOTEQUAL, PRULE_ARITHM_NOTEQUAL},{CLOSEDPAREN, END}}),
    
    new NonTerminal("term", TERM, 
            new int[][]{{ID,PRULE_FACTOR}, {MINUS,PRULE_FACTOR}, 
                        {OPENPAREN,PRULE_FACTOR}, {CONST,PRULE_FACTOR}}),
    new NonTerminal("term-tail", TERM_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END},{ELSE,END},{REPEAT, END},
                        {OR, END}, {AND, END}, {LESS, END}, {LESSEQUAL, END}, 
                        {EQUAL, END}, {GREATEREQUAL, END},{GREATER, END}, 
                        {NOTEQUAL, END}, {PLUS, PRULE_TERM_PLUS}, 
                        {MINUS, PRULE_TERM_MINUS},{CLOSEDPAREN, END}, 
                        {LCURLY, END}}),
    
    new NonTerminal("factor", FACTOR, 
            new int[][]{{ID, PRULE_ATOMIC}, {MINUS, PRULE_MINUS}, 
                        {OPENPAREN, PRULE_PAREN_EXPR}, {CONST, PRULE_ATOMIC}}),
    new NonTerminal("factor-tail", FACTOR_TAIL, 
            new int[][]{{SEMICOLON, END}, {THEN, END}, {ELSE, END}, 
                        {REPEAT, END}, {OR, END}, {AND, END}, {LESS, END}, 
                        {LESSEQUAL, END}, {EQUAL, END}, {GREATEREQUAL, END}, 
                        {GREATER, END},{NOTEQUAL, END}, {PLUS,END},{MINUS,END}, 
                        {MULTIPLY, PRULE_MUL}, {DIVIDE, PRULE_DIV}, 
                        {POW, PRULE_FACTOR_POW}, {MOD, PRULE_FACTOR_MOD}, 
                        {CLOSEDPAREN, END}, {LCURLY, END}}),
    new NonTerminal("atom", ATOMIC, 
            new int[][]{{ID, PRULE_ID}, {CONST, PRULE_CONST}}),
    new NonTerminal("def", DEFINITION, 
            new int[][]{{FUNC, PRULE_FUNCTION_DEF}, {ID, PRULE_ASSIGN_GLOBAL}, 
                        {LCURLY, END}}),
    
    new NonTerminal("func", FUNCTION, 
            new int[][]{{FUNC, PRULE_FUNCTION}}),
    new NonTerminal("func-tail", FUNC_TAIL, 
            new int[][]{{LCURLY, END}}),
    new NonTerminal("func-body", FUNCTION_BODY,
            new int[][]{{OPENPAREN, PRULE_FUNCTION_BODY}}),
    new NonTerminal("anonymous-func", ANONYMOUS_FUNC,
            new int[][]{{OPENPAREN, PRULE_ANONYMOUS_FUNC_BODY}}),
    
    new NonTerminal("params", PARAMETERS, 
            new int[][]{{ID, PRULE_ID_PARAM}, {CLOSEDPAREN, END}}),
    new NonTerminal("params-tail", PARAMS_TAIL, 
            
            new int[][]{{COMMA, PRULE_ID_PARAM_TAIL}, {CLOSEDPAREN, END}}),
    new NonTerminal("func-call", FUNC_CALL, 
            new int[][]{{CALL, PRULE_FUNCTION_CALL}}),
    new NonTerminal("stmt-id", STMT_ID, 
            new int[][]{{BECOMES,PRULE_ASSIGN},{OPENPAREN,PRULE_RETURN_STMT}}),
    new NonTerminal("return-stmt", RETURN_STMT, 
            new int[][]{{RETURN, PRULE_RETURN}}),
    new NonTerminal("expr-list", EXPR_LIST, 
            new int[][]{{COMMA, PRULE_EXPR_LIST}}),
    new NonTerminal("round-stmt", ROUND_STMT, 
            new int[][]{{ROUND, PRULE_ROUND}}),
    new NonTerminal("isset-expr", ISSET_EXPR, 
            new int[][]{{ISSET, PRULE_ISSET}}),
    
    new NonTerminal("array-expr", ARRAY_EXPR, 
            new int[][]{{ARRAY, PRULE_ARRAY}}),
    new NonTerminal("array-body", ARRAY_BODY, 
            new int[][]{{COMMA, PRULE_ARRAY_LINEAR_BODY}, {CLOSEDPAREN, END},
                        {COLON, PRULE_ARRAY_KEY_VALUE_BODY}}),
    new NonTerminal("array-linear-body", ARRAY_LINEAR_BODY, 
            new int[][]{{COMMA, PRULE_ARRAY_LINEAR_BODY}, {CLOSEDPAREN, END}}),
    
    new NonTerminal("array-key-value-body-tail", ARRAY_KEY_VALUE_BODY_TAIL, 
            new int[][]{{COMMA, PRULE_ARRAY_KEY_VALUE_BODY_TAIL}, {CLOSEDPAREN, END}}),
    new NonTerminal("array-key-value-body", ARRAY_KEY_VALUE_BODY, 
            new int[][]{{COLON, PRULE_ARRAY_KEY_VALUE_BODY}}),

    new NonTerminal("array-access", ARRAY_ACCESS, 
            new int[][]{}),
  };
  
  private static final ProductionRule[] GRAMMAR = {
    new ProductionRule(PRULE_START,
		new int[]{DEFINITION}),
    new ProductionRule(PRULE_STMT_LIST, 
		new int[]{STATEMENT, STMT_TAIL}),
    new ProductionRule(PRULE_STMT_TAIL, 
		new int[]{SEMICOLON, STATEMENT, STMT_TAIL}),
    new ProductionRule(PRULE_IF_STMT, 
		new int[]{IF_STMT}),
    new ProductionRule(PRULE_LOOP_STMT, 
		new int[]{LOOP_STMT}),
    new ProductionRule(PRULE_BREAK_STMT, 
		new int[]{BREAK_STMT}),
    new ProductionRule(PRULE_ASSIGN_STMT, 
		new int[]{ASSIGN_STMT}),
    new ProductionRule(PRULE_READ_STMT, 
		new int[]{READ_STMT}),
    new ProductionRule(PRULE_PRINT_STMT, 
		new int[]{PRINT_STMT}),
    new ProductionRule(PRULE_IF, 
		new int[]{IF, EXPRESSION, STMT_BLOCK, ELSE_PART}),
    new ProductionRule(PRULE_ELSE, 
		new int[]{ELSE, STMT_BLOCK}),
    new ProductionRule(PRULE_LOOP, 
		new int[]{LOOP, ID, COLON, STMT_BLOCK}),
    new ProductionRule(PRULE_BREAK, 
		new int[]{BREAK, ID_OPTION}),
    new ProductionRule(PRULE_ID, 
		new int[]{ID}),
    new ProductionRule(PRULE_ASSIGN, 
		new int[]{ID, BECOMES, EXPRESSION}),
    new ProductionRule(PRULE_PRINT, 
		new int[]{PRINT, EXPRESSION, EXPR_LIST}),
    new ProductionRule(PRULE_READ, 
		new int[]{READ, ID, PARAMS_TAIL}),
    new ProductionRule(PRULE_ATOM_LIST_TAIL, 
		new int[]{COMMA, ATOMIC, ATOM_LIST_TAIL}),
    new ProductionRule(PRULE_BOOL_TERM, 
		new int[]{BOOL_TERM, BOOL_TERM_TAIL}),
    new ProductionRule(PRULE_BOOL_TERM_OR, 
		new int[]{OR, BOOL_TERM, BOOL_TERM_TAIL}),
    new ProductionRule(PRULE_BOOL_FACTOR, 
		new int[]{BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new ProductionRule(PRULE_BOOL_FACTOR_AND, 
		new int[]{AND, BOOL_FACTOR, BOOL_FACTOR_TAIL}),
    new ProductionRule(PRULE_ARITHM_EXPR, 
		new int[]{ARITHM_EXPR, RELATION_OPTION}),
    new ProductionRule(PRULE_ARITHM_LESS, 
		new int[]{LESS, ARITHM_EXPR}),
    new ProductionRule(PRULE_ARITHM_LESSEQUAL, 
		new int[]{LESSEQUAL, ARITHM_EXPR}),
    new ProductionRule(PRULE_ARITHM_EQUAL, 
		new int[]{EQUAL, ARITHM_EXPR}),
    new ProductionRule(PRULE_ARITHM_GREATEREQUAL, 
		new int[]{GREATEREQUAL, ARITHM_EXPR}),
    new ProductionRule(PRULE_ARITHM_GREATER, 
		new int[]{GREATER, ARITHM_EXPR}),
    new ProductionRule(PRULE_ARITHM_NOTEQUAL, 
		new int[]{NOTEQUAL, ARITHM_EXPR}),
    new ProductionRule(PRULE_TERM, 
		new int[]{TERM, TERM_TAIL}),
    new ProductionRule(PRULE_TERM_PLUS, 
		new int[]{PLUS, TERM, TERM_TAIL}),
    new ProductionRule(PRULE_TERM_MINUS, 
		new int[]{MINUS, TERM, TERM_TAIL}),
    new ProductionRule(PRULE_FACTOR, 
		new int[]{FACTOR, FACTOR_TAIL}),
    new ProductionRule(PRULE_MUL, 
		new int[]{MULTIPLY, FACTOR, FACTOR_TAIL}),
    new ProductionRule(PRULE_DIV, 
		new int[]{DIVIDE, FACTOR, FACTOR_TAIL}),
    new ProductionRule(PRULE_MINUS, 
		new int[]{MINUS, FACTOR}),
    new ProductionRule(PRULE_ATOMIC, 
		new int[]{ATOMIC}),
    new ProductionRule(PRULE_PAREN_EXPR, 
		new int[]{OPENPAREN, EXPRESSION, CLOSEDPAREN}),
    new ProductionRule(PRULE_CONST, 
		new int[]{CONST}),
    new ProductionRule(PRULE_FACTOR_POW, 
		new int[]{POW, FACTOR, FACTOR_TAIL}),
    new ProductionRule(PRULE_FACTOR_MOD, 
		new int[]{MOD, FACTOR, FACTOR_TAIL}),
    new ProductionRule(PRULE_CONCAT, 
		new int[]{CONCAT, EXPRESSION, EXPR_LIST}),
    new ProductionRule(PRULE_SLICE, 
		new int[]{SLICE, EXPRESSION, COMMA, ARITHM_EXPR, COMMA, ARITHM_EXPR}),
    new ProductionRule(PRULE_STRCMP, 
		new int[]{STRCMP, EXPRESSION, COMMA, EXPRESSION}),
    new ProductionRule(PRULE_FUNCTION_DEF, 
		new int[]{FUNCTION, DEFINITION}),
    new ProductionRule(PRULE_ASSIGN_GLOBAL, 
		new int[]{ASSIGN_STMT, SEMICOLON, DEFINITION}),
    new ProductionRule(PRULE_FUNCTION, 
		new int[]{FUNC, ID, FUNCTION_BODY}),
    new ProductionRule(PRULE_ID_PARAM, 
		new int[]{ID, PARAMS_TAIL}),
    new ProductionRule(PRULE_ID_PARAM_TAIL, 
		new int[]{COMMA, ID, PARAMS_TAIL}),
    new ProductionRule(PRULE_FUNCTION_CALL, 
		new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN}),
    new ProductionRule(PRULE_FUNC_CALL_STMT, 
		new int[]{FUNC_CALL}),
    new ProductionRule(PRULE_RETURN_STMT, 
		new int[]{RETURN_STMT}),
    new ProductionRule(PRULE_RETURN, 
		new int[]{RETURN, EXPRESSION}),
    new ProductionRule(PRULE_FUNC_ASSIGN_GLOBAL, 
		new int[]{FUNC_CALL, SEMICOLON, DEFINITION}),
    new ProductionRule(PRULE_EXPR_LIST, 
		new int[]{COMMA, EXPRESSION, EXPR_LIST}),
    new ProductionRule(PRULE_ISSET, 
		new int[]{ISSET, ID}),
    new ProductionRule(PRULE_ROUND_STMT, 
		new int[]{ROUND_STMT}),
    new ProductionRule(PRULE_ROUND, 
		new int[]{ROUND, ID, COMMA, ARITHM_EXPR}),
    new ProductionRule(PRULE_ISSET_EXPR, 
		new int[]{ISSET_EXPR}),
    new ProductionRule(PRULE_STMT_BLOCK, 
		new int[]{RCURLY, STMT_LIST, LCURLY}),
    new ProductionRule(PRULE_FUNCTION_BODY, 
		new int[]{OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK}),
    new ProductionRule(PRULE_ANONYMOUS_FUNC, 
		new int[]{FUNC, ANONYMOUS_FUNC}),
    new ProductionRule(PRULE_ANONYMOUS_FUNC_BODY, 
		new int[]{FUNCTION_BODY}),
    
    new ProductionRule(PRULE_ARRAY_EXPR,
        new int[]{ARRAY_EXPR}),
    new ProductionRule(PRULE_ARRAY,
        new int[]{ARRAY, OPENPAREN, EXPRESSION, ARRAY_BODY, CLOSEDPAREN}),
    
    new ProductionRule(PRULE_ARRAY_LINEAR_BODY,
        new int[]{COMMA, EXPRESSION, ARRAY_LINEAR_BODY}),
    
    new ProductionRule(PRULE_ARRAY_KEY_VALUE_BODY,
        new int[]{COLON, EXPRESSION, ARRAY_KEY_VALUE_BODY_TAIL}),
    new ProductionRule(PRULE_ARRAY_KEY_VALUE_BODY_TAIL,
        new int[]{COMMA, EXPRESSION, ARRAY_KEY_VALUE_BODY}),
    
    new ProductionRule(PRULE_ARRAY_ACCESS,
        new int[]{GET, OPENPAREN, EXPRESSION, COMMA, EXPRESSION, CLOSEDPAREN}),
    
    new ProductionRule(END, 
		new int[]{}),
  };
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    Language rowdy = Language.build(GRAMMAR, TERMINALS, NONTERMINALS);

    RowdyParseTree rowdyProgram = new RowdyParseTree(rowdy);
    Tokenizer parser = new Tokenizer(TERMINALS, SPECIAL_SYMBOLS, ID, CONST);
    String programFileName = args[0];
    parser.parse(programFileName);
    
    try {
      rowdyProgram.build(parser);
      List<Value> programParameters = new ArrayList<>();

      for (int p = 1; p < args.length; p++) {
        String in = args[p];
        if (Character.isDigit(in.charAt(0))) {
          programParameters.add(new Value(Float.parseFloat(args[p])));
        } else {
          String argStr = args[p];
          if (argStr.equals("true") || argStr.equals("false")){
            programParameters.add(new Value(Boolean.valueOf(argStr)));
          } else {
            programParameters.add(new Value(args[p]));
          }
        }
      }
      rowdyProgram.execute(programParameters);
    }catch (Exception e) {
      System.out.println("Runtime Exception: " + e.getMessage());
      rowdyProgram.dumpCallStack();
    }

  }

}
