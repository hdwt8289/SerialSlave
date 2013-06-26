import javax.swing.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Vector;


public class Slave {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //在Windows系统中，可以实现Swing界面跟Windows的GUI界面相同
            Vector V_Mac = new Vector();
            V_Mac.add("C0-CB-38-36-CC-D9");
            V_Mac.add("68-A8-6D-37-FB-3A");
            V_Mac.add("FC-4D-D4-32-7B-49");
            V_Mac.add("8C-89-A5-20-A9-42");
            V_Mac.add("90-2B-34-A7-9B-DC");
            ///获取本机MAC地址
            InetAddress ia = InetAddress.getLocalHost();//获取本地IP对象
            String sMac = getMACAddress(ia);
            System.out.println("MAC：" + sMac);
            if (V_Mac.contains(sMac)) {
                ParamerSet parmSet = new ParamerSet();
                parmSet.setFrame();
            } else {
                JOptionPane.showConfirmDialog(null, "本机器没有授权！");
            }
        } catch (Exception ex) {
        }

    }

    private static String getMACAddress(InetAddress ia) throws Exception {
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        //下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }
}
