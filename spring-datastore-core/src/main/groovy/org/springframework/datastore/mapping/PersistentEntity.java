package org.springframework.datastore.mapping;

import org.springframework.datastore.mapping.lifecycle.Initializable;
import org.springframework.datastore.mapping.types.Association;

import java.util.List;

/**
 *
 * Represents a persistent entity
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface PersistentEntity extends Initializable {

    /**
     * The entity name including any package prefix
     *
     * @return The entity name
     */
    String getName();

    /**
     * Returns the identity of the instance
     *
     * @return The identity
     */
    PersistentProperty getIdentity();

    /**
     * A list of properties to be persisted
     * @return A list of PersistentProperty instances
     */
    List<PersistentProperty> getPersistentProperties();

    /**
     * A list of the associations for this entity. This is typically
     * a subset of the list returned by {@link #getPersistentProperties()}
     *
     * @return A list of associations
     */
    List <Association> getAssociations();

    /**
     * Obtains a PersistentProperty instance by name
     *
     * @param name The name of the property
     * @return The PersistentProperty or null if it doesn't exist
     */
    PersistentProperty getPropertyByName(String name);

    /**
     * @return The underlying Java class for this entity
     */
    Class getJavaClass();

    /**
     * Tests whether the given instance is an instance of this persistent entity
     *
     * @param obj The object
     * @return True if it is
     */
    boolean isInstance(Object obj);

    /**
     * Defines the mapping between this persistent entity
     * and an external form
     *
     * @return The ClassMapping instance
     */
    ClassMapping getMapping();

    /**
     * Constructs a new instance
     * @return The new instnace
     */
    Object newInstance();

    /**
     * A list of property names that a persistent
     * @return A List of strings
     */
    List<String> getPersistentPropertyNames();

    /**
     * @return Returns the name of the class decapitalized form
     */
    String getDecapitalizedName();

    /**
     * Returns whether the specified entity asserts ownership over this
     * entity
     *
     * @param owner The owning entity
     * @return True if it does own this entity
     */
    boolean isOwningEntity(PersistentEntity owner);

    /**
     * Returns the parent entity of this entity
     * @return The ParentEntity instance
     */
    PersistentEntity getParentEntity();

    /**
     * Obtains the root entity of an inheritance hierarchy
     * @return The root entity
     */
    PersistentEntity getRootEntity();

    /**
     * Whether this entity is a root entity
     * @return True if it is a root entity
     */
    boolean isRoot();

    /**
     * The discriminator used when persisting subclasses of an inheritance hierarchy
     * @return The discriminator
     */
    String getDiscriminator();

    /**
     * Obtains the MappingContext where this PersistentEntity is defined
     * @return The MappingContext instance
     */
    MappingContext getMappingContext();
}
