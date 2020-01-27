package chapter03;

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

    public static final int SECTOR_SIZE = 512;
    public static final int CYLINDER_COUNT = 80;
    public static final int SECTOR_COUNT = 18;

    private MAGNETIC_HEAD magneticHead = MAGNETIC_HEAD.MAGNETIC_HEAD_0;
    private int curCylinder = 0;
    private int curSector = 0;

    private HashMap<Integer, List<List<byte[]>>> floppy = new HashMap<>();

    public Floppy() {
        init();
    }

    private void init() {
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_0.ordinal(), initDisk());
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(), initDisk());
    }

    private List<List<byte[]>> initDisk() {
        List<List<byte[]>> res = new ArrayList<>();
        for (int i = 0; i < CYLINDER_COUNT; i++) {
            res.add(initCylinder());
        }
        return res;
    }

    private List<byte[]> initCylinder() {
        List<byte[]> res = new ArrayList<>();
        for (int i = 0; i < SECTOR_COUNT; i++) {
            byte[] sector = new byte[SECTOR_SIZE];
            res.add(sector);
        }
        return res;
    }

    public void setMagneticHead(MAGNETIC_HEAD head) {
        magneticHead = head;
    }

    public void setCylinder(int c) {
        if (c < 0) curCylinder = 0;
        else if (c >= 80) curCylinder = 79;
        else curCylinder = c;
    }

    public void setSector(int s) {
        if (s < 0) curSector = 0;
        else if (s > 18) curSector = 18 - 1;
        else curSector = s - 1;
    }

    public byte[] readFloppy(MAGNETIC_HEAD head, int c, int s) {
        setMagneticHead(head);
        setCylinder(c);
        setSector(s);
        List<List<byte[]>> dis = floppy.get(magneticHead.ordinal());
        List<byte[]> cylinder = dis.get(curCylinder);

        byte[] sector = cylinder.get(curSector);
        return sector;
    }

    public void writeFloppy(MAGNETIC_HEAD head, int c, int s, byte[] bytes) {
        setMagneticHead(head);
        setCylinder(c);
        setSector(s);

        List<List<byte[]>> dis = floppy.get(magneticHead.ordinal());
        List<byte[]> cylinder = dis.get(curCylinder);

        byte[] buffer = cylinder.get(curSector);
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);

    }

    public void makeFloppy(String name) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(name))) {
            for (int i = 0; i < CYLINDER_COUNT; i++) {
                for (int j = 0; j <= MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(); j++) {
                    for (int k = 1; k < SECTOR_COUNT + 1; k++) {
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
        try (FileInputStream inputStream = new FileInputStream("boot3.bat")) {
            byte[] from = new byte[SECTOR_SIZE];
            int cnt = 0, k;
            while ((k = inputStream.read()) != -1) {
                from[cnt++] = (byte) k;
            }
            from[510] = 0x55;
            from[511] = (byte) 0xaa;
            floppy.writeFloppy(MAGNETIC_HEAD.MAGNETIC_HEAD_0, 0, 1, from);
            byte[] bytes = new byte[SECTOR_SIZE];
            String s = "NIMSILEFROMTHIS";
            cnt = 0;
            for (byte c : s.getBytes()) bytes[cnt++] = c;
            floppy.writeFloppy(MAGNETIC_HEAD.MAGNETIC_HEAD_0, 1, 2, bytes);
            floppy.makeFloppy("system3.img");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
