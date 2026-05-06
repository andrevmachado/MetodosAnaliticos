import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConfigLoader {
    public long seed;
    public int randoms;
    public List<Double> rndNumbers = new ArrayList<>();
    public boolean usePredefinedRandoms = false;
    private List<Long> seeds = new ArrayList<>();
    public Map<String, Queue> queues = new HashMap<>();
    public List<InitialArrival> initialArrivals = new ArrayList<>();

    public static class InitialArrival {
        String queueName;
        double time;
    }

    private static class RouteData {
        String source;
        String target;
        double probability;
    }

    private Map<String, Map<String, String>> queueData = new HashMap<>();
    private List<RouteData> routeDataList = new ArrayList<>();

    public void load(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSection = "";
            String currentQueue = "";
            
           while ((line = br.readLine()) != null) {
                line = line.split("#")[0].trim();
                if (line.isEmpty() || line.equals("!PARAMETERS")) continue;

                if (line.startsWith("rndnumbersPerSeed:")) {
                    randoms = Integer.parseInt(line.substring(18).trim());
                } else if (line.equals("seeds:")) {
                    currentSection = "seeds";
                } else if (currentSection.equals("seeds") && line.startsWith("-")) {
                    seeds.add(Long.parseLong(line.substring(1).trim()));
                } else if (line.equals("rndnumbers:")) {
                    currentSection = "rndnumbers";
                } else if (currentSection.equals("rndnumbers") && line.startsWith("-")) {
                    rndNumbers.add(Double.parseDouble(line.substring(1).trim()));
                } else if (line.equals("queues:")) {
                    currentSection = "queues";
                } else if (line.equals("network:")) {
                    currentSection = "network";
                } else if (line.equals("arrivals:")) {
                    currentSection = "arrivals";
                } else if (currentSection.equals("queues")) {
                    if (line.endsWith(":")) {
                        currentQueue = line.substring(0, line.length() - 1).trim();
                    } else if (!currentQueue.isEmpty()) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            queueData.computeIfAbsent(currentQueue, k -> new HashMap<>()).put(parts[0].trim(), parts[1].trim());
                        }
                    }
                } else if (currentSection.equals("network")) {
                    // limpa tudo que é coisa aqui
                    String cleanLine = line.replace("\u00A0", " ").trim();

                    if (cleanLine.startsWith("-") && cleanLine.contains("source:")) {
                        RouteData rd = new RouteData();
                        rd.source = cleanLine.split("source:")[1].trim();
                        String targetLine = br.readLine().split("#")[0].replace("\u00A0", " ").trim();
                        rd.target = targetLine.split("target:")[1].trim();
                        String probLine = br.readLine().split("#")[0].replace("\u00A0", " ").trim();
                        rd.probability = Double.parseDouble(probLine.split("probability:")[1].trim());

                        routeDataList.add(rd);
                    }
                } else if (currentSection.equals("arrivals")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        InitialArrival ia = new InitialArrival();
                        ia.queueName = parts[0].trim();
                        ia.time = Double.parseDouble(parts[1].trim());
                        initialArrivals.add(ia);
                    }
                }
            }
        }

        if (!seeds.isEmpty()) {
            seed = seeds.get(0);
            usePredefinedRandoms = false;
        } else if (!rndNumbers.isEmpty()) {
            usePredefinedRandoms = true;
            randoms = rndNumbers.size();
        }

        finalizeQueues();
    }

    private void finalizeQueues() {
        for (String name : queueData.keySet()) {
            Map<String, String> data = queueData.get(name);
            int cap = Integer.parseInt(data.getOrDefault("capacity", "10000"));
            int ser = Integer.parseInt(data.getOrDefault("servers", "1"));
            double aMin = Double.parseDouble(data.getOrDefault("minArrival", "0"));
            double aMax = Double.parseDouble(data.getOrDefault("maxArrival", "0"));
            double sMin = Double.parseDouble(data.getOrDefault("minService", "0"));
            double sMax = Double.parseDouble(data.getOrDefault("maxService", "0"));
            
            queues.put(name, new Queue(name, cap, ser, aMin, aMax, sMin, sMax));
        }

        for (RouteData rd : routeDataList) {
            Queue src = queues.get(rd.source);
            Queue dst = queues.get(rd.target);
            if (src != null && dst != null) {
                src.addRoute(dst, rd.probability);
            }
        }
    }
}
