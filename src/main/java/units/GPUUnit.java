package units;


import org.apache.commons.lang3.StringUtils;

public class GPUUnit {
    String Name;
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
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getChip() {
        return Chip;
    }

    public void setChip(String chip) {
        Chip = chip;
    }

    public String getReleased() {
        return Released;
    }

    public void setReleased(String released) {
        Released = released;
    }

    public String getBus() {
        return Bus;
    }

    public void setBus(String bus) {
        Bus = bus;
    }

    public int getMemory_Size() {
        return Memory_Size;
    }

    public void setMemory_Size(int memory_Size) {
        Memory_Size = memory_Size;
    }

    public String getMemory_Type() {
        return Memory_Type;
    }

    public void setMemory_Type(String memory_Type) {
        Memory_Type = memory_Type;
    }

    public int getMemory_Bus() {
        return Memory_Bus;
    }

    public void setMemory_Bus(int memory_Bus) {
        Memory_Bus = memory_Bus;
    }

    public int getGPU_Clock() {
        return GPU_Clock;
    }

    public void setGPU_Clock(int GPU_Clock) {
        this.GPU_Clock = GPU_Clock;
    }

    public int getM_Clock() {
        return M_Clock;
    }

    public void setM_Clock(int m_Clock) {
        M_Clock = m_Clock;
    }

    public int getShaders() {
        return Shaders;
    }

    public void setShaders(int shaders) {
        Shaders = shaders;
    }

    public int getTMUs() {
        return TMUs;
    }

    public void setTMUs(int TMUs) {
        this.TMUs = TMUs;
    }

    public int getROPs() {
        return ROPs;
    }

    public void setROPs(int ROPs) {
        this.ROPs = ROPs;
    }

    public int getMultiplier() {
        return Multiplier;
    }

    public void setMultiplier(int multiplier) {
        Multiplier = multiplier;
    }



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
