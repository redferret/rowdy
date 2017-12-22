package rowdy;



import java.util.ArrayList;
import java.util.List;


/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
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
    
    public static final int prog = 40, stmt_list = 41, stmt = 42, 
            stmt_tail = 43, if_stmt = 44, loop_stmt = 45, break_stmt = 46, 
            assign_stmt = 47, read_stmt = 48, print_stmt = 49, expr = 50, 
            else_part = 51, id_option = 52, atom_list_tail = 53, bool_term = 54, 
            bool_term_tail = 55, bool_factor = 56, bool_factor_tail = 57, 
            arith_expr = 58, relation_option = 59, term = 60, term_tail = 61,
            factor = 62, factor_tail = 63, atom = 64, def = 65, func = 66, 
            func_tail = 67, params = 68, params_tail = 69, func_call = 70, stmt_id = 71,
            return_stmt = 72, expr_list = 73, round_stmt = 74, isset_expr = 75;
    
    private static String[] nonTerminals = {"prog", "stmt-list", "stmt", "stmt-tail", 
                            "if-stmt", "loop-stmt", "break-stmt", "assign-stmt",
                            "read-stmt", "print-stmt", "expr", "else-part", 
                            "id-option", "atom-list-tail", "bool-term", 
                            "bool-term-tail", "bool-factor", "bool-factor-tail",
                            "arith-expr", "relation-option", "term","term-tail",
                            "factor", "factor-tail", "atom", "def", "func", "func-tail",
                            "params", "params-tail", "func-call", "stmt-id", "return-stmt",
                            "expr-list", "round-stmt", "isset-expr"};
    
    private static final String[] terminals = {"PERIOD", ";", "if", "then", "else",
        "fi", "loop", "ID", ":", "repeat", "break", "=", "print",
        "read", ",", "or", "and", "<", "<=", "==", ">=",
        ">", "!=", "+", "-", "*", "/", "(", ")", "CONST", "^", "%", 
        "concat", "slice", "strcmp", "func", "->", "return", "isset", "round"};
    
    private static String mSymbols=". ( ) ; + - * / != = >= <= < > : == , ^ % ->";
    
    //Each element on the top most level is a production rule.
    private static int[][] grammarRules = {
    {def, PERIOD},
    {stmt, stmt_tail},
    {SEMICOLON, stmt, stmt_tail},
    {},
    {if_stmt},
    {loop_stmt},
    {break_stmt},
    {assign_stmt},
    {read_stmt},
    {print_stmt},
    {},//10
    {IF, expr, THEN, stmt_list, else_part},
    {ELSE, stmt_list, FI},
    {FI},
    {LOOP, ID, COLON, stmt_list, REPEAT},
    {BREAK, id_option},
    {ID}, 
    {}, 
    {ID, BECOMES, expr},//18
    {PRINT, expr, expr_list},//
    {READ, ID, params_tail},//20
    {COMMA, atom, atom_list_tail},
    {},
    {bool_term, bool_term_tail}, 
    {OR, bool_term, bool_term_tail},
    {},
    {bool_factor, bool_factor_tail},
    {AND, bool_factor, bool_factor_tail},
    {},
    {arith_expr, relation_option},
    {LESS, arith_expr},//30
    {LESSEQUAL, arith_expr},
    {EQUAL, arith_expr},
    {GREATEREQUAL, arith_expr},
    {GREATER, arith_expr},
    {NOTEQUAL, arith_expr},
    {},
    {term, term_tail}, 
    {PLUS, term, term_tail}, 
    {MINUS, term, term_tail}, 
    {},//40
    {factor, factor_tail},
    {MULTIPLY, factor, factor_tail},
    {DIVIDE, factor, factor_tail},
    {},
    {MINUS, factor},
    {atom},
    {OPENPAREN, expr, CLOSEDPAREN},
    {ID},
    {CONST},
    {POW, factor, factor_tail},// 50
    {MOD, factor, factor_tail},
    {CONCAT, expr, expr_list},
    {SLICE, expr, COMMA, arith_expr, COMMA, arith_expr},
    {STRCMP, expr, COMMA, expr},
    
    {func, def},//55
    {assign_stmt, SEMICOLON, def}, //56
    {},//57
    {FUNC, ID, OPENPAREN, params, CLOSEDPAREN, stmt_list, PERIOD},//58
    {ID, params_tail},//59
    {},//60
    {COMMA, ID, params_tail},//61
    {},//62
    {CALL, ID, OPENPAREN, expr, expr_list, CLOSEDPAREN},//63
    {func_call},//64
    {return_stmt},
    {RETURN, expr},
    {func_call, SEMICOLON, def},
    {COMMA, expr, expr_list},//68
    {},//69
    {ISSET, ID},
    {round_stmt},
    {ROUND, ID, COMMA, arith_expr},
    {isset_expr}
    };
    /**
     * Each hint maps respectively to nonTerminals list.
     * {<terminal>, <production rule>}
     */
    private static int[][][] grammarHints ={
    // prog
    {{FUNC, 0}, {ID, 0}},
    // stmt-list
    {{PERIOD,3},{SEMICOLON,1},{IF, 1},{ELSE, 1},{FI, 1},{LOOP, 1},{ID, 1},
     {REPEAT, 1},{BREAK, 1},{PRINT, 1},{READ, 1},{CONCAT, 1}, {SLICE, 1}, 
     {STRCMP, 1},{CALL, 1}, {RETURN, 1}, {ROUND, 1}},
    // stmt
    {{PERIOD,10}, {SEMICOLON, 10}, {IF, 4}, {ELSE, 10}, {FI, 10}, {LOOP, 5}, 
     {ID, 7}, {REPEAT, 10}, {BREAK, 6}, {PRINT, 9}, {READ, 8},{CALL, 64}, {RETURN, 65},
     {ROUND, 71}, {ISSET, 73}},
    // stmt-tail
    {{PERIOD, 3}, {SEMICOLON, 2}, {ELSE, 3}, {FI, 3}, {REPEAT, 3}, {IF, 1},
     {PRINT, 1}, {READ, 1},{LOOP, 1}, {ID, 1},{CALL, 1}, {RETURN, 1}, {ROUND, 1}},
    {{IF, 11}},
    {{LOOP, 14}},
    {{BREAK, 15}},
    {{ID, 18}},
    {{READ, 20}},
    {{PRINT, 19}},
    // expr
    {{ID, 23}, {MINUS, 23}, {OPENPAREN, 23}, {CONST, 23}, {CONCAT, 52}, 
     {SLICE, 53}, {STRCMP, 54},{CALL, 64}, {ISSET, 73}},
     
     
     
    {{ELSE, 12},{FI, 13}},
    {{PERIOD, 17},{SEMICOLON, 17},{ELSE, 17},{FI, 17},
     {ID, 16},{REPEAT, 17}},
    {{PERIOD, 22},{SEMICOLON, 22},{ELSE,22},{FI, 22},{REPEAT, 22},{COMMA, 21}},
    {{ID, 26},{MINUS, 26},{OPENPAREN, 26},{CONST, 26}},
    {{PERIOD, 25},{SEMICOLON, 25},{THEN,25},{ELSE,25},{FI,25},{REPEAT, 25},
     {OR, 24},{CLOSEDPAREN, 25}},
    {{ID, 29},{MINUS, 29},{OPENPAREN, 29},{CONST, 29}},
    {{PERIOD, 28},{SEMICOLON, 28},{THEN, 28},{ELSE, 28},{FI, 28},{REPEAT,28},
     {OR, 28},{AND, 27},{CLOSEDPAREN, 28}},
    {{ID, 37},{MINUS, 37},{OPENPAREN, 37},{CONST, 37}},
    {{PERIOD, 36},{SEMICOLON, 36},{THEN, 36},{ELSE, 36},{FI, 36},{REPEAT,36},
     {OR, 36},{AND, 36},{LESS, 30},{LESSEQUAL, 31},{EQUAL,32},{GREATEREQUAL,33},
     {GREATER,34},{NOTEQUAL,35},{CLOSEDPAREN,36}},
    {{ID, 41},{MINUS, 41},{OPENPAREN, 41},{CONST, 41}},
    {{PERIOD, 40},{SEMICOLON, 40},{THEN, 40},{ELSE, 40},{FI, 40},{REPEAT,40},
     {OR, 40},{AND, 40},{LESS, 40},{LESSEQUAL, 40},{EQUAL,40},{GREATEREQUAL,40},
     {GREATER,40},{NOTEQUAL,40},{PLUS, 38},{MINUS, 39},{CLOSEDPAREN,40}},
    {{ID, 46},{MINUS, 45},{OPENPAREN, 47},{CONST, 46}},
    {{PERIOD, 44},{SEMICOLON, 44},{THEN, 44},{ELSE, 44},{FI, 44},{REPEAT,44},
     {OR, 44},{AND, 44},{LESS,44},{LESSEQUAL, 44},{EQUAL,44},{GREATEREQUAL,44},
     {GREATER,44},{NOTEQUAL,44},{PLUS,44},{MINUS,44},{MULTIPLY,42},{DIVIDE,43},
     {POW, 50},{MOD, 51},{CLOSEDPAREN,44}},
    {{ID, 48},{CONST, 49}},
    
    
    {{FUNC, 55},{ID, 56}},//def
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
        
        
        Language rowdy = Language.build(terminals, nonTerminals, 
                grammarRules, grammarHints);
        
        ParseTree rowdyProgram = new ParseTree(rowdy);
        rowdyProgram.build(args[0], terminals, mSymbols, CONST, ID);
        
        List<Value> params = new ArrayList<>();
        
        for (int p = 1; p < args.length; p++){
        	String in = args[p];
        	if (Character.isDigit(in.charAt(0))){
        		params.add(new Value(Float.parseFloat(args[p])));
        	}else{
        		params.add(new Value(args[p]));
        	}
        	
        }
        
        rowdyProgram.execute(params);

    }
    
}
