package chapter08;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Floppy {
    private enum MAGNETIC_HEAD {
        MAGNETIC_HEAD_0,
        MAGNETIC_HEAD_1
    }
    // 每个扇区的大小 512 字节
    private static final int SECTOR_SIZE = 512;
    // 每个柱面, 18个扇区
    private static final int SECTOR_NUM = 18;
    // 每个盘片 80 个柱面
    private static final int CYLINDER_NUM = 80;

    private MAGNETIC_HEAD curHead = MAGNETIC_HEAD.MAGNETIC_HEAD_0;
    private int curCylinder = 0, curSector = 0;

    private HashMap<Integer, List<List<byte[]>>> floppy = new HashMap<>();

    private void setCurHead(MAGNETIC_HEAD head) {
        curHead = head;
    }

    private void setCurSector(int c) {
        // 扇区 (1 - 18)
        if (c < 1) curSector = 0;
        else if (c > 18) curSector = 17;
        else curSector = c - 1;
    }
    // 柱面  从 0 开始
    private void setCurCylinder(int c) {
        if (c < 0) curCylinder = 0;
        else if (c > 79) curCylinder = 79;
        else curCylinder = c;
    }

    private List<byte[]> initCylinder() {
        List<byte[]> bytes = new ArrayList<>();
        for (int i = 0; i < SECTOR_NUM; i++) {
            bytes.add(new byte[SECTOR_SIZE]);
        }
        return bytes;
    }

    private List<List<byte[]>> initDisk() {
        List<List<byte[]>> res = new ArrayList<>();
        for (int i = 0; i < CYLINDER_NUM; i++) {
            res.add(initCylinder());
        }
        return res;
    }

    private void init() {
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_0.ordinal(), initDisk());
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(), initDisk());
    }

    public Floppy() {
        init();
    }

    private byte[] readFloppy(MAGNETIC_HEAD head, int c, int s) {
        setCurHead(head);
        setCurCylinder(c);
        setCurSector(s);
        return floppy.get(curHead.ordinal()).get(curCylinder).get(curSector);
    }

    private void writeToFloppy(MAGNETIC_HEAD head, int c, int s, byte[] buffer) {
        byte[] bytes = readFloppy(head, c, s);
        System.arraycopy(buffer, 0, bytes, 0, Math.min(buffer.length, bytes.length));
    }

    private void writeFromFile(String fileName, MAGNETIC_HEAD head, int c, int s) {
        byte[] bytes = readFloppy(head, c, s);
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            int k, cnt = 0;
            while ((k = inputStream.read()) != -1) bytes[cnt++] = (byte) k;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeFloppy(String name) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(name))) {
            for (int i = 0; i < CYLINDER_NUM; i++) {
                for (int j = 0; j <= MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(); j++) {
                    for (int k = 1; k < SECTOR_NUM + 1; k++) {
                        byte[] buf = readFloppy(MAGNETIC_HEAD.values()[j], i, k);
                        out.write(buf);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Floppy floppy = new Floppy();
        // 写入引导扇区
        floppy.writeFromFile("boot8.bat", MAGNETIC_HEAD.MAGNETIC_HEAD_0, 0, 1);
        // 写入 1 柱面, 2 扇区
        floppy.writeFromFile("kernel8.bat", MAGNETIC_HEAD.MAGNETIC_HEAD_0, 1, 2);
        floppy.makeFloppy("system81.img");
    }
}
