package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 512, nullable = false)
    private String description;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Long ownerId;

    //TODO при реализации запросов уточнить вид связи
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
