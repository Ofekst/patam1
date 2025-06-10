package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class GenericConfig implements Config {
    private String configPath;
    private final ArrayList<ParallelAgent> agents = new ArrayList<>();

    public void setConfFile(String configPath){
        this.configPath = configPath;
    }

    private List<String> readFile(String configPath) throws IOException{
        List <String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(configPath))){
                String line;
                while ((line = reader.readLine())!=null){
                    lines.add(line);
                }
            }
        return lines;
    }

    @Override
    public void create() {
        if (this.configPath.isEmpty()){
            throw new IllegalStateException ("The confing path does not exists");
        }
        List<String> lines;

        try {
            lines = readFile(this.configPath);
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read config file with error: " + e);
        }
        if (lines.size() % 3 != 0){
            throw new IllegalArgumentException("Config file format is invalid. " +
                    "Each agent requires 3 lines.");
        }

        for (int i =0; i < lines.size();i+=3){
            String agentType = lines.get(i).trim();
            String [] subs = lines.get(i+1).split(",");
            String [] pubs = lines.get(i+2).split(",");
            try {
                Class<?> agentClass = Class.forName(agentType);
                Constructor<?> constructor = agentClass.getConstructor(String[].class,
                        String[].class);
                Object agent = constructor.newInstance(subs,pubs);
                this.agents.add(new ParallelAgent((Agent) agent));

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("No such agent type " + e);
            } catch (InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException  e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void close() {
        for (ParallelAgent agent : this.agents) {
            agent.close();
        }
        agents.clear();
    }
}
