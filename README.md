If you have Notepad++ provided is the RowdyForNotepad, import to get code formatting

This is an interpreter, developed in Java, I wrote for Principals of Programming Languages. The interpretor uses recursive-decent parsing

Consult the [Rowdy Wiki](https://github.com/redferret/rowdy/wiki) for more information

# Building Rowdy
Clone the repo down and run Ant a the root of the project. If you want to most current release go [here](https://github.com/redferret/Rowdy-Release)

# Framework
The idea of how Rowdy is implemented is using the Parse tree as the physical program. Each node in the tree has the ability to be executed separate of the rest of the tree. This allows for the execution of sub-programs within the larger program. Not every tree node can be executed successfully without issue though. For instance executing just a return statement without executing a function will lead to some unknown behavior. Growdy returns a parse tree based on the `NodeFactory` provided by Growdy. This can be a very simple factory or complex enough to tell Growdy how to build each node and what kind of data abstraction the node will have.

# Grammar
Rowdy allows for the ability of adding, subtracting, or modifying the grammar. Changing the grammar in the `RowdyGrammar` won't directly change the language until the grammar is put into Growdy to build a new `gr` file. Without the `gr` file Growdy won't know how to build a parse tree based on the source code.

# Testing
The testing framework is setup to allow each node to be tested in isolation. There is a utility to help take code and feed it into Growdy to build a parse tree. This allows each snipet of rowdy code to be tested and that its behavior is showing the expected results when rowdy code is executed.
