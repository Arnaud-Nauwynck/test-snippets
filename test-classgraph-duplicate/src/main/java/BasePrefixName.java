import java.util.Arrays;
import java.util.List;

public class BasePrefixName {

    public static String getBasePrefixName(List<String> strings) {
        if (strings.isEmpty()) {
            return "";
        }

        String basePrefix = strings.get(0);
        for (String string : strings) {
            if (!string.startsWith(basePrefix)) {
                basePrefix = "";
                break;
            }
        }

        return basePrefix;
    }

    public static String getBasePrefixName2(List<String> strings) {
        if (strings.isEmpty()) {
            return "";
        }

        String basePrefix = strings.get(0);
        for (String string : strings) {
            if (!string.startsWith(basePrefix)) {
                basePrefix = "";
                break;
            }
        }

        return basePrefix;
    }

    public static String findCommonPrefix(String[] strings) {
        if (strings.length == 0) {
            return "";
        }

        String prefix = strings[0];
        for (int i = 1; i < strings.length; i++) {
            prefix = findCommonPrefix(prefix, strings[i]);
            if (prefix.length() == 0) {
                return "";
            }
        }

        return prefix;
    }

    private static String findCommonPrefix(String prefix, String string) {
        int minLength = Math.min(prefix.length(), string.length());
        StringBuilder commonPrefixBuilder = new StringBuilder();
        for (int i = 0; i < minLength; i++) {
            if (prefix.charAt(i) == string.charAt(i)) {
                commonPrefixBuilder.append(prefix.charAt(i));
            } else {
                break;
            }
        }

        return commonPrefixBuilder.toString();
    }


    public static void main(String[] args) {
        List<String> strings = Arrays.asList("foo", "bar", "foobar");
        String basePrefixName = BasePrefixName.getBasePrefixName(strings);
        System.out.println(basePrefixName); // Output: foo

        String basePrefixName2 = BasePrefixName.getBasePrefixName2(strings);
        System.out.println("basePrefixName2: " + basePrefixName2); // Output: foo

        String[] strings3 = {"hello", "hell", "helo"};
        String commonPrefix = findCommonPrefix(strings3);
        System.out.println("commonPrefix: " + commonPrefix);

    }
}