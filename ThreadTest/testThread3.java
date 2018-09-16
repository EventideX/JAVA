import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class testThread3 {

    private static ReentrantReadWriteLock lock=new ReentrantReadWriteLock();
    private static Semaphore vmutex=new Semaphore(0);

    private static StringBuilder file = new StringBuilder();
    private static long charNum = 0;
    private static long lineNum = 0;

    public static void main(String[] args) throws Exception{
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<Long> futureChar = executor.submit(new testThread3.countChar());
        Future<Long> futureLine = executor.submit(new testThread3.countLine());

        executor.submit(new testThread3.fileReader());

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
                inputStreamReader = new InputStreamReader
                        (new FileInputStream("input.txt"));

                if (inputStreamReader != null) {
                    bufferedReader = new BufferedReader(inputStreamReader);
                }

                lock.writeLock().lock();
                while ((in = bufferedReader.read()) != -1) {
                    file.append((char) in);
                }
                lock.writeLock().unlock();
                vmutex.release(2);

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

    static class countChar implements Callable<Long> {
        public Long call() {
            int i = 0;

            while (1>0) {
                try {
                    vmutex.acquire();
                    lock.readLock().lock();
                    while (i < file.length()) {
                        charNum++;
                        i++;
                    }
                    lock.readLock().unlock();
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

            while (1>0) {
                try {
                    vmutex.acquire();
                    lock.readLock().lock();
                    while (i < file.length()) {
                        if (file.charAt(i) == '\n') {
                            lineNum++;
                        }
                        i++;
                    }
                    if (file.charAt(--i) != '\n') {
                        lineNum++;
                    }
                    lock.readLock().unlock();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return lineNum;
        }
    }
}
