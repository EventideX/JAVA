import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class testThread2 {
    private static Semaphore rmutex=new Semaphore(1);
    private static Semaphore cmutex=new Semaphore(2);
    private static Semaphore vmutex=new Semaphore(0);
    private static int count=0;

    private static StringBuilder file = new StringBuilder();
    private static long charNum = 0;
    private static long lineNum = 0;

    public static void main(String[] args) throws Exception{
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<Long> futureChar = executor.submit(new testThread2.countChar());
        Future<Long> futureLine = executor.submit(new testThread2.countLine());

        executor.submit(new testThread2.fileReader());

        System.out.println(futureChar.get());
        System.out.println(futureLine.get());

        executor.shutdown();
    }

    static class fileReader implements Runnable {
        public void run() {
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            int in = 0;

            try {
                rmutex.acquire();
                inputStreamReader = new InputStreamReader
                        (new FileInputStream("input.txt"));

                if (inputStreamReader != null) {
                    bufferedReader = new BufferedReader(inputStreamReader);
                }

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
                vmutex.release(2);
            }
        }
    }

    static class countChar implements Callable<Long> {
        public Long call() {
            int i = 0;
            while (1 > 0) {
                try {
                    vmutex.acquire();
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
            return charNum;
        }
    }

    static class countLine implements Callable<Long> {
        public Long call() {
            int i = 0;
            while (1 > 0) {
                try {
                    vmutex.acquire();
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
            return lineNum;
        }
    }
}
