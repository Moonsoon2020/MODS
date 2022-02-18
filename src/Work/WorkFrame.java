package Work;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Scanner;

class WorkFrame extends JFrame {
    JPanel setPanel;
    JPanel getPanel;
    int Width = 1200; // границы графического интерфейса программы в пикселях
    int Height = 500;
    JSpinner [][] spinners;
    int spin = 0;
    String[] strut;
    private int X = -1, Y = -1; // координаты точки
    private static final int R = 5; // радиус точки
    WorkMain workMain;
    boolean flagBig = true; // состояние программы
    WorkFrame(WorkMain workMain) {
        this.workMain = workMain;
        setLayout(new BorderLayout());
        setPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0,0,1000,1000);

            }
        };
        // все векторные величины
        String vector = """
                Сила Н
                Скорость м/с
                Ускорение м/с2
                """;
        setPanel.setLayout(new FlowLayout());
        // все величины
        Scanner scanner = new Scanner("""
                8
                Ничего не выбрано
                Сила Н
                Масса кг
                Скорость м/с
                Ускорение м/с2
                Время мс
                X м
                Y м
                """);
        int obc = scanner.nextInt();
        String[] str = new String[obc];
        spinners = new JSpinner[obc - 1][];
        strut = new String[obc - 1];
        scanner.nextLine();
        for (int i = 0; i < str.length; i++) {
            str[i] = scanner.nextLine();
        }
        JComboBox<String> jComboBox = new JComboBox<>(str);
        jComboBox.addActionListener(e -> {
            if (jComboBox.getSelectedIndex() != 0){
                String s = jComboBox.getItemAt(jComboBox.getSelectedIndex());
                jComboBox.removeItem(s);
                if (!vector.contains(s))
                    spinners[spin] = newElement(s, setPanel, true);
                else
                    spinners[spin] = newElement(s, setPanel, false);
                jComboBox.setSelectedIndex(0);
                strut[spin++] = s;
            }
        });
        JButton button_zanovo = new JButton("Начать заново");
        setPanel.add(jComboBox);
        JButton button = new JButton("Начать моделирование");
        button.addActionListener(e -> {
            if (flagBig) {
                for (int i = 0; i < spin; i++) {
                    // сбор данных и их отправка в workmain
                    switch (strut[i]) {
                        case "Ускорение м/с2" -> workMain.setA((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Сила Н" -> workMain.setF((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Масса кг" -> workMain.setMass((int) spinners[i][0].getValue());
                        case "Скорость м/с" -> workMain.setV((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Время мс" -> workMain.setT((int) spinners[i][0].getValue());
                        case "X м" -> workMain.setX((int) spinners[i][0].getValue());
                        case "Y м" -> workMain.setY((int) spinners[i][0].getValue());
                    }
                }
                if (!workMain.startModeling()) {
                    setPanel.removeAll();
                    es:
                    for (String s : str) {
                        for (int j = 0; j <= jComboBox.getMaximumRowCount(); j++) {
                            if (Objects.equals(s, jComboBox.getItemAt(j)))
                                continue es;
                        }
                        jComboBox.addItem(s);
                    }
                    setPanel.add(jComboBox);
                    setPanel.add(button);
                    setPanel.add(button_zanovo);
                    repaint();
                } else {
                    button.setText("Закрыть модель");
                    flagBig = false;
                    restart(button_zanovo, jComboBox, false);
                }
            } else {
                workMain.noPaint();
                restart(button_zanovo, jComboBox,true);
                button.setText("Начать моделирование");
                X = -1;
                Y = -1;
                repaint();
                flagBig = true;
            }
        });
        button.setPreferredSize(new Dimension(180 , 25));
        setPanel.add(button);
        button_zanovo.setPreferredSize(new Dimension(180 , 25));
        button_zanovo.addActionListener(e -> {
            setPanel.removeAll();
            es:
            for (String s : str) {
                for (int j = 0; j <= jComboBox.getMaximumRowCount(); j++) {
                    if (Objects.equals(s, jComboBox.getItemAt(j)))
                        continue es;
                }
                jComboBox.addItem(s);
            }
            setPanel.add(jComboBox);
            setPanel.add(button);
            setPanel.add(button_zanovo);
            repaint();
        });
        setPanel.add(button_zanovo);
        setPanel.setPreferredSize(new Dimension(200, Height));
        add(setPanel,BorderLayout.WEST);
        getPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // прорисовка компонентов
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 5 , 2000);
                g.setColor(Color.WHITE);
                g.fillRect(5, 0, Width - 200 , Height);
                g.setColor(Color.BLACK);
                g.fillRect(15,5,5 , Height - 55);
                g.fillRect(15,Height - 50 , Width - 400, 5);
                g.setColor(Color.RED);
                if (X != -1 && Y != -1) {
                    g.fillOval(X, Y, R, R); // прорисовка точки (если необходимо)
                }
            }
        };
        getPanel.setPreferredSize(new Dimension(Width - Width * 2 / 7,Height));
        add(getPanel, BorderLayout.CENTER);
        setSize(new Dimension(Width,Height));
        setTitle("MODS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    // рестарт списка настроек точки
    private void restart(JButton button_zanovo, JComboBox comboBox, boolean b){
        for (JSpinner[] spinner : spinners)
            if (spinner != null)
                for (JSpinner jSpinner : spinner) jSpinner.setEnabled(b);
        button_zanovo.setEnabled(b);
        comboBox.setEnabled(b);
    }
    // настройка сбора информации
    private JSpinner[] newElement(String name, JPanel panel, boolean check) {
        JLabel label = new JLabel();
        JSpinner spinner = new JSpinner();
        label.setText(name + "-");
        if (check) {
            label.setPreferredSize(new Dimension(100, 25));
            spinner.setPreferredSize(new Dimension(70, 25));
            spinner.addChangeListener(e -> {
                if ((int) spinner.getValue() < 0){
                    spinner.setValue(0);
                }
            });
            panel.add(label);
            panel.add(spinner);
            revalidate();
            return new JSpinner[]{spinner};
        } else {
            label.setPreferredSize(new Dimension(90, 25));
            spinner.setPreferredSize(new Dimension(35, 25));
            JLabel labelGr = new JLabel("Гр-");
            JSpinner spinnerGr = new JSpinner();
            spinnerGr.addChangeListener(e -> {
                int n = (int) spinnerGr.getValue();
                if (n < 0 || n > 360){ // границы
                    spinnerGr.setValue(0);
                }
            });
            spinner.addChangeListener(e -> {
                if ((int) spinner.getValue() < 0){ // границы
                    spinner.setValue(0);
                }
            });
            spinnerGr.setPreferredSize(new Dimension(35, 25));
            panel.add(label);
            panel.add(spinner);
            panel.add(labelGr);
            panel.add(spinnerGr);
            revalidate();
            return new JSpinner[]{spinner, spinnerGr};
        }
    }
    // замена координат и перерисовка точки
    public void paint(double x, double y) {
        X = (int) x + 20;
        Y = (int) (Height - y - 55);
        repaint();
    }
}