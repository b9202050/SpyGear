package ntu.embedded.spycar;

public enum ControlType {
    FORWARD('F'), BACKWARD('B'), LEFT('L'), RIGHT('R'),
    FORWARD_H('f'), BACKWARD_H('b'), LEFT_H('l'), RIGHT_H('r'),
    STOP('S');

    private char code;

    private ControlType(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
}
