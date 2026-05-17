import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Todo extends JPanel {
    public static ArrayList<AddTask> tasks = new ArrayList<>();
    JLabel addL,delL, Heading,timerLabel;
    JPanel Task,timerPanel,progressPanel;
    JTextField txt;
    JButton add, del, startTimer, pauseTimer, resetTimer;
    Timer pomodoroTimer;
    int pomodoroDuration = 25 * 60; // 25 minutes in seconds
    int currentTimer = pomodoroDuration;
    boolean isTimerRunning = false;
    JProgressBar progressBar;
    public Todo() {

        Heading = new JLabel("TO-DO\n");
        Heading.setFont(new java.awt.Font("Georgia",Font.BOLD,30));
        txt = new JTextField(200);
        txt.setBackground(new Color(250, 229, 191));
        addL = new JLabel("Enter the task:");
        addL.setFont(new java.awt.Font("Georgia",Font.BOLD,14));
        add = new JButton("Add Task");
        add.setBackground(new Color(106, 156, 137));
        delL = new JLabel("TASKS");
        delL.setFont(new Font("Arial", Font.BOLD, 20));
        del = new JButton("Delete task");
        del.setBackground(new Color(205, 92, 8));
        del.setForeground(new Color(242, 229, 191));
        Task = new JPanel();
        Task.setLayout(new BoxLayout(Task, BoxLayout.Y_AXIS));
        Task.setBackground(new Color(242, 229, 191));

        progressPanel = new JPanel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar);

        setBackground(new Color(242, 229, 191));
        setPreferredSize(new Dimension(580, 700));
        setLayout(null);
        add(Heading);
        add(addL);
        add(delL);
        add(txt);
        add(add);
        add(del);
        add(Task);
        add(progressPanel);
        timerPanel = new JPanel(new FlowLayout());
        timerLabel = new JLabel("25:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        startTimer = new JButton("Start");
        pauseTimer = new JButton("Pause");
        resetTimer = new JButton("Reset");
        timerPanel.add(timerLabel);
        timerPanel.add(startTimer);
        timerPanel.add(pauseTimer);
        timerPanel.add(resetTimer);
        add(timerPanel);
        timerPanel.setBackground(new Color(242, 229, 191));
        timerLabel.setBackground(new Color(218,112,214));
        startTimer.setBackground(new Color(106, 156, 137));
        pauseTimer.setBackground(new Color(233, 196, 106));
        resetTimer.setBackground(new Color(244, 162, 97));
        progressPanel.setBackground(new Color(242, 229, 191));
        progressBar.setForeground(new Color(106, 156, 137));

        Heading.setBounds(235,60,120,45);
        addL.setBounds(85, 130, 120, 25);
        txt.setBounds(235, 130, 125, 25);
        delL.setBounds(245, 225, 281, 25);
        Task.setBounds(225, 270, 140, 0);
        add.setBounds(385, 130, 100, 25);
        del.setBounds(230, 335, 100, 25);
        timerPanel.setBounds(50,450,500,30);
        progressPanel.setBounds(50,400,500,30);


        add.addActionListener(new AddButtonListener());
        del.addActionListener(new DeleteListener());
        startTimer.addActionListener(e -> startPomodoroTimer());
        pauseTimer.addActionListener(e -> pausePomodoroTimer());
        resetTimer.addActionListener(e -> resetPomodoroTimer());
        pomodoroTimer = new Timer(1000, new TimerListener());
    }

    private void updateProgressBar() {
        int completedTasks = (int) tasks.stream().filter(task -> {
            for (Component component : Task.getComponents()) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.getText().equals(task.getName()) && checkBox.isSelected()) {
                        return true;
                    }
                }
            }
            return false;
        }).count();

        int totalTasks = tasks.size();
        int progress = totalTasks == 0 ? 0 : (completedTasks * 100 / totalTasks);
        progressBar.setValue(progress);
    }

public class AddButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        String text = txt.getText();
        tasks.add(new AddTask(text));

        JCheckBox nameCheckBox = new JCheckBox(text);

        Task.add(nameCheckBox);
        nameCheckBox.setBackground(new Color(244, 162, 97));
        nameCheckBox.setFont(new Font("Georgia", Font.BOLD, 16));
        Task.setBounds(200, 270, 400, Task.getHeight() + 30);
        del.setBounds(230, del.getY() + 25, 100, 25);
        timerPanel.setBounds(50, timerPanel.getY() + 25, 500, 30);
        progressPanel.setBounds(50, progressPanel.getY() + 25, 500, 30);
        updateProgressBar();
        JFrame frame = (JFrame) getRootPane().getParent();
        frame.setSize(frame.getWidth(), frame.getHeight() + 25);
        frame.pack();
        txt.setText("");

        nameCheckBox.addItemListener(event -> {
            if (nameCheckBox.isSelected()) {
                nameCheckBox.setBackground(new Color(138, 177, 125));
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(nameCheckBox, "Yay! Task Complete!!");
                });
            } else {
                nameCheckBox.setBackground(new Color(244, 162, 97));
            }
            nameCheckBox.repaint();
            updateProgressBar();
        });

    }
    }


public class DeleteListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
        Component[] components = Task.getComponents();
        ArrayList<JCheckBox> checkBoxesToRemove = new ArrayList<>();


        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    checkBoxesToRemove.add(checkBox);

                }
            }
        }

        for (JCheckBox checkBox : checkBoxesToRemove) {
            Task.remove(checkBox);
            tasks.removeIf(task -> task.getName().equals(checkBox.getText()));
        }

        updateProgressBar();
        Task.revalidate();
        Task.repaint();
        del.setBounds(230, del.getY() - (checkBoxesToRemove.size() * 25), 100, 25);
        JFrame frame = (JFrame) getRootPane().getParent();
        frame.setSize(frame.getWidth(), frame.getHeight() - (checkBoxesToRemove.size() * 25));
        frame.pack();
    }
    }
        private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (currentTimer > 0) {
                currentTimer--;
                updateTimerLabel();
            } else {
                pomodoroTimer.stop();
                isTimerRunning = false;
                JOptionPane.showMessageDialog(null, "Pomodoro session complete! Time for a break.");
                currentTimer = pomodoroDuration;
                updateTimerLabel();
            }
        }
    }

    private void startPomodoroTimer() {
        if (!isTimerRunning) {
            pomodoroTimer.start();
            isTimerRunning = true;
        }
    }


    private void pausePomodoroTimer() {
        pomodoroTimer.stop();
        isTimerRunning = false;
    }


    private void resetPomodoroTimer() {
        pomodoroTimer.stop();
        isTimerRunning = false;
        currentTimer = pomodoroDuration;
        updateTimerLabel();
    }


    private void updateTimerLabel() {
        int minutes = currentTimer / 60;
        int seconds = currentTimer % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }


    public static void main(String[] args){
        JFrame todo=new JFrame("TO-DO");
        todo.setTitle("TO-DO");
        todo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        todo.getContentPane().add(new Todo());

        todo.setBackground(Color.getHSBColor(240,17,100));
        todo.pack();
        todo.setVisible(true);

    }
}
