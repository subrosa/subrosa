package com.subrosagames.subrosa.domain.payment;

import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import org.joda.money.Money;
import com.subrosagames.subrosa.domain.account.Account;

/**
 * Encapsulates a single financial transaction.
 */
public class Transaction {

    private AssassinGame game;
    private Account account;
    private TransactionType type;
    private Money amount;
    private String paypalTransactionId;
    private Money paypalFee;

    public AssassinGame getGame() {
        return game;
    }

    public void setGame(AssassinGame game) {
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
