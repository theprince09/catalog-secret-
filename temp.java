import java.io.*;
import java.util.*;

public class temp {
    static class Point {
        int x;
        long y;

        Point(int x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line.trim());
        }
        br.close();
        String json = sb.toString().replaceAll("\\s+", "");

        List<List<Point>> allTestCases = new ArrayList<>();
        List<Integer> kList = new ArrayList<>();

        int tIndex = json.indexOf("\"testcases\"");
        int bracketStart = json.indexOf("[", tIndex);
        int bracketEnd = json.lastIndexOf("]");

        String all = json.substring(bracketStart + 1, bracketEnd);

        // Split individual test cases (naive approach)
        String[] cases = all.split("\\},\\{");
        for (int i = 0; i < cases.length; i++) {
            String test = (i == 0 ? "" : "{") + cases[i] + (i == cases.length - 1 ? "" : "}");

            int k = Integer.parseInt(getBetween(test, "\"k\":", ","));
            kList.add(k);

            String roots = test.substring(test.indexOf("\"roots\":{") + 8);
            roots = roots.substring(roots.indexOf("{") + 1, roots.lastIndexOf("}"));

            String[] rootPairs = roots.split("(?<=\\}),");
            List<Point> points = new ArrayList<>();
            for (String pair : rootPairs) {
                String[] parts = pair.split(":\\{");
                int x = Integer.parseInt(parts[0].replaceAll("\"", ""));

                String val = getBetween(parts[1], "\"value\":\"", "\"");
                int base = Integer.parseInt(getBetween(parts[1], "\"base\":", "}"));
                long y = Long.parseLong(val, base);

                points.add(new Point(x, y));
                if (points.size() == k) break;
            }
            allTestCases.add(points);
        }

        for (int i = 0; i < allTestCases.size(); i++) {
            double secret = lagrangeInterpolation(allTestCases.get(i), 0);
            System.out.println("Secret for testcase " + (i + 1) + ": " + Math.round(secret));
        }
    }

    static String getBetween(String str, String start, String end) {
        int s = str.indexOf(start) + start.length();
        int e = str.indexOf(end, s);
        return str.substring(s, e);
    }

    static double lagrangeInterpolation(List<Point> points, int x) {
        double result = 0;
        for (int i = 0; i < points.size(); i++) {
            double term = points.get(i).y;
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    term *= (double)(x - points.get(j).x) / (points.get(i).x - points.get(j).x);
                }
            }
            result += term;
        }
        return result;
    }
}
