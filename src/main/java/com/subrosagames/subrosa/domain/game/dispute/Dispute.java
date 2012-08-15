package com.subrosagames.subrosa.domain.game.dispute;

import com.subrosagames.subrosa.domain.game.Player;
import com.subrosagames.subrosa.domain.game.Game;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 7:46 午後
 * To change this template use File | Settings | File Templates.
 */
public class Dispute {

    private Game game;
    private Player plaintiff;
    private Player defendant;
    private DisputeType type;
    private DisputeStatus status;
    private String complaint;
    private List<DisputeComment> comments;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
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
