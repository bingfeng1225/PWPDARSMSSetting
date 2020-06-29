package cn.haier.bio.medical.rsms.setting;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSResponse;
import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSQueryModulesResponse;
import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSQueryPDAModulesResponse;
import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSBaseReceive;
import cn.haier.bio.medical.rsms.setting.entity.send.RSMSBaseSend;
import cn.haier.bio.medical.rsms.setting.tools.RSMSSettingTools;
import cn.qd.peiwen.pwtools.ByteUtils;
import cn.qd.peiwen.pwtools.EmptyUtils;
import cn.qd.peiwen.socket.IPWSocketClientListener;
import cn.qd.peiwen.socket.PWSocketCilent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class RSMSSettingManager implements IPWSocketClientListener {
    private PWSocketCilent client;
    private static RSMSSettingManager manager;
    private WeakReference<IRSMSSettingListener> listener;


    public static RSMSSettingManager getInstance() {
        if (manager == null) {
            synchronized (RSMSSettingManager.class) {
                if (manager == null)
                    manager = new RSMSSettingManager();
            }
        }
        return manager;
    }

    private RSMSSettingManager() {

    }

    public void init() {
        if (EmptyUtils.isEmpty(this.client)) {
            this.client = new PWSocketCilent("RSMSSettingManager");
            this.client.setHost("192.168.7.1");
            this.client.setPort(9998);
            this.client.setReadTimeout(120);
            this.client.setWriteTimeout(60);
            this.client.setConnectTimeout(5000);
            this.client.setListener(this);
            this.client.init();
        }
    }


    public void enable() {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.enable();
        }
    }

    public void disable() {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.disable();
        }
    }

    public void release() {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.disable();
            this.client.release();
            this.client = null;
        }
    }

    public void write(Object msg) {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.write(msg);
        }
    }

    public void writeAndFlush(Object msg) {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.writeAndFlush(msg);
        }
    }

    public void changeListener(IRSMSSettingListener listener){
        this.listener = new WeakReference<>(listener);
    }

    private void loggerPrint(String message) {
        if(EmptyUtils.isNotEmpty(this.listener)){
            this.listener.get().onLoggerPrint(message);
        }
    }

    @Override
    public void onSocketClientInitialized(PWSocketCilent client) {
        this.loggerPrint("" + client + " initialized");
    }

    @Override
    public void onSocketClientConnecting(PWSocketCilent client) {
        this.loggerPrint("" + client + " connecting");
    }

    @Override
    public void onSocketClientConnected(PWSocketCilent client) {
        this.loggerPrint("" + client + " connected");
        RSMSBaseSend entity = new RSMSBaseSend(RSMSSettingTools.RSMS_COMMAND_QUERY_PDA_MODULES);
        client.writeAndFlush(entity);
        if(EmptyUtils.isNotEmpty(this.listener)){
            this.listener.get().onConnected();
        }
    }

    @Override
    public void onSocketClientDisconnecting(PWSocketCilent client) {
        this.loggerPrint("" + client + " disconnecting");
    }

    @Override
    public void onSocketClientDisconnected(PWSocketCilent client) {
        this.loggerPrint("" + client + " disconnected");
        this.listener.get().onDisconnected();
    }

    @Override
    public void onSocketClientReleaseing(PWSocketCilent client) {
        this.loggerPrint("" + client + " releaseing");
    }

    @Override
    public void onSocketClientReleased(PWSocketCilent client) {
        this.loggerPrint("" + client + " released");
    }

    @Override
    public void onSocketClientExceptionCaught(PWSocketCilent client, Throwable throwable) {
        if(EmptyUtils.isNotEmpty(this.listener)){
            this.listener.get().onExceptionCaught(throwable);
        }
    }

    @Override
    public boolean onSocketClientInitDecoder(PWSocketCilent client, SocketChannel channel) {
        return false;
    }

    @Override
    public boolean onSocketClientInitEncoder(PWSocketCilent client, SocketChannel channel) {
        return false;
    }

    @Override
    public void onSocketClientReadTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {
        this.loggerPrint("" + client + " read timeout");
        this.disable();
    }

    @Override
    public void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {
        this.loggerPrint("" + client + " write timeout");
        RSMSBaseSend entity = new RSMSBaseSend(RSMSSettingTools.RSMS_COMMAND_QUERY_MODULES);
        client.writeAndFlush(entity);
    }

    @Override
    public void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg) throws Exception {
        this.loggerPrint("" + client + " message received");
        RSMSBaseReceive entity = (RSMSBaseReceive) msg;
        switch (((RSMSBaseReceive) msg).getCommandType()) {
            case RSMSSettingTools.RSMS_RESPONSE_QUERY_PDA_MODULES: {
                RSMSQueryPDAModulesResponse response = (RSMSQueryPDAModulesResponse) entity;
                if(response.getDeviceType() != (byte)0xA0){
                    if(EmptyUtils.isNotEmpty(this.listener)){
                        this.listener.get().onAModelConfigEntered();
                    }
                }else{
                    if(EmptyUtils.isNotEmpty(this.listener)){
                        this.listener.get().onBModelConfigEntered();
                    }
                }
                break;
            }
            case RSMSSettingTools.RSMS_RESPONSE_CONFIG_A_MODEL:
            case RSMSSettingTools.RSMS_RESPONSE_CONFIG_B_MODEL: {
                if(EmptyUtils.isNotEmpty(this.listener)){
                    this.listener.get().onConfigSuccessed();
                }
                client.disable();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onSocketClientMessageEncode(PWSocketCilent client, ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        this.loggerPrint("" + client + " message encode");
        RSMSBaseSend entity = (RSMSBaseSend) msg;
        buffer.writeBytes(RSMSSettingTools.HEADER, 0, RSMSSettingTools.HEADER.length); //帧头 2位
        byte[] buf = EmptyUtils.isEmpty(entity) ? new byte[0] : entity.packageSendMessage();
        //数据长度 = type(1) + cmd(1) + device(1) + entity(n) + check(1)
        buffer.writeShort(4 + buf.length); //长度 2位
        buffer.writeShort(entity.getCommandType());   //2位
        buffer.writeByte(RSMSSettingTools.DEVICE);  //1位

        buffer.writeBytes(buf, 0, buf.length); //其他参数 N位

        byte l8sum = RSMSSettingTools.computeL8SumCode(buffer.array(), 2, buffer.readableBytes() - 2);
        buffer.writeByte(l8sum);  //校验和  1位
        buffer.writeBytes(RSMSSettingTools.TAILER, 0, RSMSSettingTools.TAILER.length); //帧尾 2位

        buffer.markReaderIndex();
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data, 0, data.length);
        buffer.resetReaderIndex();
        this.loggerPrint("RSMS Send:" + ByteUtils.bytes2HexString(data, true, ", "));
    }

    @Override
    public void onSocketClientMessageDecode(PWSocketCilent client, ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        this.loggerPrint("" + client + " message decode");
        while (buffer.readableBytes() > 4) {
            //帧头监测
            int headerIndex = RSMSSettingTools.indexOf(buffer, RSMSSettingTools.HEADER);
            if (headerIndex == -1) {
                if (buffer.readableBytes() >= 256) {
                    byte[] data = new byte[buffer.readableBytes()];
                    buffer.readBytes(data, 0, data.length);
                    buffer.discardReadBytes();
                    this.loggerPrint("缓冲区内的数据超过256，且不包含正常数据头，丢弃全部：" + ByteUtils.bytes2HexString(data));
                }
                break;
            }
            if (headerIndex > 0) {
                //抛弃帧头以前的数据
                byte[] data = new byte[headerIndex];
                buffer.readBytes(data, 0, headerIndex);
                buffer.discardReadBytes();
                this.loggerPrint("丢弃帧头前不合法数据：" + ByteUtils.bytes2HexString(data));
                continue;
            }
            //长度监测
            //数据长度 = type(1) + cmd(1) + data(n) + check(1)
            //总长度 = header(2) + len(2) + data(len) + tailer(2)
            short len = buffer.getShort(2);
            if (buffer.readableBytes() < len + 6) {
                break;
            }
            //帧尾监测
            int tailerIndex = RSMSSettingTools.indexOf(buffer, RSMSSettingTools.TAILER);
            if (tailerIndex != len + 4) {
                //当前包尾位置错误 丢掉正常的包头以免重复判断
                buffer.skipBytes(2);
                buffer.discardReadBytes();
                this.loggerPrint("帧尾位置不匹配，丢弃帧头，查找下一帧数据");
                continue;
            }
            buffer.markReaderIndex();
            byte[] data = new byte[len + 6];
            buffer.readBytes(data, 0, data.length);
            //校验和检验
            if (!RSMSSettingTools.checkFrame(data)) {
                buffer.resetReaderIndex();
                buffer.skipBytes(2);
                buffer.discardReadBytes();
                this.loggerPrint("校验和不匹配，丢弃帧头，查找下一帧数据");
                continue;
            }
            this.loggerPrint("RSMS Recv:" + ByteUtils.bytes2HexString(data, true, ", "));
            short type = buffer.getShort(4);
            buffer.discardReadBytes();
            switch (type) {
                case RSMSSettingTools.RSMS_RESPONSE_QUERY_MODULES: {
                    RSMSQueryModulesResponse entity = RSMSSettingTools.parseRSMSModulesEntity(data);
                    out.add(entity);
                    break;
                }
                case RSMSSettingTools.RSMS_RESPONSE_QUERY_PDA_MODULES: {
                    RSMSQueryPDAModulesResponse entity = RSMSSettingTools.parseRSMSPDAModulesEntity(data);
                    out.add(entity);
                    break;
                }
                case RSMSSettingTools.RSMS_RESPONSE_CONFIG_B_MODEL:
                case RSMSSettingTools.RSMS_RESPONSE_CONFIG_A_MODEL: {
                    RSMSResponse entity = RSMSSettingTools.parseRSMSResponseEntity(data);
                    entity.setCommandType(type);
                    out.add(entity);
                    break;
                }
                default:
                    byte[] bytes = ByteUtils.short2Bytes((short) type);
                    this.loggerPrint("指令" + ByteUtils.bytes2HexString(bytes, true) + "暂不支持");
                    break;
            }
        }
    }
}
