package Components;

public class roomData {

    public String Client;
    public String Site;
    public String Room;
    public String Date;

    public Preset[] Presets;

    public int[][] RoomData;

    public roomData(String[] strData, Preset[] pres, int[][] rmd)
    {
        if(strData.length == 4)
        {
            Client = strData[0];
            Site = strData[1];
            Room = strData[2];
            Date = strData[3];

            Presets = pres;

            RoomData = rmd;
        }
    }
}
