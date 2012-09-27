package com.subrosagames.subrosa.domain.game.dispute;

import java.util.List;

import com.subrosagames.subrosa.domain.game.AssassinsGame;
import com.subrosagames.subrosa.domain.game.Player;

public class Dispute {

    private AssassinsGame game;
    private Player plaintiff;
    private Player defendant;
    private DisputeType type;
    private DisputeStatus status;
    private String complaint;
    private List<DisputeComment> comments;

    public AssassinsGame getGame() {
        return game;
    }

    public void setGame(AssassinsGame game) {
        this.game = game;
    }

    public Player getPlaintiff() {
        return plaintiff;
    }

    public void setPlaintiff(Player plaintiff) {
        this.plaintiff = plaintiff;
    }

    public Player getDefendant() {
        return defendant;
    }

    public void setDefendant(Player defendant) {
        this.defendant = defendant;
    }

    public DisputeType getType() {
        return type;
    }

    public void setType(DisputeType type) {
        this.type = type;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

}
