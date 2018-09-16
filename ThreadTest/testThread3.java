import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class testThread3 {
    static ReentrantReadWriteLock lock=new ReentrantReadWriteLock();

    static StringBuilder file = new StringBuilder();
    static long charNum = 0;
    static long lineNum = 0;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new testThread3.fileReader());
        executor.submit(new testThread3.countChar());
        executor.submit(new testThread3.countLine());

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

                lock.writeLock().lock();
                while ((in = bufferedReader.read()) != -1) {
                    file.append((char) in);
                }
                lock.writeLock().unlock();

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

            lock.readLock().lock();
            while (i < file.length()) {
                charNum++;
                i++;
            }
            lock.readLock().unlock();
        }
    }

    static class countLine implements Runnable {
        public void run() {
            int i = 0;

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
        }
    }
}
