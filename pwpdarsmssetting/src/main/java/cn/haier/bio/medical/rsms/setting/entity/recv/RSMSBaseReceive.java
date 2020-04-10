package cn.haier.bio.medical.rsms.setting.entity.recv;

public class RSMSBaseReceive {
    protected int commandType;

    public RSMSBaseReceive() {
    }

    public RSMSBaseReceive(int commandType) {
        this.commandType = commandType;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }
}
