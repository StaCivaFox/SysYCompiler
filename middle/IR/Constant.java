package middle.IR;

import middle.IR.Type.ArrayType;
import middle.IR.Type.Type;

public class Constant extends Value {
    public String constantData;
    public Constant(Type dataType, String constantData) {
        super(ValueType.ConstantVTy, dataType);
        this.constantData = constantData;
    }

    public int getIntValue() {
        int res;
        try {
            res = Integer.parseInt(constantData);

        } catch (NumberFormatException e) {
            if (this.constantData.charAt(0) == '\\') {
                res = getEscapeCharAscii(this.constantData.charAt(1));
            }
            else
                res = this.constantData.charAt(0);
        }
        return res;
    }

    public boolean isStringConst() {
        return (this.dataType instanceof ArrayType);
    }

    private int getEscapeCharAscii(char c) {
        return switch (c) {
            case 'a' -> 7;
            case 'b' -> 8;
            case 't' -> 9;
            case 'n' -> 10;
            case 'v' -> 11;
            case 'f' -> 12;
            case '0' -> 0;
            default -> c;
        };
    }

    @Override
    public String toString() {
        if (isStringConst()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            for (int i = 0; i < this.constantData.length(); i++) {
                if (this.constantData.charAt(i) == '\\') {
                    int escapeCharAscii = getEscapeCharAscii(this.constantData.charAt(i + 1));
                    String hexString = String.format("%02X", escapeCharAscii);
                    sb.append("\\").append(hexString);
                    i++;
                    continue;
                }
                /*if (this.constantData.charAt(i) == '0') {
                    sb.append("\\00");
                    continue;
                }*/
                sb.append(this.constantData.charAt(i));
            }
            sb.append("\"");
            return sb.toString();
        }
        else {
            return String.valueOf(this.getIntValue());
        }
    }

    @Override
    public String getUseStr() {
        return dataType + " " +
                this.getIntValue();
    }

    @Override
    public String getVirtualReg() {
        return String.valueOf(this.getIntValue());
    }

}
