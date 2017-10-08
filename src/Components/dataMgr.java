package Components;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;

public class dataMgr {

    public static roomData readData(String dir) {
        String[] output;

        try {
            Reader reader = new BufferedReader(new FileReader(dir));

            String str = "";

            int rdd = reader.read();
            while (rdd != -1) {
               str = str.concat(String.valueOf((char)rdd));
                rdd = reader.read();
            }

            reader.close();

            output = str.split(",");

        String[] titleData = new String[]{output[0],
                                          output[1],
                                          output[2],
                                          output[3]};

        String[] dataArr = new String[output.length-4];

        for(int i = 0; i < dataArr.length; i++)
            dataArr[i] = Normalizer.normalize(output[i+4], Normalizer.Form.NFC);

        //test if colour data is text or rgb
        ArrayList<Preset> presetArrayList = new ArrayList<>();
        if(dataArr[3].split("-").length == 3) { //rgb colour val

            //get presets
            for (int i = 0; i < dataArr.length; i += 5) {

                //check name if exists
                boolean isUnique = true;
                for (Preset pre: presetArrayList) {
                    if(dataArr[i+2].equals(pre.text)) {
                        isUnique = false;
                        break;
                    }
                }

                if(isUnique) {
                    String[] clrStr = dataArr[i+3].split("-");

                    boolean isMoveable = false;
                    if(dataArr[i+4].equals("Yes")) {
                        isMoveable = true;
                    }

                    presetArrayList.add(new Preset(dataArr[i+2],
                            Integer.parseInt(clrStr[0]),
                            Integer.parseInt(clrStr[1]),
                            Integer.parseInt(clrStr[2]),
                            isMoveable));
                }

            }

        } else { //string colour

            //get presets
            for (int i = 0; i < dataArr.length; i += 5) {

                //check name if exists
                boolean isUnique = true;
                for (Preset pre: presetArrayList) {
                    if(dataArr[i+2].equals(pre.text)){
                        isUnique = false;
                        break;
                        }
                }

                if(isUnique) {
                    Color clr = Color.getColor(dataArr[i+3]);

                    boolean isMoveable = true;
                    if(dataArr[i+4].equals("No")) {
                        isMoveable = false;
                    }

                    presetArrayList.add(new Preset(dataArr[i+2],
                            clr.getRed(),
                            clr.getBlue(),
                            clr.getGreen(),
                            isMoveable));
                }

            }
        }
        ///get x y data
        Point large = getLargest(dataArr);
        int[][] data = new int[large.x][];
        for (int i = 0; i <data.length; i++) {
            data[i] = new int[large.y];
        }

        for (int i = 0; i < dataArr.length; i += 5) {
            int ind = 0;
            for (int j = 0; j < presetArrayList.size(); j++) {
                if(dataArr[i+2].equals(presetArrayList.get(j).text)) {
                    ind = j;
                    break;
                }
            }

            data[Integer.parseInt(dataArr[i])][Integer.parseInt(dataArr[i+1])] = ind;
        }
        Preset[] retPre = new Preset[0];
        retPre = presetArrayList.toArray(retPre);
        return new roomData(titleData, retPre, data);
        } catch (Throwable T) {
            JOptionPane.showMessageDialog(null, T.getMessage(),"Load File",  JOptionPane.WARNING_MESSAGE);

            return new roomData(new String[]{"NO FILE", "NO FILE", "NO FILE", "NO FILE"},
                    new Preset[]{new Preset("Empty", 255,255,255, false)},
                    new int[][]{new int[]{0}});
        }
    }

    public static void writeData(roomData rd, String dir) {
        String saveStr = "";

        String[] iniTitle = new String[]{rd.Client, rd.Site, rd.Room, rd.Date};

        for (int i = 0; i < iniTitle.length; i++)
            saveStr += iniTitle[i] + ",";

        for (int i = 0; i < rd.RoomData.length; i++) {
            for (int j = 0; j < rd.RoomData[i].length; j++) {
                String moveableStr = "No";
                if(rd.Presets[rd.RoomData[i][j]].isMoveable)
                    moveableStr = "Yes";

                String clrStr = String.format("%d-%d-%d",   rd.Presets[rd.RoomData[i][j]].rgb[0],
                                                            rd.Presets[rd.RoomData[i][j]].rgb[1],
                                                            rd.Presets[rd.RoomData[i][j]].rgb[2]);

                saveStr += String.format("%d,%d,%s,%s,%s,", i, j,
                                        rd.Presets[rd.RoomData[i][j]].text,
                                        clrStr,
                                        moveableStr);
            }
        }

        try {
            File fl = new File(dir);
            fl.createNewFile();
            FileWriter fw = new FileWriter(fl);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(saveStr);
            bw.close();
        } catch (IOException T) {
            JOptionPane.showMessageDialog(null, T.getMessage(),"Save File",  JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void writeRobo(roomData rd, String dir) {
        String saveStr = "";
        for (int i = 0; i < rd.RoomData.length; i++)
            for (int j = 0; j < rd.RoomData[i].length; j++) {
                if(rd.RoomData[i][j] != 0)
                {
                    String yesNo = "No";
                    if(rd.Presets[rd.RoomData[i][j]].isMoveable)
                        yesNo = "Yes";
                    saveStr = saveStr.concat(String.format("%d,%d,%s", i,j, yesNo));
                }
            }

        try {
            File fl = new File(dir);
            fl.createNewFile();
            FileWriter fw = new FileWriter(fl);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(saveStr);
            bw.close();
        } catch (IOException T) {
            JOptionPane.showMessageDialog(null, T.getMessage(),"Robo Code",  JOptionPane.WARNING_MESSAGE);
        }
    }

    public static Point getLargest(String[] str) {

        Point largest = new Point();

        for (int i = 0; i < str.length; i += 5) {

            Point thisPoint = new Point(Integer.parseInt(str[i]), Integer.parseInt(str[i+1]));

            if(thisPoint.x > largest.x)
                largest.x = thisPoint.x+1;

            if(thisPoint.y > largest.y)
                largest.y = thisPoint.y+1;
        }

        return largest;
    }
}
