package View;

import Model.Screen.ScreenObserver;
import Model.Screen.SignatureScreen_Model;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

public class SignatureScreen_View extends AScreenView implements ScreenObserver {
    private JLabel ChosenFilePath_Label;
    private JButton LoadPrivateKey_Button,
            LoadFile_Button,
            DeselectFile_Button,
            CreateSignature_Button,
            CheckSignature_Button
    ;
    private JTextArea PrivateKey_TextArea,
            Signature_TextArea,
            InputText_TextArea,
            OutputText_TextArea;
    ;
    private JRadioButton[] ChooseInputType_RadioButtons;
    private JTabbedPane SignatureNavigator_TabbedPane;
    private JSplitPane MainSplitter_SplitPane;
    private JFileChooser UserFileChosen_FileChooser;
    private PropertyChangeSupport EventFire_Support;

    public void onChangePrivateKey(Consumer<String> callback) {
        PrivateKey_TextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                callback.accept(PrivateKey_TextArea.getText());
            }
        });
    }

    public void onInputFileChosen(Consumer<File> callback) {
        EventFire_Support.addPropertyChangeListener("input_file_chosen", event -> {
            callback.accept((File) event.getNewValue());
        });
    }

    public void onLoadFileButton_Click(Consumer<Void> callback) {
        LoadFile_Button.addActionListener(x -> callback.accept(null));
    }

    public void onCheckingSignatureButton_Click(Consumer<Void> callback) {
        CheckSignature_Button.addActionListener(x -> callback.accept(null));
    }

    public void onChangeSignatureInputText(Consumer<String> callback) {
        Signature_TextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String signatureText = Signature_TextArea.getText();
                if (signatureText != null && signatureText.length() > 0) {
                    callback.accept(Signature_TextArea.getText());
                }
            }
        });
    }

    public void onChangeInputType(Consumer<Integer> callback) {
        ChooseInputType_RadioButtons[0].setActionCommand("file");
        ChooseInputType_RadioButtons[1].setActionCommand("text");
        ItemListener listener = (e) -> {
            callback.accept(ChooseInputType_RadioButtons[0].isSelected() ?
                    SignatureScreen_Model.INPUT_FILE : SignatureScreen_Model.INPUT_TEXT);
        };
        ChooseInputType_RadioButtons[0].addItemListener(listener);
        ChooseInputType_RadioButtons[1].addItemListener(listener);
    }

    public void onCreateSignatureButton_Click(Consumer<Void> callback) {
        CreateSignature_Button.addActionListener(x -> callback.accept(null));
    }

    public void onInputTextChange(Consumer<String> callback) {
        InputText_TextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                callback.accept(InputText_TextArea.getText());
            }
        });
    }

    @Override
    public void initialComponent() {
        LoadPrivateKey_Button = new JButton("Load key");
        LoadFile_Button = new JButton("Choose file");
        DeselectFile_Button = new JButton("Cancel file");
        SignatureNavigator_TabbedPane = new JTabbedPane();
        PrivateKey_TextArea = new JTextArea();
        Signature_TextArea = new JTextArea();
        InputText_TextArea = new JTextArea();
        OutputText_TextArea = new JTextArea(); {
            OutputText_TextArea.setColumns(15);
            OutputText_TextArea.setRows(10);
        }
        ChooseInputType_RadioButtons = new JRadioButton[]{
                new JRadioButton("File"),
                new JRadioButton("Text"),
        };
        ChosenFilePath_Label = new JLabel("No file has been selected.");
        SignatureNavigator_TabbedPane = new JTabbedPane();
        UserFileChosen_FileChooser = new JFileChooser();
        EventFire_Support = new PropertyChangeSupport(this);
        CreateSignature_Button = new JButton("Create signature");
        CheckSignature_Button = new JButton("Validate signature");
        MainSplitter_SplitPane = new JSplitPane();
        MainSplitter_SplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    }
    @Override
    public void initialLayout() {
        JPanel AlgorithmSettingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các thành phần

        gbc.gridy = 0; {
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            AlgorithmSettingsPanel.add(new JLabel("Input type:"), gbc);

            gbc.gridx = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT)); {
                ButtonGroup group = new ButtonGroup(); {
                    group.add(ChooseInputType_RadioButtons[0]);
                    group.add(ChooseInputType_RadioButtons[1]);
                }
                wrapper.add(ChooseInputType_RadioButtons[0]);
                wrapper.add(ChooseInputType_RadioButtons[1]);
            }
            AlgorithmSettingsPanel.add(wrapper, gbc);
        }

        gbc.gridy = 1; {
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            AlgorithmSettingsPanel.add(new JLabel("Input:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            JScrollPane scrollPane = new JScrollPane(InputText_TextArea); {
                InputText_TextArea.setColumns(35);
                InputText_TextArea.setRows(5);
                InputText_TextArea.setLineWrap(true);
                InputText_TextArea.setWrapStyleWord(true);
                InputText_TextArea.addPropertyChangeListener("change_visible", e -> {
                    boolean isShow = (boolean) e.getNewValue();
                    scrollPane.setVisible(isShow);
                });
            }

            JPanel PanelFileWrapper = new JPanel(); {
                var layout = new FlowLayout(FlowLayout.LEFT, 5,0);
                PanelFileWrapper.setLayout(layout);
                PanelFileWrapper.add(LoadFile_Button);
                PanelFileWrapper.add(DeselectFile_Button);
                PanelFileWrapper.add(ChosenFilePath_Label);
            }
            AlgorithmSettingsPanel.add(scrollPane, gbc);
            AlgorithmSettingsPanel.add(PanelFileWrapper, gbc);
        }

        gbc.gridy = 3; {
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            AlgorithmSettingsPanel.add(new JLabel("Private key:"), gbc);

            gbc.gridx = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER; // Chiếm hết cột còn lại
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel wrapper = new JPanel(new BorderLayout()); {
                JScrollPane privateKeyScrollPane = new JScrollPane(PrivateKey_TextArea);
                PrivateKey_TextArea.setWrapStyleWord(true);
                PrivateKey_TextArea.setLineWrap(true);
                PrivateKey_TextArea.setColumns(35);
                PrivateKey_TextArea.setRows(5);
                privateKeyScrollPane.setViewportView(PrivateKey_TextArea);
                wrapper.add(privateKeyScrollPane, BorderLayout.CENTER);

                JPanel PrivateKeyWrapper_JPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                PrivateKeyWrapper_JPanel.add(LoadPrivateKey_Button);
                wrapper.add(PrivateKeyWrapper_JPanel, BorderLayout.SOUTH);
            }
            AlgorithmSettingsPanel.add(wrapper, gbc);
        }

        // Đóng gói panel vào một JPanel Wrapper để căn giữa
        var titledBorder  = BorderFactory.createTitledBorder("Algorithm settings");
        {
            titledBorder.setTitlePosition(TitledBorder.TOP); // Tiêu đề nằm ở phía trên
            titledBorder.setTitleJustification(TitledBorder.LEFT); // Căn giữa tiêu đề

            // Thêm padding (margin bên trong) cho TitledBorder
            titledBorder.setBorder(BorderFactory.createCompoundBorder(
                   new EmptyBorder(0, 10, 0, 10), // Thêm padding vào các cạnh của tiêu đề
                    titledBorder.getBorder()
            ));
        }
        JPanel AlgorithmSettingsPanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JScrollPane HeaderScrollPane = new JScrollPane(AlgorithmSettingsPanelWrapper); {
            HeaderScrollPane.setBorder(new EmptyBorder(0,0,10,0));
        }
        AlgorithmSettingsPanelWrapper.add(AlgorithmSettingsPanel);
        AlgorithmSettingsPanelWrapper.setBorder(titledBorder);

        MainSplitter_SplitPane.setTopComponent(HeaderScrollPane);
        MainSplitter_SplitPane.setBottomComponent(SignatureNavigator_TabbedPane);
        MainSplitter_SplitPane.setDividerSize(10);

        // Thêm vào layout chính của cửa sổ
        this.setLayout(new BorderLayout());
        this.add(MainSplitter_SplitPane, BorderLayout.CENTER); {
            var wrapper = new JPanel(new BorderLayout());
            var output_wrapper = new JPanel(new GridLayout()); {
                JScrollPane scrollPane = new JScrollPane(OutputText_TextArea); {
                    scrollPane.setBorder(new EmptyBorder(5,5,5,5));
                    OutputText_TextArea.setWrapStyleWord(true);
                    OutputText_TextArea.setLineWrap(true);
                    OutputText_TextArea.setColumns(35);
                    OutputText_TextArea.setRows(5);
                    OutputText_TextArea.setEditable(false);
                    scrollPane.setViewportView(OutputText_TextArea);
                }
                output_wrapper.add(scrollPane);
                output_wrapper.setBorder(new CompoundBorder(
                        new EmptyBorder(0, 10, 10, 10),
                        new TitledBorder("Output signature")
                ));
            };
            wrapper.add(output_wrapper, BorderLayout.CENTER);
            var btn_wrapper = new JPanel(); {
                btn_wrapper.add(CreateSignature_Button);
                btn_wrapper.setBorder(new EmptyBorder(0,0,10,0));
            }
            wrapper.add(btn_wrapper, BorderLayout.SOUTH);
            MainSplitter_SplitPane.setBottomComponent(wrapper);
        }
    }

    public void onLoadPrivateKeyButton_Click(Consumer<Void> callback) {
        LoadPrivateKey_Button.addActionListener(e -> callback.accept(null));
    }


    public void onChooseLocation_ForSavePrivateKey(Consumer<File> callback) {
        EventFire_Support.addPropertyChangeListener("user_choose_save_private_key_location", (event) -> {
            File file = (File) event.getNewValue();
            callback.accept(file);
        });
    }

    public void onChooseLocation_ForLoadPrivateKey(Consumer<File> callback) {
        EventFire_Support.addPropertyChangeListener("user_choose_load_private_key_location", (event) -> {
            File file = (File) event.getNewValue();
            callback.accept(file);
        });
    }

    public int openJFileChooser_ForLoadPrivateKey() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files and PEM Files (*.txt, *.pem)", "txt", "pem");
        UserFileChosen_FileChooser.setFileFilter(filter);
        int result = UserFileChosen_FileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = UserFileChosen_FileChooser.getSelectedFile();
            EventFire_Support.firePropertyChange("user_choose_load_private_key_location", null, file);
        }
        return result;
    }

    public void onChooseLocation_ForSavePublicKey(Consumer<File> callback) {
        EventFire_Support.addPropertyChangeListener("user_choose_save_public_key_location", (event) -> {
            File file = (File) event.getNewValue();
            callback.accept(file);
        });
    }

    public void onChooseLocation_ForLoadPublicKey(Consumer<File> callback) {
        EventFire_Support.addPropertyChangeListener("user_choose_load_public_key_location", (event) -> {
            File file = (File) event.getNewValue();
            callback.accept(file);
        });
    }

    @Override
    public void update(String event, Map<String, Object> data) {
        switch (event) {
            case "change_input_mode" -> {
                int currentInputMode = (int) data.get("current_input_mode");
                if (currentInputMode == SignatureScreen_Model.INPUT_FILE) {
                    InputText_TextArea.firePropertyChange("change_visible", true, false);
                    LoadFile_Button.setVisible(true);
                    DeselectFile_Button.setVisible(true);
                    if (!ChooseInputType_RadioButtons[0].isSelected()) {
                        ChooseInputType_RadioButtons[0].setSelected(true);
                    }
                } else {
                    InputText_TextArea.firePropertyChange("change_visible", false, true);
                    LoadFile_Button.setVisible(false);
                    DeselectFile_Button.setVisible(false);
                    if (!ChooseInputType_RadioButtons[1].isSelected()) {
                        ChooseInputType_RadioButtons[1].setSelected(true);
                    }
                }
            }
            case "created_signature" -> {
                String signature = (String) data.get("signature_created");
                OutputText_TextArea.setText(signature);
            }
            case "open_file_chooser_for_chosen_file" -> {
                int result = UserFileChosen_FileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    var file = UserFileChosen_FileChooser.getSelectedFile();
                    EventFire_Support.firePropertyChange("input_file_chosen", null, file);
                    ChosenFilePath_Label.setText(file.getAbsolutePath());
                }
            }
            case "load_private_key" -> {
                String secretKey = (String) data.get("current_private_key");
                PrivateKey_TextArea.setText(secretKey);
            }
        }
    }
}
