public class Formulate {
    /*产生式数组，从r1开始
    * */
    public String[] formulate = {
            " ",
            "E' -> S'",
            "S' -> INT ID",
            "S' -> ID = S",
            "S -> S + A",
            "S -> S - A",
            "S -> A",
            "A -> A * B",
            "A -> B",
            "B -> ( S )",
            "B -> ID",
            "B -> CONST_INT"
    };

}
