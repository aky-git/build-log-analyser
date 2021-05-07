package org.akycorp.build.analyser;

import com.google.gson.Gson;
import org.akycorp.build.db.DBManager;
import org.akycorp.build.model.BuildEvent;
import org.akycorp.build.model.BuildLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * @author  : ajay_kumar_yadav
 * @created : 05/05/21
 */
public class BuildLogAnalyser {
    private static final Logger LOG = LoggerFactory.getLogger(BuildLogAnalyser.class);
    private String path;
    private Gson gson = new Gson();
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ConcurrentHashMap<String, BuildEvent> map = new ConcurrentHashMap();
    ConcurrentLinkedQueue<String> taskQueue = new ConcurrentLinkedQueue<>();
    ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()*2,
            (runnable) -> new Thread(runnable,"build-analyser-thread-" + poolNumber.getAndIncrement()));

    public BuildLogAnalyser(String path){
        this.path = path;
    }

    public void process() throws Exception{
        LOG.debug("processing build log file from location {}", this.path);
        try(InputStream inputStream = Files.newInputStream(Paths.get(path));
            Scanner scanner = new Scanner(new InputStreamReader(inputStream));){
            while (scanner.hasNext()) {
                taskQueue.add(scanner.nextLine());
                CompletableFuture.supplyAsync(()->{
                    String logStr = taskQueue.poll();
                    if(logStr != null){
                        BuildLog log = gson.fromJson(logStr, BuildLog.class);
                        BuildEvent buildEvent = getOrCreate(log.getId());
                        BuildLog.populateBuildEvent(log, buildEvent);
                        if(buildEvent.isProcessed()){
                            DBManager.saveEvent(buildEvent);
                            LOG.debug("processed build log event {}", buildEvent);
                            return buildEvent;
                        }
                    }
                    return null;
                }, executorService).thenApply(buildEvent -> {
                    if(buildEvent != null){
                        map.remove(buildEvent.getEventId());
                    }
                    return null;
                });
            }
            executorService.shutdown();
            //wait for process to finish
            while (!executorService.isTerminated()){
            }
        }
    }

    private BuildEvent getOrCreate(String key){
        return map.computeIfAbsent(key, k-> new BuildEvent());
    }

}
