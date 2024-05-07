package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 * The IngredientService is used to handle CRUD operations on the Recipe model.
 * In addition to all functionality from `Service`, we also have functionality
 * for retrieving a single Ingredient by name.
 *
 * @author Kiran Rao
 *
 */
@Component
@Transactional
public class IngredientService extends Service {

    /**
     * IngredientRepository, to be autowired in by Spring and provide CRUD
     * operations on ingredient model.
     */
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    protected JpaRepository getRepository () {
        // TODO Auto-generated method stub
        return ingredientRepository;
    }

    /**
     * Find a ingredient with the provided name
     *
     * @param name
     *            Name of the ingredient to find
     * @return found ingredient, null if none
     */
    public Ingredient findByName ( final String name ) {
        return ingredientRepository.findByName( name );
    }

}
