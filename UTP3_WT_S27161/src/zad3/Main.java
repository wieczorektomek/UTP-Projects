/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad3;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(TaskManager::new);
  }
}

class TaskManager extends JFrame {
  private final DefaultListModel<String> taskListModel;
  private final JList<String> taskList;
  private final JTextArea outputArea;
  private final ExecutorService executorService;
  private final List<Future<Integer>> tasks;
  private int taskCounter = 0;

  public TaskManager() {

    setTitle("Task Manager");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setVisible(true);

    taskListModel = new DefaultListModel<>();
    taskList = new JList<>(taskListModel);
    JScrollPane listScrollPane = new JScrollPane(taskList);
    listScrollPane.setPreferredSize(new Dimension(200, 300));
    add(listScrollPane, BorderLayout.WEST);

    outputArea = new JTextArea(20, 40);
    outputArea.setEditable(false);
    JScrollPane outputScrollPane = new JScrollPane(outputArea);
    add(outputScrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = getJPanel();

    add(buttonPanel, BorderLayout.SOUTH);

    executorService = Executors.newFixedThreadPool(3);
    tasks = new ArrayList<>();

    pack();
    setLocationRelativeTo(null);
  }

  private JPanel getJPanel() {
    JPanel buttonPanel = new JPanel();

    JButton addTaskButton = new JButton("Add Task");
    addTaskButton.addActionListener(e -> addNewTask());
    buttonPanel.add(addTaskButton);

    JButton checkStatusButton = new JButton("Check Status");
    checkStatusButton.addActionListener(e -> checkTaskStatus());
    buttonPanel.add(checkStatusButton);

    JButton cancelTaskButton = new JButton("Cancel Task");
    cancelTaskButton.addActionListener(e -> cancelSelectedTask());
    buttonPanel.add(cancelTaskButton);

    JButton showResultButton = new JButton("Show Result");
    showResultButton.addActionListener(e -> showTaskResult());
    buttonPanel.add(showResultButton);

    JButton shutdownButton = new JButton("Shutdown");
    shutdownButton.addActionListener(e -> shutdownExecutorService());
    buttonPanel.add(shutdownButton);
    return buttonPanel;
  }

  private void addNewTask() {
    int taskNumber = ++taskCounter;
    SumTask task = new SumTask(taskNumber, 15);
    Future<Integer> future = executorService.submit(task);
    tasks.add(future);
    taskListModel.addElement("Task " + taskNumber);
    outputArea.append("Task " + taskNumber + " submitted\n");
  }

  private void checkTaskStatus() {
    int selectedIndex = taskList.getSelectedIndex();
    if (selectedIndex == -1) {
      JOptionPane.showMessageDialog(this, "Wybierz zadanie");
      return;
    }

    Future<Integer> selectedTask = tasks.get(selectedIndex);
    String status;
    if (selectedTask.isDone()) {
      status = "Zakończone";
    } else if (selectedTask.isCancelled()) {
      status = "Anulowane";
    } else {
      status = "W trakcie realizacji";
    }

    JOptionPane.showMessageDialog(this, "Status zadania: " + status);
  }

  private void cancelSelectedTask() {
    int selectedIndex = taskList.getSelectedIndex();
    if (selectedIndex == -1) {
      JOptionPane.showMessageDialog(this, "Wybierz zadanie");
      return;
    }

    Future<Integer> selectedTask = tasks.get(selectedIndex);
    boolean cancelled = selectedTask.cancel(true);
    outputArea.append("Próba anulowania zadania " + (selectedIndex + 1) +
            ": " + (cancelled ? "Anulowane" : "Nie udało się anulować") + "\n");
  }

  private void showTaskResult() {
    int selectedIndex = taskList.getSelectedIndex();
    if (selectedIndex == -1) {
      JOptionPane.showMessageDialog(this, "Wybierz zadanie");
      return;
    }

    Future<Integer> selectedTask = tasks.get(selectedIndex);
    try {
      if (selectedTask.isDone() && !selectedTask.isCancelled()) {
        Integer result = selectedTask.get();
        JOptionPane.showMessageDialog(this, "Wynik zadania: " + result);
      } else {
        JOptionPane.showMessageDialog(this, "Zadanie nie zostało jeszcze ukończone lub zostało anulowane");
      }
    } catch (InterruptedException | ExecutionException e) {
      JOptionPane.showMessageDialog(this, "Błąd przy pobieraniu wyniku: " + e.getMessage());
    }
  }

  private void shutdownExecutorService() {
    executorService.shutdown();
    outputArea.append("Zamykanie ExecutorService\n");
  }

  private class SumTask implements Callable<Integer> {
    private final int taskNum;
    private final int limit;

    public SumTask(int taskNum, int limit) {
      this.taskNum = taskNum;
      this.limit = limit;
    }

    @Override
    public Integer call() throws Exception {
      int sum = 0;
      for (int i = 1; i <= limit; i++) {
        if (Thread.currentThread().isInterrupted()) return null;
        sum += i;
        outputArea.append("Zadanie " + taskNum + " częściowy wynik = " + sum + "\n");
        Thread.sleep(1000);
      }
      return sum;
    }
  }
}
