package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;
    // Coffee Valid Ingredient
    Ingredient               coffee       = new Ingredient( "Coffee", 500 );
    // Milk Valid Ingredient
    Ingredient               milk         = new Ingredient( "Milk", 500 );
    // Matcha Valid Ingredient
    Ingredient               matcha       = new Ingredient( "Matcha", 500 );
    // Pumpkin Spice Valid Ingredient
    Ingredient               pumpkinSpice = new Ingredient( "Pumpkin Spice", 500 );
    // Apple Cider Valid Ingredient
    Ingredient               appleCider   = new Ingredient( "Apple Cider", 500 );
    // Chili Powder Invalid Ingredient
    Ingredient               chilidPowder = new Ingredient( "Chili Powder", -500 );

    /**
     * Tests that the inventory gets consumed correctly when a recipe is made
     */
    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();
        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        recipe.addIngredient( new Ingredient( "Coffee", 5 ) );
        recipe.addIngredient( new Ingredient( "Milk", 5 ) );
        recipe.setPrice( 5 );

        ivt.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assert.assertEquals( 495, ivt.getIngredients().get( 0 ).getAmount() );
        Assert.assertEquals( 495, ivt.getIngredients().get( 1 ).getAmount() );
    }

    /**
     * Tests that the inventory gets updated correctly when ingredients are
     * added
     */
    @Test
    @Transactional
    public void testAddInventory1 () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();

        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 4 ).getAmount() );

        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "Coffee", 5 ) );
        ingredients.add( new Ingredient( "Milk", 5 ) );
        ingredients.add( new Ingredient( "Matcha", 5 ) );
        ingredients.add( new Ingredient( "Pumpkin Spice", 5 ) );
        ingredients.add( new Ingredient( "Apple Cider", 5 ) );

        ivt.addIngredients( ingredients );
        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        Assert.assertEquals( "Adding to the inventory should result in correctly-updated values for coffee", 505,
                ivt.getIngredients().get( 0 ).getAmount() );
        Assert.assertEquals( "Adding to the inventory should result in correctly-updated values for milk", 505,
                ivt.getIngredients().get( 1 ).getAmount() );
        Assert.assertEquals( "Adding to the inventory should result in correctly-updated values sugar", 505,
                ivt.getIngredients().get( 2 ).getAmount() );
        Assert.assertEquals( "Adding to the inventory should result in correctly-updated values chocolate", 505,
                ivt.getIngredients().get( 3 ).getAmount() );
        Assert.assertEquals( "Adding to the inventory should result in correctly-updated values chocolate", 505,
                ivt.getIngredients().get( 4 ).getAmount() );
    }

    /**
     * Tests that the correct exceptions are thrown when adding ingredients with
     * negative amounts
     */
    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();

        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 4 ).getAmount() );

        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "Coffee", -5 ) );
        ingredients.add( new Ingredient( "Milk", 3 ) );
        ingredients.add( new Ingredient( "Matcha", 7 ) );
        ingredients.add( new Ingredient( "Pumpkin Spice", 2 ) );
        ingredients.add( new Ingredient( "Apple Cider", 5 ) );

        try {
            ivt.addIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee",
                    500, ivt.getIngredients().get( 0 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk",
                    500, ivt.getIngredients().get( 1 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar",
                    500, ivt.getIngredients().get( 2 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate",
                    500, ivt.getIngredients().get( 3 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate",
                    500, ivt.getIngredients().get( 4 ).getAmount() );
        }
    }

    /**
     * Tests that the correct exception is thrown when all ingredients added are
     * invalid
     */
    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();

        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 4 ).getAmount() );

        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "Coffee", -5 ) );
        ingredients.add( new Ingredient( "Milk", -3 ) );
        ingredients.add( new Ingredient( "Matcha", -7 ) );
        ingredients.add( new Ingredient( "Pumpkin Spice", -2 ) );
        ingredients.add( new Ingredient( "Apple Cider", 5 ) );

        try {
            ivt.addIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee",
                    500, ivt.getIngredients().get( 0 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk",
                    500, ivt.getIngredients().get( 1 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar",
                    500, ivt.getIngredients().get( 2 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate",
                    500, ivt.getIngredients().get( 3 ).getAmount() );
            Assert.assertEquals(
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate",
                    500, ivt.getIngredients().get( 4 ).getAmount() );
        }

    }

    /**
     * Tests whether the inventory is able to correctly determine if it has
     * enough ingredients for a certain recipe
     */
    @Test
    @Transactional
    public void testEnoughIngredients () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();

        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 4 ).getAmount() );

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        recipe.addIngredient( new Ingredient( "Coffee", 5 ) );
        recipe.addIngredient( new Ingredient( "Milk", 5 ) );
        recipe.addIngredient( new Ingredient( "Matcha", 5 ) );
        recipe.addIngredient( new Ingredient( "Pumpkin Spice", 5 ) );
        recipe.addIngredient( new Ingredient( "Apple Cider", 5 ) );
        recipe.setPrice( 5 );

        ivt.useIngredients( recipe );

        assertTrue( ivt.enoughIngredients( recipe ) );

        final Recipe recipeTwo = new Recipe();
        recipeTwo.setName( "Delicious? Messy Coffee" );

        recipeTwo.addIngredient( new Ingredient( "Coffee", 500 ) );
        recipeTwo.addIngredient( new Ingredient( "Milk", 500 ) );
        recipeTwo.addIngredient( new Ingredient( "Matcha", 500 ) );
        recipeTwo.addIngredient( new Ingredient( "Pumpkin Spice", 500 ) );
        recipeTwo.addIngredient( new Ingredient( "Apple Cider", 500 ) );
        recipeTwo.setPrice( 5 );

        ivt.useIngredients( recipeTwo );

        assertFalse( ivt.enoughIngredients( recipeTwo ) );

    }

    /**
     * Tests whether the inventory throws an exception when adding an invalid
     * ingredient/duplicate ingredient
     */
    @Test
    @Transactional
    public void testAddIngredientInvalid () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.clearInventory();

        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( matcha );
        ivt.addIngredient( pumpkinSpice );
        ivt.addIngredient( appleCider );
        try {
            ivt.addIngredient( chilidPowder );
        }
        catch ( final IllegalArgumentException iae ) {
            assertEquals( "Amount cannot be negative", iae.getMessage() );
        }
        inventoryService.save( ivt );

        assertEquals( 500, ivt.getIngredients().get( 0 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 1 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 2 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 3 ).getAmount() );
        assertEquals( 500, ivt.getIngredients().get( 4 ).getAmount() );

        try {
            ivt.addIngredient( appleCider );
        }
        catch ( final IllegalArgumentException iae ) {
            assertEquals( "Duplicate Name", iae.getMessage() );
        }

    }

}
