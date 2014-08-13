package com.subrosagames.subrosa.domain.game.persistence;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.PostType;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;

/**
 * Persisted post.
 */
@Entity
@Table(name = "post")
public class PostEntity implements Post {

    @Id
    @SequenceGenerator(name = "postSeq", sequenceName = "post_post_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postSeq")
    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "game_id")
    private Integer gameId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column
    private String content;

    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "accolade_id")
    private Integer accoladeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column
    private Date created;

    @Column
    private Date modified;

    @PrePersist
    protected void prePersist() {
        created = new Date();
        modified = new Date();
    }

    @PreUpdate
    protected void preUpdate() {
        modified = new Date();
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public Integer getAccoladeId() {
        return accoladeId;
    }

    public void setAccoladeId(Integer accoladeId) {
        this.accoladeId = accoladeId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Date getModified() {
        return modified == null ? null : new Date(modified.getTime());
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : new Date(modified.getTime());
    }

    public Date getCreated() {
        return created == null ? null : new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = created == null ? null : new Date(created.getTime());
    }

}
