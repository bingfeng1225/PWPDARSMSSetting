package cn.haier.bio.medical.rsms.setting;

public interface IRSMSSettingListener {
    void onConnected();
    void onDisconnected();
    void onConfigSuccessed();
    void onAModelConfigEntered();
    void onBModelConfigEntered();
    void onLoggerPrint(String message);
    void onExceptionCaught(Throwable throwable);
}
