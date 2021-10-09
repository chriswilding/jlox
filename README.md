# Jlox

Lox is the programming language from [Crafting Interpreters](https://craftinginterpreters.com) by Bob Nystrom.

This repository contains my implementation of the jlox tree-walk interpreter written while following the book.

## Prerequisites

1. [Maven](https://maven.apache.org/install.html)
1. [Java 17](https://www.oracle.com/java/technologies/downloads/)

## Build

```sh
$ mvn clean compile
```

## Run

```sh
$ mvn clean compile exec:java -Dexec.mainClass="uk.co.chriswilding.lox.Lox"
> var hello = "world";
Token[type=VAR, lexeme=var, literal=null, line=1]
Token[type=IDENTIFIER, lexeme=hello, literal=null, line=1]
Token[type=EQUAL, lexeme==, literal=null, line=1]
Token[type=STRING, lexeme="world", literal=world, line=1]
Token[type=SEMICOLON, lexeme=;, literal=null, line=1]
Token[type=EOF, lexeme=, literal=null, line=1]
```
