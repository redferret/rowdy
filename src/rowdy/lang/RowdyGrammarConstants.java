package rowdy.lang;
public class RowdyGrammarConstants {
	public static final int 
		ID = 0,
		CONSTANT = 1,
		IF = 2,
		ELSE = 3,
		LOOP = 4,
		WHILE = 5,
		COLON = 6,
		BREAK = 7,
		BECOMES = 8,
		PRINT = 9,
		READ = 10,
		COMMA = 11,
		OR = 12,
		AND = 13,
		LESS = 14,
		LESSEQUAL = 15,
		EQUAL = 16,
		GREATEREQUAL = 17,
		GREATER = 18,
		NOTEQUAL = 19,
		PLUS = 20,
		MINUS = 21,
		MULTIPLY = 22,
		DIVIDE = 23,
		OPENPAREN = 24,
		CLOSEDPAREN = 25,
		LSQUARE = 26,
		RSQUARE = 27,
		POW = 28,
		MOD = 29,
		INCREMENT = 30,
		DECREMENT = 31,
		COMP_ADD = 32,
		COMP_SUB = 33,
		COMP_MUL = 34,
		COMP_DIV = 35,
		CAST_STR = 36,
		CAST_INT = 37,
		CAST_BINT = 38,
		CAST_SHRT = 39,
		CAST_BYT = 40,
		CAST_LNG = 41,
		CAST_BOL = 42,
		CAST_DBL = 43,
		AS = 44,
		CONCAT = 45,
		FUNC = 46,
		CALL = 47,
		RETURN = 48,
		ISSET = 49,
		ROUND = 50,
		LCURLY = 51,
		RCURLY = 52,
		CONST = 53,
		GLOBAL = 54,
		NATIVE = 55,
		DYNAMIC = 56,
		STATIC = 57,
		IMPORT = 58,
		SUPER = 59,
		THIS = 60,
		PUBLIC = 61,
		PRIVATE = 62,
		CLASS = 63,
		INHERITS = 64,
		CONSTRUCTOR = 65,
		IS = 66,
		TEMP = 67,
		NEW = 68,
		DOT = 69,
		AT = 70,
		QUESTION = 71,
		PROGRAM = 1000,
		IMPORTS = 1001,
		SINGLE_IMPORT = 1002,
		DEFINITION = 1003,
		STMT_BLOCK = 1004,
		STMT_LIST = 1005,
		STATEMENT = 1006,
		IF_STMT = 1007,
		ELSE_IF_PART = 1008,
		ELSE_IF_CASE = 1009,
		LOOP_STMT = 1010,
		WHILE_LOOP = 1011,
		ASSIGN_STMT = 1012,
		ASSIGN_VALUE = 1013,
		COMPOUND_ASSIGN = 1014,
		COMP_ADD_ = 1015,
		COMP_SUB_ = 1016,
		COMP_MUL_ = 1017,
		COMP_DIV_ = 1018,
		BECOMES_EXPR = 1019,
		ID_MODIFIER = 1020,
		GLOBAL_DEF = 1021,
		CONST_OPT = 1022,
		BREAK_STMT = 1023,
		ID_OPTION = 1024,
		PRINT_STMT = 1025,
		READ_STMT = 1026,
		RETURN_STMT = 1027,
		FUNC_OPTS = 1028,
		NATIVE_FUNC_OPT = 1029,
		DYNAMIC_OPT = 1030,
		STATIC_OPT = 1031,
		FUNC_CALL = 1032,
		FUNC_CALL_TAIL = 1033,
		ARRAY_PART = 1034,
		FUNC_PARAMS = 1035,
		FUNCTION_BODY = 1036,
		PRIVATE_SCOPE = 1037,
		FUNCTION = 1038,
		ANONYMOUS_FUNC = 1039,
		PARAMETERS = 1040,
		PARAMS_TAIL = 1041,
		ARRAY_ACCESS = 1042,
		ARRAY_EXPR = 1043,
		ARRAY_BODY = 1044,
		MAP_EXPR = 1045,
		MAP_BODY = 1046,
		MAP_ELEMENT = 1047,
		CLASS_DEF = 1048,
		OBJECT_PRIVATE = 1049,
		OBJECT_PUBLIC = 1050,
		CONSTRUCTOR_METHOD = 1051,
		SUPER_CONSTRUCTOR = 1052,
		CLASS_BODY = 1053,
		PUBLIC_MEMBERS = 1054,
		PRIVATE_MEMBERS = 1055,
		CLASS_DEFS = 1056,
		INHERIT_OPT = 1057,
		DOT_ATOMIC = 1058,
		REF_ACCESS = 1059,
		THIS_ = 1060,
		ID_ = 1061,
		NEW_OBJ = 1062,
		OBJ_OR_ARRAY = 1063,
		EXPRESSION = 1064,
		EXPRESSIONS = 1065,
		INCREMENT_EXPR = 1066,
		DECREMENT_EXPR = 1067,
		EXPR_LIST = 1068,
		CAST_AS = 1069,
		CAST_OPT = 1070,
		CAST_DBL_OPT = 1071,
		CAST_STR_OPT = 1072,
		CAST_BINT_OPT = 1073,
		CAST_INT_OPT = 1074,
		CAST_BOL_OPT = 1075,
		CAST_BYT_OPT = 1076,
		CAST_SHRT_OPT = 1077,
		CAST_LNG_OPT = 1078,
		NULL_DEFAULT = 1079,
		BOOL_EXPR = 1080,
		ROUND_EXPR = 1081,
		CONCAT_EXPR = 1082,
		ISSET_EXPR = 1083,
		BOOL_TERM = 1084,
		BOOL_TERM_TAIL = 1085,
		BOOL_FACTOR = 1086,
		BOOL_FACTOR_TAIL = 1087,
		ARITHM_EXPR = 1088,
		RELATION_OPTION = 1089,
		ARITHM_LESS = 1090,
		ARITHM_LESSEQUAL = 1091,
		ARITHM_GREATEREQUAL = 1092,
		ARITHM_GREATER = 1093,
		ARITHM_EQUAL = 1094,
		ARITHM_NOTEQUAL = 1095,
		TERM = 1096,
		TERM_TAIL = 1097,
		TERM_PLUS = 1098,
		TERM_MINUS = 1099,
		FACTOR = 1100,
		FACTOR_MINUS = 1101,
		FACTOR_TAIL = 1102,
		FACTOR_TAIL_MUL = 1103,
		FACTOR_TAIL_DIV = 1104,
		FACTOR_TAIL_MOD = 1105,
		FACTOR_TAIL_POW = 1106,
		PAREN_EXPR = 1107,
		ATOMIC = 1108,
		ATOMIC_ID = 1109,
		POST_INC_DEC = 1110,
		ATOMIC_CONST = 1111,
		ATOMIC_FUNC_CALL = 1112;
}