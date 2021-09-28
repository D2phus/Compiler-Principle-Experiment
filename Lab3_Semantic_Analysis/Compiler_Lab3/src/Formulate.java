public class Formulate {
    /*产生式数组，从r1开始
    * */
    public String[] formulate = {
            " ",
            "E' -> S'",
            "S' -> T ID",
            "T -> D C",
            "D -> INT",
            "D -> FLOAT",
            "D -> BOOLEAN",
            "D -> CHAR",
            "D -> STRING",
            "C ->",
            "C -> [ CONST_INT ] C",
            "S' -> ID = S",
            "S -> S + A",
            "S -> S - A",
            "S -> A",
            "A -> A * B",
            "A -> B",
            "A -> - B",
            "B -> ( S )",
            "B -> ID",
            "B -> CONST_INT",
            "B -> CONST_FLOAT",
            "B -> CONST_BOOLEAN",
            "B -> CONST_CHAR",
            "B -> CONST_STRING"
    };

}
