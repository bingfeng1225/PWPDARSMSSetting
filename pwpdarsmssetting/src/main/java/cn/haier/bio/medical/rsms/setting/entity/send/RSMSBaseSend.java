package cn.haier.bio.medical.rsms.setting.entity.send;

public class RSMSBaseSend {
    protected final int commandType;
    protected final boolean needResponse;

    public RSMSBaseSend(int commandType) {
        this.needResponse = true;
        this.commandType = commandType;
    }

    public RSMSBaseSend(int commandType, boolean needResponse) {
        this.commandType = commandType;
        this.needResponse = needResponse;
    }

    public int getCommandType() {
        return commandType;
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public byte[] packageSendMessage() {
        return new byte[0];
    }
}
