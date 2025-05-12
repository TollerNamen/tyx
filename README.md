# tyx
A compiled functional/object programming language

### A brief Introduction:

#### Variable Declaration
The basic form:
```
my? native? force? NAME @ TYPE = EXPRESSION
```
An Example:
```
add @Int: Int: Int = a: b: a + b

my addRepeated = a@Int: b@Int: c@Int: c * add a b
```

#### Expressions
Most common expressions like `+`, `-` or comparing `==` and `!=` are available.
##### Function Expression
Function Expressions are constructed like:
```
argument: expression
```
You can nest these as well:
```
arg1: arg2: arg3: expression
```
You can type hint the arguments as well:
```
a@Int: b@Int: a + b
```
##### Ternary
A basic condition checking
```
a <= b
? a + b
: b
```
