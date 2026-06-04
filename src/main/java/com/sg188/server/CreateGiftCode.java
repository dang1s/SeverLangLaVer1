/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.server;

import com.sg188.data.DataCenter;
import com.sg188.data.ItemOptionTemplate;
import com.sg188.data.SkillTemplate;
import com.sg188.lib.Log;
import com.sg188.real.Item;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import table.HoverIndex;
import table.TableCustom;

/**
 *
 * @author ADMIN
 */
public class CreateGiftCode extends javax.swing.JFrame {

    /**
     * Creates new form FormReadIconFromLocal
     */
    public static class Code {

        public String Code;
        public int Bac;
        public int BacKhoa;
        public int Vang;
        public int VangKhoa;
        public long Exp;
        public byte Day;
        public int count=9999;
        public Item infoItem;
    }

    private List<JTextField> jTextAll = new ArrayList<>();
    private TableRowSorter<TableModel> rowSorter;
    private String[] StringLable = {"Code", "Bạc", "Bạc Khóa", "Vàng", "Vàng Khóa", "Exp", "InfoItem"};
    //  public String OptionsShow = "";
    //public String ItemsShow = "";
    public int CountItem;
    private Item ArrItem ;
    String[] option = new String[DataCenter.gI().ItemOptionTemplate.length];
    public List<Code> Codes = new ArrayList<>();

    private void cleanUp() {
        ArrItem =null;
    }

    public CreateGiftCode() {
        initComponents();
        setValueComboBox();
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Log.debug("Selected: " + jComboBox1.getSelectedItem());
                Log.debug(", Position: " + jComboBox1.getSelectedIndex());
                jTextField9.setText(option[jComboBox1.getSelectedIndex()]);

            }
        };
        jComboBox1.addActionListener(actionListener);

        jTextAll.add(jTextBac);
        jTextAll.add(jTextCode);
        jTextAll.add(jTextBacKhoa);
        jTextAll.add(jTextVang);
        jTextAll.add(jTextVangKhoa);
        jTextAll.add(jTextExp);
        jTextAll.add(jTextItemId);
        jTextAll.add(jTextTime);
        jTextAll.add(jTextValueOption);

    }

    private void setValueComboBox() {

        int i = 0;
        for (ItemOptionTemplate Temp : DataCenter.gI().ItemOptionTemplate) {
            option[i] = "ID :" + Temp.id + " " + Temp.name;
            i++;
        }
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(option));

    }

    private boolean showListItem() {
        if (!checkInputText()) {
            showMessageWarring("Hãy nhập đầy đủ thông tin");
            return false;
        }

        String Code = jTextCode.getText();
        int BacKhoa = -1;
        int Vang = -1;
        int VangKhoa = -1;
        int Bac = -1;
        long exp = -1;
        byte day = -1;
        try {
            String textBac = jTextBac.getText();
            String textVang = jTextVang.getText();
            String textVangKhoa = jTextVangKhoa.getText();
            String textBacKhoa = jTextBacKhoa.getText();
            String textExp = jTextExp.getText();
            String textTime = jTextTime.getText();
            Bac = Integer.parseInt(textBac);
            BacKhoa = Integer.parseInt(textBacKhoa);
            Vang = Integer.parseInt(textVang);
            VangKhoa = Integer.parseInt(textVangKhoa);
            exp = Long.parseLong(textExp);
            day = Byte.parseByte(textTime);
        } catch (Exception e) {
            showMessageWarring("Không được phép bé hơn " + Integer.MIN_VALUE + " hoặc lớn hơn " + Integer.MAX_VALUE);
            e.printStackTrace();
            return false;
        }


        if (ArrItem == null) {
            showMessageWarring("Hãy tạo item trước khi thêm code!");
            return false;
        }
        Code codeDb = new Code();
        codeDb.Code = Code;
        codeDb.BacKhoa = BacKhoa;
        codeDb.Bac = Bac;
        codeDb.Vang = Vang;
        codeDb.VangKhoa = VangKhoa;
        codeDb.Exp = exp;
        codeDb.Day = day;
        codeDb.infoItem = ArrItem;
        Codes.add(codeDb);
        CountItem++;
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(StringLable);
        for(Code code : Codes)
        {
            model.addRow(new String[]{code.Code, ""+code.Bac, ""+code.BacKhoa,""+ code.Vang,""+ code.VangKhoa, ""+code.Exp, "Size Item :" + (code.infoItem == null ? 0 : 1)});
        }
        jTable1.setModel(model);
        return true;
    }

    private boolean checkInputText() {
        boolean isOk = true;
        for (JTextField text : jTextAll) {
            if (text.getText().equals("")) {
                return false;
            }
        }
        return isOk;
    }

    private void showMessageWarring(String text) {
        JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog("Warning!");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private void showMessageOk(String title, String text) {
        JOptionPane optionPane = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCode = new javax.swing.JLabel();
        jButtonUpdateDb = new javax.swing.JButton();
        jBac = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextCode = new javax.swing.JTextField();
        jTextBac = new javax.swing.JTextField();
        jTextBacKhoa = new javax.swing.JTextField();
        jTextVang = new javax.swing.JTextField();
        jTextVangKhoa = new javax.swing.JTextField();
        jTextExp = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextItemId = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextValueOption = new javax.swing.JTextField();
        jButtonInsertOption = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextTime = new javax.swing.JTextField();
        jButtonAddCode = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButtonAddItem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArInfoItem = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jCode.setText("Code");

        jButtonUpdateDb.setText("Update Db");
        jButtonUpdateDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateDbActionPerformed(evt);
            }
        });

        jBac.setText("Bạc");

        jLabel3.setText("Bạc Khóa");

        jLabel4.setText("Vàng");

        jLabel5.setText("Vàng Khóa");

        jLabel6.setText("Exp");

        jTextExp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextExpActionPerformed(evt);
            }
        });

        jLabel7.setText("Item ID");

        jTextItemId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextItemIdActionPerformed(evt);
            }
        });

        jLabel8.setText("OptionsStr");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "Code", "Bạc", "Bạc Khóa", "Vàng", "Vàng Khóa", "Exp", "InfoItem"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Long.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Xem Option ID");

        jTextField9.setText("Option From Template");

        jLabel10.setText("Set ValueOption");

        jButtonInsertOption.setText("Thêm Option");
        jButtonInsertOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInsertOptionActionPerformed(evt);
            }
        });

        jLabel11.setText("Ngày hết hạn");

        jButtonAddCode.setText("AddCode");
        jButtonAddCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddCodeActionPerformed(evt);
            }
        });

        jLabel2.setText("View OptionStr");

        jTextField1.setText("View Option Item ADD");

        jButtonAddItem.setText("AddItem");
        jButtonAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddItemActionPerformed(evt);
            }
        });

        jLabel1.setText("InfoItem");

        jTextArInfoItem.setColumns(20);
        jTextArInfoItem.setRows(5);
        jScrollPane2.setViewportView(jTextArInfoItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(236, 236, 236)
                                                .addComponent(jButtonUpdateDb, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jScrollPane1)))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jButtonInsertOption, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(274, 274, 274)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel6)
                                                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel5)
                                                                        .addComponent(jLabel4))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(0, 21, Short.MAX_VALUE))))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                                        .addComponent(jCode, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                                                                                                        .addComponent(jBac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                                        .addComponent(jLabel11))
                                                                                .addGap(41, 41, 41)
                                                                                .addComponent(jTextValueOption, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jButtonAddCode, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jTextVang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jTextVangKhoa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jTextExp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jLabel2)
                                                                                .addGap(18, 18, 18)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(jButtonAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(48, 48, 48))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(jLabel8)
                                                                        .addGap(55, 55, 55)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                                                                .addComponent(jTextTime)))
                                                                .addComponent(jTextCode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jTextBac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jTextBacKhoa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jTextItemId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jCode, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(jTextCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextVang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jBac, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextBac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextVangKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel6)
                                        .addComponent(jTextBacKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextExp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(jTextItemId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jTextTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(8, 8, 8)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jTextValueOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel2)))
                                                        .addComponent(jTextField1)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(5, 5, 5)
                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel10)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(jButtonInsertOption)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jButtonAddCode))
                                                        .addComponent(jButtonAddItem, javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(108, 108, 108)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonUpdateDb, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUpdateDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateDbActionPerformed
        // TODO add your handling code here:
        if(SqlConnection.DbMore.InsertGiftCode(Codes)){
            showMessageOk("Thành công Insert Code","OK");
        }else{
            showMessageWarring("Lỗi xảy ra");
        }
    }//GEN-LAST:event_jButtonUpdateDbActionPerformed

    private void jTextItemIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextItemIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextItemIdActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonAddCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddCodeActionPerformed
        // TODO add your handling code here:
        if(showListItem()){
            this.cleanUp();
        }


    }//GEN-LAST:event_jButtonAddCodeActionPerformed

    private void jTextExpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextExpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextExpActionPerformed

    private void jButtonAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddItemActionPerformed
        // TODO add your handling code here:
        if(ArrItem!=null)
        {
            showMessageWarring("Thư code chỉ chưa được 1 item :D");
            return;
        }
        if (jTextItemId.getText().equals("")) {
            showMessageWarring("Vui lòng điền id item");
            return;
        }
        if (jTextValueOption.getText().equals("")) {
            showMessageWarring("Vui lòng điền chỉ số option");
            return;
        }

        short idItem = -1;
        short idOption = -1;
        short valueOption = -1;
        try {

            idItem = Short.parseShort(jTextItemId.getText());
            valueOption = Short.parseShort(jTextValueOption.getText());
        } catch (Exception e) {
            showMessageWarring("Không tìm thấy item");
            return;
        }
        idOption = DataCenter.gI().ItemOptionTemplate[jComboBox1.getSelectedIndex()].id;
        Item it = new Item(idItem);
        it.strOptions = idOption + "," + valueOption;
        ArrItem = it;

        String OptionName = DataCenter.gI().ItemOptionTemplate[jComboBox1.getSelectedIndex()].name;
        StringBuilder str = new StringBuilder();
        int i = 0;
//        for (Item itShow : ArrItem) {
        if (i > 0) {
            str.append("\n");
        }
        str.append(Color.RED).append("ID :").append(ArrItem.id).append("; Name :").append(DataCenter.gI().ItemTemplate[ArrItem.id].name);
        String[] op = ArrItem.strOptions.split(";");
        for (String opS : op) {
            short id = Short.parseShort(opS.split(",")[0]);
            short value = Short.parseShort(opS.split(",")[1]);
            str.append("\n").append("Option Id :").append(id).append(";Name : ").append(OptionName.replaceAll("#", jTextValueOption.getText()));
        }

        i++;
        // }
        jTextArInfoItem.setText(str.toString());

        // jTextInfoItem.setText(ItemsShow + "\n" + OptionsShow);
        showMessageOk("Thêm thành công item", "Ok");

    }//GEN-LAST:event_jButtonAddItemActionPerformed

    private void jButtonInsertOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInsertOptionActionPerformed
        // TODO add your handling code here:

        if (ArrItem == null) {
            showMessageWarring("Hãy thêm Item trước!");
            return;
        }
        if (jTextItemId.getText().equals("")) {
            showMessageWarring("Vui lòng điền id item");
            return;
        }
        if (jTextValueOption.getText().equals("")) {
            showMessageWarring("Vui lòng điền chỉ số option");
            return;
        }
        short idOption = -1;
        short valueOption = -1;
        try{
            valueOption = Short.parseShort(jTextValueOption.getText());
        }catch(Exception e)
        {
            showMessageOk("Lỗi xảy ra", "valueOption Không được bé hơn "+Short.MIN_VALUE + " hoặc lớn hơn "+Short.MAX_VALUE);
            e.printStackTrace();
            return;
        }
        idOption = DataCenter.gI().ItemOptionTemplate[jComboBox1.getSelectedIndex()].id;
        String OptionName = DataCenter.gI().ItemOptionTemplate[jComboBox1.getSelectedIndex()].name;
        ArrItem.strOptions += ";" + idOption + "," + valueOption;
        StringBuilder str = new StringBuilder();

        str.append("ID :").append(ArrItem.id).append("; Name :").append(DataCenter.gI().ItemTemplate[ArrItem.id].name);
        String[] op = ArrItem.strOptions.split(";");
        for (String opS : op) {
            short id = Short.parseShort(opS.split(",")[0]);
            short value = Short.parseShort(opS.split(",")[1]);
            str.append("\n").append("Option Id :").append(id).append(";Name : ").append(DataCenter.gI().ItemOptionTemplate[id].name.replaceAll("#", ""+value));
        }

        jTextArInfoItem.setText(str.toString());
//        for (Item itShow : ArrItem) {
//            jTextArInfoItem.setText("ID :" + itShow.id + "; Name :" + DataCenter.gI().ItemTemplate[itShow.id].name + "\n" + "Option Id :" + idOption + ";Name : " + OptionName.replaceAll("#", jTextValueOption.getText()));
//        }

        showMessageOk("Thêm thành công option", "Ok");
    }//GEN-LAST:event_jButtonInsertOptionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateGiftCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateGiftCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateGiftCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateGiftCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateGiftCode().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBac;
    private javax.swing.JButton jButtonAddCode;
    private javax.swing.JButton jButtonAddItem;
    private javax.swing.JButton jButtonInsertOption;
    private javax.swing.JButton jButtonUpdateDb;
    private javax.swing.JLabel jCode;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArInfoItem;
    private javax.swing.JTextField jTextBac;
    private javax.swing.JTextField jTextBacKhoa;
    private javax.swing.JTextField jTextCode;
    private javax.swing.JTextField jTextExp;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextItemId;
    private javax.swing.JTextField jTextTime;
    private javax.swing.JTextField jTextValueOption;
    private javax.swing.JTextField jTextVang;
    private javax.swing.JTextField jTextVangKhoa;
    // End of variables declaration//GEN-END:variables
}
