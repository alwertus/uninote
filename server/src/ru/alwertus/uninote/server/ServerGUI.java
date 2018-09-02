package ru.alwertus.uninote.server;

import ru.alwertus.uninote.common.Values;

import javax.swing.*;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ServerGUI {
    // описание главного окна
    static JFrame jFrame = getFrame();          // переменная главного окна
    private static JFrame getFrame() {
        JFrame frame = new JFrame("UniNote Server v:" + Values.VERSION);

        // размеры и положение окна
        int width = 800;
        int height = 600;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - width)/2,(screenSize.height - height)/2,width, height);
        frame.setMinimumSize(new Dimension(width, height));
        frame.pack();

        // кнопки формы
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setVisible(true);
        return frame;
    }      // настройки окна

    static ColorPane cPaneLog = new ColorPane();

    // точка входа
    public ServerGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    // вывод текста в cPaneLog
    public static void printLog(String category, String msg) {
        Color DEFAULT_TEXT_COLOR = Color.BLACK;
        String SEPARATOR = ": ";
        switch (category) {
            case "info":
                cPaneLog.append(Color.GREEN, category + SEPARATOR);
                cPaneLog.append(DEFAULT_TEXT_COLOR, msg, true);
                break;
            case "err":
                cPaneLog.append(Color.RED, category + SEPARATOR);
                cPaneLog.append(DEFAULT_TEXT_COLOR, msg, true);
                break;

            default:
                cPaneLog.append(Color.GRAY, category + SEPARATOR + msg, true);
                break;
        }
    }

    // создание интерфейса
    private static void createGUI() {

        // добавляем всякую шнягу на главное окно
        //jFrame.add(panel);
    }



    // обработчик нажатия на кнопку UpdateListButton
    private static class UpdateListAction implements ActionListener {
        private JList<String> list;

        public UpdateListAction(JList<String> list) {
            this.list = list;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ArrayList<String> lookAndFeelList = new ArrayList<>();
            UIManager.LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
            int lookAndFeelIndex = 0;
            int currentLookAndFeelIndex = 0;
            String currentLookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();

            for (UIManager.LookAndFeelInfo info : infoArray) {
                if (info.getClassName().equals(currentLookAndFeelClassName)) {
                    currentLookAndFeelIndex = lookAndFeelIndex;
                }
                lookAndFeelList.add(info.getName());
                lookAndFeelIndex++;
            }

            String[] listDataArray = new String[lookAndFeelList.size()];
            final String[] newListData = lookAndFeelList.toArray(listDataArray);
            final int newSelectedIndex = currentLookAndFeelIndex;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    list.setListData(newListData);
                    list.setSelectedIndex(newSelectedIndex);
                }
            });
        }
    }

    // обработчик нажатия на кнопку UpdateLookAndFeelButton
    private static class UpdateLookAndFeelAction implements ActionListener {
        private JList<String> list;
        private JFrame rootFrame;

        public UpdateLookAndFeelAction(JFrame frame, JList<String> list) {
            this.rootFrame = frame;
            this.list = list;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String lookAndFeelName = list.getSelectedValue();
            UIManager.LookAndFeelInfo[] infoArray =
                    UIManager.getInstalledLookAndFeels();

            for (UIManager.LookAndFeelInfo info : infoArray) {
                if (info.getName().equals(lookAndFeelName)) {
                    String message = "Look&feel was changed to " + lookAndFeelName;
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        SwingUtilities.updateComponentTreeUI(rootFrame);
                    } catch (ClassNotFoundException e1) {
                        message = "Error: " + info.getClassName() + " not found";
                    } catch (InstantiationException e1) {
                        message = "Error: instantiation exception";
                    } catch (IllegalAccessException e1) {
                        message = "Error: illegal access";
                    } catch (UnsupportedLookAndFeelException e1) {
                        message = "Error: unsupported look and feel";
                    }
                    JOptionPane.showMessageDialog(null, message);
                    break;
                }
            }
        }
    }

    private static void primeri() {
        // цветной лог(скролл)
        JList<String> list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane logScrollPane = new JScrollPane(list);

        // цветной лог
        cPaneLog.add(logScrollPane, BorderLayout.CENTER);

        // панель с выводом лога
        JPanel logPanel = new JPanel();
        logPanel.setBackground(Color.BLUE);
        logPanel.setBorder(BorderFactory.createBevelBorder(1));
        logPanel.add(cPaneLog);

        JButton sendAllBtn = new JButton("Send all");

        // общая панель
        JPanel panel = new JPanel();
        //panel.setBackground(Color.GREEN);
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.setLayout(new BorderLayout());
        panel.add(logPanel, BorderLayout.NORTH);
        panel.add(sendAllBtn, BorderLayout.NORTH);

    /*
    JList<String> list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(list);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(listScrollPane, BorderLayout.CENTER);

        ActionListener updateButtonListener = new UpdateListAction(list);
        updateButtonListener.actionPerformed(
                new ActionEvent(list, ActionEvent.ACTION_PERFORMED, null)
        );

        JButton updateListButton = new JButton("Update list");
        JButton updateLookAndFreeButton = new JButton("Update Look&Feel");

        JPanel btnPannel = new JPanel();
        btnPannel.setBackground(Color.BLUE);
        btnPannel.setLayout(new BoxLayout(btnPannel, BoxLayout.LINE_AXIS));
        btnPannel.add(updateListButton);
        btnPannel.add(Box.createHorizontalStrut(5));
        btnPannel.add(updateLookAndFreeButton);


        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);
        bottomPanel.add(btnPannel);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);                                             // перенос строк
        textArea.append(Color.BLUE, "123");


        updateListButton.addActionListener(updateButtonListener);
        updateLookAndFreeButton.addActionListener(
                new UpdateLookAndFeelAction(frame, list)
        );
     */
    }
}

