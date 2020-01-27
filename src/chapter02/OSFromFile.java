package chapter02;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OSFromFile {
    private List<Integer> bytes = new ArrayList<>();

    private void readFromFile(String s) throws IOException {
        File file = new File(s);
        FileInputStream in = new FileInputStream(file);
        int k;
        while ((k = in.read()) != -1) {
            bytes.add(k);
        }
        int len = 0x1fe;
        int curSize = bytes.size();
        // 头 512 字节的最后 的两个字节必须是 55aa
        for (int i = 0; i < len - curSize; i++) {
            bytes.add(0);
        }
        bytes.add(0x55);
        bytes.add(0xaa);
        bytes.add(0xf0);
        bytes.add(0xff);
        bytes.add(0xff);

        len = 0x168000;
        curSize = bytes.size();
        for (int i = 0; i < len - curSize; i++) {
            bytes.add(0);
        }
    }
    private void make() throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream("system2.img"));) {
            for (Integer c : bytes) out.writeByte(c.byteValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        OSFromFile fromFile = new OSFromFile();
        fromFile.readFromFile("boot.bat");
        fromFile.make();
    }
}
