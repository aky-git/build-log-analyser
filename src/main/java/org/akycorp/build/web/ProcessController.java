package org.akycorp.build.web;

import com.google.gson.Gson;
import org.akycorp.build.analyser.BuildLogAnalyser;
import org.akycorp.build.db.DBManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * @author  : ajay_kumar_yadav
 * @created : 06/05/21
 */
@RestController
public class ProcessController {
    private Gson gson = new Gson();
    @PostMapping("/process")
    public String process(@RequestBody String request) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, String> resMap = gson.fromJson(request, HashMap.class);
        BuildLogAnalyser buildLogAnalyser = new BuildLogAnalyser(resMap.get("filePath"));
        buildLogAnalyser.process();
        long millis = (System.currentTimeMillis() - start)/1000;
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("msg", "time taken to process file - " + millis + " ms");
        return gson.toJson(responseMap);
    }

    @GetMapping("/fetch/{limit}")
    public String fetch(@PathVariable int limit) throws Exception {
        long start = System.currentTimeMillis();
        ArrayList<Map<String, String>> resposeList = DBManager.fetchEvents(limit);
        long millis = (System.currentTimeMillis() - start)/1000;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("msg", "data fetched in " + millis + " ms");
        responseMap.put("data", resposeList);
        return gson.toJson(responseMap);
    }

    @GetMapping("/fetchAll")
    public String fetchAll() throws Exception {
        long start = System.currentTimeMillis();
        ArrayList<Map<String, String>> resposeList = DBManager.fetchEvents(-1);
        long millis = (System.currentTimeMillis() - start)/1000;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("msg", "data fetched in " + millis + " ms");
        responseMap.put("data", resposeList);
        return gson.toJson(responseMap);
    }
}
