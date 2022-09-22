# Clorth

A Forth implementation in Clojure.

## Your first Forth program

Start the REPL with `clj` and then type `@clorth` to start the Clorth interpreter. The following is a small Forth program coming from the first chapter of [Starting Forth](). Please type every line at the prompt and hit the <return> key. The following are all words definitions and they start with ":" and end with ";":


```forth
: star 42 emit;
: stars 0 do star loop;
: margin cr 30 spaces;
: blip margin star;
: bar margin 5 stars;
```

We just created a small "fun" language:

```forth
bar blip bar blip blip cr
                             *****
                             *
                             *****
                             *
                             *
```

Let's create one last word so we can just type the single letter "F":

```forth
: F bar blip bar blip blip cr
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

### Difference from Forth

* The Forth expression `1 . "2" 3` results in the following stack: [1 3] and "2" is printed on screen. In Forth proper, "2" should also appear as part of the stack and 1 should be printed instead. This is due to some restriction using the Clojure reader.
