package cn.haier.bio.medical.rsms.setting.tools;

import java.net.NetworkInterface;
import java.util.Enumeration;

import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSResponse;
import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSQueryModulesResponse;
import cn.haier.bio.medical.rsms.setting.entity.recv.RSMSQueryPDAModulesResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RSMSSettingTools {
    public static final byte DEVICE = (byte) 0xA2;
    public static final byte DTE_CONFIG = (byte) 0xB0;
    public static final byte PDA_CONFIG = (byte) 0xB1;
    public static final byte[] HEADER = {(byte) 0x55, (byte) 0xAA};
    public static final byte[] TAILER = {(byte) 0xEA, (byte) 0xEE};
    public static final byte[] DEFAULT_MAC = {
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };
    public static final byte[] DEFAULT_BE_CODE = {
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20
    };

    public static final int RSMS_COMMAND_QUERY_MODULES = 0x1103;
    public static final int RSMS_RESPONSE_QUERY_MODULES = 0x1203;

    public static final int RSMS_COMMAND_QUERY_PDA_MODULES = 0x1104;
    public static final int RSMS_RESPONSE_QUERY_PDA_MODULES = 0x1204;

    public static final int RSMS_COMMAND_CONFIG_A_MODEL = 0x1304;
    public static final int RSMS_RESPONSE_CONFIG_A_MODEL = 0x1404;

    public static final int RSMS_COMMAND_CONFIG_B_MODEL = 0x1305;
    public static final int RSMS_RESPONSE_CONFIG_B_MODEL = 0x1405;

    public static boolean checkFrame(byte[] data) {
        byte check = data[data.length - 3];
        byte l8sum = computeL8SumCode(data, 2, data.length - 5);
        return (check == l8sum);
    }

    public static byte[] packageString(String src) {
        ByteBuf buffer = Unpooled.buffer(22);
        buffer.writeByte('\"');
        if (null != src && src.length() > 0) {
            byte[] bytes = src.getBytes();
            buffer.writeBytes(bytes, 0, bytes.length);
        }
        buffer.writeByte('\"');
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data, 0, buffer.readableBytes());
        buffer.release();
        return data;
    }

    public static int indexOf(ByteBuf haystack, byte needle) {
        //遍历haystack的每一个字节
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            if(needle == haystack.getByte(i)){
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(ByteBuf haystack, byte[] needle) {
        //遍历haystack的每一个字节
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            int needleIndex;
            int haystackIndex = i;
            /*haystack是否出现了delimiter，注意delimiter是一个ChannelBuffer（byte[]）
            例如对于haystack="ABC\r\nDEF"，needle="\r\n"
            那么当haystackIndex=3时，找到了“\r”，此时needleIndex=0
            继续执行循环，haystackIndex++，needleIndex++，
            找到了“\n”
            至此，整个needle都匹配到了
            程序然后执行到if (needleIndex == needle.capacity())，返回结果
            */
            for (needleIndex = 0; needleIndex < needle.length; needleIndex++) {
                if (haystack.getByte(haystackIndex) != needle[needleIndex]) {
                    break;
                } else {
                    haystackIndex++;
                    if (haystackIndex == haystack.writerIndex() && needleIndex != needle.length - 1) {
                        return -1;
                    }
                }
            }

            if (needleIndex == needle.length) {
                // Found the needle from the haystack!
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }

    public static RSMSQueryModulesResponse parseRSMSModulesEntity(byte[] data) {
        ByteBuf buffer = Unpooled.copiedBuffer(data);
        buffer.skipBytes(6);

        RSMSQueryModulesResponse entity = new RSMSQueryModulesResponse();
        byte[] mcu = new byte[12];
        buffer.readBytes(mcu, 0, mcu.length);
        entity.setMcu(mcu);

        byte[] mac = new byte[6];
        buffer.readBytes(mac, 0, mac.length);
        entity.setMac(mac);

        entity.setCode(parseString(buffer));

        entity.setImei(parseString(buffer));
        entity.setIccid(parseString(buffer));
        entity.setPhone(parseString(buffer));

        entity.setModuleVersion(parseString(buffer));
        entity.setWifiVersion(parseString(buffer));
        entity.setMcuVersion(parseString(buffer));

        entity.setOperator(parseString(buffer));
        return entity;
    }

    public static RSMSQueryPDAModulesResponse parseRSMSPDAModulesEntity(byte[] data) {
        ByteBuf buffer = Unpooled.copiedBuffer(data);
        buffer.skipBytes(6);
        RSMSQueryPDAModulesResponse entity = new RSMSQueryPDAModulesResponse();
        entity.setDeviceType(buffer.readByte());
        entity.setConfigType(buffer.readByte());
        return entity;
    }

    public static RSMSResponse parseRSMSResponseEntity(byte[] data) {
        ByteBuf buffer = Unpooled.copiedBuffer(data);
        buffer.skipBytes(6);
        RSMSResponse entity = new RSMSResponse();
        entity.setResponse(buffer.readByte());
        return entity;
    }

    public static byte[] short2Bytes(short value) {
        byte bytes[] = new byte[2];
        bytes[0] = (byte) (0xff & (value >> 8));
        bytes[1] = (byte) (0xff & value);
        return bytes;
    }

    public static String bytes2HexString(byte[] data) {
        return bytes2HexString(data, false);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag) {
        return bytes2HexString(data, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        return bytes2HexString(data, 0, data.length, hexFlag, separator);
    }

    public static String bytes2HexString(byte[] data, int offset, int len) {
        return bytes2HexString(data, offset, len, false);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag) {
        return bytes2HexString(data, offset, len, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        if (offset < 0 || offset > data.length - 1) {
            throw new IllegalArgumentException("The offset index out of bounds");
        }
        if (len < 0 || offset + len > data.length) {
            throw new IllegalArgumentException("The len can not be < 0 or (offset + len) index out of bounds");
        }
        String format = "%02X";
        if (hexFlag) {
            format = "0x%02X";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = offset; i < offset + len; i++) {
            buffer.append(String.format(format, data[i]));
            if (separator == null) {
                continue;
            }
            if (i != (offset + len - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }
    //校验和取低8位算法
    public static byte computeL8SumCode(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        return computeL8SumCode(data, 0, data.length);
    }

    public static byte computeL8SumCode(byte[] data, int offset, int len) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        if (offset < 0 || offset > data.length - 1) {
            throw new IllegalArgumentException("The offset index out of bounds");
        }
        if (len < 0 || offset + len > data.length) {
            throw new IllegalArgumentException("The len can not be < 0 or (offset + len) index out of bounds");
        }
        int sum = 0;
        for (int pos = offset; pos < offset + len; pos++) {
            sum += data[pos];
        }
        return (byte) sum;
    }

    private static String parseString(ByteBuf buffer) {
        buffer.skipBytes(1); //跳过头\"
        int index = indexOf(buffer, (byte) ('\"'));
        byte[] data = new byte[index - buffer.readerIndex()];
        buffer.readBytes(data, 0, data.length);
        buffer.skipBytes(1);//跳过尾\"
        return new String(data);
    }

    private static byte[] getMachineHardwareAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface element = interfaces.nextElement();
                if ("wlan0".equals(element.getName())) {
                    return element.getHardwareAddress();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
