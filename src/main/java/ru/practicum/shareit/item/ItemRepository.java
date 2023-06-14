package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long userId);

    @Query(value = "SELECT * FROM items AS i WHERE i.is_available = true AND " +
            "(LOWER(i.name) LIKE CONCAT('%', LOWER(?1) , '%') OR " +
            "LOWER(i.description) LIKE CONCAT('%', LOWER(?1), '%')) ",
            nativeQuery = true)
    List<Item> findByNameOrDescription(String searchString);

}
