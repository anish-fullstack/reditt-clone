package com.epsilon.redit.model;

import com.epsilon.redit.enumeration.VoteType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends BaseIdEntity implements Serializable {

    private static final long serialVersionUID = 74350L;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private PortalUser portalUser;
}
