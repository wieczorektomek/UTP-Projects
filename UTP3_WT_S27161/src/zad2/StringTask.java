package zad2;

public class StringTask implements Runnable {

    private final String word;
    private final int num;
    private String concatenatedWord = "";
    private volatile TaskState state;
    private Thread thread;


    public StringTask(String word, int num){
        this.word = word;
        this.num = num;
        state = TaskState.CREATED;
    }

    @Override
    public void run() {
        state = TaskState.RUNNING;
        try {
            for (int i = 0; i < num; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                concatenatedWord += word;
            }
            state = TaskState.READY;
        }catch (InterruptedException exc){
            state = TaskState.ABORTED;
        }
    }

    public String getResult(){
        return concatenatedWord;
    }

    public TaskState getState() {
        return state;
    }


    public void start(){
        thread = new Thread(this);
        thread.start();
    }


    public void abort() {
        if (thread != null) thread.interrupt();
    }


    public boolean isDone() {
        return state == TaskState.ABORTED || state == TaskState.READY;
    }

    enum TaskState {
        CREATED,
        RUNNING,
        ABORTED,
        READY
    }

}
