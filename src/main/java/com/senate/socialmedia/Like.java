package com.senate.socialmedia;

import jakarta.persistence.*;

@Entity
@Table(name = "post_likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kim beğendi?
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Neyi beğendi?
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // Getter/Setter ve constructor'ları ekleyin.
    // (JPA, bu model üzerinden kolayca 'Post 5'i beğenen kullanıcıları' bulabilir.)
}