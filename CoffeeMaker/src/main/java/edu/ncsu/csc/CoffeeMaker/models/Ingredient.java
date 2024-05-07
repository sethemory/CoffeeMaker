package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Ingredient extends DomainObject {

    @Id
    @GeneratedValue
    private Long   id;
    private String name;
    private int    amount;

    public Ingredient ( final String name, final int amount ) {
        super();
        this.name = name;
        this.amount = amount;
    }

    public Ingredient () {
        super();
    }

    @Override
    public Long getId () {
        return id;
    }

    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName ( final String name ) {
        this.name = name;
    }

    public int getAmount () {
        return amount;
    }

    public void setAmount ( final int amount ) {
        this.amount = amount;
    }

    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", name=" + name + ", amount=" + amount + "]";
    }

}
