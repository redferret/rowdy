Consult the Wiki for more the language specs

This is an interpreter, developed in Java, I wrote for Principals of Programming Languages. The interpretor uses recursive-decent parsing to build the parse tree which is then executed during runtime.

The structure of the interpreter could be better but this was a class project and we had so much to do during that semester.

Some key parts to the interpreter are:
### Objects
* Function
* Hint
* Rule
* Symbol
* Terminal, NonTerminal
* Token
* Value

### Structures/Submodules
RowdyParseTree
Tokenizer - Similar to Lex or a lexer
Language

The Tokenizer is a lexer that parses Rowdy code and places all the tokens into a stack to be later consumed.
Language takes structured arrays representing the grammar for the language. When you build the language everything from
the structured grammar array is pieced together into two maps, the grammar and the symbols being used. The purpose of the language is to aid the RowdyParseTree when the parse tree is being constructed.
The parse tree will then use the tokeniser to pop off each token and uses that token to get a production rule from the language and adds the correct symbols to the parse tree.
Once the tree is constructed the main function node is executed. 

One aspect of the parse tree is that expressions aren't evaluated during the construction of the parse tree. One reason was the expression may contain functions that haven't been parsed yet and don't exist yet in memory. However this could be fixed by just checking to see if the expression contains a function call. If part of the expression can be evaluated then it would be helpful later in the program to have constants collapse into a single value.
