package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotNull(message = "Category of product should not be null")
    private Category category;

    @Min(value = 0, message = "The minimum of price is 0")
    private double price;

    private String currency;

    @Min(value = 0, message = "The minimum of discount is 0")
    @Max(value = 100, message = "The maximum of discount is 100")
    private double discount;

    private String discountDescription;

    private List<String> imageURLs;
}
