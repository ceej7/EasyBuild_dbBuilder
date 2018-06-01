package units;


import org.apache.commons.lang3.StringUtils;

public class GPUUnit {
    public String Name;
    String Chip;
    String Released;
    String Bus;
    int Memory_Size;
    String Memory_Type;
    int Memory_Bus;
    int GPU_Clock;
    int M_Clock;
    int Shaders;

    int TMUs;
    int ROPs;
    int Multiplier;

    public GPUUnit(String name,String chip,String released,String bus,String memory,String gpu_clock,String m_clock,String STR)
    {
        Name=name;
        Chip=chip;
        Released=released;
        Bus=bus;
        String[] ms= StringUtils.split(memory ,",");
        String[] _m=ms[0].split("\\D+");
        if(ms[0].contains("x"))
        {
            Memory_Size=Integer.parseInt(_m[0]);
            Multiplier=Integer.parseInt(_m[1]);
        }
        else{
            Memory_Size=Integer.parseInt(_m[0]);
            Multiplier=1;
        }
        Memory_Type=ms[1];
        Memory_Bus=Integer.parseInt(StringUtils.replace(ms[2]," ","").split("\\D+")[0]);
        GPU_Clock=Integer.parseInt(gpu_clock.split("\\D+")[0]);
        M_Clock=Integer.parseInt(m_clock.split("\\D+")[0]);
        String[] str=StringUtils.split(STR,"/");
        for (int i = 0; i < str.length; i++) {
            String _str=StringUtils.replace(str[i]," ","");
            str[i]=_str;
        }
        if(str.length==4)
        {
            Shaders=Integer.parseInt(str[0].split("\\D+")[0])+Integer.parseInt(str[1].split("\\D+")[0]   );
            TMUs=Integer.parseInt(str[2].split("\\D+")[0]);
            ROPs=Integer.parseInt(str[3].split("\\D+")[0]);
        }
        else{
            Shaders=Integer.parseInt(str[0].split("\\D+")[0]);
            TMUs=Integer.parseInt(str[1].split("\\D+")[0]);
            ROPs=Integer.parseInt(str[2].split("\\D+")[0] );
        }
    }

    @Override
    public String toString() {
        return  Name+" "+
         Chip+" "+
         Released+" "+
         Bus+" "+
         Memory_Size+" "+
         Memory_Type+" "+
         Memory_Bus+" "+
         GPU_Clock+" "+
         M_Clock+" "+
         Shaders+" "+
         TMUs+" "+
         ROPs+" "+
         Multiplier;
    }
}
