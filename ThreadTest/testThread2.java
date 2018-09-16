import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class testThread2 {
    static Semaphore rmutex=new Semaphore(1);
    static Semaphore cmutex=new Semaphore(2);
    static int count=0;

    static StringBuilder file = new StringBuilder();
    static long charNum = 0;
    static long lineNum = 0;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new testThread2.fileReader());
        executor.submit(new testThread2.countChar());
        executor.submit(new testThread2.countLine());

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

                rmutex.acquire();
                while ((in = bufferedReader.read()) != -1) {
                    file.append((char) in);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                rmutex.release();
            }
        }
    }

    static class countChar implements Runnable {
        public void run() {
            int i = 0;
            while (1 > 0) {
                try {
                    cmutex.acquire();
                    if (count==0) {
                        rmutex.acquire();
                    }
                    count++;
                    cmutex.release();

                    while (i < file.length()) {
                        charNum++;
                        i++;
                    }

                    cmutex.acquire();
                    count--;
                    if (count==0) {
                        rmutex.release();
                    }
                    cmutex.release();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class countLine implements Runnable {
        public void run() {
            int i = 0;
            while (1 > 0) {
                try {
                    cmutex.acquire();
                    if (count==0) {
                        rmutex.acquire();
                    }
                    count++;
                    cmutex.release();

                    while (i < file.length()) {
                        if (file.charAt(i) == '\n') {
                            lineNum++;
                        }
                        i++;
                    }
                    if (file.charAt(--i) != '\n') {
                        lineNum++;
                    }

                    cmutex.acquire();
                    count--;
                    if (count==0) {
                        rmutex.release();
                    }
                    cmutex.release();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
