package middle.IR;

import middle.IR.Instructions.Instruction;

import java.util.LinkedHashMap;

public class SlotTracker {
    private final LinkedHashMap<Value, Integer> slot;

    private SlotTracker() {
        this.slot = new LinkedHashMap<>();
    }

    private static class SlotTrackInstance {
        private static final SlotTracker INSTANCE = new SlotTracker();
    }

    public static SlotTracker getInstance() {
        return SlotTrackInstance.INSTANCE;
    }

    //记录一个Function中所有Value的标号
    public void trace(Function function) {
        int slot = 0;
        this.slot.clear();
        //加入参数
        for (Argument argument : function.fParams) {
            this.slot.put(argument, slot++);
        }
        //加入每个基本块中的每条指令（注意每个基本块也占一个slot标号）
        for (BasicBlock basicBlock : function.basicBlocks) {
            this.slot.put(basicBlock, slot++);
            for (Instruction instruction : basicBlock.instructions) {
                if (!instruction.isVoidTy()) {
                    this.slot.put(instruction, slot++);
                }
            }
        }
    }

    //获取一个Value对应的标号
    public int getSlot(Value value) {
        return this.slot.get(value);
    }
}
