package cn.haier.bio.medical.rsms.setting.entity.send;

import cn.haier.bio.medical.rsms.setting.tools.RSMSSettingTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RSMSAModelConfigEntity extends RSMSSendBaseEntity {
    private String code;
    private String username;
    private String password;

    public RSMSAModelConfigEntity() {
        super(RSMSSettingTools.RSMS_COMMAND_CONFIG_A_MODEL);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public byte[] packageSendMessage() {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(RSMSSettingTools.packageString(code));
        buffer.writeBytes(RSMSSettingTools.packageString(username));
        buffer.writeBytes(RSMSSettingTools.packageString(password));
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data, 0, buffer.readableBytes());
        buffer.release();
        return data;
    }
}
