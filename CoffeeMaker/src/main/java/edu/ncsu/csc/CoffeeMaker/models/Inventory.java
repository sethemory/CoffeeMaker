package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        for ( final Ingredient i : r.getIngredients() ) {
            for ( final Ingredient ingre : this.ingredients ) {
                if ( i.getName().equalsIgnoreCase( ingre.getName() ) && i.getAmount() > ingre.getAmount() ) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {
            for ( final Ingredient i : r.getIngredients() ) {
                for ( final Ingredient ingre : this.ingredients ) {
                    if ( i.getName().equalsIgnoreCase( ingre.getName() ) ) {
                        ingre.setAmount( ingre.getAmount() - i.getAmount() );
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Update ingredients in the inventory
     *
     * @return true if successful, false if not
     */
    public boolean addIngredients ( final List<Ingredient> ingredients ) {
        for ( final Ingredient i : ingredients ) {
            if ( i.getAmount() < 0 ) {
                throw new IllegalArgumentException( "Amount cannot be negative" );
            }
        }
        for ( final Ingredient i : ingredients ) {
            for ( final Ingredient ingre : this.ingredients ) {
                if ( i.getName().equalsIgnoreCase( ingre.getName() ) ) {
                    ingre.setAmount( ingre.getAmount() + i.getAmount() );
                }
            }
        }
        return true;
    }

    /**
     * Add ingredient in the inventory
     *
     * @return true if successful, false if not
     */
    public boolean addIngredient ( final Ingredient ingredient ) {
        if ( ingredient.getAmount() < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        for ( final Ingredient i : this.ingredients ) {
            if ( ingredient.getName().equalsIgnoreCase( i.getName() ) ) {
                throw new IllegalArgumentException( "Duplicate Name" );
            }
        }
        this.ingredients.add( ingredient );
        return true;
    }

    /**
     * Used to clear inventory for testing
     */
    public void clearInventory () {
        ingredients.clear();
    }

}
