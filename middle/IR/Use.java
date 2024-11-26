package middle.IR;

public class Use {
    public Value value;
    public User user;

    //创建use关系对象的同时，将其加入全局的uses
    public Use(Value value, User user) {
        this.user = user;
        this.value = value;
        user.getContext().saveUse(this);
    }
}
