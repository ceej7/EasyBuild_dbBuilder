package util;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.sql.Types;
public class Formatting {
    public static String DateFormat(String _date)
    {
        String[] str= StringUtils.split(_date," ");
        int year;
        int month;
        if(str[0].contains("Jan"))
        {
            month=1;
        }
        else if(str[0].contains("Feb"))
        {
            month=2;
        }
        else if(str[0].contains("Mar"))
        {
            month=3;
        }
        else if(str[0].contains("Apr"))
        {
            month=4;
        }
        else if(str[0].contains("May"))
        {
            month=5;
        }
        else if(str[0].contains("Jun"))
        {
            month=6;
        }
        else if(str[0].contains("Jul"))
        {
            month=7;
        }
        else if(str[0].contains("Aug"))
        {
            month=8;
        }
        else if(str[0].contains("Sep"))
        {
            month=9;
        }
        else if(str[0].contains("Oct"))
        {
            month=10;
        }
        else if(str[0].contains("Nov"))
        {
            month=11;
        }
        else if(str[0].contains("Dec"))
        {
            month=12;
        }
        else{
            return null;
        }

        return Integer.parseInt(str[1])+"-"+month+"-01";
    }
}
