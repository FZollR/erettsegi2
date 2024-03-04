import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class Main{


    public static void main(String[] args) {
        List<String> weatherData = readWeatherData("tavirathu13.txt");

        Map<String, List<String>> cityWindData = extractCityWindData(weatherData);
        saveCityWindFiles(cityWindData);
        System.out.println("Files created successfully.");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter city code:");
        String cityCode = scanner.nextLine();

        String lastMeasurementTime = getLastMeasurementTime(cityCode, weatherData);
        if (lastMeasurementTime != null) {
            System.out.println("Last measurement time for city " + cityCode + ": " + lastMeasurementTime);
        } else {
            System.out.println("No measurement data found for city " + cityCode);
        }

        String[] minTempData = getMinTemperatureMeasurement(weatherData);
        String[] maxTempData = getMaxTemperatureMeasurement(weatherData);

        System.out.println("Lowest temperature:");
        System.out.println("City: " + minTempData[0]);
        System.out.println("Time: " + minTempData[1]);
        System.out.println("Temperature: " + minTempData[2] + " °C");

        System.out.println("Highest temperature:");
        System.out.println("City: " + maxTempData[0]);
        System.out.println("Time: " + maxTempData[1]);
        System.out.println("Temperature: " + maxTempData[2] + " °C");

        List<String> calmWeatherMeasurements = findCalmWeatherMeasurements(weatherData);

        if (!calmWeatherMeasurements.isEmpty()) {
            System.out.println("Szélcsend volt a következő időpontokban:");
            for (String measurement : calmWeatherMeasurements) {
                System.out.println(measurement);
            }
        } else {
            System.out.println("Nem volt szélcsend a mérések idején.");
        }

        Map<String, Integer> avgTemperature = calculateAverageTemperature("tavirathu13.txt");
        Map<String, Integer> temperatureFluctuation = calculateTemperatureFluctuation("tavirathu13.txt");

        for (String city : avgTemperature.keySet()) {
            int avgTemp = avgTemperature.get(city);
            String avgTempString = avgTemp == -1 ? "NA" : String.valueOf(avgTemp);
            int fluctuation = temperatureFluctuation.get(city);
            System.out.println(city + " Középhőmérséklet: " + avgTempString + "; Hőmérséklet-ingadozás: " + fluctuation);
        }
    }

    private static List<String> readWeatherData(String filename) {
        List<String> weatherData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                weatherData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherData;
    }

    private static String getLastMeasurementTime(String cityCode, List<String> weatherData) {
        String lastMeasurementTime = null;

        for (String line : weatherData) {
            String[] data = line.split(" ");
            if (data.length >= 4 && data[0].equals(cityCode)) {
                String measurementTime = data[1];
                if (lastMeasurementTime == null || measurementTime.compareTo(lastMeasurementTime) > 0) {
                    lastMeasurementTime = measurementTime;
                }
            }
        }

        return lastMeasurementTime != null ? lastMeasurementTime.substring(0, 2) + ":" + lastMeasurementTime.substring(2) : null;
    }

    private static String[] getMinTemperatureMeasurement(List<String> weatherData) {
        String[] minTempData = null;
        int minTemp = Integer.MAX_VALUE;

        for (String line : weatherData) {
            String[] data = line.split(" ");
            int temperature = Integer.parseInt(data[3]);
            if (temperature < minTemp) {
                minTemp = temperature;
                minTempData = new String[]{data[0], data[1], data[3]};
            }
        }

        return minTempData;
    }

    private static String[] getMaxTemperatureMeasurement(List<String> weatherData) {
        String[] maxTempData = null;
        int maxTemp = Integer.MIN_VALUE;

        for (String line : weatherData) {
            String[] data = line.split(" ");
            int temperature = Integer.parseInt(data[3]);
            if (temperature > maxTemp) {
                maxTemp = temperature;
                maxTempData = new String[]{data[0], data[1], data[3]};
            }
        }

        return maxTempData;
    }

    private static List<String> findCalmWeatherMeasurements(List<String> weatherData) {
        List<String> calmMeasurements = new ArrayList<>();

        for (String line : weatherData) {
            String[] data = line.split(" ");
            if (data.length >= 4 && data[2].equals("00000")) {
                calmMeasurements.add(data[0] + " " + data[1]);
            }
        }

        return calmMeasurements;
    }

    private static Map<String, Integer> calculateAverageTemperature(String filename) {
        Map<String, Integer> avgTemperature = new HashMap<>();
        Map<String, Integer> hourSum = new HashMap<>();
        Map<String, Integer> hourCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                String cityCode = data[0];
                int temperature = Integer.parseInt(data[3]);
                int hour = Integer.parseInt(data[1].substring(0, 2));
                int hours =Integer.parseInt(data[1].substring(0, 1));
                int hourss =Integer.parseInt(data[1].substring(1, 2));

                if (hourss == 1 && hours ==0 || hourss == 7 && hours ==0|| hour == 13 || hour == 19) {
                    hourSum.put(cityCode, hourSum.getOrDefault(cityCode, 0) + temperature);
                    hourCount.put(cityCode, hourCount.getOrDefault(cityCode, 0) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String cityCode : hourSum.keySet()) {
            int sum = hourSum.get(cityCode);
            int count = hourCount.get(cityCode);
            if (count == 4) {
                // hibás volt eredetileg mert 0-ra nézte a feltételt fordítva ellenőrizte

                avgTemperature.put(cityCode, -1); // NA if no data available
            } else {
                avgTemperature.put(cityCode, sum / count);
            }
        }

        return avgTemperature;
    }

    private static Map<String, Integer> calculateTemperatureFluctuation(String filename) {
        Map<String, Integer> temperatureFluctuation = new HashMap<>();
        Map<String, Integer> minTemperature = new HashMap<>();
        Map<String, Integer> maxTemperature = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                String cityCode = data[0];
                int temperature = Integer.parseInt(data[3]);

                if (!minTemperature.containsKey(cityCode) || temperature < minTemperature.get(cityCode)) {
                    minTemperature.put(cityCode, temperature);
                }
                if (!maxTemperature.containsKey(cityCode) || temperature > maxTemperature.get(cityCode)) {
                    maxTemperature.put(cityCode, temperature);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String cityCode : minTemperature.keySet()) {
            int minTemp = minTemperature.get(cityCode);
            int maxTemp = maxTemperature.get(cityCode);
            temperatureFluctuation.put(cityCode, maxTemp - minTemp);
        }

        return temperatureFluctuation;
    }
   static Map<String, List<String>> extractCityWindData(List<String> weatherData) {
        Map<String, List<String>> cityWindData = new HashMap<>();

        for (String line : weatherData) {
            String[] data = line.split(" ");
            String cityCode = data[0];
            String measurementTime = data[1];
            String windSpeed = data[2];

            cityWindData.computeIfAbsent(cityCode, k -> new ArrayList<>()).add(measurementTime + " " + windSpeed);
        }

        return cityWindData;
    }

    static void saveCityWindFiles(Map<String, List<String>> cityWindData) {
        for (String cityCode : cityWindData.keySet()) {
            String filename = cityCode + ".txt";
            List<String> windData = cityWindData.get(cityCode);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(cityCode);
                writer.newLine();
                for (String data : windData) {
                    String[] parts = data.split(" ");
                    String measurementTime = parts[0];
                    String windSpeed = parts[1].substring(3,5);

                    //NAGY HIBA VOLT H NEM SUBSTRINGELTE, így kíírt több száz hashtaget!!!

                    writer.write(measurementTime + " ");
                    try {
                        int speed = Integer.parseInt(windSpeed);
                        for (int i = 0; i < speed; i++) {
                            writer.write("#");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid wind speed: " + windSpeed);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
