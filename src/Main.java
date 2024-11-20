import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    private static ParkingLot parkingLot = new ParkingLot(10.0);
    private static JTable ticketTable;
    private static DefaultTableModel model;
    private static JLabel availableSpotsLabel;
    private static JLabel totalRevenueLabel;
    private static TableRowSorter<DefaultTableModel> sorter;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Gerenciamento de Estacionamento");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Resumo do Estacionamento"));

        availableSpotsLabel = new JLabel("Vagas Disponíveis: " + parkingLot.getAvailableSpots());
        totalRevenueLabel = new JLabel("Receita Total: R$ 0.00");
        availableSpotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(availableSpotsLabel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(totalRevenueLabel);

        JPanel actionPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Ações"));

        JLabel vehicleLabel = new JLabel("Número do Veículo:");
        JTextField vehicleField = new JTextField();

        JLabel descriptionLabel = new JLabel("Descrição do Veículo:");
        JTextField descriptionField = new JTextField();

        JButton generateButton = new JButton("Gerar Ticket");
        generateButton.setBackground(new Color(60, 179, 113));
        generateButton.setForeground(Color.WHITE);

        JButton payButton = new JButton("Pagar Ticket Selecionado");
        payButton.setBackground(new Color(255, 69, 0));
        payButton.setForeground(Color.WHITE);

        JButton exportButton = new JButton("Exportar para .txt");
        exportButton.setBackground(new Color(30, 144, 255));
        exportButton.setForeground(Color.WHITE);

        actionPanel.add(vehicleLabel);
        actionPanel.add(vehicleField);
        actionPanel.add(descriptionLabel);
        actionPanel.add(descriptionField);
        actionPanel.add(generateButton);
        actionPanel.add(payButton);
        actionPanel.add(exportButton);

        model = new DefaultTableModel(new Object[]{"ID do Ticket", "Número do Veículo", "Descrição", "Entrada", "Saída", "Pago"}, 0);
        ticketTable = new JTable(model);
        ticketTable.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(model);
        ticketTable.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(ticketTable);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        filterPanel.add(new JLabel("Pesquisar:"));
        filterPanel.add(searchField);

        JButton showAllButton = new JButton("Mostrar Todos");
        JButton showPaidButton = new JButton("Mostrar Pagos");
        JButton showUnpaidButton = new JButton("Mostrar Não Pagos");

        filterPanel.add(showAllButton);
        filterPanel.add(showPaidButton);
        filterPanel.add(showUnpaidButton);

        JButton settingsButton = new JButton("Configurações");
        settingsButton.setBackground(new Color(128, 128, 128));
        settingsButton.setForeground(Color.WHITE);
        filterPanel.add(settingsButton);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }

            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        showAllButton.addActionListener(e -> sorter.setRowFilter(null));
        showPaidButton.addActionListener(e -> sorter.setRowFilter(RowFilter.regexFilter("Pago", 5)));
        showUnpaidButton.addActionListener(e -> sorter.setRowFilter(RowFilter.regexFilter("Não", 5)));

        settingsButton.addActionListener(e -> {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                "Usuário:", usernameField,
                "Senha:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Autenticação", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals("admin") && password.equals("admin")) {
                    JTextField capacityField = new JTextField(String.valueOf(parkingLot.getCapacity()));
                    JTextField firstThreeHoursRateField = new JTextField(String.valueOf(parkingLot.getFirstThreeHoursRate()));
                    JTextField subsequentHoursRateField = new JTextField(String.valueOf(parkingLot.getSubsequentHoursRate()));


                    Object[] configMessage = {
                        "Capacidade Máxima:", capacityField,
                        "Tarifa (primeiras 3 horas):", firstThreeHoursRateField,
                        "Tarifa (horas subsequentes):", subsequentHoursRateField
                    };

                    int configOption = JOptionPane.showConfirmDialog(null, configMessage, "Configurações do Estacionamento", JOptionPane.OK_CANCEL_OPTION);
                    if (configOption == JOptionPane.OK_OPTION) {
                        try {
                            int newCapacity = Integer.parseInt(capacityField.getText());
                            double newFirstThreeHoursRate = Double.parseDouble(firstThreeHoursRateField.getText());
                            double newSubsequentHoursRate = Double.parseDouble(subsequentHoursRateField.getText());

                            parkingLot.setCapacity(newCapacity);
                            parkingLot.setRates(newFirstThreeHoursRate, newSubsequentHoursRate);

                            JOptionPane.showMessageDialog(null, "Configurações atualizadas com sucesso!");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Por favor, insira valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(summaryPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.EAST);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        frame.add(mainPanel);

        generateButton.addActionListener(e -> {
            String vehicleNumber = vehicleField.getText();
            String vehicleDescription = descriptionField.getText();
            if (!vehicleNumber.isEmpty() && !vehicleDescription.isEmpty()) {
                Ticket newTicket = parkingLot.generateTicket(vehicleNumber, vehicleDescription);
                if (newTicket != null) {
                    model.addRow(new Object[]{
                        newTicket.getTicketId(),
                        newTicket.getVehicleNumber(),
                        newTicket.getVehicleDescription(),
                        newTicket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        "Ainda estacionado",
                        "Não"
                    });
                    vehicleField.setText("");
                    descriptionField.setText("");
                    updateSummaryLabels();
                    JOptionPane.showMessageDialog(frame, "Ticket gerado com sucesso para o veículo " + vehicleNumber);
                } else {
                    JOptionPane.showMessageDialog(frame, "Capacidade do estacionamento atingida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, insira o número do veículo e a descrição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        payButton.addActionListener(e -> {
            int selectedRow = ticketTable.getSelectedRow();
            if (selectedRow >= 0) {
                String ticketId = model.getValueAt(selectedRow, 0).toString();
                boolean success = parkingLot.payTicket(ticketId);
                if (success) {
                    model.setValueAt("Pago", selectedRow, 5);
                    LocalDateTime exitTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
                    model.setValueAt(exitTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), selectedRow, 4);
                    updateSummaryLabels();
                    JOptionPane.showMessageDialog(frame, "Pagamento realizado com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Ticket já pago ou não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, selecione um ticket na tabela.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        exportButton.addActionListener(e -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Tickets_" + System.currentTimeMillis() + ".txt"))) {
                writer.write("Tickets Gerados:\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j) + "\t");
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(frame, "Dados exportados com sucesso!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao exportar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static void updateSummaryLabels() {
        availableSpotsLabel.setText("Vagas Disponíveis: " + parkingLot.getAvailableSpots());
        totalRevenueLabel.setText("Receita Total: R$ " + String.format("%.2f", parkingLot.getTotalRevenue()));
    }
}
