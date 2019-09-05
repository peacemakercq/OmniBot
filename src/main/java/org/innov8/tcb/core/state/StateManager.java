package org.innov8.tcb.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Manage the statesFlow
 * Created by wangqi on 2019/9/3.
 */
public class StateManager {
    private Logger logger = LogManager.getLogger();

    private volatile static StateManager instance;
    private final String DEFAULT_OPTION = "IDN";
    private StatesFlow statesFlow;

    private final String stateDefDirectory = System.getProperty("state.load.dir", "src/main/resources");

    private ConcurrentMap<String, StatesFlow> fileToFlowMap = new ConcurrentHashMap<>();

    private final Pattern pattern = Pattern.compile(" *(\\w+|\\[\\*]) *--> *(\\w+|\\[\\*]) *(:.*|)");
    private StateManager() {

        File fileDirectory = new File(stateDefDirectory);
        FileFilter filter = new FileFilter();
        String[] files = fileDirectory.list(filter);
        if (files != null && files.length > 0) {
            for (String file : files) {
                try {
                    statesFlow = new StatesFlow(file);
                    loadStatesFlow(file);
                    fileToFlowMap.put(file, statesFlow);
                } catch (Exception e) {
                    logger.error("Failed to load statesFlowOld from file: {}", file);
                }
            }
        }
        statesFlow = null;
    }

    public String getStateDefDirectory() {
        return stateDefDirectory;
    }

    public static StateManager getInstance() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }
        return instance;
    }

    public State getRootState(String filename) {
        StatesFlow statesFlow = fileToFlowMap.get(filename);
        if (statesFlow != null) {
            return statesFlow.getRootState();
        }
        logger.warn("Cannot fine flow defined for {}", filename);
        return null;
    }

    public State getNext(String filename, String currentStateId, String option) {
        StatesFlow statesFlow = fileToFlowMap.get(filename);
        State state = statesFlow.getState(currentStateId);
        if (state == null) {
            return null;
        }
        if (state.containOption(option)) {
            return state.getNext(option);
        }
        logger.debug("option \"{}\" does not exist for state {}, using default!", option, state.getId());
        return state.getNext(DEFAULT_OPTION);
    }

    /**
     * States flow will be loaded to statesFlow.
     * @param stateFile the file to be loaded. filename pattern: "//w+.puml"
     */
    private void loadStatesFlow(String stateFile) {
        logger.info("Loading file: {}", stateFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(stateDefDirectory.concat("/").concat(stateFile)))){
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug("Loading line: {}", line);
                parseLine(line);
            }
            logger.info("file loaded successfully! filename: {}, statesFlow: {}", stateFile, statesFlow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create state from line.
     * @param line read line from file
     */
    private void parseLine(String line) {
        if (!pattern.matcher(line).matches()) {
            return;
        }
        String[] split = line.split("-->");
        String id = split[0].trim();
        String nextInfo = split[1];
        int index = nextInfo.indexOf(':');


        System.out.println("line: " + line + ", id: " + id + ", nextInfo: " + nextInfo);

        String nextId = (index > 0) ? nextInfo.substring(0, index).trim() : nextInfo.trim();
        String option = DEFAULT_OPTION;
        if (index > 0 && nextInfo.length() >= index) {
            option = nextInfo.substring(index + 1).trim();
        }
        State nextState = statesFlow.nextStateFromId(nextId);
        statesFlow.addState(nextId, nextState);
        State state = statesFlow.getState(id);
        if (state == null) {
            System.out.println("Error! state is not defined yet! id={}" + id);
            return;
        }
        state.addNext(option, nextState);
    }

    private class FileFilter implements FilenameFilter {
        public boolean accept(File file, String fileName) {
            Pattern pattern = Pattern.compile("\\w+.puml");

            return pattern.matcher(fileName).matches();
        }
    }

}
