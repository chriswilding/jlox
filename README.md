# Jlox

Lox is the programming language from [Crafting Interpreters](https://craftinginterpreters.com) by Bob Nystrom.

This repository contains my implementation of the jlox tree-walk interpreter written while following the book. My implementation uses a number of Java 17 features includes records and switch expressions.

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
> fun fib(n) { if (n < 2) return n; return fib(n - 2) + fib(n - 1); }
> print fib(35) == 9227465;
true
```
