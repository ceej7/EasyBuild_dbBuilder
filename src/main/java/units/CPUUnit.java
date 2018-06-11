package units;

import org.apache.commons.lang3.StringUtils;

public class CPUUnit {
    String Name;
    String Codename;
    int Cores;
    int Threads;
    String Socket;
    int Process;
    int Clock;
    float Multi;
    int CacheL1;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCodename() {
        return Codename;
    }

    public void setCodename(String codename) {
        Codename = codename;
    }

    public int getCores() {
        return Cores;
    }

    public void setCores(int cores) {
        Cores = cores;
    }

    public int getThreads() {
        return Threads;
    }

    public void setThreads(int threads) {
        Threads = threads;
    }

    public String getSocket() {
        return Socket;
    }

    public void setSocket(String socket) {
        Socket = socket;
    }

    public int getProcess() {
        return Process;
    }

    public void setProcess(int process) {
        Process = process;
    }

    public int getClock() {
        return Clock;
    }

    public void setClock(int clock) {
        Clock = clock;
    }

    public float getMulti() {
        return Multi;
    }

    public void setMulti(float multi) {
        Multi = multi;
    }

    public int getCacheL1() {
        return CacheL1;
    }

    public void setCacheL1(int cacheL1) {
        CacheL1 = cacheL1;
    }

    public int getCacheL2() {
        return CacheL2;
    }

    public void setCacheL2(int cacheL2) {
        CacheL2 = cacheL2;
    }

    public int getCacheL3() {
        return CacheL3;
    }

    public void setCacheL3(int cacheL3) {
        CacheL3 = cacheL3;
    }

    public int getTDP() {
        return TDP;
    }

    public void setTDP(int TDP) {
        this.TDP = TDP;
    }

    public String getReleased() {
        return Released;
    }

    public void setReleased(String released) {
        Released = released;
    }

    int CacheL2;
    int CacheL3;
    int TDP;
    String Released;

    @Override
    public String toString() {
        return Name+" "+Codename+" "+Cores+" "+Threads+" "+Socket+" "+Process+" "+Clock+" "+Multi+" "+CacheL1+" "+CacheL2+" "+CacheL3+" "+TDP+" "+Released;
    }

    /**
     * 为了JD提取数据并格式化而存在
     * @param name
     * @param codename
     * @param cores
     * @param socket
     * @param process
     * @param clock
     * @param multi
     * @param cache
     * @param tdp
     * @param released
     */
    public CPUUnit(String name,String codename,String cores,String socket,String process,String clock,String multi,String cache,String tdp, String released)
    {
        Name=name;
        Codename=codename;
        String[]  _cores=StringUtils.split(cores,'/');
        Cores=Integer.parseInt(StringUtils.replace(_cores[0]," ",""));
        if(_cores.length>1)
        {
            Threads=Integer.parseInt(StringUtils.replace(_cores[1]," ",""));
        }
        else{
            Threads=-1;
        }
        Socket=socket;
        Process=Integer.parseInt(process.split("\\D+")[0]);
        Clock=Integer.parseInt(clock.split("\\D+")[0]);
        Multi=Float.parseFloat(StringUtils.replace(multi,"x",""));
        String[]  _cache=cache.split("\\D+");
        CacheL1=Integer.parseInt(_cache[0]);
        CacheL2=Integer.parseInt(_cache[1]);
        CacheL3=Integer.parseInt(_cache[2]);
        TDP=Integer.parseInt(tdp.split("\\D+")[0]);
        Released=released;

    }
}
