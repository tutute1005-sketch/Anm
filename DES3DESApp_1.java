import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

// Import Apache POI & PDFBox tương thích bộ thư viện chính thức trong lib
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DES3DESApp_1 extends JFrame {

    private JTextArea taEncryptInput;
    private JComboBox<String> cbEncryptKeyLength;
    private JTextField tfEncryptKey;
    private JComboBox<String> cbEncryptOutputFormat;
    private JTextArea taEncryptOutput;

    private JTextArea taDecryptInput;
    private JComboBox<String> cbDecryptKeyLength;
    private JTextField tfDecryptKey;
    private JComboBox<String> cbDecryptInputFormat;
    private JTextArea taDecryptOutput;

    private final Color bgColor = new Color(185, 217, 247); 
    private final Color btnGreen = new Color(76, 175, 80);
    private final Color btnRed = new Color(244, 67, 54);
    private final Color btnOrange = new Color(255, 152, 0);
    private final Color btnBlue = new Color(33, 150, 243);
    private final Color btnGray = new Color(158, 158, 158);
    private final Color btnPink = new Color(244, 143, 177);

    public DES3DESApp_1() {
        setTitle("DES Encryption/Decryption Tool - HaUI Network Security");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2, 20, 0));

        add(createEncryptionPanel());
        add(createDecryptionPanel());
    }

    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Mã hóa (Encryption)", 
                0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; gbc.weighty = 0.2;
        panel.add(new JLabel("Văn bản gốc:"), gbc);

        taEncryptInput = new JTextArea();
        taEncryptInput.setLineWrap(true); taEncryptInput.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9; gbc.weighty = 0.2;
        panel.add(new JScrollPane(taEncryptInput), gbc);

        JPanel pnlInputButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlInputButtons.setBackground(bgColor);
        JButton btnLoadInput = new JButton("Tải tệp văn bản");
        btnLoadInput.setBackground(btnBlue); btnLoadInput.setForeground(Color.WHITE);
        JButton btnSaveInputText = new JButton("Lưu văn bản gốc");
        btnSaveInputText.setBackground(btnOrange); btnSaveInputText.setForeground(Color.WHITE);
        pnlInputButtons.add(btnLoadInput); pnlInputButtons.add(btnSaveInputText);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(pnlInputButtons, gbc);

        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Độ dài khóa:"), gbc);

        cbEncryptKeyLength = new JComboBox<>(new String[]{"8 bytes (64 bit)", "16 bytes (128 bit)", "24 bytes (192 bit)"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(cbEncryptKeyLength, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Nhập khóa:"), gbc);

        tfEncryptKey = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(tfEncryptKey, gbc);

        JPanel pnlKeyButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlKeyButtons.setBackground(bgColor);
        JButton btnGenKey = new JButton("Sinh khóa"); btnGenKey.setBackground(btnGray);
        JButton btnLoadKey = new JButton("Tải khóa"); btnLoadKey.setBackground(btnBlue); btnLoadKey.setForeground(Color.WHITE);
        JButton btnSaveKey = new JButton("Lưu khóa"); btnSaveKey.setBackground(btnPink);
        pnlKeyButtons.add(btnGenKey); pnlKeyButtons.add(btnLoadKey); pnlKeyButtons.add(btnSaveKey);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE;
        panel.add(pnlKeyButtons, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Định dạng đầu ra:"), gbc);

        cbEncryptOutputFormat = new JComboBox<>(new String[]{"Base64", "Hex"});
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(cbEncryptOutputFormat, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.1; gbc.weighty = 0.2;
        panel.add(new JLabel("Kết quả mã hóa:"), gbc);

        taEncryptOutput = new JTextArea(); taEncryptOutput.setEditable(false);
        taEncryptOutput.setLineWrap(true); taEncryptOutput.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.9; gbc.weighty = 0.2;
        panel.add(new JScrollPane(taEncryptOutput), gbc);

        JPanel pnlActionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlActionButtons.setBackground(bgColor);
        JButton btnEncrypt = new JButton("Mã hóa"); btnEncrypt.setBackground(btnGreen); btnEncrypt.setForeground(Color.WHITE);
        JButton btnClear = new JButton("Xóa"); btnClear.setBackground(btnRed); btnClear.setForeground(Color.WHITE);
        JButton btnSaveOutput = new JButton("Lưu kết quả"); btnSaveOutput.setBackground(btnOrange); btnSaveOutput.setForeground(Color.WHITE);
        pnlActionButtons.add(btnEncrypt); pnlActionButtons.add(btnClear); pnlActionButtons.add(btnSaveOutput);

        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE;
        panel.add(pnlActionButtons, gbc);

        btnLoadInput.addActionListener(e -> loadAdvancedFileToTextArea(taEncryptInput));
        btnSaveInputText.addActionListener(e -> saveTextAreaToFile(taEncryptInput, "original_text.txt"));
        
        btnGenKey.addActionListener(e -> {
            int keyLenBytes = getSelectedKeyLength(cbEncryptKeyLength);
            byte[] keyBytes = new byte[keyLenBytes];
            new SecureRandom().nextBytes(keyBytes);
            StringBuilder hexKey = new StringBuilder();
            for (byte b : keyBytes) hexKey.append(String.format("%02X", b));
            tfEncryptKey.setText(hexKey.toString());
            tfDecryptKey.setText(hexKey.toString());
        });

        btnLoadKey.addActionListener(e -> loadFileToTextField(tfEncryptKey));
        btnSaveKey.addActionListener(e -> saveTextFieldToFile(tfEncryptKey, "secret.key"));
        btnClear.addActionListener(e -> {
            taEncryptInput.setText(""); tfEncryptKey.setText(""); taEncryptOutput.setText("");
        });
        btnSaveOutput.addActionListener(e -> saveTextAreaToFile(taEncryptOutput, "encrypted.txt"));

        btnEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plainText = taEncryptInput.getText().trim();
                String keyInput = tfEncryptKey.getText().trim();
                int keyLength = getSelectedKeyLength(cbEncryptKeyLength);

                List<String> errors = new ArrayList<>();
                if (plainText.isEmpty()) errors.add("- Trường [Văn bản gốc] trống.");
                if (keyInput.isEmpty()) errors.add("- Trường [Nhập khóa] trống.");
                else {
                    boolean isHex = keyInput.matches("(?i)[0-9a-f]+");
                    if (isHex && keyInput.length() != keyLength * 2) {
                        errors.add("- Khóa Hex bảo mật yêu cầu chính xác " + (keyLength * 2) + " ký tự.");
                    } else if (!isHex && keyInput.getBytes(StandardCharsets.UTF_8).length < keyLength) {
                        errors.add("- Khóa văn bản thô yêu cầu tối thiểu " + keyLength + " ký tự.");
                    }
                }

                if (!errors.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, String.join("\n", errors), "Cảnh báo dữ liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    byte[] keyBytes = processKeyBytes(keyInput, keyLength);
                    String algo = (keyLength == 8) ? "DES/ECB/PKCS5Padding" : "DESede/ECB/PKCS5Padding";
                    String keyType = (keyLength == 8) ? "DES" : "DESede";

                    SecretKey secretKey;
                    if (keyLength == 8) {
                        DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
                        secretKey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);
                    } else {
                        secretKey = new SecretKeySpec(keyBytes, keyType);
                    }

                    Cipher cipher = Cipher.getInstance(algo);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

                    if (cbEncryptOutputFormat.getSelectedItem().toString().equals("Base64")) {
                        taEncryptOutput.setText(Base64.getEncoder().encodeToString(encryptedBytes));
                    } else {
                        StringBuilder hexString = new StringBuilder();
                        for (byte b : encryptedBytes) hexString.append(String.format("%02X", b));
                        taEncryptOutput.setText(hexString.toString());
                    }
                    JOptionPane.showMessageDialog(panel, "Mã hóa hoàn tất thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Lỗi mã hóa: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createDecryptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Giải mã (Decryption)", 
                0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.RED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; gbc.weighty = 0.2;
        panel.add(new JLabel("Văn bản mật mã:"), gbc);

        taDecryptInput = new JTextArea();
        taDecryptInput.setLineWrap(true); taDecryptInput.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9; gbc.weighty = 0.2;
        panel.add(new JScrollPane(taDecryptInput), gbc);

        JButton btnLoadInput = new JButton("Tải tệp mã hóa");
        btnLoadInput.setBackground(btnBlue); btnLoadInput.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnLoadInput, gbc);

        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Độ dài khóa:"), gbc);

        cbDecryptKeyLength = new JComboBox<>(new String[]{"8 bytes (64 bit)", "16 bytes (128 bit)", "24 bytes (192 bit)"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(cbDecryptKeyLength, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Nhập khóa:"), gbc);

        tfDecryptKey = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(tfDecryptKey, gbc);

        JPanel pnlKeyButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlKeyButtons.setBackground(bgColor);
        JButton btnLoadKey = new JButton("Tải khóa"); btnLoadKey.setBackground(btnBlue); btnLoadKey.setForeground(Color.WHITE);
        JButton btnSaveKey = new JButton("Lưu khóa"); btnSaveKey.setBackground(btnPink);
        pnlKeyButtons.add(btnLoadKey); pnlKeyButtons.add(btnSaveKey);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE;
        panel.add(pnlKeyButtons, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.1; gbc.weighty = 0.05;
        panel.add(new JLabel("Định dạng đầu vào:"), gbc);

        cbDecryptInputFormat = new JComboBox<>(new String[]{"Base64", "Hex"});
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.9; gbc.weighty = 0.05;
        panel.add(cbDecryptInputFormat, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.1; gbc.weighty = 0.2;
        panel.add(new JLabel("Kết quả giải mã:"), gbc);

        taDecryptOutput = new JTextArea(); taDecryptOutput.setEditable(false);
        taDecryptOutput.setLineWrap(true); taDecryptOutput.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.9; gbc.weighty = 0.2;
        panel.add(new JScrollPane(taDecryptOutput), gbc);

        JPanel pnlActionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlActionButtons.setBackground(bgColor);
        JButton btnDecrypt = new JButton("Giải mã"); btnDecrypt.setBackground(btnBlue); btnDecrypt.setForeground(Color.WHITE);
        JButton btnClear = new JButton("Xóa"); btnClear.setBackground(btnRed); btnClear.setForeground(Color.WHITE);
        JButton btnSaveOutput = new JButton("Lưu kết quả"); btnSaveOutput.setBackground(btnOrange); btnSaveOutput.setForeground(Color.WHITE);
        pnlActionButtons.add(btnDecrypt); pnlActionButtons.add(btnClear); pnlActionButtons.add(btnSaveOutput);

        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 0.9; gbc.weighty = 0.05; gbc.fill = GridBagConstraints.NONE;
        panel.add(pnlActionButtons, gbc);

        btnLoadInput.addActionListener(e -> loadAdvancedFileToTextArea(taDecryptInput));
        btnLoadKey.addActionListener(e -> loadFileToTextField(tfDecryptKey));
        btnSaveKey.addActionListener(e -> saveTextFieldToFile(tfDecryptKey, "secret.key"));
        btnClear.addActionListener(e -> {
            taDecryptInput.setText(""); tfDecryptKey.setText(""); taDecryptOutput.setText("");
        });
        btnSaveOutput.addActionListener(e -> saveTextAreaToFile(taDecryptOutput, "decrypted.txt"));

        btnDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cipherText = taDecryptInput.getText().trim();
                String keyStr = tfDecryptKey.getText().trim();
                int keyLength = getSelectedKeyLength(cbDecryptKeyLength);
                String format = cbDecryptInputFormat.getSelectedItem().toString();

                if (cipherText.isEmpty() || keyStr.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "❗ Vui lòng nhập đầy đủ văn bản mật mã và khóa để giải mã.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean isKeyError = false;
                boolean isCipherFormatError = false;
                String keyErrorMessage = "";
                String cipherErrorMessage = "";

                byte[] keyBytes = null;
                try {
                    boolean isHex = keyStr.matches("(?i)[0-9a-f]+");
                    if (isHex && keyStr.length() != keyLength * 2) {
                        isKeyError = true;
                        keyErrorMessage = "- Khóa cấu trúc Hex yêu cầu chính xác " + (keyLength * 2) + " ký tự.";
                    } else if (!isHex && keyStr.getBytes(StandardCharsets.UTF_8).length < keyLength) {
                        isKeyError = true;
                        keyErrorMessage = "- Khóa văn bản thô yêu cầu đạt tối thiểu " + keyLength + " ký tự.";
                    } else {
                        keyBytes = processKeyBytes(keyStr, keyLength);
                    }
                } catch (Exception ex) {
                    isKeyError = true;
                    keyErrorMessage = "- Khóa mật mã sai cấu trúc định dạng.";
                }

                byte[] decodedBytes = null;
                String cleanCipherText = cipherText.replaceAll("\\s+", "");
                try {
                    if (format.equals("Base64")) {
                        if (!cleanCipherText.matches("^[A-Za-z0-9+/={}\\s\\r\\n]+$")) throw new IllegalArgumentException();
                        decodedBytes = Base64.getDecoder().decode(cleanCipherText);
                    } else {
                        if (!cleanCipherText.matches("(?i)[0-9a-f]+")) throw new IllegalArgumentException();
                        decodedBytes = hexStringToBytes(cleanCipherText);
                    }
                } catch (Exception ex) {
                    isCipherFormatError = true;
                    cipherErrorMessage = "- Văn bản mật mã sai định dạng cấu trúc đầu vào (" + format + ").";
                }

                if (isKeyError && isCipherFormatError) {
                    JOptionPane.showMessageDialog(panel, "Giải mã thất bại!\n" + cipherErrorMessage + "\n" + keyErrorMessage, "Lỗi Hệ Thống Đồng Thời", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (isCipherFormatError) {
                    JOptionPane.showMessageDialog(panel, "Giải mã thất bại!\n" + cipherErrorMessage, "Lỗi Định Dạng Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (isKeyError) {
                    JOptionPane.showMessageDialog(panel, "Giải mã thất bại!\n" + keyErrorMessage, "Lỗi Cấu Trúc Khóa", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String algo = (keyLength == 8) ? "DES/ECB/PKCS5Padding" : "DESede/ECB/PKCS5Padding";
                    String keyType = (keyLength == 8) ? "DES" : "DESede";

                    SecretKey secretKey;
                    if (keyLength == 8) {
                        DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
                        secretKey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);
                    } else {
                        secretKey = new SecretKeySpec(keyBytes, keyType);
                    }

                    Cipher cipher = Cipher.getInstance(algo);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    byte[] decryptedBytes = cipher.doFinal(decodedBytes);

                    taDecryptOutput.setText(new String(decryptedBytes, StandardCharsets.UTF_8));
                    JOptionPane.showMessageDialog(panel, "Giải mã dữ liệu hoàn tất thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Giải mã thất bại!\n- Khóa giải mã bí mật bị sai (Thuật toán không thể bóc tách block padding).", "Lỗi Mật Mã", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void loadAdvancedFileToTextArea(JTextArea textArea) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Tài liệu văn bản (*.txt, *.docx, *.pdf)", "txt", "docx", "pdf");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fileName = file.getName().toLowerCase();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new Thread(() -> {
                try {
                    if (fileName.endsWith(".docx")) {
                        try (FileInputStream fis = new FileInputStream(file); XWPFDocument document = new XWPFDocument(fis)) {
                            StringBuilder docText = new StringBuilder();
                            for (XWPFParagraph p : document.getParagraphs()) docText.append(p.getText()).append("\n");
                            String resultText = docText.toString();
                            SwingUtilities.invokeLater(() -> {
                                textArea.setText(resultText);
                                setCursor(Cursor.getDefaultCursor());
                                JOptionPane.showMessageDialog(DES3DESApp_1.this, "Đã trích xuất văn bản Word thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }
                    } else if (fileName.endsWith(".pdf")) {
                        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
                            PDFTextStripper pdfStripper = new PDFTextStripper();
                            String pdfText = pdfStripper.getText(document);
                            SwingUtilities.invokeLater(() -> {
                                textArea.setText(pdfText);
                                setCursor(Cursor.getDefaultCursor());
                                JOptionPane.showMessageDialog(DES3DESApp_1.this, "Đã trích xuất dữ liệu PDF thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }
                    } else {
                        String textContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        SwingUtilities.invokeLater(() -> {
                            textArea.setText(textContent);
                            setCursor(Cursor.getDefaultCursor());
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(DES3DESApp_1.this, "Lỗi khi đọc tệp tin: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }

    private byte[] processKeyBytes(String keyInput, int expectedLen) throws Exception {
        byte[] rawBytes = keyInput.matches("(?i)[0-9a-f]+") ? hexStringToBytes(keyInput) : keyInput.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[expectedLen];
        System.arraycopy(rawBytes, 0, keyBytes, 0, Math.min(rawBytes.length, expectedLen));
        return keyBytes;
    }

    private int getSelectedKeyLength(JComboBox<String> comboBox) {
        int index = comboBox.getSelectedIndex();
        switch (index) {
            case 0:
                return 8;
            case 1:
                return 16;
            case 2:
                return 24;
            default:
                return 8;
        }
    }

    private byte[] hexStringToBytes(String hex) {
        int len = hex.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return result;
    }

    private void loadFileToTextField(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                textField.setText(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể tải khóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTextFieldToFile(JTextField textField, String defaultName) {
        if (textField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu khóa để lưu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter out = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8.name())) {
                out.print(textField.getText());
                JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTextAreaToFile(JTextArea textArea, String defaultName) {
        if (textArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu văn bản để lưu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter out = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8.name())) {
                out.print(textArea.getText());
                JOptionPane.showMessageDialog(this, "Lưu tệp văn bản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DES3DESApp_1().setVisible(true));
    }
}