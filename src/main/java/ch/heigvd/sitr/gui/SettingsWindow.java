package ch.heigvd.sitr.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class SettingsWindow {
    private JPanel SettingsPanel;

    private JLabel acronym;
    private JLabel title;
    private JLabel subtitle1;
    private JSeparator separator1;

    private JLabel scenarioLabel;
    private JComboBox scenarioSelector;
    private JSeparator separator2;

    private JSpinner controlerCount1;
    private JSpinner controlerCount2;
    private JSpinner controlerCount3;
    private JSeparator separator3;

    private JLabel subtitle2;
    private JComboBox behaviorSelector;
    private JSeparator sparator4;

    private JButton startButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Paramétrage");
        frame.setContentPane(new SettingsWindow().SettingsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        SpinnerModel model1 = new SpinnerNumberModel(0, 0, 1000, 1);
        SpinnerModel model2 = new SpinnerNumberModel(0, 0, 1000, 1);
        SpinnerModel model3 = new SpinnerNumberModel(0, 0, 1000, 1);
        controlerCount1 = new JSpinner(model1);
        controlerCount2 = new JSpinner(model2);
        controlerCount3 = new JSpinner(model3);
    }
}
