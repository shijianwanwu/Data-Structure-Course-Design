package com.byr.project.utils;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Compress {

    public Compress(){

    }

    //哈夫曼节点
    private static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left;
        Node right;

        public Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        public Node(char character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    /**
     * 将content压缩到folderPath对应的路径下，名为name,如果存到文件里返回文件路径，如果把编码存到字符串里就直接返回字符串
     *
     * @param content
     * @param folderPath
     * @throws IOException
     */
    public static String compress(String content, String folderPath, String name) throws IOException {
        // 构建哈夫曼树
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : content.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        //用优先队列构造
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);
        }

        Node root = queue.poll();

        // 生成哈夫曼编码表
        Map<Character, String> huffmanCodeMap = new HashMap<>();
        Compress cc=new Compress();
        cc.generateHuffmanCode(root, "", huffmanCodeMap);

        // 将content转换为二进制字符串
        StringBuilder binaryContent = new StringBuilder();
        for (char c : content.toCharArray()) {
            String huffmanCode = huffmanCodeMap.get(c); // 获取字符 c 对应的哈夫曼编码
            binaryContent.append(huffmanCode); // 将哈夫曼编码追加到 binaryContent 中
        }
        binaryContent.insert(0, "1");
        //System.out.println(binaryContent.toString());
        // 将二进制字符串转换为字节数组
        byte[] bytes = new BigInteger(binaryContent.toString(), 2).toByteArray();

        //将哈夫曼编码表中的键值对以如下形式写入字符串中：
        String ans=cc.mapToString(huffmanCodeMap);

        // 将字节数组写入到二进制文件中
        File file = new File(folderPath, name + ".bin"); // 添加文件扩展名".bin"
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ans; //返回字符串形式的哈夫曼编码
    }

    //将哈夫曼编码以写为字符串形式，格式如下
    //字符在前其对应哈夫曼编码在后，紧密相连
    //例如：a1001j111x101101 其中a的哈夫曼编码为1001、j的哈夫曼编码为111、x的哈夫曼编码为101101
    public String mapToString(Map<Character, String> huffmanCodeMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, String> entry : huffmanCodeMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    //构造哈夫曼编码
    private void generateHuffmanCode(Node node, String code, Map<Character, String> huffmanCodeMap) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            huffmanCodeMap.put(node.character, code);
        }
        generateHuffmanCode(node.left, code + "0", huffmanCodeMap);
        generateHuffmanCode(node.right, code + "1", huffmanCodeMap);
    }

    /**
     * 返回folderPath对应的路径下名为name的内容，huffman是compress传回的字符串
     * @param folderPath
     * @param name
     * @return
     */
    //解压缩
    public static String decompress(String folderPath, String name, String huffman) {
        String filePath = folderPath + "/" + name + ".bin";
        Compress cc=new Compress();
        Map<String, Character> huffmanTable = cc.buildHuffmanTable(huffman);//将字符串形式的哈夫曼编码，复原为哈夫曼编码表
        StringBuilder originalContent = new StringBuilder();//原文字符串

        try {
            // 读取二进制文件内容
            byte[] binaryData = cc.readBinaryFile(filePath);

            // 将二进制数据转换为字符串
            String binaryString = cc.bytesToBinaryString(binaryData);
            StringBuilder currentCode = new StringBuilder();

            for (char bit : binaryString.toCharArray()) {
                currentCode.append(bit);
                if (huffmanTable.containsKey(currentCode.toString())) {
                    originalContent.append(huffmanTable.get(currentCode.toString()));
                    currentCode.setLength(0); // 清空当前编码
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originalContent.toString();//返回原文字符串
    }

    //将二进制文件字节形式读入，并以字节数组的形式返回
    private  byte[] readBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] data = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }

        return data;
    }

    //将二进制文件变为字节后，安位转化成字符串形式的‘1’和‘0’
    private  String bytesToBinaryString(byte[] bytes) {
        StringBuilder binaryString = new StringBuilder();
        
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                binaryString.append((char) ('0' + bit));
            }
        }
        //return binaryString.toString().replaceAll("^0+", ""); 
        return binaryString.toString().replaceAll("^0+1", "");
    }

    //将字符串形式的huffman编码重新转化为哈夫曼编码表
    public  Map<String, Character> buildHuffmanTable(String huffman) { 
        Map<String, Character> huffmanTable = new HashMap<>();
        int i=0,j=0;
        for (i = 0; i < huffman.length(); i++) {
            char c = huffman.charAt(i);
            if (c != '1' && c != '0') {
                StringBuilder code = new StringBuilder();
                j=i+1;
                while (true) {
                    if(j<huffman.length() && (huffman.charAt(j)=='1' || huffman.charAt(j)=='0')){
                        code.append(huffman.charAt(j));
                        j++;
                    }
                    else{
                        break;
                    }
                }
                huffmanTable.put(code.toString(),c);
            }
        }
       
        return huffmanTable;
    }

    /**
     * 判断des是否在origin中出现，若出现则为true否则为false,不能用直接用find这种要用一些算法比如kmp之类的
     * @param des
     * @param origin
     * @return
     */
    public boolean search(String des, String origin) {//使用kmp算法，查找匹配字符串
        int[] next = buildNext(des);
        int i = 0, j = 0;
        while (i < origin.length() && j < des.length()) {
            if (j == -1 || origin.charAt(i) == des.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        return j == des.length();
    }

    private int[] buildNext(String pattern) {
        int[] next = new int[pattern.length()];
        next[0] = -1;
        int k = -1;
        int j = 0;
        while (j < pattern.length() - 1) {
            if (k == -1 || pattern.charAt(j) == pattern.charAt(k)) {
                ++k;
                ++j;
                next[j] = k;
            } else {
                k = next[k];
            }
        }
        return next;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String content=sc.nextLine();
        String folderpath=sc.nextLine();
        String name=sc.nextLine();
        String ans,end;
        try{
            //传入要压缩的字符串content、保存至文件夹的地址folderpath、压缩成二进制文件的文件名name
            ans=Compress.compress(content, folderpath, name);
            System.out.println("this: "+ans);

            //解压缩
            end=Compress.decompress(folderpath, name, ans);
            System.out.println(end);

            //输入要查询的字符串query与母串end(此处先用end了)
            String query=sc.nextLine();
            Compress cc=new Compress();
            if(cc.search(query, end)){
                System.out.println("yes");
            }
            else{
                System.out.println("No");
            }
        }
        catch(IOException e){

        }
        sc.close();
    }
}

//样例如下：
//testconent
//shh eufwguefuw uygdyuwgyd...qiwygduq/>
//dyqhdqijd hhhhhh.
/*gdiifh dshifijwj iajdijije.s
D:\temp
99990*/