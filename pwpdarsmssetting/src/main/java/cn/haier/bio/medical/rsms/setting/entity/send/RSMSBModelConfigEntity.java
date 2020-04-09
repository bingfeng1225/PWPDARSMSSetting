package cn.haier.bio.medical.rsms.setting.entity.send;

import cn.haier.bio.medical.rsms.setting.tools.RSMSSettingTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RSMSBModelConfigEntity extends RSMSSendBaseEntity {
    private byte model; //联网模式 0x01->4G,0x02->WIFI,0x03->AUTO

    private String code; //冰箱编码BE

    private String port;//端口
    private String address;//服务器域名/IP地址

    private String username; //MQTT服务器验证用户名
    private String password; //MQTT服务器验证用户密码

    private String wifiName;//WIFI名称
    private String wifiPassword;//WIFI密码

    private String apn;//APN
    private String apnName;//APN用户名
    private String apnPassword;//APN密码

    public RSMSBModelConfigEntity() {
        super(RSMSSettingTools.RSMS_COMMAND_CONFIG_B_MODEL);
    }

    public byte getModel() {
        return model;
    }

    public void setModel(byte model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public String getApnPassword() {
        return apnPassword;
    }

    public void setApnPassword(String apnPassword) {
        this.apnPassword = apnPassword;
    }

    @Override
    public byte[] packageSendMessage() {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(model);
        buffer.writeBytes(RSMSSettingTools.packageString(code));
        buffer.writeBytes(RSMSSettingTools.packageString(address));
        buffer.writeBytes(RSMSSettingTools.packageString(port));
        buffer.writeBytes(RSMSSettingTools.packageString(username));
        buffer.writeBytes(RSMSSettingTools.packageString(password));
        buffer.writeBytes(RSMSSettingTools.packageString(wifiName));
        buffer.writeBytes(RSMSSettingTools.packageString(wifiPassword));
        buffer.writeBytes(RSMSSettingTools.packageString(apn));
        buffer.writeBytes(RSMSSettingTools.packageString(apnName));
        buffer.writeBytes(RSMSSettingTools.packageString(apnPassword));
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data, 0, buffer.readableBytes());
        buffer.release();
        return data;
    }
}
