package com.subrosagames.subrosa.domain.game.persistence;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.PostType;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted game post.
 */
@Data
@Entity
@Table(name = "post")
public class PostEntity extends BaseEntity implements Post {

    @Id
    @SequenceGenerator(name = "postSeq", sequenceName = "post_post_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postSeq")
    @Column(name = "post_id")
    @Getter
    @Setter
    private Integer postId;

    @JsonIgnore
    @ManyToOne(targetEntity = BaseGame.class)
    @JoinColumn(name = "game_id")
    @Getter
    @Setter
    private Game game;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @Getter
    @Setter
    private Account account;

    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private PostType postType;

    @Column
    @Getter
    @Setter
    private String content;

    @Column(name = "history_id")
    @Getter
    @Setter
    private Integer historyId;

    @Column(name = "accolade_id")
    @Getter
    @Setter
    private Integer accoladeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    @Getter
    @Setter
    private Image image;

}
