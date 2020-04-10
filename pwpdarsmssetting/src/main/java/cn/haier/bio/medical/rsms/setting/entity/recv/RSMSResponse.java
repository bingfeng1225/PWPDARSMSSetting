package cn.haier.bio.medical.rsms.setting.entity.recv;

public class RSMSResponse extends RSMSBaseReceive {
    private byte response;

    public RSMSResponse() {

    }

    public byte getResponse() {
        return response;
    }

    public void setResponse(byte response) {
        this.response = response;
    }
}
