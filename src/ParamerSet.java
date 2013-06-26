import com.mongodb.*;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ProcessImageListener;
import com.serotonin.modbus4j.exception.ModbusInitException;
import gnu.io.CommPortIdentifier;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

public class ParamerSet extends JPanel {
    private JLabel lblIP;
    private JTextField txtIp;
    private JLabel lblName;
    private JComboBox txtProjectName;
    private JLabel lblNo;
    private JComboBox cmbNo;
    private JLabel lblBote;
    private JComboBox cmbBote;
    private JLabel lblLength;
    private JComboBox cmbLength;
    private JLabel lblParity;
    private JComboBox cmbParity;
    private JLabel lblStopBit;
    private JComboBox cmbStopBit;
    private JLabel lblDelay;
    private JTextField txtDelay;
    private JButton btnOk;
    private JButton btnCancel;
    private static String paramIp;
    private static String paramName;
    private static String paramNo;
    private static int paramBote;
    private static int parmLength;
    private static int parmParity;
    private static int parmStopBit;
    private static int parmDelay = 0;
    static Mongo m = null;
    static DB db = null;
    static ModbusSlaveSet slave = null;
    static boolean isStop = true;
    private static JFrame f;

    public void setFrame() {
        f = new JFrame("数据通讯参数设置");
        //获取屏幕分辨率的工具集
        Toolkit tool = Toolkit.getDefaultToolkit();
        //利用工具集获取屏幕的分辨率
        Dimension dim = tool.getScreenSize();
        //获取屏幕分辨率的高度
        int height = (int) dim.getHeight();
        //获取屏幕分辨率的宽度
        int width = (int) dim.getWidth();
        //设置位置
        f.setLocation((width - 300) / 2, (height - 400) / 2);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setContentPane(this);
        f.setSize(320, 260);
        f.setResizable(false);

        lblIP = new JLabel("主机名");
        txtIp = new JTextField(20);
        try {
            InetAddress addr = InetAddress.getLocalHost();
            txtIp.setText(addr.getHostAddress().toString());
        } catch (Exception ex) {
        }

        lblNo = new JLabel("端口号");
        cmbNo = new JComboBox();
        cmbNo.setEditable(true);
        cmbNo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                cmbNo.removeAllItems();
                CommPortIdentifier portId = null;
                Enumeration portList;
                portList = CommPortIdentifier.getPortIdentifiers();
                while (portList.hasMoreElements()) {
                    portId = (CommPortIdentifier) portList.nextElement();
                    cmbNo.addItem(portId.getName());
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        lblName = new JLabel("工程名");
        txtProjectName = new JComboBox();
        txtProjectName.setEditable(true);
        txtProjectName.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                txtProjectName.removeAllItems();
                Mongo m1 = null;
                try {
                    m1 = new Mongo(txtIp.getText().toString(), 27017);
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                for (String name : m1.getDatabaseNames()) {
                    txtProjectName.addItem(name);
                }
                m1.close();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        lblBote = new JLabel("波特率");
        cmbBote = new JComboBox();
        cmbBote.addItem(9600);
        cmbBote.addItem(19200);
        cmbBote.addItem(57600);
        cmbBote.addItem(115200);

        lblLength = new JLabel("数据长度");
        cmbLength = new JComboBox();
        cmbLength.addItem(8);
        cmbLength.addItem(7);

        lblParity = new JLabel("校验");
        cmbParity = new JComboBox();
        cmbParity.addItem("None");
        cmbParity.addItem("Odd");
        cmbParity.addItem("Even");

        lblStopBit = new JLabel("停止位");
        cmbStopBit = new JComboBox();
        cmbStopBit.addItem(1);
        cmbStopBit.addItem(2);

        lblDelay = new JLabel("刷新");
        txtDelay = new JTextField(20);


        btnOk = new JButton("确定");
        btnOk.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paramIp = txtIp.getText().toString();
                paramName = txtProjectName.getSelectedItem().toString();
                paramNo = cmbNo.getSelectedItem().toString();
                paramBote = Integer.parseInt(cmbBote.getSelectedItem().toString());
                parmLength = Integer.parseInt(cmbLength.getSelectedItem().toString());
                parmParity = cmbParity.getSelectedIndex();
                parmStopBit = Integer.parseInt(cmbStopBit.getSelectedItem().toString());
                parmDelay = Integer.parseInt(txtDelay.getText().toString());

                if (!paramName.equals("") && !paramNo.equals("")) {
                    receiveData(paramIp, paramName, paramNo, paramBote, parmLength, parmParity, parmStopBit, parmDelay);
                } else {

                }
            }
        });
        btnCancel = new JButton("取消");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(9, 2));

        p1.add(lblIP);
        p1.add(txtIp);

        p1.add(lblNo);
        p1.add(cmbNo);

        p1.add(lblName);
        p1.add(txtProjectName);


        p1.add(lblBote);
        p1.add(cmbBote);

        p1.add(lblLength);
        p1.add(cmbLength);

        p1.add(lblParity);
        p1.add(cmbParity);

        p1.add(lblStopBit);
        p1.add(cmbStopBit);

        p1.add(lblDelay);
        p1.add(txtDelay);
        txtDelay.setText("500");

        p1.add(btnOk);
        p1.add(btnCancel);

        p1.validate();

        f.add(p1);
        f.validate();
    }

    public void receiveData(String ip, String paramName, String paramNo, int paramBote, int paramLength, int parmParity, int parmStopBit, int parmDelay) {
        try {
            try {
                m = new Mongo(ip, 27017);
                db = m.getDB(paramName);
                //db.authenticate("test", "123".toCharArray());
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (MongoException e) {
                e.printStackTrace();
            }
            final DBCollection coll = db.getCollection("DATAIN");
            final DBCollection collout = db.getCollection("DATAOUT");
            DBCollection meta = db.getCollection("META");

            //记录数据字段
            final Map map1 = new HashMap();
            final Map map2 = new HashMap();
            Map map00 = new HashMap();
            Map map01 = new HashMap();
            Map map02 = new HashMap();
            Map map03 = new HashMap();

            final Map m_ai_max = new HashMap();
            final Map m_ai_min = new HashMap();
            final Map m_ao_max = new HashMap();
            final Map m_ao_min = new HashMap();

            DBCursor cursor = meta.find();
            while (cursor.hasNext()) {
                //记录数据类型
                DBObject dbo = cursor.next();
                String name = dbo.get("_id").toString();
                String type = dbo.get("type").toString();
                String addr = dbo.get("addr").toString();
                Double max = (Double) dbo.get("max");
                Double min = (Double) dbo.get("min");
                if (type.equals("AI")) {
                    map00.put(name, addr);
                    m_ai_max.put(name, max);
                    m_ai_min.put(name, min);
                }
                if (type.equals("DI")) {
                    map01.put(name, addr);
                }
                if (type.equals("AO")) {
                    map02.put(name, addr);
                }
                if (type.equals("DO")) {
                    map03.put(name, addr);
                }
            }
            map1.put("AI", map00);
            map1.put("DI", map01);

            map2.put("AO", map02);
            map2.put("DO", map03);


            SerialParameters params = new SerialParameters();
            params.setCommPortId(paramNo);
            params.setBaudRate(paramBote);
            params.setDataBits(paramLength);
            params.setParity(parmParity);
            params.setStopBits(parmStopBit);
            ModbusFactory modbusFactory = new ModbusFactory();
            slave = modbusFactory.createRtuSlave(params);

            slave.addProcessImage(getModscanProcessImage(1, map00, coll));
            slave.addProcessImage(getModscanProcessImage(2, map01, coll));
            slave.addProcessImage(getModscanProcessImage(3, map02, collout));
            slave.addProcessImage(getModscanProcessImage(4, map03, collout));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        slave.start();
                    } catch (ModbusInitException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            ScheduledExecutorService timerRead = Executors.newScheduledThreadPool(1);
            timerRead.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            new TaskRead(map1, m_ai_max, m_ai_min, coll, slave);
                        }
                    },
                    500,
                    500,
                    TimeUnit.MILLISECONDS);
            ScheduledExecutorService timerWrite = Executors.newScheduledThreadPool(2);
            timerWrite.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            new TaskWrite(map2, m_ao_max, m_ao_min, collout, slave);
                        }
                    },
                    500,
                    500,
                    TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
        }
    }

    private class BasicProcessImageListener implements ProcessImageListener {
        public void coilWrite(int offset, boolean oldValue, boolean newValue) {
            System.out.println("Coil at " + offset + " was set from " + oldValue + " to " + newValue);
        }

        public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
            System.out.println("HR at " + offset + " was set from " + oldValue + " to " + newValue);
        }

    }

    private BasicProcessImage getModscanProcessImage(int slaveId, Map map, DBCollection coll) {
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        processImage.setAllowInvalidAddress(true);
        processImage.setInvalidAddressValue(Short.valueOf("0"));
        // Add an image listener.
        processImage.addListener(new BasicProcessImageListener());
        return processImage;
    }
    ///数据读取
    public class TaskRead implements Runnable {
        public TaskRead(Map map1, Map m_max, Map m_min, DBCollection coll, ModbusSlaveSet slave) {
            try {
                synchronized (slave) {
                    Map map = new HashMap();
                    Set set1 = map1.entrySet();
                    Iterator it1 = set1.iterator();
                    while (it1.hasNext()) {
                        Map.Entry<String, Map<String, String>> entry = (Map.Entry<String, Map<String, String>>) it1.next();
                        Map<String, String> map2 = entry.getValue();
                        for (Iterator it2 = map2.entrySet().iterator(); it2.hasNext(); ) {
                            Map.Entry<String, String> entry2 = (Map.Entry<String, String>) it2.next();
                            String name = entry2.getKey().toString();
                            String paramAddr = entry2.getValue().toString();
                            int fun = (int) (Double.parseDouble(paramAddr));
                            if (paramAddr.substring(0, 1).equals("4")) {
                                Short d4 = slave.getProcessImage(1).getInputRegister(fun % 10000);
                                double dmax = (Double) m_max.get(name);
                                double dmin = (Double) m_min.get(name);
                                double dValue = 0;
                                if (d4 >= 0) {
                                    dValue = (dmax - dmin) * d4 / 32000 + dmin;
                                } else {
                                    dValue = (dmax - dmin) * d4 / (-32000) + dmin;
                                }
                                map.put(name, dValue);
                            }
                            if (paramAddr.substring(0, 1).equals("3")) {
                                Short d3 = slave.getProcessImage(1).getHoldingRegister(fun % 10000);
                                double dmax = (Double) m_max.get(name);
                                double dmin = (Double) m_min.get(name);
                                double dValue = 0;

                                if (d3 >= 0) {
                                    dValue = dmax * d3 / 32000;
                                } else {
                                    dValue =  dmin * d3 / (-32000);
                                }
                                map.put(name, dValue);
                            }
                            if (paramAddr.substring(0, 1).equals("2")) {
                                map.put(name, slave.getProcessImage(2).getInput(fun % 10000));
                            }
                            if (paramAddr.substring(0, 1).equals("1")) {
                                Boolean a = slave.getProcessImage(2).getCoil(fun % 10000 - 1);
                                map.put(name, a);
                            }
                        }
                    }

                    Calendar calendar = Calendar.getInstance();
                    Date dd = calendar.getTime();

                    BasicDBObject doc = new BasicDBObject();
                    doc.put("_id", dd);

                    Set set = map.entrySet();
                    Iterator it = set.iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> entry1 = (Map.Entry<String, String>) it.next();
                        doc.put(entry1.getKey(), entry1.getValue());
                    }
                    coll.insert(doc);
                }
            } catch (Exception ex) {
            }
        }

        public void run() {
        }
    }
    ///数据回写
    public class TaskWrite implements Runnable {

        public TaskWrite(Map map1, Map m_max, Map m_min, DBCollection coll, ModbusSlaveSet slave) {

            try {
                synchronized (slave) {

                    Calendar calener = Calendar.getInstance();
                    Date d1 = calener.getTime();
                    Date d2 = new Date(calener.getTime().getTime() - 5000);
                    BasicDBObject b2 = new BasicDBObject();
                    b2.put("$gte", d2);
                    b2.put("$lte", d1);
                    DBCursor cursor = coll.find(new BasicDBObject("_id", b2)).sort(new BasicDBObject("_id", -1)).limit(1);
                    while (cursor.hasNext()) {
                        DBObject dbo = cursor.next();
                        Set set1 = map1.entrySet();
                        Iterator it1 = set1.iterator();
                        while (it1.hasNext()) {
                            Map.Entry<String, Map<String, String>> entry = (Map.Entry<String, Map<String, String>>) it1.next();
                            Map<String, String> map2 = entry.getValue();
                            for (Iterator it2 = map2.entrySet().iterator(); it2.hasNext(); ) {
                                Map.Entry<String, String> entry2 = (Map.Entry<String, String>) it2.next();
                                String name = entry2.getKey().toString();
                                String paramAddr = entry2.getValue().toString();
                                int fun = (int) (Double.parseDouble(paramAddr));
                                if (paramAddr.substring(0, 1).equals("4")) {
                                    double value = Double.parseDouble(dbo.get(name).toString());
                                    double dmax = (Double) m_max.get(name);
                                    double dmin = (Double) m_min.get(name);
                                    double dValue=0;
                                    if(value>dmax||value<dmin){
                                    if(value>=0){
                                       dValue = 32000 * (int) (value / dmax);
                                    }
                                    if(value<0) {
                                        dValue = -32000 * (int) (value / dmin);
                                    }
                                    //slave.getProcessImage(3).setInputRegister(fun % 10000, (short) dValue);
                                    }
                                    else {   ///参数超限报警
                                        JOptionPane.showMessageDialog(null,"参数超限");
                                        slave.stop();
                                    }
                                }
                                if (paramAddr.substring(0, 1).equals("3")) {
                                    double value = Double.parseDouble(dbo.get(name).toString());
                                    double dmax = (Double) m_max.get(name);
                                    double dmin = (Double) m_min.get(name);
                                    double dValue=0;
                                    if(value>dmax||value<dmin){
                                        if(value>=0){
                                            dValue = 32000 * (int) (value / dmax);
                                        }
                                        if(value<0) {
                                            dValue = -32000 * (int) (value / dmin);
                                        }
                                        //slave.getProcessImage(3).setHoldingRegister(fun % 10000, (short) dValue);
                                    }
                                    else {   ///参数超限报警
                                        JOptionPane.showMessageDialog(null,"参数超限");
                                        slave.stop();
                                    };
                                }
                                if (paramAddr.substring(0, 1).equals("2")) {
                                    String value = dbo.get(name).toString();
                                    ///slave.getProcessImage(4).setInput(fun % 10000, Boolean.valueOf(value));
                                }
                                if (paramAddr.substring(0, 1).equals("1")) {
                                    String value = dbo.get(name).toString();
                                    //slave.getProcessImage(4).setCoil(fun % 10000, Boolean.valueOf(value));
                                }
                            }

                        }
                    }
                }
            } catch (Exception ex) {
            }
        }

        public void run() {

        }
    }

}
