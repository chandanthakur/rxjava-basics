package helpers;

public class Utils {

    public static String printVerbose(String value) {
        return printVerbose(null, value);
    }

    static long startTime = System.currentTimeMillis();

    public static String printVerbose(final String tag, final String value) {
        String tagNorm = tag != null ? tag : "printVerbose";
        String tagPrefix = (System.currentTimeMillis() - startTime) + ":" + tagNorm + ":" + Thread.currentThread().getName() + ":";
        System.out.println(tagPrefix + value);
        return value;
    }

    static public void threadSleep(long timeMS) {
      threadSleep(timeMS, false);
    }

    static public void threadSleep(long timeMS, boolean isVerbose) {
        try {
            if(isVerbose) {
                System.out.println(Thread.currentThread().getName() + " sleep begin for " + timeMS + " milliseconds");
            }

            Thread.sleep(timeMS);

            if(isVerbose) {
                System.out.println(Thread.currentThread().getName() + " sleep ends");
            }
        } catch (Exception ex) {

        }
    }
}
