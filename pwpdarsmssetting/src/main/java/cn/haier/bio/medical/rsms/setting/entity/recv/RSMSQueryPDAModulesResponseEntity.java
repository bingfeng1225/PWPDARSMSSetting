package cn.haier.bio.medical.rsms.setting.entity.recv;

import cn.haier.bio.medical.rsms.setting.tools.RSMSSettingTools;
import cn.qd.peiwen.pwtools.ByteUtils;

public class RSMSQueryPDAModulesResponseEntity extends RSMSRecvBaseEntity {
    private byte deviceType;
    private byte configType;

    public RSMSQueryPDAModulesResponseEntity() {
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
        buffer.append("设备类型：" + ByteUtils.bytes2HexString(new byte[]{this.deviceType}) + "\n");
        buffer.append("配置类型：" + ByteUtils.bytes2HexString(new byte[]{this.configType}) + "\n");
        return buffer.toString();
    }
}
