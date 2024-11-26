package middle.IR;

import frontend.Token;

public class Op {
    public enum OpType {
        add,
        sub,
        mul,
        sdiv,
        srem,
        icmp,
        and,
        or,
        //icmp cond ops
        eq,
        ne,
        sgt,
        sge,
        slt,
        sle,
        Error
    }

    public OpType opType;
    public Op(OpType opType) {
        this.opType = opType;
    }

    static public OpType Op2Type(Token token) {
        if (token.getContent().equals("+")) return OpType.add;
        if (token.getContent().equals("-")) return OpType.sub;
        if (token.getContent().equals("*")) return OpType.mul;
        if (token.getContent().equals("/")) return OpType.sdiv;
        if (token.getContent().equals(">")) return OpType.sgt;
        if (token.getContent().equals("<")) return OpType.slt;
        if (token.getContent().equals(">=")) return OpType.sge;
        if (token.getContent().equals("<=")) return OpType.sle;
        if (token.getContent().equals("==")) return OpType.eq;
        if (token.getContent().equals("!=")) return OpType.ne;
        if (token.getContent().equals("&&")) return OpType.and;
        if (token.getContent().equals("||")) return OpType.or;
        if (token.getContent().equals("%")) return OpType.srem;
        return OpType.Error;
    }

    @Override
    public String toString() {
        return opType.toString();
    }
}
