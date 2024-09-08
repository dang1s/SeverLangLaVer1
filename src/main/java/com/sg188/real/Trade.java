package com.sg188.real;

import com.sg188.lib.Utlis;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trade {

    public Trader[] traders = new Trader[2];
    public boolean isFinish = false;
    public void openUITrade() {
        try {
            traders[0].player.service.openUITrade(traders[1].player.Info.name);
            traders[1].player.service.openUITrade(traders[0].player.Info.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeUITrade() {
        try {
            Char _char1 = traders[0].player;
            Char _char2 = traders[1].player;
            _char1.service.resetScreen();
            _char2.service.resetScreen();
            _char1.cleanTrade();
            _char2.cleanTrade();
            isFinish = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void tradeItemLock(Trader trader) {
        try {
            (trader == this.traders[0] ? this.traders[1] : this.traders[0]).player.service.tradeItemLock(trader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewItemInfo(Char _char) {
        try {
            Trader trader = (_char == this.traders[0].player) ? this.traders[1] : this.traders[0];
            _char.service.viewItemInfo(trader,trader.itemTradeOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            if (!this.isFinish && traders[0].accept && traders[1].accept) {
                Char _char1 = traders[0].player;
                Char _char2 = traders[1].player;
                boolean isError = false;
                String error1 = "";
                String error2 = "";
                try {
                    int num = _char1.getCountNullItemBag();
                    if (traders[1].itemTradeOrder.size() > num) {
                        isError = true;
                        error1 = "Hành trang của bạn không đủ chỗ trống";
                        error2 = "Hành trang của đối phương không đủ chỗ trống";
                        return;
                    }
                    num = _char2.getCountNullItemBag();
                    if (traders[0].itemTradeOrder.size() > num) {
                        isError = true;
                        error2 = "Hành trang của bạn không đủ chỗ trống";
                        error1 = "Hành trang của đối phương không đủ chỗ trống";
                        return;
                    }
                    if (traders[0].coinTradeOrder > _char1.Bag.bac) {
                        isError = true;
                        error1 = "Bạn không đủ Bạc để giao dịch";
                        error2 = "Đối phương không đủ Bạc để giao dịch";
                        return;
                    }
                    if (traders[1].coinTradeOrder > _char2.Bag.bac) {
                        isError = true;
                        error2 = "Bạn không đủ Bạc để giao dịch";
                        error1 = "Đối phương không đủ Bạc để giao dịch";
                        return;
                    }
                    if (traders[0].coinTradeOrder > 500000000) {
                        isError = true;
                        error1 = "Số Bạc tối đa có thể giao dịch là 500.000.000 Bạc.";
                        error2 = "Đối phương đã giao dịch quá giới hạn 500.000.000 Bạc.";
                        return;
                    }
                    if (traders[1].coinTradeOrder > 500000000) {
                        isError = true;
                        error2 = "Số Bạc tối đa có thể giao dịch là 500.000.000 Bạc.";
                        error1 = "Đối phương đã giao dịch quá giới hạn 500.000.000 Bạc.";
                        return;
                    }
                    int numberItem = traders[1].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[1].itemTradeOrder) {
                            int id = item.id;
                            int index = item.index;
                            int quantity = item.getAmount();
                            if (_char2.Bag.arrItemBag[index] == null || _char2.Bag.arrItemBag[index].id != id
                                    || _char2.Bag.arrItemBag[index].getAmount() != quantity || _char2.Bag.arrItemBag[index].isLock) {
                                isError = true;
                                error2 = "Vật phẩm ở ô " + (index + 1) + " không hợp lệ.";
                                error1 = "Đối phương giao dịch vật phẩm không hợp lệ.";
                                return;
                            }
                        }
                    }
                    numberItem = traders[0].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[0].itemTradeOrder) {
                            int id = item.id;
                            int index = item.index;
                            int quantity = item.getAmount();
                            if (_char1.Bag.arrItemBag[index] == null || _char1.Bag.arrItemBag[index].id != id
                                    || _char1.Bag.arrItemBag[index].getAmount() != quantity || _char1.Bag.arrItemBag[index].isLock) {
                                isError = true;
                                error1 = "Vật phẩm ở ô " + (index + 1) + " không hợp lệ.";
                                error2 = "Đối phương giao dịch vật phẩm không hợp lệ.";
                                return;
                            }
                        }
                    }
                    numberItem = traders[1].itemTradeOrder.size();
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
                    String strDate = formatter.format(date);
                    if (numberItem > 0) {
                        for (Item item : traders[1].itemTradeOrder) {
                            int index = item.index;
                            int quantity = item.getAmount();
                            if (_char2.Bag.arrItemBag[index] != null && _char2.Bag.arrItemBag[index].has(quantity)) {
                                Utlis.writing("giaodich/" + _char2.Info.name + ".txt", "Item: " + item.getItemTemplate().name + "[" + item.getItemTemplate().id + "]\nSố Lượng: " + item.amount + "\n" + _char2.Info.name + "=>" + _char1.Info.name + "\nTime: " + strDate + "\n-----------------\n");
                                _char1.addItem(item);
                                _char1.msgAddItemBag(item);
                                _char2.Bag.arrItemBag[index] = null;
                                _char2.msgRemoveItemBag(item);
                            }
                        }
                    }
                    numberItem = traders[0].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[0].itemTradeOrder) {
                            int index = item.index;
                            int quantity = item.getAmount();
                            if (_char1.Bag.arrItemBag[index] != null && _char1.Bag.arrItemBag[index].has(quantity)) {
                                Utlis.writing("giaodich/" + _char1.Info.name + ".txt", "Item: " + item.getItemTemplate().name + "[" + item.getItemTemplate().id + "]\nSố Lượng: " + item.amount + "\n" + _char1.Info.name + "=>" + _char2.Info.name + "\nTime: " + strDate + "\n-----------------\n");
                                _char2.addItem(item);
                                _char2.msgAddItemBag(item);
                                _char1.Bag.arrItemBag[index] = null;
                                _char1.msgRemoveItemBag(item);
                            }
                        }
                    }
                    _char1.addBac(traders[1].coinTradeOrder - traders[0].coinTradeOrder);
                    Utlis.writing("giaodich/" + _char2.Info.name + ".txt",_char2.Info.name+" Đã gửi "+traders[1].coinTradeOrder+" cho "+_char1.Info.name + "\nTime: " + strDate + "\n-----------------\n");
                    _char2.addBac(traders[0].coinTradeOrder - traders[1].coinTradeOrder);
                    Utlis.writing("giaodich/" + _char1.Info.name + ".txt",_char1.Info.name+" Đã gửi "+traders[0].coinTradeOrder+" cho "+_char2.Info.name + "\nTime: " + strDate + "\n-----------------\n");

                    _char1.service.resetAllScreen();
                    _char2.service.resetAllScreen();
                    this.isFinish = true;
                } finally {
                    if (isError) {
                        closeUITrade();
                        _char1.service.alertMessage(error1);
                        _char2.service.alertMessage(error2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
