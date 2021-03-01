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
		THROW = 45,
		TRY = 46,
		CATCH = 47,
		CONCAT = 48,
		FUNC = 49,
		CALL = 50,
		RETURN = 51,
		ISSET = 52,
		ROUND = 53,
		LCURLY = 54,
		RCURLY = 55,
		CONST = 56,
		GLOBAL = 57,
		NATIVE = 58,
		DYNAMIC = 59,
		STATIC = 60,
		IMPORT = 61,
		SUPER = 62,
		THIS = 63,
		PUBLIC = 64,
		PRIVATE = 65,
		CLASS = 66,
		INHERITS = 67,
		CONSTRUCTOR = 68,
		IS = 69,
		TEMP = 70,
		NEW = 71,
		DOT = 72,
		AT = 73,
		QUESTION = 74,
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
		THROW_STMT = 1028,
		TRY_CATCH = 1029,
		OPT_CATCH = 1030,
		FUNC_OPTS = 1031,
		NATIVE_FUNC_OPT = 1032,
		DYNAMIC_OPT = 1033,
		STATIC_OPT = 1034,
		FUNC_CALL = 1035,
		FUNC_CALL_TAIL = 1036,
		ARRAY_PART = 1037,
		FUNC_PARAMS = 1038,
		FUNCTION_BODY = 1039,
		PRIVATE_SCOPE = 1040,
		FUNCTION = 1041,
		ANONYMOUS_FUNC = 1042,
		PARAMETERS = 1043,
		PARAMS_TAIL = 1044,
		ARRAY_ACCESS = 1045,
		ARRAY_EXPR = 1046,
		ARRAY_BODY = 1047,
		MAP_EXPR = 1048,
		MAP_BODY = 1049,
		MAP_ELEMENT = 1050,
		CLASS_DEF = 1051,
		OBJECT_PRIVATE = 1052,
		OBJECT_PUBLIC = 1053,
		CONSTRUCTOR_METHOD = 1054,
		SUPER_CONSTRUCTOR = 1055,
		CLASS_BODY = 1056,
		PUBLIC_MEMBERS = 1057,
		PRIVATE_MEMBERS = 1058,
		CLASS_DEFS = 1059,
		INHERIT_OPT = 1060,
		DOT_ATOMIC = 1061,
		REF_ACCESS = 1062,
		THIS_ = 1063,
		ID_ = 1064,
		NEW_OBJ = 1065,
		OPT_NEW = 1066,
		OBJ_OR_ARRAY = 1067,
		EXPRESSION = 1068,
		EXPRESSIONS = 1069,
		INCREMENT_EXPR = 1070,
		DECREMENT_EXPR = 1071,
		EXPR_LIST = 1072,
		CAST_AS = 1073,
		CAST_OPT = 1074,
		CAST_DBL_OPT = 1075,
		CAST_STR_OPT = 1076,
		CAST_BINT_OPT = 1077,
		CAST_INT_OPT = 1078,
		CAST_BOL_OPT = 1079,
		CAST_BYT_OPT = 1080,
		CAST_SHRT_OPT = 1081,
		CAST_LNG_OPT = 1082,
		NULL_DEFAULT = 1083,
		BOOL_EXPR = 1084,
		ROUND_EXPR = 1085,
		CONCAT_EXPR = 1086,
		ISSET_EXPR = 1087,
		BOOL_TERM = 1088,
		BOOL_TERM_TAIL = 1089,
		BOOL_FACTOR = 1090,
		BOOL_FACTOR_TAIL = 1091,
		ARITHM_EXPR = 1092,
		RELATION_OPTION = 1093,
		ARITHM_LESS = 1094,
		ARITHM_LESSEQUAL = 1095,
		ARITHM_GREATEREQUAL = 1096,
		ARITHM_GREATER = 1097,
		ARITHM_EQUAL = 1098,
		ARITHM_NOTEQUAL = 1099,
		TERM = 1100,
		TERM_TAIL = 1101,
		TERM_PLUS = 1102,
		TERM_MINUS = 1103,
		FACTOR = 1104,
		FACTOR_MINUS = 1105,
		FACTOR_TAIL = 1106,
		FACTOR_TAIL_MUL = 1107,
		FACTOR_TAIL_DIV = 1108,
		FACTOR_TAIL_MOD = 1109,
		FACTOR_TAIL_POW = 1110,
		PAREN_EXPR = 1111,
		ATOMIC = 1112,
		ATOMIC_ID = 1113,
		POST_INC_DEC = 1114,
		ATOMIC_CONST = 1115,
		ATOMIC_FUNC_CALL = 1116;
}