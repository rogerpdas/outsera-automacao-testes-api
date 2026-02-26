package models;

public record ProductPayload(
    String title,
    Double price,
    String description,
    String category,
    Integer stock
) {
}
