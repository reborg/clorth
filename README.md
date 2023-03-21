# Clorth

A Forth implementation in Clojure.

## Your first Forth program

Start the REPL with `clj` and then type `@clorth` to start the Clorth interpreter. The following is a small Forth program coming from the first chapter of [Starting Forth](https://www.forth.com/wp-content/uploads/2018/01/Starting-FORTH.pdf). Please type every line at the prompt and hit the <return> key. The following are all words definitions and they start with ":" and end with ";":


```forth
: star 42 emit;
: stars 0 do star loop;
: margin cr 30 spaces;
: dot margin star;
: line margin 5 stars;
```

We just created a small "fun" language:

```forth
line dot line dot dot cr
                             *****
                             *
                             *****
                             *
                             *
```

Let's create one last word so we can just type the single letter "F":

```forth
: F line dot line dot dot cr
F
                             *****
                             *
                             *****
                             *
                             *
```

### Interop

Clorth allows deep integration with Clojure. To call a function in the Clojure standard library, you need to use the `_` (underscore) operator. By default, what follows `_` is resolved in the `clorth.core` namespace and invoked on the first element on the stack:

```forth
1 2 3 _ inc .
> 4
```
