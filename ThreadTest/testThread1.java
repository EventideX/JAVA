import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testThread1 {
    static boolean isRead = true;
    static StringBuilder file = new StringBuilder();
    static long charNum = 0;
    static long lineNum = 0;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new testThread1.fileReader());
        executor.submit(new testThread1.countChar());
        executor.submit(new testThread1.countLine());

        System.out.println(charNum);
        System.out.println(lineNum);

        executor.shutdown();
    }

    static class fileReader implements Runnable {
        public void run() {
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            int in = 0;

            try {
                inputStreamReader = new InputStreamReader
                        (new FileInputStream("input.txt"));

                if (inputStreamReader != null) {
                    bufferedReader = new BufferedReader(inputStreamReader);
                }

                while ((in = bufferedReader.read()) != -1) {
                    file.append((char) in);
                }

                isRead = false;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class countChar implements Runnable {
        public void run() {
            int i = 0;
            while (1 > 0) {
                if (!isRead) {
                    while (i < file.length()) {
                        charNum++;
                        i++;
                    }
                    break;
                }
            }
        }
    }

    static class countLine implements Runnable {
        public void run() {
            int i = 0;
            while (1 > 0) {
                if (!isRead) {
                    while (i < file.length()) {
                        if (file.charAt(i) == '\n') {
                            lineNum++;
                        }
                        i++;
                    }
                    if (file.charAt(--i) != '\n') {
                        lineNum++;
                    }
                    break;
                }
            }
        }
    }
}
