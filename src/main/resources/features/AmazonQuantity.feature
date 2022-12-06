Feature: Amazon product

  Scenario Outline: Test to add product, then Verify the total price and Quantity of amazon product

    Given the amazon site "https://www.amazon.com" for online shopping
    When customer search for product "<product>" in home page
    Then user wants to add first product to cart with quantity as <quantity>
    Then user navigates to Cart page
    And user check the total price and quantity of product in cart
    When user reduces the quantity as <updatedQty>
    And user check the total price and quantity of product in cart

    Examples:
    |      product     | quantity   |   updatedQty  |
    |   hats for men   |     2      |      1        |