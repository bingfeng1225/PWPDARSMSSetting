package cn.haier.bio.medical.rsms.setting.entity.recv;

import cn.haier.bio.medical.rsms.setting.tools.RSMSSettingTools;

public class RSMSQueryPDAModulesResponse extends RSMSBaseReceive {
    private byte deviceType;
    private byte configType;

    public RSMSQueryPDAModulesResponse() {
        super(RSMSSettingTools.RSMS_RESPONSE_QUERY_PDA_MODULES);
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getConfigType() {
        return configType;
    }

    public void setConfigType(byte configType) {
        this.configType = configType;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("设备类型：" + RSMSSettingTools.bytes2HexString(new byte[]{this.deviceType}) + "\n");
        buffer.append("配置类型：" + RSMSSettingTools.bytes2HexString(new byte[]{this.configType}) + "\n");
        return buffer.toString();
    }
}
