package com.subrosagames.subrosa.domain.payment;

import org.joda.money.Money;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AssassinsGame;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 8:07 午後
 * To change this template use File | Settings | File Templates.
 */
public class Transaction {

    private AssassinsGame game;
    private Account account;
    private TransactionType type;
    private Money amount;
    private String paypalTransactionId;
    private Money paypalFee;

    public AssassinsGame getGame() {
        return game;
    }

    public void setGame(AssassinsGame game) {
        this.game = game;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getPaypalTransactionId() {
        return paypalTransactionId;
    }

    public void setPaypalTransactionId(String paypalTransactionId) {
        this.paypalTransactionId = paypalTransactionId;
    }

    public Money getPaypalFee() {
        return paypalFee;
    }

    public void setPaypalFee(Money paypalFee) {
        this.paypalFee = paypalFee;
    }
}
