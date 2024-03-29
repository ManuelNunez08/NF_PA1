import java.util.List;
import java.util.Collections;
import java.util.OptionalDouble;

class ArrayStatistics {
    // Method to find the minimum value in the list
    public long findMin(List<Long> list) {
        return Collections.min(list);
    }

    // Method to find the maximum value in the list
    public long findMax(List<Long> list) {
        return Collections.max(list);
    }

    // Method to find the mean (average) of the list
    public double findMean(List<Long> list) {
        OptionalDouble average = list.stream()
                .mapToLong(Long::longValue)
                .average();
        double result = average.isPresent() ? average.getAsDouble() : 0;
        return Math.round(result * 1000) / 1000.0;
    }

    // Method to find the standard deviation of the list
    public double findStdDev(List<Long> list) {
        double mean = findMean(list);
        double sumOfSquaredDifferences = list.stream()
                .mapToDouble(num -> Math.pow(num - mean, 2))
                .sum();
        double result = Math.sqrt(sumOfSquaredDifferences / list.size());
        return Math.round(result * 1000) / 1000.0;
    }

    public void print_stats(List<Long> list) {
        double min = findMin(list) / 1000000.0;
        double max = findMax(list) / 1000000.0;
        double mean = findMean(list) / 1000000.0;
        double stddev = findStdDev(list) / 1000000;

        System.out.println();
        System.out.println("MIN: " + Math.round(min * 1000) / 1000.0);
        System.out.println("MAX: " + Math.round(max * 1000) / 1000.0);
        System.out.println("MEAN: " + Math.round(mean * 1000) / 1000.0);
        System.out.println("STD_DEV: " + Math.round(stddev * 1000) / 1000.0);
        System.out.println();
    }
}
